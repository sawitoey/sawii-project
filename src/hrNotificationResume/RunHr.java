package hrNotificationResume;

import com.topgun.database.DBManager;
import com.topgun.database.DBManagerFactory;


public class RunHr {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBManager db = DBManagerFactory.create155DBManager();
		db.close();

	}

}
