package com.revenera.gcs.listeners;

import com.revenera.gcs.AppContext;
import com.revenera.gcs.Beans;
import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class RequestAttributeListener implements ServletRequestAttributeListener {
  private static final LoggingFactory logger = LoggingFactory.create(RequestAttributeListener.class);

  public RequestAttributeListener() {
    logger.me(this);
  }

  /*
   * ServletRequestAttributeListener
   */
  @Override
  public void attributeAdded(ServletRequestAttributeEvent servletRequestAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeRemoved(ServletRequestAttributeEvent servletRequestAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeReplaced(ServletRequestAttributeEvent servletRequestAttributeEvent) {
    logger.in();
  }
}
