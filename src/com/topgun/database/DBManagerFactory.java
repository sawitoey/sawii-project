package com.topgun.database;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBManagerFactory {
	
	private static final Logger logger = LogManager.getLogger(DBManagerFactory.class);
	private static BasicDataSource datasource155 = null;
	private static BasicDataSource datasource66 = null;
	
	private static void init155Connection()
	{
		if(datasource155 == null)
		{
			java.sql.Connection connectionTest = null;
			try
			{
				logger.info("start initial 155 Pool");
				datasource155 = new BasicDataSource();
				datasource155.setUrl(DatabaseConfiguration.POOL_155_URL);
				datasource155.setUsername(DatabaseConfiguration.POOL_155_USERNAME);
				datasource155.setPassword(DatabaseConfiguration.POOL_155_PASSWORD);
				datasource155.setDriverClassName(DatabaseConfiguration.POOL_155_DRIVER);
				datasource155.setInitialSize(DatabaseConfiguration.POOL_155_INITIAL_SIZE);
				datasource155.setMaxOpenPreparedStatements(DatabaseConfiguration.POOL_155_MAX_TOTAL);
				datasource155.setPoolPreparedStatements(DatabaseConfiguration.POOL_155_PREPARED_STATEMENTS);
				connectionTest = datasource155.getConnection();
				connectionTest.close();
				logger.info("finish initial 155 Pool");
			}
			catch(Exception e)
			{
				logger.error("init 155 pool", e);
			}
			finally
			{
				
			}
			
		}
	}
	
	private static void init66Connection()
	{
		if(datasource66 == null)
		{
			java.sql.Connection connectionTest = null;
			try
			{
				logger.info("start initial 66 Pool");
				datasource66 = new BasicDataSource();
				datasource66.setUrl(DatabaseConfiguration.POOL_66_URL);
				datasource66.setUsername(DatabaseConfiguration.POOL_66_USERNAME);
				datasource66.setPassword(DatabaseConfiguration.POOL_66_PASSWORD);
				datasource66.setDriverClassName(DatabaseConfiguration.POOL_66_DRIVER);
				datasource66.setInitialSize(DatabaseConfiguration.POOL_66_INITIAL_SIZE);
				datasource66.setMaxOpenPreparedStatements(DatabaseConfiguration.POOL_66_MAX_TOTAL);
				datasource66.setPoolPreparedStatements(DatabaseConfiguration.POOL_66_PREPARED_STATEMENTS);
				connectionTest = datasource66.getConnection();
				connectionTest.close();
				logger.info("finish initial 66 Pool");
			}
			catch(Exception e)
			{
				logger.error("init 66 pool", e);
			}
		}
	}
	
	public static DBManager create155DBManager()
	{
		DBManager db = null;
		try
		{
			init155Connection();
			db = new DBManager(datasource155.getConnection());
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		return db;
	}
	
	public static DBManager create66DBManager()
	{
		DBManager db = null;
		try
		{
			init66Connection();
			db = new DBManager(datasource66.getConnection());
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		return db;
	}
}
