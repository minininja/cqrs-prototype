package org.dorkmaster.library.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dorkmaster.library.CommandConfig;
import org.dorkmaster.library.model.Book;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Api
@Path("/lookup")
@Produces("application/json")
public class LookupResource {
    private CommandConfig commandConfig;
    private Multimap<String,String> isbnPrefixConversion = LinkedHashMultimap.create();

    // needed by swagger
    public LookupResource() {
        isbnPrefixConversion.put("018926", "0445");
        isbnPrefixConversion.put("027778", "0449");
        isbnPrefixConversion.put("037145", "0812");
        isbnPrefixConversion.put("042799", "0785");
        isbnPrefixConversion.put("043144", "0688");
        isbnPrefixConversion.put("044903", "0312");
        isbnPrefixConversion.put("045863", "0517");
        isbnPrefixConversion.put("046594", "0694");
        isbnPrefixConversion.put("047132", "0152");
        isbnPrefixConversion.put("051487", "0816");
        isbnPrefixConversion.put("051488", "0140");
        isbnPrefixConversion.put("060771", "0002");
        isbnPrefixConversion.put("065373", "0373");
        isbnPrefixConversion.put("070992", "0523");
        isbnPrefixConversion.put("070993", "0446");
        isbnPrefixConversion.put("070999", "0345");
        isbnPrefixConversion.put("071001", "0380");
        isbnPrefixConversion.put("071009", "0440");
        isbnPrefixConversion.put("071125", "0886");
        isbnPrefixConversion.put("071136", "0451");
        isbnPrefixConversion.put("071149", "0451");
        isbnPrefixConversion.put("071152", "0515");
        isbnPrefixConversion.put("071162", "0451");
        isbnPrefixConversion.put("071268", "0821");
        isbnPrefixConversion.put("071831", "0425");
        isbnPrefixConversion.put("071842", "0843");
        isbnPrefixConversion.put("072742", "0441");
        isbnPrefixConversion.put("076714", "0671");
        isbnPrefixConversion.put("076783", "0553");
        isbnPrefixConversion.put("076814", "0449");
        isbnPrefixConversion.put("078021", "0872");
        isbnPrefixConversion.put("079808", "0394");
        isbnPrefixConversion.put("090129", "0679");
        isbnPrefixConversion.put("099455", "0061");
        isbnPrefixConversion.put("099769", "0451");
        isbnPrefixConversion.put("034057", "093187");
        isbnPrefixConversion.put("034057", "155560");
        isbnPrefixConversion.put("037145", "0765");
        isbnPrefixConversion.put("050694", "0345");
        isbnPrefixConversion.put("076714", "0743");
        isbnPrefixConversion.put("077434", "0743");
        isbnPrefixConversion.put("645573", "1595");
    }

    public LookupResource(CommandConfig commandConfig) {
        this();
        this.commandConfig = commandConfig;
    }

    private void ifNotNullAdd(Set<Book> set, Book book) {
        if (null != book )
            set.add(book);
    }
    private String subString(String src, int start, int end) {
        if (start <= end && src.length() >= start && src.length() >= end) {
            return src.substring(start, end);
        }
        return null;
    }

