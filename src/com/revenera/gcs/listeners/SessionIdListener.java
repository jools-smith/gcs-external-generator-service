package com.revenera.gcs.listeners;

import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
@SuppressWarnings("unused")
@WebListener
public class SessionIdListener implements HttpSessionIdListener {
  private static final LoggingFactory logger = LoggingFactory.create(SessionIdListener.class);

  public SessionIdListener() {
    logger.me(this);
  }

  /*
   * HttpSessionIdListener
   */
  @Override
  public void sessionIdChanged(HttpSessionEvent httpSessionEvent, String s) {
    logger.in();
  }
}
