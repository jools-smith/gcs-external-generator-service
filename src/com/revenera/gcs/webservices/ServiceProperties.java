package com.revenera.gcs.webservices;

import com.revenera.gcs.logging.Loggable;

public class ServiceProperties extends Loggable implements ServiceImplementor {
  String interfaceName;
  String implementor;
  
  @Override
  public String getInterfaceName() {
    return this.interfaceName;
  }

  @Override
  public void setInterfaceName(final String value) {
    this.interfaceName = value;
  }

  @Override
  public String getImplementorName() {
    return this.implementor;
  }

  @Override
  public void setImplementorName(final String value) {
    this.implementor =  value;
  }
}
