package com.revenera.gcs.webservices;

import java.util.Collection;
import java.util.List;

public interface ServiceManagement {
  void addService(ServiceProperties service);
  ServiceProperties getService(String serviceName);
  Collection<ServiceProperties> getServices();
}
