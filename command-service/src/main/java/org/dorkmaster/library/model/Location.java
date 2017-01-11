package org.dorkmaster.library.model;

public class Location {
    protected String id;
    protected String description;

    public String getId() {
        return id;
    }

    public Location setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Location setDescription(String description) {
        this.description = description;
        return this;
    }
}
