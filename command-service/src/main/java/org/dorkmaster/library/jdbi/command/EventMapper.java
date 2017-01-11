package org.dorkmaster.library.jdbi.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.dorkmaster.library.event.Event;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Converts serialized events back into event objects
 */
public class EventMapper implements ResultSetMapper<Event> {
    @Override
    public Event map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        Clob clob = resultSet.getClob("content");
        InputStream in = null;
        try {
            in = clob.getAsciiStream();
            return new ObjectMapper().readValue(in, Event.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        return null;
    }
}
