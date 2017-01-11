package org.dorkmaster.library.materializer.transformer;

import org.dorkmaster.library.event.Event;
import org.dorkmaster.library.event.MoveBook;
import org.dorkmaster.library.materializer.Materializer;
import org.dorkmaster.library.materializer.dao.BookDao;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

public abstract class MoveBookMaterializer implements Materializer, GetHandle {
    @CreateSqlObject
    abstract BookDao dao();

    @Override
    public boolean canProcess(Event event) {
        return event instanceof MoveBook;
    }

    @Override
    public void process(Event event) {
        MoveBook mb = (MoveBook) event;
        dao().updateLocation(mb.getItemId(), mb.getLocationId());
    }
}
