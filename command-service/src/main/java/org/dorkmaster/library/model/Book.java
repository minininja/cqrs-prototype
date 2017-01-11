package org.dorkmaster.library.model;

public class Book extends Item {
    protected String title;
    protected String author;
    protected Location location;

    public String getTitle() {
        return title;
    }

    public Book setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Book setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public Book setLocation(Location location) {
        this.location = location;
        return this;
    }
}
