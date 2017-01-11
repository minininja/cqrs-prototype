package org.dorkmaster.library.model;

public class Item {
    protected String id;
    protected String foreignId;

    public String getId() {
        return id;
    }

    public Item setId(String id) {
        this.id = id;
        return this;
    }

    public String getForeignId() {
        return foreignId;
    }

    public Item setForeignId(String foreignId) {
        this.foreignId = foreignId;
        return this;
    }
}

