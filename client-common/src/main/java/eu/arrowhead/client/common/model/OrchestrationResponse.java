/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.client.common.model;

import java.util.ArrayList;
import java.util.List;

public class OrchestrationResponse {

  private List<OrchestrationForm> response = new ArrayList<>();

  public OrchestrationResponse() {
  }

  public OrchestrationResponse(List<OrchestrationForm> response) {
    this.response = response;
  }

  public List<OrchestrationForm> getResponse() {
    return response;
  }

  public void setResponse(List<OrchestrationForm> response) {
    this.response = response;
  }

}
