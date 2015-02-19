package org.xllapp.commons.jdbc;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.easymock.EasyMock;
import org.junit.Test;
import org.xllapp.commons.jdbc.BatchUpdateTemplate;


/**
 *
 *
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved 
 * @Company: 北京福富软件有限公司 
 * @author 陈作朋 Oct 21, 2013
 * @version 1.00.00
 * @history:
 * 
 */
public class BatchUpdateTemplateTest {

	//TODO BatchUpdateTemplate 单元测试

	@Test
	public void testBatchUpdateException1() throws SQLException{
		
		String sql="";
		
		int argCount=3;
		
		List<Object[]> batchArgs=new ArrayList<Object[]>(){
			{
				add(new Object[]{"1","a","p"});
				add(new Object[]{"2","b","p"});
				add(new Object[]{"3","c","p"});
			}
		};
		
		int[] updateCounts=new int[]{Statement.SUCCESS_NO_INFO};
		
		DataSource dataSource = EasyMock.createMock(DataSource.class);
		Connection connection=EasyMock.createMock(Connection.class);
		PreparedStatement preparedStatement=EasyMock.createNiceMock(PreparedStatement.class);
		BatchUpdateException updateException=new BatchUpdateException("Test", updateCounts);
		
		EasyMock.expect(dataSource.getConnection()).andReturn(connection).times(1);
		EasyMock.expect(connection.prepareStatement(sql)).andReturn(preparedStatement).times(1);
		
		connection.setAutoCommit(false);
		EasyMock.expectLastCall().times(1);
		
		EasyMock.expect(preparedStatement.executeBatch()).andThrow(updateException).times(1);
		
		connection.commit();
		EasyMock.expectLastCall().times(1);
		
		preparedStatement.close();
		EasyMock.expectLastCall().times(1);
		
		connection.close();
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(dataSource,connection,preparedStatement);
		
		BatchUpdateTemplate batchUpdateTemplate=new BatchUpdateTemplate(dataSource);
		
		batchUpdateTemplate.batchUpdate(sql, batchArgs);
		
		EasyMock.verify(dataSource,connection,preparedStatement);
	}
	
	@Test
	public void testBatchUpdateException2() throws SQLException{
		
		String sql="";
		
		int argCount=3;
		
		List<Object[]> batchArgs=new ArrayList<Object[]>(){
			{
				add(new Object[]{"1","a","p"});
				add(new Object[]{"2","b","p"});
				add(new Object[]{"3","c","p"});
			}
		};
		
		int[] updateCounts=new int[]{Statement.EXECUTE_FAILED,Statement.EXECUTE_FAILED,Statement.EXECUTE_FAILED};
		
		DataSource dataSource = EasyMock.createMock(DataSource.class);
		Connection connection=EasyMock.createMock(Connection.class);
		PreparedStatement preparedStatement=EasyMock.createNiceMock(PreparedStatement.class);
		BatchUpdateException updateException=new BatchUpdateException("Test", updateCounts);
		
		EasyMock.expect(dataSource.getConnection()).andReturn(connection).times(1);
		EasyMock.expect(connection.prepareStatement(sql)).andReturn(preparedStatement).times(1);
		
		connection.setAutoCommit(false);
		EasyMock.expectLastCall().times(1);
		
		EasyMock.expect(preparedStatement.executeBatch()).andThrow(updateException).times(1);
		
		connection.rollback();
		EasyMock.expectLastCall().times(1);
		
		preparedStatement.close();
		EasyMock.expectLastCall().times(1);
		
		connection.close();
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(dataSource,connection,preparedStatement);
		
		BatchUpdateTemplate batchUpdateTemplate=new BatchUpdateTemplate(dataSource);
		
		batchUpdateTemplate.batchUpdate(sql, batchArgs);
		
		EasyMock.verify(dataSource,connection,preparedStatement);
	}
}
