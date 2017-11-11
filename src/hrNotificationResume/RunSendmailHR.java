package hrNotificationResume;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.topgun.database.DBManager;
import com.topgun.database.DBManagerFactory;

public class RunSendmailHR {

	public static void main(String[] args) {
		int totalJob=0;
		int countResume7day = 0;
		int countResumeTrack = 0;
		int ins = 0;
		int idEmp = 0;
		String idUser = "";
		int id_position=0;
		String onlineDate="";
		String com_name="";
		String idIns="";
		int id = 0;
		int count=0;
		int total_sent=0;
		int total_com_dt=0;
		int total_user = 0;
		int last = 0;
		int total_com = 0;
		int lastCom = 0;
	/*--------getCurrentDate--------*/
		
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -1);
		String m1=String.valueOf((now.get(Calendar.MONTH) + 1) < 10 ? "0"+String.valueOf((now.get(Calendar.MONTH) + 1)):String.valueOf((now.get(Calendar.MONTH) + 1)));
		String d1=String.valueOf(now.get(Calendar.DATE) < 10 ? "0"+String.valueOf(now.get(Calendar.DATE)):String.valueOf(now.get(Calendar.DATE)));
		String curr = now.get(Calendar.YEAR)+""+m1+""+d1;
		String currFormat = d1+"/"+m1+"/"+now.get(Calendar.YEAR);
		
