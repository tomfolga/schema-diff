package org.tomfolga.dbutils.schemadiff;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CompareDatabasesServlet extends HttpServlet {
	private static final String DB2 = "db2";
	private static final String DB1 = "db1";
	private static final String REPORT_GROUP = "report_group";
	private static final long serialVersionUID = 1L;
	private final ReportGenerator reportGenerator;

	public CompareDatabasesServlet(ReportGenerator reportGenerator) {
		this.reportGenerator = reportGenerator;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter pw = resp.getWriter();
		String db1 = req.getParameter(DB1);
		String db2 = req.getParameter(DB2);
		String reportGroup = req.getParameter(REPORT_GROUP);
		pageHeader(pw);
		reportGenerator.produceReport(reportGroup, new ReportHtmlComposer(pw), db1, db2);
		pageFooter(pw);
		pw.flush();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter pw = resp.getWriter();
		pageHeader(pw);
		pw.append("<form method=\"post\">");
		pw.append("<table>");
		pw.append("<tr>");
		pw.append("<td>Database 1:</td><td>");
		printChoiceDropDown(pw,DB1,reportGenerator.getDbs().keySet());
		pw.append("</td></tr>");
		pw.append("<td>Database 2:</td><td>");
		printChoiceDropDown(pw,DB2,reportGenerator.getDbs().keySet());
		pw.append("</td></tr>");
		pw.append("<td>Report Group:</td><td>");
		printChoiceDropDown(pw,REPORT_GROUP,reportGenerator.getReportConfigs().keySet());		
		pw.append("</td></tr>");
		pw.append("<td>");
		printSubmitButton(pw,"Compare");
		pw.append("</td></tr>");
		pw.append("</form>");
		pageFooter(pw);
		pw.flush();
	}


	private void printSubmitButton(PrintWriter pw, String string) {
		pw.append("<input type=\"submit\" value=\""+string+"\">");
		
	}

	private void printChoiceDropDown(PrintWriter pw, String name, Set<String> values) {
		pw.append("<select name=\""+name+"\">");
		for (String dbname : values) {
			pw.append("<option value=\""+dbname+"\">"+dbname+"</option>");	
		}
		pw.append("</select>");
	}

	private void pageHeader(PrintWriter pw) {
		pw.append("<html><head><link rel=StyleSheet href=\"/webapp/style.css\" type=\"text/css\"></head><body>");

	}

	private void pageFooter(PrintWriter pw) {
		pw.append("</body></html>");

	}

}
