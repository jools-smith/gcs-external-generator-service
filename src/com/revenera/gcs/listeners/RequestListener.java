package com.revenera.gcs.listeners;

import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@SuppressWarnings("unused")
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
    Optional.ofNullable((HttpServletRequest) event.getServletRequest()).ifPresent(req -> {
      logger.debug().log("destroyed",
          req.getMethod(),
          req.getScheme(),
          req.getRequestURI());
    });
  }

  @Override
  public void requestInitialized(ServletRequestEvent event) {
    Optional.ofNullable((HttpServletRequest) event.getServletRequest()).ifPresent(req -> {
      logger.debug().log("initialized",
          req.getMethod(),
          req.getScheme(),
          req.getRequestURI(),
          req.getQueryString(),
          req.getHeader("User-Agent"),
          req.getContentType(),
          req.getProtocol());
    });
  }
}

