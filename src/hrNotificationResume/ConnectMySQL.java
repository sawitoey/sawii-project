package hrNotificationResume;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectMySQL {
	public static Connection getConnection(String host,int port,String username, String password,String sid){
		Connection result=null;
		try{
			if(result==null){
				Class.forName("com.mysql.jdbc.Driver");
				String url = "jdbc:mysql://"+host+":"+port+"/"+sid;
				result = DriverManager.getConnection(url, username, password);	
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void closeConnection(Connection conn){
		try{	
			if(conn!=null)
				conn.close();		
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			conn=null;
		}
	}
}
