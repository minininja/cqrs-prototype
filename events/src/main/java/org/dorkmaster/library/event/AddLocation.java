package org.dorkmaster.library.event;

public class AddLocation extends AbstractEvent {
    private String id;
    private String description;

    public String getId() {
        return id;
    }

    public AddLocation setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AddLocation setDescription(String description) {
        this.description = description;
        return this;
    }
}
