package com.revenera.gcs;

import com.revenera.gcs.logging.LoggingFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebServlet("/control/*")
public class ControlServlet extends HttpServlet {
  private static final LoggingFactory logger = LoggingFactory.create(ControlServlet.class);

  enum Resources {
    STATUS("/status"), HEALTH("/health"), START("/start"), INJECT("/inject"), INVALID("");
    private final String path;

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

  public ControlServlet() {
    super();
    logger.me(this);
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        logger.get().debug("Received GET request {0} {1}", req.getRequestURI(), req.getPathInfo());

        resp.setContentType("application/json");

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
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        logger.get().debug("Received POST request {0} {1}", req.getRequestURI(), req.getPathInfo());

        resp.setContentType("application/json");

        switch (Resources.fromPath(req.getPathInfo())) {
          case START:
            start(req, resp);
            break;
          case INJECT:
            inject(req, resp);
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
  }

  @Override
  public String getServletName() {
    return "Revenera GCS Control Servlet";
  }

  @Override
  public void destroy() {
    logger.in();
    super.destroy();
  }

  @Override
  public String getServletInfo() {
    return "Revenera GCS Control Servlet v1.0";
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    logger.in();
    super.init(config);
  }

  @Override
  public void init() throws ServletException {
    logger.in();
    super.init();
  }

  /**
   * IMPLEMENTOR METHODS
   **/

  enum InjectTypes {
    ECHO("log-echo"), LEVEL("log-level");
    final String value;

    InjectTypes(final String value) {
      this.value = value;
    }
  }
  private void getStatus(final HttpServletRequest ignored, final HttpServletResponse resp) {
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void getHealth(final HttpServletRequest ignored, final HttpServletResponse resp) {
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void start(final HttpServletRequest ignored, final HttpServletResponse resp) {
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void inject(final HttpServletRequest request, final HttpServletResponse resp) {

    final Map<String, String[]> map = request.getParameterMap();

    map.forEach((key, value) ->
        logger.get().info("injecting parameter " + key + ": " + Arrays.toString(value)));

    if (map.containsKey(InjectTypes.ECHO.value)) {
      //TODO:fix this
    }

    if (map.containsKey(InjectTypes.LEVEL.value)) {
      //TODO:fix this
    }

    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
