package org.xllapp.commons.jdbc;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此类提供进行JDBC批量处理的便捷方法.
 * 
 * @author dylan.chen Oct 16, 2013
 * 
 */
public class BatchUpdateTemplate {

	private final static Logger logger = LoggerFactory.getLogger(BatchUpdateTemplate.class);

	private final static Logger batchUpdateMessagelogger = LoggerFactory.getLogger("BatchUpdateTemplate.batchupdate");

	private DataSource dataSource;
	
	public BatchUpdateTemplate(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		int[] result = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
			for (int i = 0; i < batchArgs.size(); i++) {
				Object[] args = batchArgs.get(i);
				for (int j = 0; j < args.length; j++) {
					pstmt.setObject(j + 1, args[j]);
				}
				pstmt.addBatch();
			}
			result = pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {

			logger.error("failure to batchUpdate - SQL: " + sql, e);

			if (e instanceof BatchUpdateException) {
				handleBatchUpdateException(connection,pstmt,sql, batchArgs, (BatchUpdateException) e);
			}
		} finally {
			if (null != pstmt) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					logger.error("failure to close PreparedStatement", e);
				}
			}
			if (null != connection) {
				try {
					connection.close();
				} catch (Exception e) {
					logger.error("failure to close Connection", e);
				}
			}

		}
		return result;
	}

	private void handleBatchUpdateException(Connection connection,PreparedStatement pstmt,String sql, List<Object[]> batchArgs, BatchUpdateException batchUpdateException) {
		int batchSize = batchArgs.size();
		int[] updateCounts = batchUpdateException.getUpdateCounts();
		int handledCount = updateCounts.length;

		String bid = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

		batchUpdateMessagelogger.error("------ BatchUpdate[{}] --- batchSize: {} --- handledCount: {} --- Exception caused by {} ------", bid, batchSize,handledCount, batchUpdateException.getLocalizedMessage());

		batchUpdateMessagelogger.error("BatchUpdate[{}] - updateCounts: {}", bid,Arrays.toString(updateCounts));

		boolean isRoolback=false;
		
		if (handledCount < batchSize) {
			
			try {
				
				connection.commit();
				
				batchUpdateMessagelogger.error("BatchUpdate[{}] - failed update[SQL: {},Args: {}],caused by {}", bid, sql, Arrays.toString(batchArgs.get(handledCount)), batchUpdateException.getLocalizedMessage());

				for (int j = updateCounts.length + 1; j < batchArgs.size(); j++) {
					batchUpdateMessagelogger.error("BatchUpdate[{}] - interrupted -  SQL: {},Args: {}", bid, sql, Arrays.toString(batchArgs.get(j)));
				}
				
			} catch (Exception e) {
				logger.error("BatchUpdate["+bid+"] failure to commit - SQL: " + sql, e);
				isRoolback=true;
			}
			
		} else {
			
			isRoolback=true;
			
		}
		
		if(isRoolback){
			
			try {
				connection.rollback();
			} catch (SQLException e1) {
				logger.error("BatchUpdate["+bid+"] failure to rollback - SQL: " + sql, e1);
			} finally {
				try {
					pstmt.clearBatch();
				} catch (SQLException e2) {
					logger.error("BatchUpdate["+bid+"] failure to clearBatch - SQL: " + sql, e2);
				}
			}

			batchUpdateMessagelogger.error("BatchUpdate[{}] - rollback BatchUpdate,caused by {}", bid, batchUpdateException.getLocalizedMessage());

			for (int i = 0; i < updateCounts.length; i++) {
				batchUpdateMessagelogger.error("BatchUpdate[{}] - rollback - SQL: {},Args: {}", bid, sql, Arrays.toString(batchArgs.get(i)));
			}
			
		}

		batchUpdateMessagelogger.error("------ BatchUpdate[{}] ------", bid);
	}

}
