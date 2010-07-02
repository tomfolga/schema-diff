package org.tomfolga.dbutils.schemadiff;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

public class ReportGenerator implements IReportGenerator {

	
	private final Map<String,IDataSource> dbs;


	public ReportGenerator( Map<String,IDataSource> dbs, Map<String, Properties> reportConfigs  ) {
		this.dbs = dbs;
		this.reportConfigs = reportConfigs;
	}
	
	private final Map<String, Properties> reportConfigs;

	
	
	@Override
	public void produceReport(String reportGroupName, IReportComposer reportComposer, String dbName1, String dbName2) throws IOException {
		Properties reportGroupConfig = getReportConfigs().get(reportGroupName);
		ReportGroup reportGroup = new ReportGroup(reportGroupName, reportGroupConfig, dbs.get(dbName1), dbs.get(dbName2));
		PrintWriter pw = new PrintWriter(System.out);
		reportComposer.composerReportHeader(dbs.get(dbName1),dbs.get(dbName2));
		for (Report report : reportGroup.getReports().values()) {
			try {
				TableDiffProcessor tdp = new TableDiffProcessor(report, new TableDao(dbs.get(dbName1)),
					new TableDao(dbs.get(dbName2)));
			tdp.loadTables();
			TableDiff tableDiff = tdp.diff();			
			reportComposer.composerReport(report, tableDiff);
			} catch (CannotGetJdbcConnectionException e ){
				reportComposer.composerErrorReport(report, e);
				break;
			} catch (DataAccessException e) {
				reportComposer.composerErrorReport(report, e);
			}
		}
		pw.flush();
	}


	public Map<String,IDataSource> getDbs() {
		return dbs;
	}


	public Map<String, Properties> getReportConfigs() {
		return reportConfigs;
	}

}
