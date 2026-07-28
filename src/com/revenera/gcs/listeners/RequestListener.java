package com.revenera.gcs.listeners;

import com.revenera.gcs.AppContext;
import com.revenera.gcs.Beans;
import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

@WebListener
public class RequestListener implements ServletRequestListener {
  private static final LoggingFactory logger = LoggingFactory.create(RequestListener.class);

  public RequestListener() {
    logger.me(this);
  }

  /*
   * ServletContextAttributeListener
   */
  @Override
  public void requestDestroyed(ServletRequestEvent event) {
    logger.in();
  }

  @Override
  public void requestInitialized(ServletRequestEvent request) {
    logger.in();
  }
}

