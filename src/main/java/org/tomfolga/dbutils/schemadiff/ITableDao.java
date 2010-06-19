package org.tomfolga.dbutils.schemadiff;

import java.util.List;

public interface ITableDao {

	TableData loadTableData(List<Column> keyColumnNames, List<Column> nonKeyColumnNames, String query);
	
	String getDbName();

	IDataSource getDataSource();

}
