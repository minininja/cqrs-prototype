package org.dorkmaster.library.materializer.transformer;

import org.apache.commons.lang3.StringUtils;
import org.dorkmaster.library.event.AddBook;
import org.dorkmaster.library.event.Event;
import org.dorkmaster.library.materializer.Materializer;
import org.dorkmaster.library.materializer.dao.BookDao;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

public abstract class AddBookMaterializer implements Materializer, GetHandle {
    @CreateSqlObject
    abstract BookDao dao();

    @Override
    public boolean canProcess(Event event) {
        return event instanceof AddBook;
    }

    @Override
    public void process(Event event) {
        AddBook ab = (AddBook) event;
        if (StringUtils.isNotBlank(ab.getBookSellerId())) {
            dao().addBook(ab.getId(), ab.getBookSellerId(), ab.getTitle(), ab.getAuthor(), ab.getLocationId());
        }
    }
}
