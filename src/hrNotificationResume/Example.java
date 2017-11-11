package hrNotificationResume;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.topgun.database.DBManager;
import com.topgun.database.DBManagerFactory;


public class Example 
{
	private static final Logger logger = LogManager.getLogger(Example.class);
	
	public static void main(String[] args) 
	{
		logger.info("This is database pool connection and log4j example");

		DBManager db=null;
		try
		{
			db = DBManagerFactory.create155DBManager();
			db.createPreparedStatement("SELECT 1 AS MOCKUP FROM DUAL");
			db.executeQuery();			
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		finally
		{
			db.close();
		}
		logger.info("LOG4j INFO");
		logger.error("LOG4j ERROR");
		logger.warn("LOG4j WARN");
		logger.fatal("LOG4j FATAL");
		logger.debug("LOG4j DEBUG");
	}
}
