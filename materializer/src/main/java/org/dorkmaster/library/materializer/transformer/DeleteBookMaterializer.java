package org.dorkmaster.library.materializer.transformer;

import org.dorkmaster.library.event.DeleteBook;
import org.dorkmaster.library.event.Event;
import org.dorkmaster.library.materializer.Materializer;
import org.dorkmaster.library.materializer.dao.BookDao;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

public abstract class DeleteBookMaterializer implements Materializer, GetHandle {
    @CreateSqlObject
    abstract BookDao dao();

    @Override
    public boolean canProcess(Event event) {
        return event instanceof DeleteBook;
    }

    @Override
    public void process(Event event) {
        dao().deleteBook(event.getId());
    }

}
