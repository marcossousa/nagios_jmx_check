package org.nagios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.nagios.format.FormatAdapter;
import org.nagios.properties.parsers.LongParser;
import org.nagios.properties.parsers.StringParser;
import org.nagios.properties.parsers.TypeParser;

/**
 * 
 * JMXQuery is used for local or remote request of JMX attributes It requires
 * JRE 1.5 to be used for compilation and execution. Look method main for
 * description how it can be invoked.
 * 
 */
public class JMXQuery {

  private String url;
  private int verbatim;
  private JMXConnector connector;
  private MBeanServerConnection connection;
  private Serializable warning, critical, expected;
  private String attribute, infoAttribute;
  private String attribute_key, infoKey;
  private String object;

  private FormatAdapter checkData;
  private FormatAdapter infoData;
  @SuppressWarnings("rawtypes")
  private TypeParser parser;

  private void connect() throws IOException {
    JMXServiceURL jmxUrl = new JMXServiceURL(url);
    connector = JMXConnectorFactory.connect(jmxUrl);
    connection = connector.getMBeanServerConnection();
  }

  private void disconnect() throws IOException {
    if (connector != null) {
      connector.close();
      connector = null;
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {

    JMXQuery query = new JMXQuery();

    try {
      query.parse(args);
      query.connect();
      query.execute();
      int status = query.report(System.out);
      System.exit(status);
    } catch (Exception ex) {
      int status = query.report(ex, System.out);
      System.exit(status);
    } finally {
      try {
        query.disconnect();
      } catch (IOException e) {
        int status = query.report(e, System.out);
        System.exit(status);
      }
    }
  }

  private int report(Exception ex, PrintStream out) {
    if (ex instanceof ParseError) {
      out.print(NagiosStatus.UNKNOWN + " ");
      reportException(ex, out);
      out.println(" Usage: check_jmx -help ");
      return NagiosStatus.UNKNOWN.ordinal();
    } else {
      out.print(NagiosStatus.CRITICAL + " ");
      reportException(ex, out);
      out.println();
      return NagiosStatus.CRITICAL.ordinal();
    }
  }

  private void reportException(Exception ex, PrintStream out) {

    if (verbatim < 2)
      out.print(rootCause(ex).getMessage());
    else {
      out.print(ex.getMessage() + " connecting to " + object + " by URL " + url);
    }

    if (verbatim >= 3)
      ex.printStackTrace(out);

  }

  private static Throwable rootCause(Throwable ex) {
    if (ex.getCause() == null)
      return ex;
    return rootCause(ex.getCause());
  }

  private int report(PrintStream out) {
    @SuppressWarnings("unchecked")
    NagiosStatus status = parser.check(checkData, warning, critical, expected);
    out.print(status.toString());
    out.print(" ");

    if (infoData == null || verbatim >= 2) {
      if (attribute_key != null)
        out.print(attribute + '.' + attribute_key + '=' + checkData.getValue());
      else
        out.print(attribute + '=' + checkData);
    }

    if (infoData != null) {
      out.print(infoData.toString());
    }

    out.println();
    return status.ordinal();
  }

  private void execute() throws Exception {
    Object attr = connection.getAttribute(new ObjectName(object), attribute);
    if (attr instanceof CompositeDataSupport && attribute_key == null) {
      throw new ParseError("Attribute key is null for composed data " + object);
    } else {
      checkData = FormatAdapter.getInstance(attr, attribute_key);
    }

    if (infoAttribute != null) {
      Object info_attr = infoAttribute.equals(attribute) ? attr : connection.getAttribute(new ObjectName(object),
          infoAttribute);
      infoData = FormatAdapter.getInstance(info_attr, infoKey);
    }

  }

  private void parse(String[] args) throws ParseError {
    try {
      parser = new LongParser();

      for (int i = 0; i < args.length; i++) {
        String option = args[i];
        if (option.equals("-help")) {
          printHelp(System.out);
          System.exit(NagiosStatus.UNKNOWN.ordinal());
        } else if (option.equals("-U")) {
          this.url = args[++i];
        } else if (option.equals("-O")) {
          this.object = args[++i];
        } else if (option.equals("-A")) {
          this.attribute = args[++i];
        } else if (option.equals("-I")) {
          this.infoAttribute = args[++i];
        } else if (option.equals("-J")) {
          this.infoKey = args[++i];
        } else if (option.equals("-K")) {
          this.attribute_key = args[++i];
        } else if (option.startsWith("-v")) {
          this.verbatim = option.length() - 1;
        } else if (option.equals("-w")) {
          this.warning = args[++i];
        } else if (option.equals("-c")) {
          this.critical = args[++i];
        } else if (option.equals("-e")) {
          this.expected = args[++i];
        } else if (option.equals("-T")) {
          this.parser = initParser(args[++i]);
        }
      }

      if (warning != null) {
        this.warning = parser.parse(warning);
      }
      if (critical != null) {
        this.critical = parser.parse(critical);
      }
      if (expected != null) {
        this.expected = parser.parse(expected);
      }

      if (url == null || object == null || attribute == null)
        throw new Exception("Required options not specified");

    } catch (Exception e) {
      throw new ParseError(e);
    }

  }

  @SuppressWarnings("rawtypes")
  private TypeParser initParser(String string) {
    if ("S".equals(string)) {
      return new StringParser();
    } else {
      return new LongParser();
    }
  }

  private void printHelp(PrintStream out) {
    InputStream is = getClass().getClassLoader().getResourceAsStream("org/nagios/Help.txt");
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    try {
      while (true) {
        String s = reader.readLine();
        if (s == null)
          break;
        out.println(s);
      }
    } catch (IOException e) {
      out.println(e);
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        out.println(e);
      }
    }
  }
}