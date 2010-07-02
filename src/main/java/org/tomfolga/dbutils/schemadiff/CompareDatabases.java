package org.tomfolga.dbutils.schemadiff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;

public class CompareDatabases {

	private static final String REPORTS_PROPERTIES = "reports.properties";
	private static final String DBS_PROPERTIES = "dbs.properties";
	private static final String SERVLET_URL_PART = "/compare";
	private static final String SERVLETS_BASE_URLPART = "/servlets";

	private static Log LOG = LogFactory.getLog(CompareDatabases.class);

	public static class CommandLine {
        @Option(name = "-p", usage = "http port to use.", required = true)
        private int httpPort;

        @Option(name = "-d", usage = "path to folder containing "+DBS_PROPERTIES+". Defaults to config.", required = true)
        private String dbConfigPath;
      
        @Option(name = "-r", usage = "path to folder containing "+REPORTS_PROPERTIES+". Defaults to reports.", required = true)
        private String reportConfigPath = "reports";
    }
	
	
	public static void main(String[] args) throws Exception {
		final CommandLine commandLine = new CommandLine();
        final CmdLineParser parser = new CmdLineParser(commandLine);
        final PrintWriter pw = new PrintWriter(System.out);
        try {
            parser.parseArgument(args);            
    		Map<String, Properties> reportConfigs = new TreeMap<String, Properties>();
    		Map<String, IDataSource> dbs = new TreeMap<String, IDataSource>();	
    		readProperties(commandLine.dbConfigPath, commandLine.reportConfigPath, reportConfigs, dbs);
    		ReportGenerator reportGenerator = new ReportGenerator(dbs, reportConfigs);
    		CompareDatabases cd = new CompareDatabases();
    		cd.initWebServer(reportGenerator, commandLine.httpPort);
        } catch (final CmdLineException e) {        	
        	pw.println(e.getMessage());        	
            parser.printUsage(pw,null);
            pw.println(parser.printExample(ExampleMode.ALL));
            System.exit(1);
        } catch (final Throwable e) {
        	LOG.error(e.getMessage(), e);
            System.exit(1);        
        }

	}

	private static void readProperties(String dbConfigPath, String reportConfigPath, Map<String, Properties> reportConfigs, Map<String, IDataSource> dbs)
			throws IOException {
		Properties dbProperties = loadPropertyFile(dbConfigPath,DBS_PROPERTIES);

		Properties reportProperties = loadPropertyFile(dbConfigPath,REPORTS_PROPERTIES);

		for (Entry<Object, Object> entry : reportProperties.entrySet()) {
			Properties reportConfig = loadPropertyFile(reportConfigPath,  entry.getValue() + ".properties");			
			reportConfigs.put((String) entry.getValue(), reportConfig);
		}

		
		for (Entry<Object, Object> entry : dbProperties.entrySet()) {
			String key = (String) entry.getKey();
			String dbName = key.split("\\.")[0];
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setUrl(dbProperties.getProperty(dbName + ".url"));
			dataSource.setUsername(dbProperties.getProperty(dbName + ".username"));
			dataSource.setPassword(dbProperties.getProperty(dbName + ".password"));
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

	private static Properties loadPropertyFile(String dbConfigPath,String fileName) throws IOException, FileNotFoundException {
		Properties dbProperties = new Properties();
		String fileNameWithPath = dbConfigPath+File.separator + fileName;
		LOG.info("loading " + fileNameWithPath);
		dbProperties.load(new FileReader(fileNameWithPath));
		return dbProperties;
	}

	public void initWebServer(ReportGenerator reportGenerator, int httpPort) throws Exception {
		final String WEBAPPDIR = "webapp";

		final Server server = new Server(httpPort);
		final String CONTEXTPATH = "/webapp";

		// for localhost:port/admin/index.html and whatever else is in the
		// webapp directory
		final URL warUrl = this.getClass().getClassLoader().getResource(WEBAPPDIR);
		final String warUrlString = warUrl.toExternalForm();
		server.setHandler(new WebAppContext(warUrlString, CONTEXTPATH));

		// for localhost:port/servlets/cust, etc.
		final Context context = new Context(server, SERVLETS_BASE_URLPART, Context.SESSIONS);
		context.addServlet(new ServletHolder(new CompareDatabasesServlet(reportGenerator)), SERVLET_URL_PART);
		server.start();
		LOG.info("Servlet started.");
		LOG.info("http://localhost:"+httpPort+SERVLETS_BASE_URLPART+SERVLET_URL_PART);
	}

}
