package org.dorkmaster.library.materializer.transformer;

import org.dorkmaster.library.event.AddLocation;
import org.dorkmaster.library.event.Event;
import org.dorkmaster.library.materializer.Materializer;
import org.dorkmaster.library.materializer.dao.LocationDao;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

import java.sql.SQLIntegrityConstraintViolationException;

public abstract class AddLocationMaterializer implements Materializer, GetHandle {
    @CreateSqlObject
    abstract LocationDao dao();

    @Override
    public boolean canProcess(Event event) {
        return event instanceof AddLocation;
    }

    @Override
    public void process(Event event) {
        AddLocation al = ((AddLocation) event);
        try {
            dao().addLocation(al.getId(), al.getDescription());
        }
        catch(UnableToExecuteStatementException e) {
            // duplicate entry, ignore it for now
            e.printStackTrace();
        }
    }
}
