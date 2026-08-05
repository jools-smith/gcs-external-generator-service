package com.revenera.gcs.logging;

import com.revenera.gcs.utils.Frame;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedDeque;

public class LoggingContextFactory {

  final DateTimeFormatter tomcat_formatter = DateTimeFormatter
      .ofPattern("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH)
      .withZone(ZoneId.systemDefault());

  @SuppressWarnings("unused")
  final DateTimeFormatter zulu_formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
          .withZone(ZoneId.systemDefault());

  final Deque<String> messages = new ConcurrentLinkedDeque<>();

  class Logger implements Logging {
    final Frame frame;


    Logger(final Frame.Depth depth) {
//      final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
//      for (int i = 0; i< 6; i++) {
//        System.out.printf("%d %s %s\n", i, stack[i].getClassName(), stack[i].getMethodName());
//      }
      this.frame = new Frame(depth);
    }

    @Override
    public void log(final LogLevel level, final String format, final Object... params) {

      final Appendable appendable = new StringBuilder();

      try (final Formatter formatter = new Formatter(appendable)) {

        formatter.format("%s %5s [%s] %s %s (%d) %s",
            tomcat_formatter.format(Instant.now()),
            level,
            Thread.currentThread().getName(),
            this.frame.getClassName(),
            this.frame.getMethodName(),
            this.frame.getLineNumber(),
            MessageFormat.format(format, params));

        formatter.flush();

        final String outputMessage = appendable.toString();

        // let tomcat redirection log this
        System.out.println(outputMessage);

        messages.offer(outputMessage);
      }
    }

    @Override
    public void exception(final Throwable t) {

      final StackTraceElement frame = t.getStackTrace()[0];

      log(LogLevel.ERROR, "exception {0} {1} {2} {3} {4}",
          t.getClass().getName(),
          t.getLocalizedMessage(),
          frame.getClassName(),
          frame.getMethodName(),
          frame.getLineNumber());
    }
  }

  public Logging logger() {
    return new Logger(Frame.Depth.TWO);
  }
}

