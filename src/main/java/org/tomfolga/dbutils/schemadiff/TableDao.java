package org.tomfolga.dbutils.schemadiff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class TableDao implements ITableDao {

	private static final Log LOG = LogFactory.getLog(TableDao.class);

	private final IDataSource dataSource;

	public TableDao(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public TableData loadTableData(final List<Column> keyColumnNames, final List<Column> nonKeyColumnNames, String query) {
		String modifiedQuery = VariableSubstitutor.replace(query, getDataSource().getVariableSubstitutionProperties());
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource().getDataSource());
		List<RowData> rowDataList = new LinkedList<RowData>();
		try {
			LOG.info("About to call query in " + dataSource.getName() + ":" + modifiedQuery);
			rowDataList = template.getJdbcOperations().query(modifiedQuery, new ResultSetExtractor<List<RowData>>() {

				@Override
				public List<RowData> extractData(ResultSet rs) throws SQLException, DataAccessException {
					List<RowData> result = new LinkedList<RowData>();
					for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
						int keyColumnIndex = i;
						if (keyColumnIndex >= 0 && keyColumnIndex < keyColumnNames.size()) {
							keyColumnNames.set(keyColumnIndex, new Column(rs.getMetaData().getColumnName(i + 1)));
						}
						int nonKeyColumnIndex = i - keyColumnNames.size();
						if (nonKeyColumnIndex >= 0 && nonKeyColumnIndex < nonKeyColumnNames.size()) {
							nonKeyColumnNames.set(nonKeyColumnIndex, new Column(rs.getMetaData().getColumnName(i + 1)));
						}
					}
					while (rs.next()) {
						Map<Column, Object> rowValues = new HashMap<Column, Object>();
						for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
							rowValues.put(new Column(rs.getMetaData().getColumnName(i + 1)), rs.getObject(i + 1));							
						}
						result.add(new RowData(rowValues));
					}
					return result;
				}

			});
			LOG.info("Finished running query.");
		} catch (DataAccessException e) {
			LOG.error("Wrong sql :" + dataSource.getName() + " " + query);
			throw e;
		}
		return new TableData(keyColumnNames, nonKeyColumnNames, rowDataList, modifiedQuery);
	}

	@Override
	public String getDbName() {
		return getDataSource().getName();
	}

	@Override
	public IDataSource getDataSource() {
		return dataSource;
	}

}
