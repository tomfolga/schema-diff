package org.tomfolga.dbutils.schemadiff;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RowDiff {

	private final RowKey key;
	
	private final RowData rowData1;
	
	private final RowData rowData2;
		
	
	public RowDiff(RowKey key, RowData row1, RowData row2, DiffType diffType) {
		super();
		this.key = key;
		this.rowData1 = row1;
		this.rowData2 = row2;
		this.diffType = diffType;
	}

	public static enum DiffType {
		ONLY_FIRST,
		ONLY_SECOND,
		DIFF
	}

	private final DiffType diffType;
	
	public DiffType getDiffType() {
		return diffType;
	}

	public RowKey getKey() {
		return key;
	}

	public RowData getRowData1() {
		return rowData1;
	}

	public RowData getRowData2() {
		return rowData2;
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
	
}
