package org.dorkmaster.library.event;

import java.util.Date;

public abstract class AbstractEvent implements Event {
    protected String id;
    protected Date created;
    protected String createdBy;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public AbstractEvent setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    public AbstractEvent setCreated(Date created) {
        this.created = created;
        return this;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    public AbstractEvent setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }
}
