package com.revenera.gcs.listeners;

import com.revenera.gcs.AppContext;
import com.revenera.gcs.Beans;
import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

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
  public void attributeAdded(ServletRequestAttributeEvent event) {
    logger.in();
    HttpServletRequest req = (HttpServletRequest) event.getServletRequest();

    logger.debug().log(
        req.getMethod(),
        req.getRequestURI(),
        req.getQueryString(),
        req.getRemoteAddr(),
        req.getHeader("User-Agent"),
        req.getContextPath(),
        req.getServletPath());

    logger.debug().log(event.getName(), event.getValue());


  }

  @Override
  public void attributeRemoved(ServletRequestAttributeEvent event) {
    logger.in();
    logger.debug().log(event.getName(), event.getValue());
  }

  @Override
  public void attributeReplaced(ServletRequestAttributeEvent event) {
    logger.in();
    logger.debug().log(event.getName(), event.getValue());
  }
}
