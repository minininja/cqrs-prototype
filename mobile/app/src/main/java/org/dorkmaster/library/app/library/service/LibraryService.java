package org.dorkmaster.library.app.library.service;

import org.dorkmaster.library.app.library.event.AddBookEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LibraryService {
    private String toArray(String... json) {
        StringBuilder sb = new StringBuilder();
        for (String j : json) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(j);
        }
        return sb.insert(0, "[").append("]").toString();
    }

    public AddBookEvent lookupDetails(String baseUrl, AddBookEvent event) {
        Response response = lookupBookByIsbn(baseUrl, event.bookSellerId);
        if (200 == response.code) {
            try {
                JSONObject root = new JSONObject(response.content);
                event.title = root.getString("title");
                event.author = root.getString("author");
                return event;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Response lookupBookByIsbn(String baseUrl, String isbn) {
        Response response = new Response();
        String requestUrl = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "lookup/book?isbn=" + isbn;
        InputStream in = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());
            response.code = urlConnection.getResponseCode();
            if (299 >= response.code && response.code >= 200) {
                response.contentType = urlConnection.getContentType();
                response.content = readFully(in);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(in);
        }

        return response;
    }

    public Response submitEvents(String requestUrl, String... events) {
        byte[] json = toArray(events).getBytes();
        Response response = new Response();

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-type", "application/json");
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(json);
                out.flush();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                response.code = urlConnection.getResponseCode();
                if (299 >= response.code && response.code >= 200) {
                    response.contentType = urlConnection.getContentType();
                    response.content = readFully(in);
                }
            } catch (ProtocolException e) {
                response.code = -1;
            } catch (IOException e) {
                response.code = -2;
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            response.code = -3;
            e.printStackTrace();
        }
        return response;
    }

    private void closeQuietly(InputStream in) {
        if (null != in)
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private String readFully(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] buf = new byte[1024 * 1024];
            int count;
            while ((count = in.read(buf)) > 0) {
                sb.append(new String(buf, 0, count));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    class Response {
        public int code = -1;
        public String contentType = null;
        public String content = null;
    }
}
