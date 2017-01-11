package org.dorkmaster.library.event;

public abstract class AddItem extends AbstractEvent implements Event {
    protected String id;
    protected String locationId;
    protected ItemType type;

    public String getId() {
        return id;
    }

    public AddItem setId(String id) {
        this.id = id;
        return this;
    }

    public String getLocationId() {
        return locationId;
    }

    public AddItem setLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public ItemType getType() {
        return type;
    }

    public AddItem setType(ItemType type) {
        this.type = type;
        return this;
    }
}
