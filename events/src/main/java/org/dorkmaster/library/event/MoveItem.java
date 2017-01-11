package org.dorkmaster.library.event;

public class MoveItem extends AbstractEvent implements Event {
    private String itemId;
    private String locationId;

    public String getItemId() {
        return itemId;
    }

    public MoveItem setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public String getLocationId() {
        return locationId;
    }

    public MoveItem setLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }
}
