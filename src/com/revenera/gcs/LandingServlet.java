package com.revenera.gcs;

import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.transaction.ExecutionRecord;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.lang3.SystemUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.stream.Collectors;

@WebServlet("/")
public class LandingServlet extends HttpServlet {
  private static final LoggingFactory logger = LoggingFactory.create(LandingServlet.class);
  private static final String css =
      "body {\n" +
      "    margin: 0;\n" +
      "    font-family: \"Segoe UI\", Arial, sans-serif;\n" +
      "    background-color: #f4f6f8;\n" +
      "    color: #333;\n" +
      "}\n" +
      ".banner {\n" +
      "    background-color: #003366;\n" +
      "    color: white;\n" +
      "    padding: 30px 40px;\n" +
      "}\n" +
      ".banner h1 {\n" +
      "    margin: 0;\n" +
      "    font-size: 32px;\n" +
      "    font-weight: 500;\n" +
      "}\n" +
      ".banner p {\n" +
      "    margin: 8px 0 0 0;\n" +
      "    opacity: 0.9;\n" +
      "}\n" +
      ".container {\n" +
      "    max-width: 900px;\n" +
      "    margin: 40px auto;\n" +
      "    padding: 0 20px;\n" +
      "}\n" +
      ".card {\n" +
      "    background: white;\n" +
      "    border-radius: 8px;\n" +
      "    box-shadow: 0 2px 10px rgba(0,0,0,0.08);\n" +
      "    padding: 30px;\n" +
      "}\n" +
      ".card h2 {\n" +
      "    margin-top: 0;\n" +
      "    color: #003366;\n" +
      "}\n" +
      "table {\n" +
      "    width: 100%;\n" +
      "    border-collapse: collapse;\n" +
      "    margin-top: 20px;\n" +
      "}\n" +
      "th, td {\n" +
      "    padding: 12px;\n" +
      "    text-align: left;\n" +
      "    border-bottom: 1px solid #e0e0e0;\n" +
      "}\n" +
      "th {\n" +
      "    width: 220px;\n" +
      "    color: #555;\n" +
      "    background-color: #fafafa;\n" +
      "}\n" +
      ".status {\n" +
      "    display: inline-block;\n" +
      "    padding: 4px 12px;\n" +
      "    background-color: #dff0d8;\n" +
      "    color: #3c763d;\n" +
      "    border-radius: 12px;\n" +
      "    font-size: 0.9em;\n" +
      "}\n" +
      ".footer {\n" +
      "    text-align: center;\n" +
      "    margin-top: 30px;\n" +
      "    color: #777;\n" +
      "    font-size: 0.85em;\n" +
      "}\n";

  private static final String page = "<!DOCTYPE html>\n" +
      "<html lang=\"en\">\n" +
      "<head>\n" +
      "<meta charset=\"UTF-8\">\n" +
      "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
      "<title>Revenera GCS Remote Licence Generation Service</title>\n" +
      "<style>{0}</style>\n" +
      "</head>\n" +
      "<body>\n" +
      "<div class=\"banner\">\n" +
      "    <h1>Revenera GCS Remote Licence Generation Service</h1>\n" +
      "    <p>FlexNet Operations&reg; Administration and Monitoring</p>\n" +
      "</div>\n" +
      "<div class=\"container\">\n" +
      "    <div class=\"card\">\n" +
      "        <h2>Application Information</h2>\n" +
      "        <table>\n" +
      "            <tr><th>Application</th><td>Revenera Licence Generation Service</td></tr>\n" +
      "            <tr><th>Version</th><td>{1}.{2}</td></tr>\n" +
      "            <tr><th>Release Date</th><td>{3} &nbsp;{4}</td></tr>\n" +
      "            <tr><th>Build Number</th><td>{5}</td></tr>\n" +
      "            <tr><th>Environment</th><td>{6} &nbsp;({7} &nbsp;{8})</td></tr>\n" +
      "            <tr><th>Status</th><td><spanclass=\"status\">{9} &nbsp;{10}</span></td></tr>\n" +
      "            <tr><th>Java Runtime</th><td>{11} &nbsp;{12} &nbsp;({13})</td></tr>\n" +
      "            <tr><th>Technologies</th><td>{14}</td></tr>\n" +
      "            <tr><th>Diagnostics</th>\n"+
      "                <table>\n" +
      "                    <tr><th>Method</th><th>Count</th><th>Sojourn (secs)</th><th>Mean (secs)</th></tr>\n" +
      "                    {15}\n" +
      "                 </table>\n" +
      "            </tr>\n" +
      "        </table>\n" +
      "        <p style=\"margin-top:30px\">\n" +
      "            This application provides licence generation facilities for multiple license technologies.\n" +
      "        </p>\n" +
      "    </div>\n" +
      "    <div class=\"footer\">\n" +
      "        &copy; 2026 Revenera GCS. All rights reserved.\n" +
      "    </div>\n" +
      "</div></body></html>";

  public LandingServlet() {
    super();
    logger.me(this);
  }

  private static String escape(final String text) {
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;");
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

    try (final ExecutionContext ctx = new ExecutionContext()) {

      final ApplicationProperties props = ExecutionContext.getApplicationProperties();

      final String diag = ExecutionContext.getExecutionManager().getRecords().stream()
          .sorted(Comparator.comparing(ExecutionRecord::getUpdated).reversed())
          .map(x ->
              String.format("<tr><td>%s</td><td>%d</td><td>%f</td><td>%f</td></tr>",
                  escape(x.getMethod()),
                  x.getCount(),
                  x.getTotalDuration(),
                  x.getMeanLatency()))
          .collect(Collectors.joining("\n"));

      final String html = MessageFormat.format(page,
          css,

          props.getVersionMajor(), props.getVersionMinor(),

          props.getReleaseDate(), props.getReleaseTime(),

          props.getBuildNumber(),

          SystemUtils.OS_NAME, SystemUtils.OS_VERSION, SystemUtils.OS_ARCH,

          SystemUtils.getHostName(), ExecutionContext.getApplicationDuration().toString(),

          SystemUtils.JAVA_VM_NAME, SystemUtils.JAVA_VERSION, SystemUtils.JAVA_CLASS_VERSION,

          String.join("&nbsp;&nbsp;&nbsp;", ExecutionContext.getImplementorFactory().getImplementors()),

          diag
      );

      resp.getWriter().println(html);

      resp.setStatus(HttpServletResponse.SC_OK);
    }
  }
}
