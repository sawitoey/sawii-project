package hrNotificationResume;
import com.topgun.database.DBManager;
import com.topgun.database.DBManagerFactory;

public class TransferHR {

	public static void main(String[] args) {
		DBManager db=null;
		String sql = "  SELECT distinct  d.id_emp ,d.id_user, d.mt,   (  TO_CHAR (SYSDATE, 'YYYYMMDD')  - TO_CHAR (d.mt, 'YYYYMMDD') )  as  days "+
					 "	FROM  (  "+
					 "			select max(mini_login.timestamp) as mt , mini_login.id_emp,mini_login.id_user    "+
					 "			from  mini_login, call  , jtg_user "+
					 "		   where mini_login.id_emp=call.id  "+
					 "		   and call.id = jtg_user.id_emp "+
					 "		   and mini_login.ID_USER=jtg_user.id "+
					 "		    and call.type_ in  ('A','AE','AH','D','DH','DE','DT')  "+
					 "			and mini_login.id_user is not null "+
					 "			and jtg_user.email is not null "+
					 "			and jtg_user.del_flag=0 "+
					 "			and jtg_user.noti_resume_flag=1  "+
					 "			group by mini_login.id_emp ,mini_login.id_user    "+ 
					 "	  ) d ,position f, employer g  "+
					 " WHERE   d.id_emp=f.id_emp and  f.id_emp=g.id_emp and f.flag=1 and g.flag=1  "+
					 " and     TO_CHAR (SYSDATE, 'YYYYMMDD') BETWEEN f.post_date AND f.e_d2 "+  
					 " and  TO_CHAR (SYSDATE, 'YYYYMMDD') <> f.first_online  "+
					 " and  MOD( (  TO_CHAR (SYSDATE, 'YYYYMMDD') - TO_CHAR (d.mt, 'YYYYMMDD') ) , 7)=0  "+
				     " and TO_CHAR (d.mt, 'YYYYMMDD') <>  TO_CHAR (SYSDATE, 'YYYYMMDD') and d.id_emp=455  "+
				     " order by d.id_emp,d.id_user ";
		
		 
		try
		{
			db = DBManagerFactory.create155DBManager();
			db.createPreparedStatement(sql);
			db.executeQuery();
			while(db.next())
			{
				int idEmp = db.getInt("ID_EMP");
				int idUser = db.getInt("ID_USER");
				Boolean check = checkIdEmp(idEmp,idUser);
				if(!check)
				{
					String sql_insert="	INSERT INTO HR_NOTIFICATION_RESUME(ID_EMP,ID_USER,TIMESTAMP) VALUES(?,?,SYSDATE) ";
					DBManager ds = null;
					try
					{
						ds = DBManagerFactory.create155DBManager();
						ds.createPreparedStatement(sql_insert);
						ds.setInt(1, idEmp);
						ds.setInt(2, idUser);
						ds.executeUpdate();
						//System.out.println("Export idEmp Finish => "+idEmp);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						ds.close();
					}
				}
			}
			
			MailManager mail=new MailManager();
			mail.setSender("Sawitree@topgunthailand.com");
			mail.setSubject("HR Transfer to HR_NOTIFICATION finish");
			mail.setRecepient("Sawitree@topgunthailand.com");
			mail.addHTML("<html><body>HR Transfer to HR_NOTIFICATION finish</body></html>");
			mail.send();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			db.close();
		}
	}
	
	public static Boolean checkIdEmp(int idEmp,int idUser)
	{
		Boolean result = false;
		DBManager db=null;
		String sql="";
		try
		{
			db = DBManagerFactory.create155DBManager();
			sql=" 	select id_emp from hr_notification_resume where id_emp=? and id_user=? ";
		    db.createPreparedStatement(sql);
		    db.setInt(1, idEmp);
		    db.setInt(2, idUser);
		    db.executeQuery();
		    if(db.next())
		    {
		    	result=true;
		    }
		}
		catch(Exception ex){ex.printStackTrace();}
		finally
		{
			db.close();
		}
		return result;
	}

}
