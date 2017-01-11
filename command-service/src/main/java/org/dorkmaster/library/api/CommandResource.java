package org.dorkmaster.library.api;

import io.swagger.annotations.Api;
import org.dorkmaster.library.event.Event;
import org.dorkmaster.library.service.CommandService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Collection;

@Api
@Path("/cmd")
@Produces("application/json")
public class CommandResource {
    private CommandService service;

    public CommandResource(CommandService service) {
        this.service = service;
    }

    @PUT
    public Collection<Event> submitCommand(Collection<Event> events) {
        return service.addEvents(events);
    }

    @GET
    @Path("/last")
    public Collection<Event> lastEvents(@QueryParam("cnt") @DefaultValue("10") int count) {
        return service.dao().lastEvents(count);
//        Collection<Event> result = new ArrayList<>(100);
//        result.add(new AddLocation().setDescription("description").setCreatedBy("user"));
//        result.add(new AddBook().setAuthor("author").setBookSellerId("bookSellerId").setTitle("title").setLocationId("locationId").setCreatedBy("user"));
//        result.add(new MoveItem().setItemId("itemId").setLocationId("locationId").setCreatedBy("user"));
//        result.add(new DeleteItem().setItemId("itemId").setCreatedBy("user"));
//        return result;
    }

    @GET
    @Path("/since/{id}")
    public SinceResults since(@PathParam("id") String id, @QueryParam("cnt") @DefaultValue("100") int count) {
        return new SinceResults(service.dao().countSince(id), service.dao().since(id, count));
    }

    class SinceResults {
        private long total;
        private Collection<Event> events;

        public SinceResults(long total, Collection<Event> events) {
            this.total = total;
            this.events = events;
        }

        public long getTotal() {
            return total;
        }

        public Collection<Event> getEvents() {
            return events;
        }
    }
}

