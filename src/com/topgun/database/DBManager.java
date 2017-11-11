package com.topgun.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBManager
{	
	private static final Logger logger = LogManager.getLogger(DBManager.class);
	
	private PreparedStatement stmn;	
	private Connection conn;
	private ResultSet dataset;
	
	DBManager(Connection conn)
	{
		this.conn=conn;
	}
			
	public void createPreparedStatement(String sql)  throws Exception 
	{				
		this.stmn = this.conn.prepareStatement(sql);
	}
	
	public void setInt(int index,int value) throws Exception 
	{
		this.stmn.setInt(index, value);
	}
	
	public void setString(int index,String value) throws Exception
	{
		this.stmn.setString(index,value);			
	}

	public void setDate(int index, Date value) throws Exception
	{		
		this.stmn.setDate(index,value);			
	}

	public void setTime(int index, Time value) throws Exception
	{		
		this.stmn.setTime(index,value);			
	}

	public void setFloat(int index, float value) throws Exception
	{		
		this.stmn.setFloat(index,value);			
	}
	
	public void setDouble(int index, double value) throws Exception
	{		
		this.stmn.setDouble(index,value);			
	}
			
	public void setLong(int index, long value) throws Exception
	{		
		this.stmn.setLong(index,value);			
	}

	public void setTimestamp(int index, Timestamp value) throws Exception
	{		
		this.stmn.setTimestamp(index,value);				
	}	
		
	public String getString(String field) throws Exception
	{
		return this.dataset.getString(field);
	}

	public int getInt(String field) throws Exception
	{
		return this.dataset.getInt(field);
	}
	
	public float getFloat(String field) throws Exception
	{
		return this.dataset.getFloat(field);
	}	
	
	public long getLong(String field) throws Exception
	{
		return this.dataset.getLong(field);
	}	

	public double getDouble(String field) throws Exception
	{
		return this.dataset.getDouble(field);
	}	

	public Date getDate(String field) throws Exception
	{
		return this.dataset.getDate(field);
	}	

	public Time getTime(String field) throws Exception
	{
		return this.dataset.getTime(field);
	}	
	
	public Timestamp getTimestamp(String field) throws Exception
	{
		return this.dataset.getTimestamp(field);
	}	
	
	public void addBatch() throws Exception{
		this.stmn.addBatch();
	}
	
	public int[] executeBatch() throws Exception{
		return this.stmn.executeBatch();
	}
	
	public void executeQuery() throws Exception 
	{				
		this.dataset = this.stmn.executeQuery();			
	}
	
	public int executeUpdate() throws Exception
	{
		return this.stmn.executeUpdate();
	}

	public void first() throws Exception
	{
		this.dataset.beforeFirst();
	}
	
	public boolean next() throws Exception
	{
		return this.dataset.next();
	}
	
	public void close() 
	{
		try 
		{				
			if(this.dataset!=null)
			{
				this.dataset.close();
			}
		}
		catch(Exception e) 
		{
			logger.error("close dataset", e);	
		}
		finally
		{
			this.dataset=null;
		}
		
		try
		{
			if(this.stmn!=null)
			{
				this.stmn.close();	
			}
		}
		catch(Exception e)
		{
			logger.error("close statement", e);	
		}
		finally
		{
			this.stmn=null;
		}
		
		try
		{
			if(this.conn!=null)
			{
				this.conn.close();	
			}
		}
		catch(Exception e)
		{
			logger.error("close connection", e);	
		}
		finally
		{
			this.conn=null;
		}
	}
}