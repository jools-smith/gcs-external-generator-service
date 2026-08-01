package com.revenera.gcs.implementor;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;

public interface TechnologyProperties {

  String technologyName();

  String technologyId();

  LicenseGeneratorServiceInterface generator();

  void configureTechnologyProperties(String id, String name);
}
