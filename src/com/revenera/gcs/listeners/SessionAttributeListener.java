package com.revenera.gcs.listeners;

import com.revenera.gcs.AppContext;
import com.revenera.gcs.Beans;
import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener
public class SessionAttributeListener implements HttpSessionAttributeListener {
  private static final LoggingFactory logger = LoggingFactory.create(SessionAttributeListener.class);

  public SessionAttributeListener() {
    logger.me(this);
  }

  /*
   * HttpSessionAttributeListener
   */
  @Override
  public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }

  @Override
  public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }

  @Override
  public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }
}
