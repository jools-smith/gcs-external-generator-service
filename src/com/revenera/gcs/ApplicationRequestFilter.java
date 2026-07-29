package com.revenera.gcs;

import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@WebFilter(urlPatterns = "/services/*")
public class ApplicationRequestFilter implements Filter {
  private static final LoggingFactory logger = LoggingFactory.create(ApplicationRequestFilter.class);
  
  public ApplicationRequestFilter() {
    logger.me(this);
  }

  @Override
  public void init(final FilterConfig config) throws ServletException {
    logger.debug().log(config.getFilterName(), config.getServletContext().getMajorVersion(), config.getServletContext().getMinorVersion());

    final Enumeration<String> itt = config.getInitParameterNames();
    while (itt.hasMoreElements()) {
      final String key = itt.nextElement();

      logger.info().log(key, config.getInitParameter(key));
    }
  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws ServletException, IOException {

    final HttpServletRequest req = (HttpServletRequest) request;
    logger.info().log("request",
        req.getMethod(),
        req.getRequestURI(),
        req.getRemoteAddr(),
        req.getContentType());

    final HttpServletResponse resp = (HttpServletResponse) response;
    logger.info().log("response",
        resp.getStatus());

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    logger.in();
  }
}
