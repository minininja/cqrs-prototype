package org.dorkmaster.library;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Our configuration items
 */
public class CommandConfig extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory eventDb = new DataSourceFactory();

    public DataSourceFactory getEventDb() {
        return eventDb;
    }

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;
}
