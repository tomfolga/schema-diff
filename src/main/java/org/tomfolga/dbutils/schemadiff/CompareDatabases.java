package org.tomfolga.dbutils.schemadiff;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class CompareDatabases {

	private static final String CONFIG = "/config/";
	private static Log LOG = LogFactory.getLog(CompareDatabases.class);

	
	
	public static void main(String[] args) throws Exception {
		
		Map<String, Properties> reportConfigs = new TreeMap<String, Properties>();
		Map<String, IDataSource> dbs = new TreeMap<String, IDataSource>();	
		readProperties(reportConfigs, dbs);
		ReportGenerator reportGenerator = new ReportGenerator(dbs, reportConfigs);
		CompareDatabases cd = new CompareDatabases();
		cd.initWebServer(reportGenerator);
	}

	private static void readProperties(Map<String, Properties> reportConfigs, Map<String, IDataSource> dbs)
			throws IOException {
		Properties dbProperties = new Properties();
		dbProperties.load(CompareDatabases.class.getResourceAsStream("/dbs.properties"));
		Properties reportProperties = new Properties();
		reportProperties.load(CompareDatabases.class.getResourceAsStream("/reports.properties"));

		for (Entry<Object, Object> entry : reportProperties.entrySet()) {
			Properties reportConfig = new Properties();
			String fileName = CONFIG + (String) entry.getValue() + ".properties";
			LOG.info("loading " + fileName);
			reportConfig.load(CompareDatabases.class.getResourceAsStream(fileName));
			reportConfigs.put((String) entry.getValue(), reportConfig);
		}

		
		for (Entry<Object, Object> entry : dbProperties.entrySet()) {
			String key = (String) entry.getKey();
			String dbName = key.split("\\.")[0];
			DataSource dataSource = new SingleConnectionDataSource("oracle.jdbc.driver.OracleDriver", dbProperties
					.getProperty(dbName + ".url"), dbProperties.getProperty(dbName + ".username"), dbProperties
					.getProperty(dbName + ".password"), false);
			dbs.put(dbName, new DataSourceWithProperties(dataSource, dbName,dbProperties
					.getProperty(dbName + ".url") ));
		}
		
		
		for (Entry<Object, Object> entry : dbProperties.entrySet()) {
			String key = (String) entry.getKey();
			if (key.contains(".variable.")) {
				String dbName = key.split("\\.")[0];
				String variableName = key.split("\\.")[2];
				String variableValue = (String) entry.getValue();
				IDataSource dataSource = dbs.get(dbName);
				dataSource.getVariableSubstitutionProperties().setProperty(variableName, variableValue);
			}			
		}
	}

	public void initWebServer(ReportGenerator reportGenerator) throws Exception {
		final String WEBAPPDIR = "webapp";

		final Server server = new Server(8080);
		final String CONTEXTPATH = "/webapp";

		// for localhost:port/admin/index.html and whatever else is in the
		// webapp directory
		final URL warUrl = this.getClass().getClassLoader().getResource(WEBAPPDIR);
		final String warUrlString = warUrl.toExternalForm();
		server.setHandler(new WebAppContext(warUrlString, CONTEXTPATH));

		// for localhost:port/servlets/cust, etc.
		final Context context = new Context(server, "/servlets", Context.SESSIONS);
		context.addServlet(new ServletHolder(new CompareDatabasesServlet(reportGenerator)), "/compare");
		server.start();

	}

}
