package org.tomfolga.dbutils.schemadiff;

import org.springframework.dao.DataAccessException;


public interface IReportComposer {

	public void composerReport(Report report,TableDiff tableDiff);


	void composerErrorReport(Report report, DataAccessException e);


	void composerReportHeader(IDataSource dataSource1, IDataSource dataSource2);


	void composerReportFooter();
}
