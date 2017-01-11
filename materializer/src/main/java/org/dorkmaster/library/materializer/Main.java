package org.dorkmaster.library.materializer;

import org.apache.commons.dbcp.BasicDataSource;
import org.dorkmaster.library.materializer.transformer.AddBookMaterializer;
import org.dorkmaster.library.materializer.transformer.AddLocationMaterializer;
import org.dorkmaster.library.materializer.transformer.DeleteBookMaterializer;
import org.dorkmaster.library.materializer.transformer.MoveBookMaterializer;
import org.skife.jdbi.v2.DBI;

import javax.activation.DataSource;
import java.util.Collection;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        BasicDataSource ds = new BasicDataSource();

        // TODO move this stuff into a config file
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost/library?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull");
        ds.setUsername("library");
        ds.setPassword("");
//        ds.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
//        ds.setUrl("jdbc:hsqldb:file:/tmp/event/db;shutdown=true");
//        ds.setUsername("sa");
//        ds.setPassword("");

        DBI dbi = new DBI(ds);

        Collection<Materializer> materializers = new LinkedList<>();
        materializers.add(dbi.onDemand(AddBookMaterializer.class));
        materializers.add(dbi.onDemand(AddLocationMaterializer.class));
        materializers.add(dbi.onDemand(DeleteBookMaterializer.class));
        materializers.add(dbi.onDemand(MoveBookMaterializer.class));
        EventService service = dbi.onDemand(EventService.class).setMaterializers(materializers);

        service.processEvents();

    }
}
