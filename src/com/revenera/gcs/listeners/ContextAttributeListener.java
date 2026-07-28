package com.revenera.gcs.listeners;

import com.revenera.gcs.AppContext;
import com.revenera.gcs.Beans;
import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextAttributeListener implements ServletContextAttributeListener {
  private static final LoggingFactory logger = LoggingFactory.create(ContextAttributeListener.class);

  public ContextAttributeListener() {
    logger.me(this);
  }

  /*
   * ServletContextAttributeListener
   */
  @Override
  public void attributeAdded(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeRemoved(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeReplaced(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }


}
