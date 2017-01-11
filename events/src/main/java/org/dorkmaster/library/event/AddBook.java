package org.dorkmaster.library.event;


public class AddBook extends AddItem {
    private String title;
    private String author;
    private String bookSellerId;

    public AddBook() {
        type = ItemType.BOOK;
    }

    public String getTitle() {
        return title;
    }

    public AddBook setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public AddBook setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getBookSellerId() {
        return bookSellerId;
    }

    public AddBook setBookSellerId(String bookSellerId) {
        this.bookSellerId = bookSellerId;
        return this;
    }
}
