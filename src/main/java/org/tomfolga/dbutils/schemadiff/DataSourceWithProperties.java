package org.tomfolga.dbutils.schemadiff;

import java.util.Properties;

import javax.sql.DataSource;

public class DataSourceWithProperties implements IDataSource {

	private final DataSource dataSource;
	
	private Properties variableSubstitutionProperties;
	private final String name;

	private final String connectionString;
	
	public DataSourceWithProperties(DataSource dataSource, String name, String connectionString) {
		super();
		this.dataSource = dataSource;
		this.name = name;
		this.connectionString = connectionString;
		variableSubstitutionProperties = new Properties();
	}


	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public String  getName() {
		return name;
	}

	@Override
	public Properties getVariableSubstitutionProperties() {
		return variableSubstitutionProperties;
	}


	public String getConnectionString() {
		return connectionString;
	}


}
