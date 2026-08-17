package com.revenera.gcs.implementor;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.logging.Loggable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ImplementorFactory extends Loggable implements ImplementorManagement {

  private final List<TechnologyProperties> implementors = new ArrayList<>();

  private TechnologyProperties defaultImplementor = null;

  public ImplementorFactory() {
    logger.me(this);
  }

  @Override
  public void addImplementor(final TechnologyProperties imp, final boolean isDefault) {

    logger.get().debug("{0} {1}", imp.technologyId(), imp.getClass().getSimpleName());

    this.implementors.add(imp);

    if (isDefault) {
      this.defaultImplementor = imp;
    }
  }

  @Override
  public LicenseGeneratorServiceInterface getDefaultImplementor() {
    return this.defaultImplementor.generator();
  }

  @Override
  public LicenseGeneratorServiceInterface getImplementor(final String id) {

    final TechnologyProperties implementor = this.implementors.stream()
        .filter(t -> t.technologyId().equals(id))
        .findFirst()
        .orElse(null);

    if (!Objects.isNull(implementor)) {
      return implementor.generator();
    }
    else {
      return getDefaultImplementor();
    }
  }

  @Override
  public List<String> getImplementors() {
    return this.implementors.stream()
        .map(TechnologyProperties::technologyId)
        .collect(Collectors.toList());
  }
}
