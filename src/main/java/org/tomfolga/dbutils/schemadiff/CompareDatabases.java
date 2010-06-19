package org.tomfolga.dbutils.schemadiff;

import static org.kohsuke.args4j.ExampleMode.ALL;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class CompareDatabases {

	private static final Log LOG = LogFactory.getLog(CompareDatabases.class);

	private static final String CONFIG_FOLDER_ROOT = "config";

	@Option(name = "-p", usage = "port number if starting web server")
	private int httpPort;
	

	public static void main(String[] args) throws Exception {
		new CompareDatabases().doMain(args);
	}

	public void doMain(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);

        try {
        	parser.parseArgument(args);
    		Map<String, Properties> reportConfigs = new TreeMap<String, Properties>();
    		Map<String, IDataSource> dbs = new TreeMap<String, IDataSource>();
    		readProperties(reportConfigs, dbs);
    		ReportGenerator reportGenerator = new ReportGenerator(dbs,
    				reportConfigs);
    		CompareDatabases cd = new CompareDatabases();
    		cd.initWebServer(reportGenerator);
        } catch( CmdLineException e ) {
            LOG.error(e.getMessage());
            LOG.error("java CompareDatabases [options...]");
            parser.printUsage(System.err);
            System.err.println("  Example: java CompareDatabases -p 8080 "+parser.printExample(ALL));
        }
	}
	
	private static void readProperties(Map<String, Properties> reportConfigs,
			Map<String, IDataSource> dbs) throws IOException {
		Properties dbProperties = new Properties();
		dbProperties.load(CompareDatabases.class.getResourceAsStream("/"
				+ CONFIG_FOLDER_ROOT + "/dbs.properties"));
		Properties reportProperties = new Properties();
		reportProperties.load(CompareDatabases.class.getResourceAsStream("/"
				+ CONFIG_FOLDER_ROOT + "/reports.properties"));

		parseReportsProperties(reportConfigs, reportProperties);

		parseDatabaseProperties(dbs, dbProperties);
	}

	private static void parseDatabaseProperties(Map<String, IDataSource> dbs,
			Properties dbProperties) {
		for (Entry<Object, Object> propertyNameValue : dbProperties.entrySet()) {
			String propertyName = (String) propertyNameValue.getKey();
			String dbName = propertyName.split("\\.")[0];
			DataSource dataSource = new SingleConnectionDataSource(dbProperties
					.getProperty(dbName + ".url"), dbProperties
					.getProperty(dbName + ".username"), dbProperties
					.getProperty(dbName + ".password"), false);
			dbs.put(dbName, new DataSourceWithProperties(dataSource, dbName,
					dbProperties.getProperty(dbName + ".url")));
		}

		for (Entry<Object, Object> propertyNameValue : dbProperties.entrySet()) {
			String key = (String) propertyNameValue.getKey();
			if (key.contains(".variable.")) {
				String dbName = key.split("\\.")[0];
				String variableName = key.split("\\.")[2];
				String variableValue = (String) propertyNameValue.getValue();
				IDataSource dataSource = dbs.get(dbName);
				dataSource.getVariableSubstitutionProperties().setProperty(
						variableName, variableValue);
			}
		}
	}

	private static void parseReportsProperties(
			Map<String, Properties> reportConfigs, Properties reportProperties)
			throws IOException {
		for (Entry<Object, Object> entry : reportProperties.entrySet()) {
			Properties reportConfig = new Properties();
			String fileName = "/reports/" + (String) entry.getValue()
					+ ".properties";
			System.out.println("loading " + fileName);
			reportConfig.load(CompareDatabases.class
					.getResourceAsStream(fileName));
			reportConfigs.put((String) entry.getValue(), reportConfig);
		}
	}

	public void initWebServer(ReportGenerator reportGenerator) throws Exception {
		final String WEBAPPDIR = "webapp";

		final Server server = new Server(httpPort);
		final String CONTEXTPATH = "/webapp";

		// for localhost:port/admin/index.html and whatever else is in the
		// webapp directory
		final URL warUrl = this.getClass().getClassLoader().getResource(
				WEBAPPDIR);
		final String warUrlString = warUrl.toExternalForm();
		server.setHandler(new WebAppContext(warUrlString, CONTEXTPATH));

		final Context context = new Context(server, "/servlets",
				Context.SESSIONS);
		context.addServlet(new ServletHolder(new CompareDatabasesServlet(
				reportGenerator)), "/compare");
		server.start();

	}

}
