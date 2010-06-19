package org.tomfolga.dbutils.schemadiff;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TableData {

	List<Column> keyColumnNames;

	List<Column> allColumnNames;

	List<Column> nonKeyColumnNames;

	String tableName;
	
	String errorMessage;
	
	private Map<RowKey, RowData> dataMap;

	private final String query;

	public TableData(List<Column> keyColumnNames, List<Column> nonKeyColumnNames,
			List<RowData> rowDataList, String query) {
		this.keyColumnNames = keyColumnNames;
		this.nonKeyColumnNames = nonKeyColumnNames;
		this.query = query;
		this.allColumnNames = new LinkedList<Column>();
		allColumnNames.addAll(this.keyColumnNames);
		allColumnNames.addAll(this.nonKeyColumnNames);		
		dataMap = new TreeMap<RowKey, RowData>();		
		for (RowData rowData : rowDataList) {
			RowKey key = getKey(rowData);
			dataMap.put(key, getNonKeyColumnsRowData(rowData));
		}
	}

	private RowData getNonKeyColumnsRowData(RowData rowData) {
		Map<Column, Object> nonKeyRowData = new HashMap<Column, Object>();
		for (int i = 0; i < nonKeyColumnNames.size(); i++) {
			Column nonKeyColumn = nonKeyColumnNames.get(i);
			nonKeyRowData.put(nonKeyColumn,rowData.getValue(nonKeyColumn));
		}
		return new RowData(nonKeyRowData);
	}
 
	private RowKey getKey(RowData rowData) {
		Object[] keyColumns = new Object[keyColumnNames.size()];
		for (int i = 0; i < keyColumnNames.size(); i++) {
			keyColumns[i] = rowData.getValue(keyColumnNames.get(i));
		}
		return new RowKey(keyColumns);
	}

	public Set<RowKey> getKeys() {
		return dataMap.keySet();
	}

	public RowData getRowData(RowKey rowKey) {
		return dataMap.get(rowKey);
	}

	
	public void setDataMap(Map<RowKey, RowData> dataMap) {
		this.dataMap = dataMap;
	}

	public Map<RowKey, RowData> getDataMap() {
		return dataMap;
	}

	

	public String getNonKeyDescription(RowData rowData) {
		return getKeyDescription(rowData, nonKeyColumnNames);
	}

	public String getKeyDescription(RowData rowData) {
		return getKeyDescription(rowData, keyColumnNames);
	}

	public String getAllKeyDescription(RowData rowData) {
		return getKeyDescription(rowData, allColumnNames);
	}

	public String getKeyDescription(RowData rowData, List<Column> columns) {
		StringBuffer sb = new StringBuffer();
		for (Column column: columns) {
			sb.append(column.getName()).append(": ").append(rowData.getValue(column))
					.append(", ");
		}
		return sb.toString();
	}

	public List<Object> getKeyValues(RowData rowData) {
		return getKeyValues(rowData, keyColumnNames);
	}

	public List<Object> getKeyValues(RowData rowData, List<Column> columns) {
		List<Object> keyValues = new LinkedList<Object>();
		for (Column column : columns) {
			keyValues.add(rowData.getValue(column));
		}
		return keyValues;
	}

	public String getQuery() {
		return query;
	}

}