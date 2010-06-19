package org.tomfolga.dbutils.schemadiff;

import java.io.IOException;
import java.sql.SQLException;

public interface IReportGenerator {

	public void produceReport(String reportGroupName, IReportComposer reportComposer, String dbName1, String dbMame2)  
			throws IOException, SQLException;


}
