package org.dorkmaster.library.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dorkmaster.library.event.Event;
import org.dorkmaster.library.jdbi.command.CommandDao;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * Provides transactional support for the dao
 */
public abstract class CommandService implements GetHandle {

    // we have a number of read methods on the dao that we don't need to worry about transactions for
    // so we'll make the dao available directly rather than having write a bunch of boilerplate.
    @CreateSqlObject
    public abstract CommandDao dao();

    @Transaction
    public Collection<Event> addEvents(Collection<Event> events) {
        ObjectMapper mapper = new ObjectMapper();
        for (Event event : events) {
            try {
                if (null == event.getId()) {
                    event.setId(UUID.randomUUID().toString());
                }
                event.setCreated(new Date());
                dao().storeEvent(event, mapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return events;
    }
}
