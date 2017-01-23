package org.dorkmaster.library;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.dorkmaster.library.api.CommandResource;
import org.dorkmaster.library.api.LookupResource;
import org.dorkmaster.library.api.QueryResource;
import org.dorkmaster.library.jdbi.query.BookDao;
import org.dorkmaster.library.jdbi.query.LocationDao;
import org.dorkmaster.library.service.CommandService;
import org.skife.jdbi.v2.DBI;

/**
 * The main for the the command service
 */
public class CommandApplication extends Application<CommandConfig> {

    public static void main(String[] args) throws Exception {
        new CommandApplication().run(args);
    }

    @Override
    public String getName() {
        return "library";
    }

    @Override
    public void initialize(Bootstrap<CommandConfig> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<CommandConfig>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(CommandConfig commandConfig) {
                return commandConfig.getEventDb();
            }
        });
        bootstrap.addBundle(new SwaggerBundle<CommandConfig>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(CommandConfig configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(CommandConfig configuration, Environment environment) {
        DBIFactory factory = new DBIFactory();
        DBI jdbi = factory.build(environment, configuration.getEventDb(), "hsqldb");
        CommandService service = jdbi.onDemand(CommandService.class);
        environment.jersey().register(new CommandResource(service));
        environment.jersey().register(new QueryResource(jdbi.onDemand(LocationDao.class), jdbi.onDemand(BookDao.class)));
        environment.jersey().register(new LookupResource(configuration));
    }
}