    @GET
    @Path("/book")
    public Book lookupBook(@QueryParam("isbn") String isbn) {
        Set<Book> books = new HashSet<>();
        ifNotNullAdd(books, tryGoogle(isbn));
        ifNotNullAdd(books, tryLibraryThing(isbn));

        if (1 == books.size()) {
            return books.iterator().next();
        }

        if (books.size() > 0) {
            Book result = null;
            for (Book candidate : books) {
                if (null == result) {
                    result = candidate;
                }
                // we're basically picking the one that's got the longest title/author and
                // hoping that means it's got the better detail.
                else if (1 == picker(result.getTitle(), candidate.getTitle())) {
                    result = candidate;
                } else if (1 == picker(result.getAuthor(), candidate.getAuthor())) {
                    result = candidate;
                }
            }
            return result;
        }

        if (0 == books.size()) {
            // could be a UPC A code instead, some older book are setup that way

            // 0076714 00295 1

            // 07671400295165604 scanned break it up into 076714 00295 1 65604
            // first part is publisher, convert it with the lookup table
            // second part is product id
            // third part is a check digit
            // fourth part is the last 5 of the isbn


            String pub = subString(isbn, 0, 6);
            String product = subString(isbn, 6, 11);
            String last5 = subString(isbn, 12, 17);

            // no point if we don't have the last part of the isbn (the smaller bar code on the right)
            if (StringUtils.isNotBlank(last5)) {
                Collection<String> pubPrefixes = isbnPrefixConversion.get(pub);
                if (null != pubPrefixes) {
                    for (String prefix : pubPrefixes) {

                        // do the conversion since we found a value

                        // a b c d e f g h i j k l
                        // 9 7 8 0 7 4 3 6 5 6 0 4

                        // check digit conversion
                        long check1 = 978L * 1000000000L + Long.valueOf(prefix) * 100000L + Long.valueOf(last5);
                        long check2 = 131313131313L;
                        long check = (check1 * check2) % 10;

                        // 978 is a fixed prefix for isbn-13
                        String computed = new String("978-" + prefix + "-" + last5 + "-" + check);
                        try {
                            return lookupBook(computed);
                        } catch (WebApplicationException e) {
                            // eat it
                        }
                    }
                }
            }
        }
        throw new WebApplicationException("No book found for isbn: " + isbn, 204);
    }

    private int picker(String a, String b) {
        if (null == a && null != b) {
            return 1;
        }
        if (null != a && null == b) {
            return -1;
        }
        if (a.length() > b.length()) {
            return 1;
        }
        if (a.length() == b.length()) {
            return 0;
        }
        return -1;
    }

    private Book tryLibraryThing(String isbn) {
        JsonNode root = new FluentCall(
                "http://www.librarything.com/services/rest/1.1/?method=librarything.ck.getwork&isbn=" +
                        isbn +
                        "&apikey=" +
                        commandConfig.libraryThingApiKey
        ).accept("application/json").get().asXml();

        if ("ok".equals(root.get("stat").asText())) {
            JsonNode item = root.get("ltml").get("item");
            // authors are a little weird, might have weirdness with multiple author calls
            String author = item.get("author").get("").asText();
            String title = item.get("title").asText();
            return new Book().setAuthor(author).setTitle(title);
        }

        return null;
    }

    private Book tryGoogle(String isbn) {
        JsonNode node = new FluentCall("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn).get().asJson();
        if (1 == node.get("totalItems").asInt(-1)) {
            // cool, found a record
            String title = null;
            String author = null;
            JsonNode items = node.get("items");
            if (items.isArray()) {
                JsonNode item = items.get(0);
                title = item.get("volumeInfo").get("title").asText();
                JsonNode authors = item.get("volumeInfo").get("authors");
                if (authors.isArray()) {
                    StringBuilder sb = new StringBuilder();
                    Iterator<JsonNode> it = authors.elements();
                    while (it.hasNext()) {
                        if (0 != sb.length()) {
                            sb.append(", ");
                        }
                        sb.append(it.next().asText());
                    }
                    author = sb.toString();
                }
            }
            return new Book().setTitle(title).setAuthor(author);
        }
        return null;
    }

    class FluentCall {
        private String url;
        private int status;
        private String contentType;
        private String content;
        private String accept;

        public FluentCall(String url) {
            this.url = url;
        }

        public FluentCall accept(String contentType) {
            accept = contentType;
            return this;
        }

        public FluentCall get() {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse response = null;
            InputStream in = null;
            try {
                if (StringUtils.isNotBlank(accept)) {
                    get.addHeader("Accept", contentType);
                }
                response = client.execute(get);
                status = response.getStatusLine().getStatusCode();
                // only good status for now
                if (status >= 200 && status <= 299) {
                    contentType = response.getFirstHeader("Content-Type").getValue();
                    content = IOUtils.toString(in = response.getEntity().getContent());
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(in);
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }

        public JsonNode asJson() {
            if (StringUtils.isNotBlank(contentType) && contentType.startsWith("application/json")) {
                try {
                    return new ObjectMapper().readTree(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public JsonNode asXml() {
            if (StringUtils.isNotBlank(contentType) && contentType.startsWith("application/xml")) {
                try {
                    return new XmlMapper().readTree(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }
}
