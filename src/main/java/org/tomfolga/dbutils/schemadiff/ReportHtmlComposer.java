package org.tomfolga.dbutils.schemadiff;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.dao.DataAccessException;

public class ReportHtmlComposer implements IReportComposer {

	private final PrintWriter pw;

	public ReportHtmlComposer(PrintWriter pw) {
		this.pw = pw;
	}

	@Override
	public void composerReportHeader(IDataSource dataSource1, IDataSource dataSource2) {
		pw.print("<table class=\"Design6\">");
		pw.append("<tr>\n");
		pw.append("<td>\n");
		pw.append("Difference between");
		pw.append("</td>\n");
		pw.append("<td>\n");
		pw.append(dataSource1.getName());
		pw.append("</td>\n");
		pw.append("<td>\n");
		pw.append(dataSource2.getName());
		pw.append("</td>\n");
		pw.append("</tr>\n");
		pw.append("<tr>\n");
		pw.append("<td>\n");
		pw.append("Url");
		pw.append("</td>\n");
		pw.append("<td>\n");
		pw.append(dataSource1.getConnectionString());
		pw.append("</td>\n");
		pw.append("<td>\n");
		pw.append(dataSource2.getConnectionString());
		pw.append("</td>\n");
		pw.append("</tr>\n");
		pw.append("<tr>\n");
		pw.append("<td>\n");
		pw.append("Properties");
		pw.append("</td>\n");
		pw.append("<td>\n");
		pw.append(dataSource1.getVariableSubstitutionProperties().toString());
		pw.append("</td>\n");
		pw.append("<td>\n");
		pw.append(dataSource2.getVariableSubstitutionProperties().toString());
		pw.append("</td>\n");
		pw.append("</tr>\n");
		pw.append("</table>\n");
	}

	@Override
	public void composerReportFooter() {
	}

	@Override
	public void composerReport(Report report, TableDiff tableDiff) {
		pw.print(report.getName());
		pw.append("<table cellspacing=\"0\" class=\"Design6\">\n");	
		printRowDIffHeader(report,tableDiff);
		pw.append("\n</thead>\n");
		pw.append("<tbody>");
		for (int i = 0; i < tableDiff.getRowDIff().size(); i++) {
			printRowDIff(i, tableDiff.getRowDIff().get(i));
		}
		pw.append("</tbody>");
		pw.append("</table>");
	}

	private void printRowDIffHeader(Report report, TableDiff tableDiff) {
		pw.append("<tr>");
		pw.append("<th>");
		pw.append("</th>");
		pw.append("<th>" + report.getDb1().getName() + "</th>");
		pw.append("<th>" + report.getDb2().getName() + "</th>");
		pw.append("</tr>");
		pw.print("<tr><td>Objects found</td><td>"+tableDiff.getObjectCount1()+"</td><td>"+tableDiff.getObjectCount2()+"</td></tr>");
		//pw.print("<tr><th>Sql</th><th>"+tableDiff.getSql1()+"</th><th>"+tableDiff.getSql2()+"</th></tr>");
		pw.append("<tr>");	
		pw.append("<th>");
		pw.append(Column.columnsToString(report.getKeyColumns()));
		pw.append("</th>");
		pw.append("<th colspan=2>");
		pw.append(Column.columnsToString(report.getNonKeyColumns()));
		pw.append("</th>");
		pw.append("</tr>");
		pw.append("\n");

	}

	public void printRowDIff(int rowNum, RowDiff rowDiff) {
		if (rowNum % 2 == 0) {
			pw.append("<tr>");
		} else {
			pw.append("<tr class=\"Odd\">");
		}
		pw.append("<td>");
		printList(Arrays.asList(rowDiff.getKey().getMultiKey().getKeys()), pw);
		pw.append("</td>");
		pw.append("<td>");
		if (rowDiff.getDiffType().equals(RowDiff.DiffType.ONLY_FIRST)
				|| rowDiff.getDiffType().equals(RowDiff.DiffType.DIFF)) {
			printList(new ArrayList<Object>((rowDiff.getRowData1().getValues())), pw);
		} else {
			pw.print("MISSING");
		}
		pw.append("</td>");
		pw.append("<td>");
		if (rowDiff.getDiffType().equals(RowDiff.DiffType.ONLY_SECOND)
				|| rowDiff.getDiffType().equals(RowDiff.DiffType.DIFF)) {
			printList(new ArrayList<Object>((rowDiff.getRowData2().getValues())), pw);
		} else {
			pw.print("MISSING");
		}
		pw.append("</td>");
		pw.append("</tr>");
		pw.append("\n");

	}

	public <T> void printList(Collection<T> list, PrintWriter pw) {
		if (list != null) {
			Iterator<T> iter = list.iterator();
			int i = 0;
			while (iter.hasNext()) {
				if (i > 0) {
					pw.append(", ");
				}
				Object obj = iter.next();
				if (obj == null) {
					pw.append("NULL");
				} else {
					pw.append(obj.toString());
				}
				i++;
			}
		}
	}

	@Override
	public void composerErrorReport(Report report, DataAccessException e) {
		pw.print(report.getName());
		pw.append("<table cellspacing=\"0\" class=\"Design6\">\n");
		pw.append("<thead class=\"Corner\">\n");
		pw.append("<tr>");
		pw.append("<th>");
		pw.append("Error accessing database.");
		pw.append("</th>");
		pw.append("</tr>");
		pw.append("\n</thead>\n");
		pw.append("<tbody>");
		pw.append("<tr>");
		pw.append("<td>");
		pw.print(e.getMessage());
		pw.append("</td>");
		pw.append("</tr>");
		pw.append("</tbody>");
		pw.append("</table>");

	}

}
