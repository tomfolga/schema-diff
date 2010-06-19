package org.tomfolga.dbutils.schemadiff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class ReportGroup {

	private final String name;

	private final Map<String, Report> reports;

	public ReportGroup(String name, Properties reportGroupConfig,IDataSource db1, IDataSource db2) {
		this.name = name;
		reports = new HashMap<String, Report>();
		for (Entry<Object, Object> entry : reportGroupConfig.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			String[] keyComps = key.split("\\.");
			String reportName = keyComps[0];
			Report report = reports.get(reportName);
			if (report == null) {
				report = new Report(db1, db2);
				reports.put(reportName, report);
				
			}			
			if (keyComps[1].equals("name")) {
				report.setName(value);
			}
			if (keyComps[1].equals("key_columns")) {
				report.setKeyColumns(parseColumnNames(value));
			}
			if (keyComps[1].equals("non_key_columns")) {
				report.setNonKeyColumns(parseColumnNames(value));
			}
			if (keyComps[1].equals("table_where_clause")) {
				report.setTableAndWhereClause(value);
			}
		}

	}

	public Map<String, Report> getReports() {
		return reports;
	}

	public String getName() {
		return name;
	}

	private List<Column> parseColumnNames(String commaSeparateColumnNames) {
		List<String> columnNames = Arrays.asList(commaSeparateColumnNames.toUpperCase().trim().split(","));
		List<Column> columns = new LinkedList<Column>();
		for (String columnName : columnNames) {
			columns.add(new Column(columnName));
		}
		return columns;
	}

}
