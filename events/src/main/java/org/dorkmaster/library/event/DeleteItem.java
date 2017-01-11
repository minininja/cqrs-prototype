package org.dorkmaster.library.event;

public class DeleteItem extends AbstractEvent implements Event {
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public DeleteItem setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }
}