		Calendar now1 = Calendar.getInstance();
		now1.add(Calendar.DATE, -7);
		String m2=String.valueOf((now1.get(Calendar.MONTH) + 1) < 10 ? "0"+String.valueOf((now1.get(Calendar.MONTH) + 1)):String.valueOf((now1.get(Calendar.MONTH) + 1)));
		String d2=String.valueOf(now1.get(Calendar.DATE) < 10 ? "0"+String.valueOf(now1.get(Calendar.DATE)):String.valueOf(now1.get(Calendar.DATE)));
		String currPast7day = now1.get(Calendar.YEAR)+""+m2+""+d2;
	/*-----------------------------*/
		String sql_main ="select distinct id_emp,id_user from hr_notification_resume   order by id_emp desc";
		DBManager db=null;
		try
		{
			db = DBManagerFactory.create155DBManager();
			db.createPreparedStatement(sql_main);
			db.executeQuery();
			while(db.next())
			{
				totalJob = 0;
				countResume7day = 0;
				countResumeTrack = 0;
				ins = 0;
				idEmp = 0;
				idUser = "";
				id_position=0;
				com_name="";
				onlineDate="";
				idIns="";
				idEmp = db.getInt("ID_EMP");
				idUser = db.getString("ID_USER");
				count=0;
				id=0;
				/*---------------select position this id_emp--------------*/
				String sql_position =" select distinct a.id_position,a.position_name,b.company_name,a.online_date,a.e_d2 "+
					" from position a , employer b "+
					" where to_char(sysdate,'YYYYMMDD') between post_date and e_d2 "+
					" and a.flag = 1 "+
					" and b.flag = 1 "+
					" and a.id_emp = b.id_emp "+
					" and a.id_emp =  ? "+
					" and  TO_CHAR (SYSDATE, 'YYYYMMDD') <> a.first_online "+
					" order by a.id_emp ";
				DBManager dp=null;
				try
				{
					dp = DBManagerFactory.create155DBManager();
					dp.createPreparedStatement(sql_position);
					dp.setInt(1, idEmp);
					dp.executeQuery();
					while(dp.next())
				    {
						totalJob++;
				    	id_position = dp.getInt("ID_POSITION");
				    	com_name = dp.getString("COMPANY_NAME");
				    	onlineDate = dp.getString("ONLINE_DATE");
				    	if(Integer.parseInt(onlineDate) >  Integer.parseInt(curr)) // get online and expire from position_date_b
				    	{
				    		DBManager dbp=null;
				    		String sql_date_b="select max(online_date) as online_date from  position_date_b where id_emp=? and id_p=? ";
				    		try
				    		{
				    			dbp = DBManagerFactory.create155DBManager();
				    			dbp.createPreparedStatement(sql_date_b);
				    			dbp.setInt(1, idEmp);
				    			dbp.setInt(2, id_position);
				    			dbp.executeQuery();
				    			if(dbp.next())
				    			{
				    				onlineDate=(dbp.getString("online_date")!=null)?dbp.getString("online_date"):"";
				    			}
				    		}
				    		catch(Exception ex)
				    		{
				    			ex.printStackTrace();
				    		}
				    		finally
				    		{
				    			dbp.close();
				    		}
				    	}
				    	countResumeTrack = sumResumefromInterTrack(idEmp,id_position,onlineDate,curr);
				    	if(Integer.parseInt(currPast7day)<Integer.parseInt(onlineDate))
				    	{
				    		currPast7day=onlineDate;
				    	}
				    	countResume7day = sumResumefromInterTrack(idEmp,id_position,currPast7day,curr);
						id = 0;
						if(countResume7day>0) 
						{
							ins=0;
							ins = insertSumResumefromInterTrack(idEmp,countResumeTrack,countResume7day,0,id_position,onlineDate);
							id = maxIDTrackNoti(idEmp,id_position,0);
							if(ins>0 && id>0)
							{
								if(count>0)
								{
									idIns+=",";
								}
								idIns += id;
								count++;
							}
						}
				    }
					//System.out.println("idIns="+idIns);
					//System.out.println("idEmp="+idEmp+" id_position="+id_position+" currPast7day="+currPast7day+" curr="+curr+" onlineDate="+onlineDate);
					if(idIns!=null && !idIns.equals("")  )
					{
						String body = "";
						//int last = 0;
						//--------------------Sent mail------------------------------
						String sql_mail =" select distinct a.email,a.id,a.username,b.cs from jtg_user a,call b "
								+ "	where a.id_emp=? and a.id=? and a.del_flag=0 and a.email is not null and a.noti_resume_flag=1 "
								+ " and a.id_emp=b.id ";
						DBManager dm=null;
						try
						{
							dm = DBManagerFactory.create155DBManager();
							dm.createPreparedStatement(sql_mail);
							dm.setInt(1, idEmp);
							dm.setString(2, idUser);
							dm.executeQuery();
						    while(dm.next())
							{
						    	body = "";
						    	String email = dm.getString("EMAIL");
						    	String username = dm.getString("USERNAME");
						    	String cs = dm.getString("CS");
						    	//----------bodymail-----------------------------------------
								body = contentMail(idEmp,idIns,totalJob,com_name,currFormat,curr,onlineDate,count,email,username,idUser);
						    	MailManager mail=null;
								try
								{
									mail=new MailManager();
									mail.setSenderName("JOBTOPGUN");
									mail.setSender(cs+"@topgunthailand.com");
									mail.setSubject("อัพเดตใบสมัครที่คุณอาจจะยังไม่ได้ดู");
									mail.setRecepient(email);
									//mail.setRecepient("sathiya@topgunthailand.com");
									mail.addHTML(body);
									mail.send();
									if(idEmp!=last)
									{
										total_sent++;
									}
									last=idEmp;
									insertHRNotiResumeResponse(idEmp,idUser);
									total_user++;
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
						}
						catch(Exception ex){ex.printStackTrace();}
						finally
						{
							dm.close();
						}
					}
					
				}
				catch(Exception ex){ex.printStackTrace();}
				finally
				{
					dp.close();
				}
				/*--------------------------------------------------------*/
				if(idEmp!=lastCom)
				{
					total_com++;
				}
				lastCom=idEmp;
				delIdEmp(idEmp,idUser);
			}
			total_com_dt=getTotalComAvaiPkg("DT");
			insertHRNotiResumeSummary(total_com,total_sent,total_com_dt,total_user);
			updateStatus();
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
	
	
	

	public static int sumResumefromInterTrack(int idEmp, int idPosition,String date1,String date2)
	{
		int result = 0;
		DBManager db=null;
		String sql="";
		try
		{
			db = DBManagerFactory.create155DBManager();
			sql=" select count(id) as total_resume "+
					" from inter_track   "+
					" where id_emp=? and id_position=? "
					+ " and to_char(sentdate,'YYYYMMDD') >= ? and to_char(sentdate,'YYYYMMDD') <= ? ";
		    db.createPreparedStatement(sql);
		    db.setInt(1, idEmp);
		    db.setInt(2, idPosition);
		    db.setString(3, date1);
		    db.setString(4, date2);
		    db.executeQuery();
		    if(db.next())
		    {
		    	result=db.getInt("TOTAL_RESUME");
		    }
		}
		catch(Exception ex){ex.printStackTrace();}
		finally
		{
			db.close();
		}
		return result;
	}
	
	public static void insertHRNotiResumeResponse(int idEmp,String idUser)
	{
		String sql_insert="	INSERT INTO HR_NOTI_RESUME_RESPONSE(ID_EMP,ID_USER,MAIL_DATE) VALUES(?,?,SYSDATE) ";
		DBManager ds = null;
		try
		{
			ds = DBManagerFactory.create155DBManager();
			ds.createPreparedStatement(sql_insert);
			ds.setInt(1, idEmp);
			ds.setString(2, idUser);
			ds.executeUpdate();
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
	
	public static int insertSumResumefromInterTrack(int idEmp,int totalResume,int diff,int status,int idPosition,String onlineDate)
	{
		int result = 0;
		String sql_insert="	INSERT INTO TRACK_NOTIFICATION_RESUME(ID_EMP,TOTAL_RESUME,RESUME_ADDED,STATUS,TIMESTAMP,ID_POSITION,ONLINE_DATE) VALUES(?,?,?,?,SYSDATE,?,?) ";
		DBManager ds = null;
		try
		{
			ds = DBManagerFactory.create155DBManager();
			ds.createPreparedStatement(sql_insert);
			ds.setInt(1, idEmp);
			ds.setInt(2, totalResume);
			ds.setInt(3, diff);
			ds.setInt(4, status);
			ds.setInt(5, idPosition);
			ds.setString(6,onlineDate);
			ds.executeUpdate();
			result=1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ds.close();
		}
		return result;
	}
	
	public static int updateStatus()
	{
		int result = 0;
		String upd=	" UPDATE TRACK_NOTIFICATION_RESUME " +
		    	"	SET " +
		    	"		STATUS=1 ";
		DBManager ds = null;
		try
		{
			ds = DBManagerFactory.create155DBManager();
			ds.createPreparedStatement(upd);
			ds.executeUpdate();
			result=1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ds.close();
		}
		return result;
	}
	
	
	public static int maxIDTrackNoti(int idEmp,int idPosition, int status)
	{
		int result = 0;
		DBManager db=null;
		String sql="";
		try
		{
			db = DBManagerFactory.create155DBManager();
			
			sql=" select max(id) as id from track_notification_resume where id_emp="+idEmp+" and id_position="+idPosition;
			if(status==0)
			{
				sql+=" and status=0 ";
			}
		    db.createPreparedStatement(sql);
		    db.executeQuery();
		    if(db.next())
		    {
		    	result=db.getInt("ID");
		    }
		}
		catch(Exception ex){ex.printStackTrace();}
		finally
		{
			db.close();
		}
		return result;
	}
	
	public static int delIdEmp(int idEmp,String idUser)
	{
		int result = 0;
		String upd=	" DELETE FROM HR_NOTIFICATION_RESUME WHERE ID_EMP=? AND ID_USER=? ";
		DBManager ds = null;
		try
		{
			ds = DBManagerFactory.create155DBManager();
			ds.createPreparedStatement(upd);
			ds.setInt(1, idEmp);
			ds.setString(2, idUser);
			ds.executeUpdate();
			result=1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ds.close();
		}
		return result;
	}
	
	public static String contentMail(int idEmp,String idIns,int totalJob,String com_name,String currFormat,String curr,String onlineDate,int countPosition,String email,String username,String idUser)
	{
		DBManager db=null;
		String sql="";
	    String body = "";
		Calendar now = Calendar.getInstance();
	    now.add(Calendar.DATE, -7);
	    String m1=String.valueOf((now.get(Calendar.MONTH) + 1) < 10 ? "0"+String.valueOf((now.get(Calendar.MONTH) + 1)):String.valueOf((now.get(Calendar.MONTH) + 1)));
		String d1=String.valueOf(now.get(Calendar.DATE) < 10 ? "0"+String.valueOf(now.get(Calendar.DATE)):String.valueOf(now.get(Calendar.DATE)));
	    String datePastFormat = d1+"/"+m1+"/"+now.get(Calendar.YEAR);
	    
	    Calendar now1 = Calendar.getInstance();
	    String m2=String.valueOf((now1.get(Calendar.MONTH) + 1) < 10 ? "0"+String.valueOf((now1.get(Calendar.MONTH) + 1)):String.valueOf((now1.get(Calendar.MONTH) + 1)));
		String d2=String.valueOf(now1.get(Calendar.DATE) < 10 ? "0"+String.valueOf(now1.get(Calendar.DATE)):String.valueOf(now1.get(Calendar.DATE)));
	    String currDate = now1.get(Calendar.YEAR)+""+m2+""+d2;
		try
		{
			String csDetail[] = getCsDetail(idEmp);
			db = DBManagerFactory.create155DBManager();
			sql=" select a.position_name ,b.* "+
				" from position a,TRACK_NOTIFICATION_RESUME b  "+
				" where a.id_emp=b.id_emp "+
				" and a.id_position=b.id_position "+
				" and a.id_emp in ("+idEmp+") "+
				" and b.id in ("+idIns+") "+
				" order by a.position_name	";
				
		    db.createPreparedStatement(sql);
		    db.executeQuery();
		    int oldTotal = 0;
		    int totalAll = 0;
		    int sumResume = 0;
		    int totalOnlineDay = 0;
		    
		    List<HashMap<Object, Object>> dataBeans = new ArrayList<HashMap<Object,Object>>();
		    while(db.next())
		    {
		    	
		    	totalOnlineDay = (dateDiff(db.getString("online_date"),currDate))+1;
		    	oldTotal +=((db.getInt("total_resume")-db.getInt("resume_added")));
		    	totalAll+=db.getInt("total_resume");
		    	sumResume+=db.getInt("resume_added");
		    	HashMap<Object, Object> bean = new HashMap<Object, Object>();
		    	bean.put("position_name", db.getString("position_name"));
		    	bean.put("resume_added", db.getInt("resume_added"));
		    	bean.put("resume_old", (db.getInt("total_resume")-db.getInt("resume_added")));
		    	bean.put("total_resume", db.getInt("total_resume"));
		    	bean.put("totalOnlineDay", totalOnlineDay);
		    	dataBeans.add(bean);
		    	
		    }
		/*-----------------------------*/
		    body="<!DOCTYPE html> "
		    +"<html lang='en'> "
		    +"<head> "
		    +"<meta charset='utf-8'> "
		    +"<style type='text/css'> "
		    +"body{ "
		    +"margin:0px; "
		    +"padding:0px; "
		    +"} "
		    +"a:link { "
		    +"color: #0060cf; "
		    +"text-decoration: none "
		    +"} "
		    +"text-decoration: none "
		    +"} "
		    +"a:hover { "
		    +"text-decoration: underline; "
		    +"} "
		    +"</STYLE> "
		    +"</head> "
		    +"<body> "
		    +"<table  bgcolor='#d9d9d9'  cellspacing='0' cellpadding='0'  align='center' border='0'  width='100%' style='font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif;font-size: 16px;padding:10px'> "
		    +"<tr> "
		    +"<td> "
		    +"<table cellspacing='0' cellpadding='0'  align='center' border='0' bgcolor='#ffffff' width='600'  style='border-bottom: 2px solid #e7e7e7;'> "
		    +"<tr align='center' valign='top'> "
		    +"<td  width='100%'> "
		    +"<table width='600' align='center' cellspacing='0'> "
		    +"<tr> "
		    +"<td width='100%' align='right'> "
		    +"<img src='http://mail.jobinthailand.com/content/superMatch/images/header_email.jpg' width='100%'> "
		    +"</td> "
		    +"</tr> "
		    +"</table> "
		    +"</td> "
		    +"</tr> "
		/*----------------------thai message-----------------------*/
		    +"<tr align='center' valign='top'> "
		    +"<td  width='100%'> "
		    +"<table width='500' align='center' cellspacing='0'> "
		    +"<tr> "
		    +"<td width='100%' align='left'> "
		    +"<br> "
		    +"<strong>สวัสดีค่ะ</strong><br><br> "
		    +"บริษัทของท่าน "+com_name+" ลงประกาศงานกับ JOBTOPGUN.com ไว้ทั้งหมด "+totalJob+" ตำแหน่งงาน ";
		    if(sumResume>0)
	    	{
	    		body+="ซึ่งเราอยากแจ้งให้ทราบว่ามีผู้สมัครส่งใบสมัครเข้ามาใหม่ให้ท่านแล้วเป็นจำนวน  "+sumResume+" ใบ จากทั้งหมด "+countPosition+" ตำแหน่งงาน ";
	    	}
		    body+="ในช่วงเวลาที่ท่านไม่สะดวกเข้ามาเช็คในระบบ Super E-Recruit ระหว่างวันที่ "+datePastFormat+"-"+currFormat+"<br><br> "
		    +"ท่านสามารถล็อคอินเข้าสู่ระบบ Super E-Recruit เพื่อดูใบสมัครได้ "
		    +"<a href='http://mail2.jobtopgun.com/jtg/jsk/jobfield/responseMailSupE.php?id_emp="+idEmp+"&idUser="+idUser+"&flag=1&maildate="+currDate+"'><span style='font-size:18px'><b>ที่นี่</b></span><br></a> อาจมีคนที่ท่านกำลังรออยู่ก็ได้ค่ะ รีบหน่อยนะคะเพราะผู้สมัครอาจได้ "
		    +"งานอื่นไปก่อน <br><br> "
		    +"</td> "
		    +"</tr> "
		    +"<tr> "
		    +"<td width='100%' align='right'> "
		    +"<table width='100%' align='center' cellpadding='8' cellspacing='0'  style='border:1px solid #d9d9d9;font-size:14px'> "
		    +"<tr> "
		    +"<th  width='180' bgcolor='#1e91cf' style='color: #fff;border-right:1px solid #f1f1f2;'>ตำแหน่งงาน</th> "
		    +"<th bgcolor='#1e91cf' style='color: #fff;border-right:1px solid #f1f1f2;'>ใบสมัครที่เพิ่มใหม่</th> "
		    +"<th bgcolor='#1e91cf' style='color: #fff;border-right:1px solid #f1f1f2;'>ใบสมัครเดิม</th> "
		    +"<th bgcolor='#1e91cf' style='color: #fff;border-right:1px solid #f1f1f2;'>ใบสมัครทั้งหมด</th> "
		    +"</tr> ";
		    /*------------------------show position----------------------*/
		    for(int a=0; a<dataBeans.size(); a++)
		    {
		    	HashMap<Object, Object> data = dataBeans.get(a);
		    	body+="<tr> "
					    +"<td style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'>"+data.get("position_name")+"<br>"+"(ออนไลน์มาแล้ว "+data.get("totalOnlineDay")+" วัน)</td> "
					    +"<td align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'>"+data.get("resume_added")+"</td> "
				    	+"<td align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'>"+data.get("resume_old")+"</td> "
					    +"<td align='center' style='border-bottom:1px solid #d9d9d9;'>"+data.get("total_resume")+"</td> "
					    +"</tr> ";
		    }
		    body+=	"<tr> "+
	    			"<td bgcolor='#f1f1f2' align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'><strong>รวม</strong></td> "+
	    			"<td bgcolor='#f1f1f2' align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'><strong>"+sumResume+"</strong></td> "+
	    			"<td bgcolor='#f1f1f2' align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'><strong>"+oldTotal+"</strong></td> "+
	    			"<td bgcolor='#f1f1f2' align='center' style='border-bottom:1px solid #d9d9d9;'><strong>"+totalAll+"</strong></td> "+
	    			"</tr> ";
		    /*------------------------------------------------------------*/
		    body+="</table> "
		    +"</td> "
		    +"</tr> "
		    +"<tr> "
		    +"<td width='100%' align='left'> "
		    +"<br> "
		    +"ระบบ Super E-Recruit จะช่วยให้ท่านทำงานได้ง่ายกว่าเดิม แบ่งเบางาน HR แบบไม่มีใครทำได้ เช่น "
		    +"<ul> "
		    +"<li> "
		    +"<strong>สกรีนใบสมัครที่ตรงให้ท่านเลย</strong> ไม่ต้องเสียเวลากดดูใบสมัครทุกใบ "
		    +"</li> "
		    +"<li> "
		    +"ช่วยท่าน<strong>ค้นหาคนในตำแหน่งงานยาก เฉพาะทาง หรือต้องการด่วน</strong>ด้วย Super Search "
		    +"</li> "
		    +"<li> "
		    +"จัดการตำแหน่งงานที่ท่านลงประกาศได้ด้วยตัวเอง "
		    +"</li> "
		    +"</ul> "
		    +"หากมีข้อแนะนำติชมหรือสอบถาม สามารถแจ้งได้ทุกเมื่อ เรายินดีให้บริการท่านเสมอค่ะ<br><br> "
		    +"หาคน ได้คนที่ JOBTOPGUN.com<br><br> "
		    +"<span style='font-family: serif;'> "
		    +"Best Regards,<br> ";
		    if(csDetail!=null)
		    {
		    		body+=csDetail[0]+" "+csDetail[1]+"<br> ";
		    		if(csDetail[2]!=null)
		    		{
		    			body+=csDetail[2]+"<br> ";
		    		}
		    		body+="0-2711-1685 ";
		    		if(csDetail[3]!=null)
		    		{
		    			body+="ต่อ ("+csDetail[3]+")";
		    		}
		    				
		    		body+=	"<br><a href='mailto:"+csDetail[0]+"@topgunthailand.com' target='_top'>"+csDetail[0]+"@topgunthailand.com</a></span> ";
		    				
		    }
		    body+="<br><br><br> "
		    +"<span style='font-size: 14px'>ขออภัยหากอีเมลนี้รบกวนท่าน ถ้าไม่ต้องการรับข่าวสารจาก Jobtopgun <a href='http://www.topgunthailand.com/jtg/data/cancelSupE.php?id="+idUser+"&user="+email+"'>คลิกที่นี่</a> "
		    +"<br><br><br> "
		    +"</td> "
		    +"</tr> "
		    +"</table> "
		    +"</td> "
		    +"</tr> "
		  /*----------------------eng message-----------------------*/
		    +"<tr align='center' valign='top'> "
		    +"<td  width='100%'> "
		    +"<table width='500' align='center' cellspacing='0'> "
		    +"<tr> "
		    +"<td width='100%' align='left'> "
		    +"<hr> "
		    +"<br><br> "
		    +"Hello<br><br> "
		    +"Your company, "+com_name+", has posted a total of "+totalJob+" job announcements on "
		    +"JOBTOPGUN.com. ";
		    if(sumResume>0)
	    	{
	    		body+="We would like to inform you that there have been  "+sumResume+" new applications for "+countPosition+" positions ";
	    	}
		    body+= "during the period of "+datePastFormat+"-"+currFormat+" which you may have had irregular access "
		    +"to Super E-Recruit. You can log in to Super E-Recruit to view applications "
		    +"<a href='http://mail2.jobtopgun.com/jtg/jsk/jobfield/responseMailSupE.php?flag=1&id_emp="+idEmp+"&idUser="+idUser+"&maildate="+currDate+"'><span style='font-size:18px'><b>here</b></span></a>. "
		    +"There might be some people waiting for you, so hurry up in case they find other opportunities first "
		    +"<br><br> "
		    +"</td> "
		    +"</tr> "
		    +"<tr> "
		    +"<td width='100%' align='right'> "
		    +"<table width='100%' align='center' cellpadding='8' cellspacing='0'  style='border:1px solid #d9d9d9;font-size:14px'> "
		    +"<tr> "
		    +"<th  width='180' bgcolor='#1e91cf' style='color: #fff;border-right:1px solid #f1f1f2;'>Position</th> "
		    +"<th bgcolor='#1e91cf' style='color: #fff;border-right:1px solid #f1f1f2;'>New applications</th> "
		    +"<th bgcolor='#1e91cf' style='color: #fff;border-right:1px solid #f1f1f2;'>Old applications</th> "
		    +"<th bgcolor='#1e91cf' style='color: #fff;border-right:1px solid #f1f1f2;'>Total received</th> "
		    +"</tr> ";
		    /*------------------------show position----------------------*/

		    for(int a=0; a<dataBeans.size(); a++)
		    {
		    	HashMap<Object, Object> data = dataBeans.get(a);
		    	body+="<tr> "
					    +"<td style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'>"+data.get("position_name")+"<br>"+"(been online for "+data.get("totalOnlineDay")+" days)</td> "
					    +"<td align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'>"+data.get("resume_added")+"</td> "
				    	+"<td align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'>"+data.get("resume_old")+"</td> "
					    +"<td align='center' style='border-bottom:1px solid #d9d9d9;'>"+data.get("total_resume")+"</td> "
					    +"</tr> ";
		    }
		    body+=	"<tr> "+
	    			"<td bgcolor='#f1f1f2' align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'><strong>Total</strong></td> "+
	    			"<td bgcolor='#f1f1f2' align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'><strong>"+sumResume+"</strong></td> "+
	    			"<td bgcolor='#f1f1f2' align='center' style='border-right:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;'><strong>"+oldTotal+"</strong></td> "+
	    			"<td bgcolor='#f1f1f2' align='center' style='border-bottom:1px solid #d9d9d9;'><strong>"+totalAll+"</strong></td> "+
	    			"</tr> ";
		    /*------------------------------------------------------------*/
		    body+="</table> "
		    +"</td> "
		    +"</tr> "
		    +"<tr> "
		    +"<td width='100%' align='left'> "
		    +"<br> "
		    +"Super E-Recruit makes your job easier and facilitates HR work like no other can "
		    +"<ul> "
		    +"<li> "
		    +"Screens for matching applications only. No need to spend time sifting through them all! "
		    +"</li> "
		    +"<li> "
		    +"Helps you fill difficult, urgent, or specialist positions with Super Search. "
		    +"</li> "
		    +"<li> "
		    +"Lets you manage the job posts you made by yourself. "
		    +"</li> "
		    +"</ul> "
		    +"If you have any questions or feedback, please feel free to contact us at anytime. "
		    +"We are always happy to hear from you.<br><br> "
		    +"Find the right person for the right job at JOBTOPGUN.com<br><br> "
		    +"<span style='font-family: serif;'> "
		    +"Best Regards,<br> ";
		    if(csDetail!=null)
		    {
		    		body+=csDetail[0]+" "+csDetail[1]+"<br> ";
		    		if(csDetail[2]!=null)
		    		{
		    			body+=csDetail[2]+"<br> ";
		    		}
		    		body+="0-2711-1685 ";
		    		if(csDetail[3]!=null)
		    		{
		    			body+="ext. ("+csDetail[3]+")";
		    		}
		    				
		    		body+=	"<br><a href='mailto:"+csDetail[0]+"@topgunthailand.com' target='_top'>"+csDetail[0]+"@topgunthailand.com</a></span> ";
		    				
		    }
		    body+="<br><br><br> "
		    +"<span style='font-size: 14px'>If you would like to stop receiving updates from Jobtopgun, <a href='http://www.topgunthailand.com/jtg/data/cancelSupE.php?id="+idUser+"&user="+email+"'>click here.</a> "
		    +"<br><br><br> "
		    +"</td> "
		    +"</tr> "
		    +"</table> "
		    +"</td> "
		    +"</tr> "
		    +"</table> "
		    +"</td> "
		    +"</tr>  "	  
		    +"</table> "
		    +"<img src='http://mail2.jobtopgun.com/jtg/jsk/jobfield/responseMailSupE.php?id_emp="+idEmp+"&idUser="+idUser+"&flag=2&maildate="+currDate+"' width='1px' height='1px'/> "
		    +"</body> "
		    +"</html> ";
		    
		    
		}
		catch(Exception ex){ex.printStackTrace();}
		finally
		{
			db.close();
		}
		return body;
	}
	
	public static  int getTotalResumeAdded(int idEmp,String idIns)
	{
		DBManager db=null;
		int result = 0;
		String sql="";
		try
		{
			db= DBManagerFactory.create155DBManager();
			sql=" select sum(a.resume_added) as totalAdd "+
				"	from track_notification_resume a  "+
				"	where a.id_emp="+idEmp+" and a.id in ("+idIns+") ";
		    db.createPreparedStatement(sql);
		    db.executeQuery();
		    if(db.next())
		    {
		    	result = db.getInt("totalAdd");
		    }
		}
		catch(Exception ex){ex.printStackTrace();}
		finally
		{
			db.close();
		}
		return result;
	}
	
	
	public static  String[] getCsDetail(int idEmp)
	{
		DBManager db=null;
		String[] result = new String[4];
		String sql="";
		try
		{
			db= DBManagerFactory.create155DBManager();
			sql=" select a.name,a.lastname,a.title,a.phone_ext from topgun_name a ,call b "
				+	" where b.id=? "
				+ 	" and a.username=b.cs "
				+ 	" and a.del_flag=0 ";
		    db.createPreparedStatement(sql);
		    db.setInt(1, idEmp);
		    db.executeQuery();
		    if(db.next())
		    {
		    	result[0] = db.getString("name");
		    	result[1] = db.getString("lastname");
		    	result[2] = db.getString("title");
		    	result[3] = db.getString("phone_ext");
		    }
		}
		catch(Exception ex){ex.printStackTrace();}
		finally
		{
			db.close();
		}
		return result;
	}
	
	public static int dateDiff(String startDate,String endDate)
	{
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd"); 
		
    	try {
			Date startdate = df.parse(startDate);
			Date enddate = df.parse(endDate);
			
			long diff = enddate.getTime() - startdate.getTime();
			
			int dayDiff = (int) (diff / (24 * 60 * 60 * 1000));
			
			return dayDiff;
			
		} 
    	catch(Exception ex){ex.printStackTrace();}
		return 0;
	}
	
	
	
	public static void insertHRNotiResumeSummary(int total_com,int total_sent,int total_com_dt,int total_user)
	{
		String sql_insert="	INSERT INTO HR_NOTI_RESUME_SUMMARY(MAIL_DATE,TOTAL_COM,SENT_COM,TOTAL_COM_AVAI_DT,TOTAL_USER) VALUES(SYSDATE,?,?,?,?) ";
		DBManager ds = null;
		try
		{
			ds = DBManagerFactory.create155DBManager();
			ds.createPreparedStatement(sql_insert);
			ds.setInt(1, total_com);
			ds.setInt(2, total_sent);
			ds.setInt(3, total_com_dt);
			ds.setInt(4, total_user);
			ds.executeUpdate();
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
	
	public static  int getTotalComAvaiPkg(String type)
	{
		DBManager db=null;
		int result = 0;
		String sql="";
		String sWhere = "";
		if(type.equals("DT"))
		{
			sWhere = " and  c.type_  in ('DT') ";
		}
		else
		{
			sWhere = " and  c.type_  in ('A', 'AE', 'AH', 'D', 'DE', 'DH') ";
		}
		try
		{
			
			db= DBManagerFactory.create155DBManager();
			sql=" select count(a.id_emp) as total "+
				" from employer a , emp_pkg b, call c "+
				" where a.id_emp=b.id_emp and  b.id_emp = c.id and "+
				" 	to_char(SYSDATE, 'YYYYMMDD') between  b.start_date and b.end_date "+
				"	and  trim(a.email) = '-' " +sWhere;
		    db.createPreparedStatement(sql);
		    db.executeQuery();
		    if(db.next())
		    {
		    	result = db.getInt("total");
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
