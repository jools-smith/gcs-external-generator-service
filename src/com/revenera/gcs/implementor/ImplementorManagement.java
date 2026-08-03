package com.revenera.gcs.implementor;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;

import java.util.List;

public interface ImplementorManagement {
  void addImplementor(final TechnologyProperties imp, final boolean isDefault);

  LicenseGeneratorServiceInterface getDefaultImplementor();

  LicenseGeneratorServiceInterface getImplementor(final String id);

  @SuppressWarnings("unused")
  List<String> getImplementors();
}
