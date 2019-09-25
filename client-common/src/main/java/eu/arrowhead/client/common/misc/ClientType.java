/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.client.common.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Enum for different client types, which store the ports they should start their web server on, and their properties,
   which are mandatory to have in their config file. The properties are checked at startup, to see if any mandatory one is missing.*/
public enum ClientType {
  CONSUMER(8080, 8080, null, Arrays.asList("keystore", "keystorepass", "keypass", "truststore", "truststorepass")),
  PROVIDER(8460, 8461, Arrays.asList("service_name", "service_uri", "interfaces", "metadata", "insecure_system_name"),
           Arrays.asList("keystore", "keystorepass", "keypass", "truststore", "truststorepass", "authorization_public_key", "secure_system_name")),
  PUBLISHER(8462, 8463, Arrays.asList("event_type", "insecure_system_name"),
            Arrays.asList("keystore", "keystorepass", "keypass", "truststore", "truststorepass", "secure_system_name")),
  SUBSCRIBER(8464, 8465, Arrays.asList("event_types", "notify_uri", "insecure_system_name"),
             Arrays.asList("keystore", "keystorepass", "keypass", "truststore", "truststorepass", "secure_system_name"));

  private final int insecurePort;
  private final int securePort;
  private final List<String> alwaysMandatoryFields;
  private final List<String> secureMandatoryFields;

  ClientType(int insecPort, int secPort, List<String> awf, List<String> smf) {
    insecurePort = insecPort;
    securePort = secPort;
    if (awf == null) {
      alwaysMandatoryFields = new ArrayList<>();
    } else {
      alwaysMandatoryFields = awf;
    }
    if (smf == null) {
      secureMandatoryFields = new ArrayList<>();
    } else {
      secureMandatoryFields = smf;
    }
  }

  public int getInsecurePort() {
    return insecurePort;
  }

  public int getSecurePort() {
    return securePort;
  }

  public List<String> getAlwaysMandatoryFields() {
    return alwaysMandatoryFields;
  }

  public List<String> getSecureMandatoryFields() {
    return secureMandatoryFields;
  }

}
