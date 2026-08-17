package com.revenera.gcs.webservices;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ServiceManager implements ServiceManagement{

  final List<ServiceProperties> services = new LinkedList<>();

  @Override
  public void addService(final ServiceProperties service) {
    this.services.add(service);
  }

  @Override
  public List<ServiceProperties> getServices() {
    return Collections.unmodifiableList(this.services);
  }
}
