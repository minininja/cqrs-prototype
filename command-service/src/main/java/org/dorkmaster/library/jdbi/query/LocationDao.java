package org.dorkmaster.library.jdbi.query;

import org.apache.commons.io.IOUtils;
import org.dorkmaster.library.model.Location;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public interface LocationDao {
    @SqlQuery("select id, description from location")
    @Mapper(LocationMapper.class)
    Collection<Location> list();

    class LocationMapper implements ResultSetMapper<Location> {
        @Override
        public Location map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            Location result = new Location()
                    .setId(resultSet.getString("id"))
                    .setDescription(resultSet.getString("description"));
            return result;
        }
    }
}
