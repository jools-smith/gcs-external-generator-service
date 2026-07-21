package com.revenera.gcs.implementor;

import com.revenera.gcs.Beans;

import java.nio.file.Path;
import java.time.Instant;

public class GeneratorResources {
  final String technology;
  final Instant timestamp;

  public GeneratorResources(final String technology) {
    this.technology = technology;
    this.timestamp = Instant.now();
  }

  public String getFilename() {
    return String.format("%s.%08X.license", technology, this.timestamp.toEpochMilli());
  }

  public Path getLicenseFilepath() {
    return Beans.getResourcePath("licenses", getFilename());
  }

  public Path getWorkingDirectory() {
    return Beans.getResourcePath("executable");
  }

  public Path getExecutablePath() {
    return Beans.getResourcePath("executable", "Test.exe");
  }

  public Path getExecutablePath(final String executable) {
    return Beans.getResourcePath("executable", executable);
  }

  public Path makeActualLicensePath(final String extension) {

    final String filename = String.format("%s.%08X.%s", technology, Instant.now().toEpochMilli(), extension);

    return Beans.getResourcePath("licenses", filename);
  }
}
