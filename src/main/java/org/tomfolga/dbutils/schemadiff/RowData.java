package org.tomfolga.dbutils.schemadiff;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class RowData  {

	final private HashMap<Column, Object> dataMap;
	

	public RowData(Map<Column, Object> data) {
		dataMap = new HashMap<Column, Object>();
		dataMap.putAll(data);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof RowData)) {
			return false;
		}
		RowData otherRowData = (RowData) other;
		if (otherRowData.dataMap.size() != this.dataMap.size())
			return false;
		for (Column columnName : dataMap.keySet()) {
			Object value1 = this.dataMap.get(columnName);
			Object value2 = otherRowData.dataMap.get(columnName);
			if (value1 == null && value2 != null) {
				return false;
			}
			if (value1 != null && value2 == null) {
				return false;
			}
			if (value1 != null && value2 != null && !value1.equals(value2)) {
				return false;
			}
		}
		return true;
	}

	public List<Column> getDiifColumnNames(Object other) {
		List<Column> diffColumnNames = new LinkedList<Column>();
		if (other == null) {
			return diffColumnNames;
		}
		if (!(other instanceof RowData)) {
			return diffColumnNames;
		}
		RowData otherRowData = (RowData) other;
		for (Column columnName : dataMap.keySet()) {
			Object value1 = this.dataMap.get(columnName);
			Object value2 = otherRowData.dataMap.get(columnName);
			if (value1 == null && value2 != null) {
				diffColumnNames.add(columnName);
			}
			if (value1 != null && value2 == null) {
				diffColumnNames.add(columnName);
			}
			if (value1 != null && value2 != null && !value1.equals(value2)) {
				diffColumnNames.add(columnName);
			}
		}
		return diffColumnNames;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Column columnName : dataMap.keySet()) {
			Object value1 = this.dataMap.get(columnName);
			sb.append(columnName).append(": ").append(value1).append(", ");
		}
		return sb.toString();
	}
	
	public Object getValue(Column column) {
		return dataMap.get(column);
	}
		
	public Collection<Object> getValues() {
		return dataMap.values();
	}


}
