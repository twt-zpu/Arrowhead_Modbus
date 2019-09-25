/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.client.common.exception;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ContainerRequest;

@Provider
public class BadURIExceptionMapper implements ExceptionMapper<NotFoundException> {

  @Inject
  private javax.inject.Provider<ContainerRequest> requestContext;

  public Response toResponse(NotFoundException ex) {
    ex.printStackTrace();
    ErrorMessage errorMessage = new ErrorMessage(requestContext.get().getPath(true) + " is not a valid path!", 400, ExceptionType.BAD_URI,
                                                 requestContext.get().getBaseUri().toString());
    return Response.status(Status.BAD_REQUEST).entity(errorMessage).header("Content-type", "application/json").build();
  }

}
