package org.dorkmaster.library.materializer.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface LocationDao {
    @SqlUpdate("insert into location (id, description) values (:id, :description)")
    void addLocation(@Bind("id") String id, @Bind("description") String description);
}
