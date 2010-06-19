package org.tomfolga.dbutils.schemadiff;

import java.util.LinkedList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.tomfolga.dbutils.schemadiff.RowDiff.DiffType;

public class TableDiffProcessor {

	ITableDao tableDao1;

	ITableDao tableDao2;

	TableData table1;

	TableData table2;

	private String sql;

	List<Column> keyColumnNames;

	List<Column> nonKeyColumnNames;

	public TableDiffProcessor(Report report, ITableDao tableDao1, ITableDao tableDao2) {
		this.keyColumnNames = report.getKeyColumns();
		this.nonKeyColumnNames = report.getNonKeyColumns();
		this.tableDao1 = tableDao1;
		this.tableDao2 = tableDao2;

		sql = "select " + Column.columnsToString(report.getKeyColumns()) + ","
				+ Column.columnsToString(report.getNonKeyColumns()) + " from " + report.getTableAndWhereClause();
	}

	public void loadTables() {

		table1 = loadReportingErrors(tableDao1);
		table2 = loadReportingErrors(tableDao2);

	}

	private TableData loadReportingErrors(ITableDao tableDao) {
		try {
			return tableDao.loadTableData(keyColumnNames, nonKeyColumnNames, sql);
		} catch (DataAccessException e) {
			throw new DataAccessException("Error in database" + tableDao.getDbName(), e) {
			};
		}

	}

	public TableDiff diff() throws DataAccessException {
		List<RowDiff> rowDiffs = new LinkedList<RowDiff>();
		for (RowKey rowKey : table1.getKeys()) {
			if (table2.getRowData(rowKey) == null) {
				rowDiffs.add(new RowDiff(rowKey, table1.getRowData(rowKey), table2.getRowData(rowKey),
						DiffType.ONLY_FIRST));
			}
		}
		// Object differing
		for (RowKey rowKey : table1.getKeys()) {
			if (table2.getRowData(rowKey) != null && !table1.getRowData(rowKey).equals(table2.getRowData(rowKey))) {
				rowDiffs.add(new RowDiff(rowKey, table1.getRowData(rowKey), table2.getRowData(rowKey), DiffType.DIFF));
			}
		}
		for (RowKey rowKey : table2.getKeys()) {
			if (table1.getRowData(rowKey) == null) {
				rowDiffs.add(new RowDiff(rowKey, table1.getRowData(rowKey), table2.getRowData(rowKey),
						DiffType.ONLY_SECOND));
			}
		}

		return new TableDiff(rowDiffs, table1, table2);
	}

}
