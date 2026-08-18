package com.revenera.gcs.webservices;

import com.revenera.gcs.logging.Loggable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServiceManager extends Loggable implements ServiceManagement {

  final Map<String, ServiceProperties> services = new HashMap<>();

  @Override
  public void addService(final ServiceProperties service) {
    logger.get().debug("Adding service {0} {1} {2}",
        service.getServiceName(),
        service.getLocalName(),
        service.getEndpointName());

    this.services.put(service.getLocalName(), service);
  }

  @Override
  public ServiceProperties getService(String localName) {
    return this.services.get(localName);
  }

  @Override
  public Collection<ServiceProperties> getServices() {
    return Collections.unmodifiableCollection(this.services.values());
  }
}
