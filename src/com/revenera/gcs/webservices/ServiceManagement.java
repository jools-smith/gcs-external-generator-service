package com.revenera.gcs.webservices;

import java.util.List;

public interface ServiceManagement {
  void addService(ServiceProperties service);
  List<ServiceProperties> getServices();
}
