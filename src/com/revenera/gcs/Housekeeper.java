package com.revenera.gcs;

import com.revenera.gcs.logging.LoggingFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Housekeeper {
  private static final LoggingFactory logger = LoggingFactory.create(Housekeeper.class);

  private ScheduledExecutorService scheduler = null;
  private final Map<Object, ScheduledFuture<?>> runners = new HashMap<>();

  public Housekeeper() {
    logger.me(this);
  }

  void initialize() {
    try {

      scheduler = Executors.newSingleThreadScheduledExecutor(r ->
          new Thread(r) {
            {
              setName(Application.class.getSimpleName() + ".housekeeping");
              setDaemon(false);
            }
          });
    }
    catch (final Throwable e) {
      logger.exception(e);
    }
  }

  void start(final Object id, final Runnable command, final int delay, final int period, final TimeUnit units) {
    logger.info().log("starting", id.toString(), delay, period, units);

    runners.put(id, this.scheduler.scheduleAtFixedRate(command, delay, period, units));
  }

  void cancel(final Object id) {
    try {
      logger.info().log("cancelling", id.toString());

      runners.get(id).cancel(false);
    }
    catch (final Throwable e) {
      logger.exception(e);
    }
  }

  void shutdown() {
    try {
      logger.info().log("shutting down scheduler");

      this.scheduler.shutdown();

      if (!this.scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
        logger.info().log("forcing scheduler shutdown");
        this.scheduler.shutdownNow();
      }
      else {
        logger.info().log("scheduler shutdown completed normally");
      }
    }
    catch (Throwable e) {
      logger.exception(e);
      this.scheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
