package org.dorkmaster.library.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Event extends Serializable {
    String getId();

    Event setId(String id);

    Date getCreated();

    Event setCreated(Date created);

    String getCreatedBy();

    Event setCreatedBy(String createdBy);
}
