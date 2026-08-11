package com.revenera.gcs.implementor;

import com.revenera.gcs.logging.Loggable;

abstract class AbstractTechnology extends Loggable implements TechnologyProperties {
  protected String name;
  protected String id;

  @Override
  public void configureTechnologyProperties(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String technologyId() {
    return this.id;
  }

  @Override
  public String technologyName() {
    return this.name;
  }
}
