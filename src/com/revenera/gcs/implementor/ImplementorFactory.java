package com.revenera.gcs.implementor;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.logging.LoggingFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ImplementorFactory implements ImplementorManagement {
  private final static LoggingFactory logger = LoggingFactory.create(ImplementorFactory.class);

  private final Map<String, LicenseGeneratorServiceInterface> implementors = new HashMap<>();

  private LicenseGeneratorServiceInterface defaultImplementor = null;

  public ImplementorFactory() {
    logger.me(this);
  }

  @Override
  public void addImplementor(final GeneratorBase imp, final boolean isDefault) {

    logger.debug().log("adding implementor", imp.technologyId(), imp.getClass().getSimpleName());

    this.implementors.put(imp.technologyId(), imp);

    if (isDefault) {
      this.defaultImplementor = imp;
    }
  }

  @Override
  public LicenseGeneratorServiceInterface getDefaultImplementor() {
    return this.defaultImplementor;
  }

  @Override
  public LicenseGeneratorServiceInterface getImplementor(final String id) {

    if (this.implementors.containsKey(id)) {
      final LicenseGeneratorServiceInterface impl = this.implementors.get(id);

      return this.implementors.get(id);
    }
    else {
      return getDefaultImplementor();
    }
  }

  @Override
  public List<String> getImplementors() {
    return new ArrayList<>(this.implementors.keySet());
  }
}
