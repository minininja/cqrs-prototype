package org.dorkmaster.library.app.library.event;

public class AddBookEvent {
    public String bookSellerId = null;
    public String locationId = null;
    public String title = null;
    public String author = null;
    public String createdBy = "mjackson";

    public String toJson() {
        StringBuilder sb = new StringBuilder("{")
                .append("\"@class\":\"org.dorkmaster.library.event.AddBook\"")
                .append(", \"createdBy\":\"").append(createdBy).append("\"");

        sb.append(", \"bookSellerId\":\"").append(bookSellerId).append('\"');
        sb.append(", \"locationId\":\"").append(locationId).append('\"');
        if ( null != title )
            sb.append(", \"title\":\"").append(title).append('\"');
        if ( null != author)
            sb.append(", \"author\":\"").append(author).append('\"');

        return sb.append("}").toString();
    }
}
