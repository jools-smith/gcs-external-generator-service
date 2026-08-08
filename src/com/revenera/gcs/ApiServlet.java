package com.revenera.gcs;

import com.revenera.gcs.logging.Level;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.utils.Serializer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {
  private static final LoggingFactory logger = LoggingFactory.create(ApiServlet.class);

  enum Resources {
    STATUS("/status"), HEALTH("/health"), START("/start"), SHUTDOWN("/shutdown"), INVALID("");
    private String path;
    Resources(final String path) {
      this.path = path;
    }

    static Resources fromPath(final String path) {
      for (final Resources r : Resources.values()) {
        if (r.path.equals(path)) {
          return r;
        }
      }

      return INVALID;
    }
  }


  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

    try {
      logger.get().debug("Received GET request {0} {1}", req.getRequestURI(), req.getPathInfo());

      switch (Resources.fromPath(req.getPathInfo())) {
        case STATUS:
          getStatus(req, resp);
          break;
        case HEALTH:
          getHealth(req, resp);
          break;
        default:
          resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    }
    catch (Exception e) {
      logger.exception(e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    try {
      logger.get().debug("Received POST request {0} {1}", req.getRequestURI(), req.getPathInfo());

      switch (Resources.fromPath(req.getPathInfo())) {
        case START:
          start(req, resp);
          break;
        case SHUTDOWN:
          shutdown(req, resp);
          break;
        default:
          resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    }
    catch (Exception e) {
      logger.exception(e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private void getStatus(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    try (final ExecutionContext ctx = new ExecutionContext()) {
      resp.setContentType("application/json");
      Serializer.serializeJsonIndented(resp.getWriter(), ExecutionContext.getApplicationProperties());
    }
  }

  private void getHealth(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    try (final ExecutionContext ctx = new ExecutionContext()) {
      resp.setContentType("application/json");
      Serializer.serializeJsonIndented(resp.getWriter(), ExecutionContext.getRecordsBag());
    }
  }

  private void start(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    try (final ExecutionContext ctx = new ExecutionContext()) {
      resp.setContentType("application/json");
      resp.getWriter().write("STARTED");
    }
  }

  private void shutdown(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    try (final ExecutionContext ctx = new ExecutionContext()) {
      resp.setContentType("application/json");
      resp.getWriter().write("SHUTDOWN");
    }
  }
}
