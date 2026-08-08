package com.revenera.gcs.implementor.hbk;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.implementor.AbstractGenerator;
import com.revenera.gcs.implementor.GeneratorImplementor;
import com.revenera.gcs.implementor.GeneratorResources;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.lang3.NotImplementedException;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@GeneratorImplementor(technologyId = "HBK", technologyName = "HBK/LMX License Technology", isDefault = false)
public class HBK_LicenseAbstractGenerator extends AbstractGenerator implements LicenseGeneratorServiceInterface {

  final Function<XMLGregorianCalendar, String> parse_expiration_date = date ->
      date == null ? "perpetual" : date.toXMLFormat();

  final Function<XMLGregorianCalendar, String> parse_start_date = date ->
      date == null ? Instant.now().toString() : date.toXMLFormat();

  @Override
  public LicenseGeneratorServiceInterface generator() {
    return this;
  }

  @Override
  public PingResponse ping(final PingRequest request) {
    ExecutionContext.injectRequest(request);

    return ExecutionContext.injectResponse(super.doPing());
  }

  @Override
  public GeneratorResponse generateLicense(final GeneratorRequest request) {
    logger.in();
    try {
      ExecutionContext.injectRequest(request);

      final GeneratorResources res = new GeneratorResources(technologyId());

      final Path executablePath = res.getExecutablePath("hbk-signer.exe");
      logger.get().debug("executable path", executablePath.toString());

      final Path workingDirectory = res.getWorkingDirectory();
      logger.get().debug("working directory", workingDirectory.toString());

      final Path inputLicenseFilePath = res.getLicenseFilepath();
      logger.get().debug("input license file", inputLicenseFilePath.toString());

      final Path outputLicenseFilePath = Paths.get(inputLicenseFilePath.toAbsolutePath() + ".json");
      logger.get().debug("output license file", outputLicenseFilePath.toString());

      final Object payload = new LinkedHashMap<String,Object>() {
        {
          putIfAbsent("timestamp", Instant.now().toString());
          putIfAbsent("entitlement-id", request.getEntitlementID());
          putIfAbsent("activation-id", request.getActivationID());
          putIfAbsent("fulfilment-id", request.getFulfilementID());
          putIfAbsent("fulfilment-count", request.getFulfillCount());
          putIfAbsent("start-date", parse_start_date.apply(request.getStartDate()));
          putIfAbsent("expiration-date", parse_expiration_date.apply(request.getExpirationDate()));
          putIfAbsent("license-technology", request.getLicenseTechnology().getName());
          putIfAbsent("sold-to", request.getSoldTo().getName());
          //noinspection CodeBlock2Expr
          Optional.ofNullable(request.getSoldTo()).ifPresent(s -> {
            putIfAbsent("sold-to", s.getName());
          });
          //noinspection CodeBlock2Expr
          Optional.ofNullable(request.getSoldToUsers()).ifPresent(s -> {
            putIfAbsent("sold-to-users", s.stream().map(OrgUnitContact::getDisplayName).collect(Collectors.toList()));
          });
          putIfAbsent("products", request.getEntitledProducts().stream().map(x -> new LinkedHashMap<String,Object>() {{
            putIfAbsent("name", x.getName());
            putIfAbsent("version", x.getVersion());
            putIfAbsent("category", x.getProductCategory().getValue().getName());
            putIfAbsent("features", x.getFeatures().stream().map(y -> new LinkedHashMap<String,Object>() {
              {
                putIfAbsent("name", y.getName());
                putIfAbsent("version", y.getVersion());
                putIfAbsent("count", y.getCount());
              }
            }).collect(Collectors.toList()));
          }}).collect(Collectors.toList()));
        }
      };

      //TODO:debug
      ExecutionContext.injectPayload(payload);

      final String json = Serializer.safeSerializeJson(payload);
      logger.get().debug("payload", json);

      // TODO: create the license file template
      Files.write(inputLicenseFilePath.toAbsolutePath(), json.getBytes());

      final ProcessBuilder pb = new ProcessBuilder(
          executablePath.toAbsolutePath().toString(),
          inputLicenseFilePath.toAbsolutePath().toString(),
          outputLicenseFilePath.toAbsolutePath().toString()
      );
      pb.directory(workingDirectory.toFile());

      // TODO: invoke the file signer
      final Process proc = pb.start();
      logger.get().debug("started process");

      final boolean status = proc.waitFor(30, TimeUnit.SECONDS);
      logger.get().debug("finished process");

      // TODO: ingest the license file and delete off disk once read
      try (final BufferedReader reader =
               new BufferedReader(
                   new InputStreamReader(
                       Files.newInputStream(outputLicenseFilePath, StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE)))) {

        return ExecutionContext.injectResponse(new GeneratorResponse() {
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
        });
      }
    }
    catch (final Throwable t) {
      logger.exception(t);
      throw new RuntimeException(t);
    }
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet request) {
    logger.in();
    try {
      ExecutionContext.injectRequest(request);

      return ExecutionContext.injectResponse(new ConsolidatedLicense() {
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
        }
      });
    }
    catch (final Throwable t) {
      logger.exception(t);
      throw new RuntimeException(t);
    }
  }

  // DELEGATE

  @Override
  public Status validateProduct(final ProductRequest request) {
    ExecutionContext.injectRequest(request);

    return ExecutionContext.injectResponse(super.doValidateProduct(request));
  }

  @Override
  public Status validateLicenseModel(final LicenseModelRequest request) {
    ExecutionContext.injectRequest(request);

    return ExecutionContext.injectResponse(super.doValidateLicenseModel(request));
  }

  @Override
  public LicenseFileDefinitionMap generateLicenseFilenames(final GeneratorRequest request) {
    ExecutionContext.injectRequest(request);

    throw new NotImplementedException("generateLicenseFilenames");
  }

  @Override
  public LicenseFileDefinitionMap generateConsolidatedLicenseFilenames(final ConsolidatedLicenseResquest request) {
    ExecutionContext.injectRequest(request);

    throw new NotImplementedException("generateConsolidatedLicenseFilenames");
  }

  @Override
  public String generateCustomHostIdentifier(final HostIdRequest request) {
    ExecutionContext.injectRequest(request);

    throw new NotImplementedException("generateCustomHostIdentifier");
  }

  @Override
  public void registerConfirmation() {
    logger.get().info("Registering Confirmation {0} {1} {2}", this.getClass().getSimpleName(), this.id, this.name);
  }
}
