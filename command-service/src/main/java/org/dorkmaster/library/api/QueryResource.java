package org.dorkmaster.library.api;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.NotImplementedException;
import org.dorkmaster.library.event.ItemType;
import org.dorkmaster.library.jdbi.query.BookDao;
import org.dorkmaster.library.jdbi.query.LocationDao;
import org.dorkmaster.library.model.Item;
import org.dorkmaster.library.model.Location;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;

@Api
@Path("/query")
@Produces("application/json")
public class QueryResource {
    private LocationDao locationDao;

    // we probably want to have a strategy pattern to do the item queries, but since we only have one
    // type we'll leave it simple for now
    private BookDao bookDao;

    public QueryResource(LocationDao locationDao, BookDao bookDao) {
        this.locationDao = locationDao;
        this.bookDao = bookDao;
    }

    @GET
    @Path("/location/items/{locationId}")
    public Collection<Item> listLocation(@PathParam("locationId") String id) {
        return bookDao.listByLocation(id);
    }

    @GET
    @Path("/item/whereIs/{type}/{foreignId}")
    public Collection<Item> listLocation(@PathParam("type") ItemType type, @PathParam("foreignId") String id) {
        if (ItemType.BOOK == type)
            return bookDao.findByBookSellerId(id);
        throw new NotImplementedException("Only books are supported currently");
    }

    @GET
    @Path("/location/list")
    public Collection<Location> listLocations() {
        return locationDao.list();
    }
}
