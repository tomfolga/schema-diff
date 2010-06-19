package org.tomfolga.dbutils.schemadiff;

import java.util.Properties;

import javax.sql.DataSource;

public interface IDataSource {

	DataSource getDataSource();
	Properties getVariableSubstitutionProperties();
	String getName();
	String getConnectionString();
}
