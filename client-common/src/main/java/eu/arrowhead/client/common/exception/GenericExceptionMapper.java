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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

  @Inject
  private javax.inject.Provider<ContainerRequest> requestContext;
  @Inject
  private javax.inject.Provider<ContainerResponse> responseContext;

  @Override
  public Response toResponse(Exception ex) {
    ex.printStackTrace();
    int errorCode = 500; //Internal Server Error
    String origin = requestContext.get() != null ? requestContext.get().getAbsolutePath().toString() : "unknown";
    if (responseContext.get() != null && responseContext.get().getStatusInfo().getFamily() != Family.OTHER) {
      errorCode = responseContext.get().getStatus();
    }

    ErrorMessage errorMessage = new ErrorMessage(ex.getClass().toString() + ": " + ex.getMessage(), errorCode, ExceptionType.GENERIC, origin);
    return Response.status(errorCode).entity(errorMessage).header("Content-type", "application/json").build();
  }

}
