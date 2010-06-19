package org.tomfolga.dbutils.schemadiff;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Report {

	
	private  String name;
	private  List<Column> keyColumns;
	private  List<Column> nonKeyColumns;	
	private  String tableAndWhereClause;
	private final  IDataSource db1;
	private final  IDataSource db2;

	public Report(IDataSource db1,IDataSource db2) {
		this.db1 = db1;
		this.db2 = db2;
	}	

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public void setTableAndWhereClause(String tableAndWhereClause) {
		this.tableAndWhereClause = tableAndWhereClause;
	}

	public String getTableAndWhereClause() {
		return tableAndWhereClause;
	}

	public void setNonKeyColumns(List<Column> nonKeyColumns) {
		this.nonKeyColumns = nonKeyColumns;
	}

	public List<Column> getNonKeyColumns() {
		return nonKeyColumns;
	}

	public void setKeyColumns(List<Column> keyColumns) {
		this.keyColumns = keyColumns;
	}

	public List<Column> getKeyColumns() {
		return keyColumns;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public IDataSource getDb1() {
		return db1;
	}

	public IDataSource getDb2() {
		return db2;
	}

	


}
