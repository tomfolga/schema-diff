package org.tomfolga.dbutils.schemadiff;

import java.util.Properties;

import javax.sql.DataSource;

public class DataSourceWithProperties implements IDataSource {

	private final DataSource dataSource;
	
	private Properties variableSubstitutionProperties;
	private final String name;

	private final String connectionString;

	private final String username;

	private final String password;
	
	public DataSourceWithProperties(DataSource dataSource, String name, String connectionString, String username, String password) {
		super();
		this.dataSource = dataSource;
		this.name = name;
		this.connectionString = connectionString;
		this.username = username;
		this.password = password;
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


	@Override
	public String getUsername() {
		return username;
	}


	@Override
	public String getPassword() {
		return password;
	}


}
