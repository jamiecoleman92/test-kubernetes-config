package io.openliberty.guides.inventory;

import java.util.Properties;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.client.SystemClient;

@RequestScoped
@Path("/systems")
public class InventoryResource {

  @Inject
  InventoryManager manager;

  @Inject
  SystemClient systemClient;

  @GET
  @Path("/{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPropertiesForHost(@PathParam("hostname") String hostname) {
    // Get properties for host
    Properties props = systemClient.getProperties(hostname);
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("{ \"error\" : \"Unknown hostname " + hostname
                             + " or the resource may not be running on the"
                             + " host machine\" }")
                     .build();
    }

    // Add to inventory
    manager.add(hostname, props);
    return Response.ok(props).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public InventoryList listContents() {
    return manager.list();
  }

  @POST
  @Path("/reset")
  public void reset() {
    manager.reset();
  }
}