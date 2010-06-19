package org.tomfolga.dbutils.schemadiff;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


public class Column {

	private final String name;


	public Column(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public static String columnsToString(List<Column> columns) {
		StringBuilder sb = new StringBuilder();
		for (int i =0 ;i< columns.size(); i++) {
			if (i>0) {
				sb.append(", ");
			}
			sb.append(columns.get(i).getName());
		}
		return sb.toString();
	}
	
}
