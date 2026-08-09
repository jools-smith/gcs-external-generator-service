package com.revenera.gcs;

import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.utils.Constants;
import com.revenera.gcs.utils.HandyBag;
import com.revenera.gcs.utils.Serializer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@WebServlet("/diagnostics/*")
public class DiagnosticServlet extends HttpServlet {
  private static final LoggingFactory logger = LoggingFactory.create(DiagnosticServlet.class);

  enum Resources {
    STATUS("/status"), HEALTH("/health"), START("/start"), SHUTDOWN("/shutdown"), INVALID("");
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
  }

  private void getStatus(final HttpServletRequest ignored, final HttpServletResponse resp) throws IOException {
    Serializer.serializeJsonIndented(resp.getWriter(), ExecutionContext.getApplicationProperties());
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void getHealth(final HttpServletRequest ignored, final HttpServletResponse resp) throws IOException {
    final MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
    final ThreadMXBean threads = ManagementFactory.getThreadMXBean();
    final OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

    final HandyBag bag = new HandyBag()
        .beginSection("app")
        .with("service", ExecutionContext.getApplicationProperties().getVersionDetails())
        .with("status", "running")
        .with("timestamp", Instant.now().toString())
        .with("upTime", ExecutionContext.getApplicationDuration().toString())
        .endSection()

        .beginSection("java")
        .with("version", System.getProperty("java.version"))
        .with("vendor", System.getProperty("java.vendor"))
        .with("vmName", System.getProperty("java.vm.name"))
        .with("vmVersion", System.getProperty("java.vm.version"))
        .with("jvmUptime", Duration.of(runtime.getUptime(), ChronoUnit.MILLIS).toString())
        .endSection()

        .beginSection("memory")
        .with("heapUsedMB", memory.getHeapMemoryUsage().getUsed() / Constants.MB)
        .with("heapCommittedMB", memory.getHeapMemoryUsage().getCommitted() / Constants.MB)
        .with("heapMaxMB", memory.getHeapMemoryUsage().getMax() / Constants.MB)
        .with("nonHeapUsedMB", memory.getNonHeapMemoryUsage().getUsed() / Constants.MB)
        .with("nonHeapCommittedMB", memory.getNonHeapMemoryUsage().getCommitted() / Constants.MB)
        .endSection()

        .beginSection("threads")
        .with("live", threads.getThreadCount())
        .with("daemon", threads.getDaemonThreadCount())
        .with("peak", threads.getPeakThreadCount())
        .endSection()

        .beginSection("system")
        .with("processors", os.getAvailableProcessors())
        .with("systemLoadAverage", os.getSystemLoadAverage())
        .endSection();

    Serializer.serializeJsonIndented(resp.getWriter(), bag);
    resp.setStatus(HttpServletResponse.SC_OK);

  }

  private void start(final HttpServletRequest ignored, final HttpServletResponse resp) throws IOException {
    resp.getWriter().write("STARTED");
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void shutdown(final HttpServletRequest ignored, final HttpServletResponse resp) throws IOException {
    resp.getWriter().write("SHUTDOWN");
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
