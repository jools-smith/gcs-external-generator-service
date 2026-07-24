package com.revenera.gcs.implementor.fnps;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicGeneratorException;
import com.revenera.gcs.implementor.GeneratorBase;
import com.revenera.gcs.implementor.GeneratorImplementor;
import com.revenera.gcs.implementor.GeneratorResources;
import com.revenera.gcs.utils.Serializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@GeneratorImplementor(technologyId = "FNPS", technologyName = "FNP Subscription License Technology", isDefault = false)
public class FNPS_LicenseGenerator extends GeneratorBase {

  static class FeatureDefinition {
    public String name;
    public String version;
    public long count;
  }

  @Override
  public GeneratorResponse generateLicense(final GeneratorRequest request) throws LicGeneratorException {
    logger.in();
    try {
      final GeneratorResources res = new GeneratorResources(technologyId());

      final Path executablePath = res.getExecutablePath("hbk-signer.exe");
      logger.debug().log("executable path %s", executablePath.toString());

      final Path workingDirectory = res.getWorkingDirectory();
      logger.debug().log("working directory %s", workingDirectory.toString());

      final Path inputLicenseFilePath = res.getLicenseFilepath();
      logger.debug().log("input license file %s", inputLicenseFilePath.toString());

      final Path outputLicenseFilePath = Paths.get(inputLicenseFilePath.toAbsolutePath().toString() + ".json");
      logger.debug().log("output license file %s", outputLicenseFilePath.toString());

      final Object payload = new HashMap<String,Object>() {
        {
          put("timestamp", Instant.now().toEpochMilli());
          put("activationID", request.getActivationID());
          put("entitlementID", request.getEntitlementID());
          put("fulfilementID", request.getFulfilementID());
          put("fulfillCount", request.getFulfillCount());
          put("licenseTechnology", request.getLicenseTechnology().getName());
          put("products", request.getEntitledProducts().stream().map(x -> new HashMap<String,Object>() {{
            put("name", x.getName());
            put("featutes", x.getFeatures().stream().map(y -> new FeatureDefinition() {
              {
                this.name = y.getName();
                this.version = y.getVersion();
                this.count = y.getCount();
              }
            }).collect(Collectors.toList()));
          }}).collect(Collectors.toList()));
        }
      };

      final String json = Serializer.safeSerializeJson(payload);
      logger.debug().log("payload %s", json);

      Files.write(inputLicenseFilePath.toAbsolutePath(), json.getBytes());

      final ProcessBuilder pb = new ProcessBuilder(
          executablePath.toAbsolutePath().toString(),
          inputLicenseFilePath.toAbsolutePath().toString(),
          outputLicenseFilePath.toAbsolutePath().toString()
      );

      pb.directory(workingDirectory.toFile());

      final Process proc = pb.start();
      logger.debug().log("started process");

      final boolean status = proc.waitFor(30, TimeUnit.SECONDS);
      logger.debug().log("finished process");

      try (final BufferedReader reader =
               new BufferedReader(
                   new InputStreamReader(
                       Files.newInputStream(outputLicenseFilePath, StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE)))) {

        return new GeneratorResponse() {
          {
            this.licenseFiles = request.getLicenseTechnology().getLicenseFileDefinitions()
                .stream()
                .filter(x -> x.getLicenseStorageType() == LicenseFileTypeENC.TEXT)
                .map(x -> (new LicenseFileMapItem() {
                  {
                    name = x.getName();
                    value = reader.lines().collect(Collectors.joining("\n"));
                  }
                })).collect(Collectors.toList());

            this.complete = true;
          }
        };
      } // file is deleted here
    }
    catch (final Throwable t) {
      logger.exception(t);
      throw new RuntimeException(t);
    }
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet request) throws LicGeneratorException {
    logger.in();
    try {
      return new ConsolidatedLicense() {
        {
          this.fulfillments = request.getFulfillments();

          this.licFiles = request.getFulfillments().stream()
              .filter(x -> x.getLicenseFileType() == LicenseFileTypeENC.TEXT)
              .flatMap(fid -> fid.getLicenseFiles().stream())
              .collect(Collectors.groupingBy(LicenseFileMapItem::getName))
              .entrySet().stream()
              .map(x ->new LicenseFileMapItem() {
                {
                  this.name = x.getKey();
                  this.value = x.getValue().stream()
                      .map(x -> x.getValue().toString())
                      .collect(Collectors.joining("\n"));
                }
              }).collect(Collectors.toList());

          // debug
          //this.licFiles.forEach(file -> logger.array(Log.Level.debug, file.getName(), file.getValue()));
        }
      };
    }
    catch (final Throwable t) {
      logger.exception(t);
      throw new RuntimeException(t);
    }
  }
}
