package com.revenera.gcs.implementor;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;

public interface ImplementorManagement {
  void addImplementor(final GeneratorBase imp, final boolean isDefault);

  LicenseGeneratorServiceInterface getDefaultImplementor();

  LicenseGeneratorServiceInterface getImplementor(final String id);
}
