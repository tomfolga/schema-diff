package org.tomfolga.dbutils.schemadiff;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class TableDiff {

	private final List<RowDiff> rowDIff;
	private final TableData tableData1;
	private final TableData tableData2;

	public TableDiff(List<RowDiff> rowDIff, TableData tableData1, TableData tableData2) {
		super();
		this.rowDIff = rowDIff;
		this.tableData1 = tableData1;
		this.tableData2 = tableData2;
	}

	public int getObjectCount1() {
		return tableData1.getKeys().size();
	}

	public int getObjectCount2() {
		return tableData2.getKeys().size();
	}

	public List<RowDiff> getRowDIff() {
		return rowDIff;
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

	public String getSql1() {
		return tableData1.getQuery();
	}

	public String getSql2() {
		return tableData2.getQuery();
	}

}
