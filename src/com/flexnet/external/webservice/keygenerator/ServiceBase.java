package com.flexnet.external.webservice.keygenerator;

import com.revenera.gcs.logging.Loggable;
import com.revenera.gcs.logging.LoggingFactory;


public abstract class ServiceBase extends Loggable {

//  protected final LoggingFactory logger = LoggingFactory.create(this.getClass());

  protected ServiceBase() {

    this.logger.in();
  }

}