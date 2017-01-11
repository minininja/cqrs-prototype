package org.dorkmaster.library.materializer.dao;

import org.dorkmaster.library.event.Event;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.Collection;

@UseStringTemplate3StatementLocator
public interface EventDao {
    @SqlQuery("select last_event_id from materializer_status")
    String lastId();

    @SqlQuery("select id from event where pk = (select min(pk) from event)")
    String firstEventId();

    @SqlUpdate("update materializer_status set last_event_id = :eventId")
    void updateLastId(@Bind("eventId") String id);

    @SqlQuery("select content from event where pk > (select pk from event where id = :eventId) order by pk limit <count>")
    @Mapper(EventMapper.class)
    Collection<Event> findAfterId(@Bind("eventId") String id, @Define("count") int count);

    @SqlQuery("select content from event order by pk limit <count>")
    @Mapper(EventMapper.class)
    Collection<Event> findEventsWithNoLastSeen(@Define("count") int count);
}
