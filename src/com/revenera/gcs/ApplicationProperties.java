package com.revenera.gcs;

import com.revenera.gcs.logging.LoggingFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ApplicationProperties {
  private static final LoggingFactory logger = LoggingFactory.create(ApplicationProperties.class);

  enum Items {
    TIMESTAMP("build.timestamp"),
    VERSION_MAJOR("build.version.major"),
    VERSION_MINOR("build.version.minor"),
    BUILD_NUMBER("build.number"),
    DATE("build.date"),
    TIME("build.time"),

    USER("build.username"),
    RELEASE("build.category"),

    LOGGING_THRESHOLD("app.logging.level"),
    LOGGING_ECHO("app.logging.echo"),
    HOUSEKEEPING_INTERVAL("app.housekeeping");

    final String name;
    Items(String name) {
      this.name = name;
    }
  }

  private final Map<Items,Object> properties = new LinkedHashMap<>();

  public ApplicationProperties() {
    logger.me(this);

    try {
      final Properties props = new Properties();
      props.load(Beans.class.getResourceAsStream("/revenera.properties"));

      Arrays.stream(Items.values()).forEach(item -> properties.put(item, props.getProperty(item.name)));
    }
    catch (final IOException ignored) {
      //TODO: just ignore this nothing can be done
    }
  }

  public String getTimestamp() {
    return this.properties.get(Items.TIMESTAMP).toString();
  }

  public String getVersion() {
    return Stream.of(this.properties.get(Items.VERSION_MAJOR),
        this.properties.get(Items.VERSION_MINOR),
        this.properties.get(Items.BUILD_NUMBER),
        this.properties.get(Items.RELEASE))
        .map(Object::toString)
        .collect(Collectors.joining("."));
  }

  public String getDate() {
    return this.properties.get(Items.DATE).toString();
  }

  public String getTime() {
    return this.properties.get(Items.TIME).toString();
  }



  public int getHousekeepingFrequency() {
    return Integer.parseInt(this.properties.get(Items.HOUSEKEEPING_INTERVAL).toString());
  }

  public String getLoggingLevel() {
    return this.properties.get(Items.LOGGING_THRESHOLD).toString();
  }

  @SuppressWarnings("unused")
  public boolean getLoggingEcho() {
    return Arrays.asList("true","on")
        .contains(this.properties.get(Items.LOGGING_ECHO).toString().toLowerCase());
  }

  public String getUser() {
    return this.properties.get(Items.USER).toString();
  }

  public String getRelease() {
    return this.properties.get(Items.RELEASE).toString();
  }

  public String getVersionDetails() {

    return Stream.of(this.properties.get(Items.VERSION_MAJOR),
            this.properties.get(Items.VERSION_MINOR),
            this.properties.get(Items.BUILD_NUMBER),
            this.properties.get(Items.RELEASE),
            this.properties.get(Items.DATE),
            this.properties.get(Items.TIME),
            this.properties.get(Items.USER))
        .map(Object::toString)
        .collect(Collectors.joining(" | "));
  }
}