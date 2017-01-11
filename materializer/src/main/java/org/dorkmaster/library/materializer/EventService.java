package org.dorkmaster.library.materializer;

import org.dorkmaster.library.event.Event;
import org.dorkmaster.library.materializer.dao.EventDao;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.Transaction;

import java.util.Collection;

public abstract class EventService {
    @CreateSqlObject
    abstract EventDao dao();

    private Collection<Materializer> materializers;

    @Transaction
    public void processEvents() {
        Collection<Event> events;

        boolean processed;
        String lastSeen = dao().lastId();
        if (null == lastSeen) {
            events = dao().findEventsWithNoLastSeen(10);
        } else {
            events = dao().findAfterId(lastSeen, 10);
        }

        while (!events.isEmpty()) {
            for (Event event : events) {
                processed = false;
                for (Materializer materializer : materializers) {
                    if (materializer.canProcess(event)) {
                        materializer.process(event);
                        processed = true;
                    }
                }
                if (!processed) {
                    System.out.println("No materializer for event with id of " + event.getId());
                }
                else {
                    System.out.println("Processed event with id of " + event.getId());
                }
                lastSeen = event.getId();
                dao().updateLastId(lastSeen);
            }
            events = dao().findAfterId(lastSeen, 10);
        }
    }

    public EventService setMaterializers(Collection<Materializer> materializers) {
        this.materializers = materializers;
        return this;
    }
}
