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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class TableDao implements ITableDao {

	private static final Log LOG = LogFactory.getLog(TableDao.class);

	private final IDataSource dataSource;

	public TableDao(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public TableData loadTableData(List<Column> keyColumnNames, List<Column> nonKeyColumnNames, String query) {
		String modifiedQuery = VariableSubstitutor.replace(query, getDataSource().getVariableSubstitutionProperties());
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource().getDataSource());
		List<RowData> rowDataList = new LinkedList<RowData>();
		try {
			LOG.info("About to call:" + modifiedQuery);
			rowDataList = template.query(modifiedQuery, new RowMapper<RowData>() {

				public RowData mapRow(ResultSet rs, int rowNum) throws SQLException {
					Map<Column, Object> rowValues = new HashMap<Column, Object>();
					for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
						rowValues.put(new Column(rs.getMetaData().getColumnName(i + 1)), rs.getObject(i + 1));
					}
					return new RowData(rowValues);
				}

			});
			LOG.info("Finished running query.");
		} catch (DataAccessException e) {
			LOG.info("Wrong sql:" + query);
			throw e;
		}
		return new TableData(keyColumnNames, nonKeyColumnNames,		rowDataList,modifiedQuery );
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
