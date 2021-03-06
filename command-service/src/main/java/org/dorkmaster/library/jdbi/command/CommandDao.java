package org.dorkmaster.library.jdbi.command;

import org.dorkmaster.library.event.Event;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.Collection;

/**
 * Methods to write events and get them back from the db
 */
@UseStringTemplate3StatementLocator
public interface CommandDao {
    @SqlUpdate("insert into event (id, created, created_by, content) values (:event.id, :event.created, :event.createdBy, :content)")
    void storeEvent(@BindBean("event") Event event, @Bind("content") String json);

    @SqlQuery("select content from (select pk, content from event order by pk desc limit <max>) grr order by pk")
    @Mapper(EventMapper.class)
    Collection<Event> lastEvents(@Define("max") int count);

    @SqlQuery("select content from event where pk > (select pk from event where id = :id) order by pk limit <max>")
    @Mapper(EventMapper.class)
    Collection<Event> since(@Bind("id") String id, @Define("max") int count);

    @SqlQuery("select count(1) from event where pk > (select pk from event where id = :id)")
    long countSince(@Bind("id") String id);
}
