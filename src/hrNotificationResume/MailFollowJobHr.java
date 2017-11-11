package hrNotificationResume;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

import com.topgun.database.DBManager;
import com.topgun.database.DBManagerFactory;
public class MailFollowJobHr {


	public static void main(String[] args) {
		String bodymail = "";
		String contactEmail = "";
		String email = "";
		String[] arrEmail = null;
		String[] arrContactEmail = null;
		int total_buy = 0;
		int total_use = 0;
		String enddate = "";
		String company_name = "";
		String cs = "";
		int counter = 0;
		String sql_main =" select distinct *  from "+
						" (  "+
						"	select  a.id_emp, "+
						"	(s_use_job+fr_p_use +  f_use_job+fr_s_use+voc_use_job+fr_v_use+fr_sme_use+fr_exp10_use ) as total_use, "+
						" 	(s_num_job+fr_p_num+f_num_job+fr_s_num+voc_num_job+fr_v_num+fr_sme_num+fr_exp10_num) as total_buy, "+
						"	a.start_date, a.end_date,d.company_name,lower(d.EMAIL) as EMAIL ,lower(d.CONTACTEMAIL) as CONTACTEMAIL,e.cs "+
						"	from emp_pkg a, ext_emp_pkg  b,  ( SELECT   MAX (no_) AS mx_no, id_emp FROM   emp_pkg GROUP BY   id_emp ) c, "+
						" 	employer d,call e "+
						"	where a.no_=b.no_ and b.no_=c.mx_no "+
						"		and a.id_emp=c.id_emp and c.id_emp=d.id_emp and d.id_emp=e.id "+
						"		and e.type_ in ('A','AE','AH','D','DE','DH','J') "+
						"		and end_date > to_char(SYSDATE,'YYYYMMDD') "+
						"		and (s_num_job + fr_p_num  > 0 or  f_num_job+fr_s_num > 0 ) "+
						"		and ((d.EMAIL is not null and d.EMAIL <> '-') or ((d.CONTACTEMAIL is not null and d.CONTACTEMAIL <> '-'))) "+
						//" and a.id_emp=2806 "+
						" )  "+
						" where total_buy>total_use ";
		//System.out.println(sql_main);
		DBManager db=null;
		try
		{
			db = DBManagerFactory.create155DBManager();
			db.createPreparedStatement(sql_main);
			db.executeQuery();
			while(db.next())
			{
				bodymail = "";
				contactEmail="";
				bodymail = "";
				email = "";
				total_buy = 0;
				total_use = 0;
				enddate = "";
				company_name = "";
				cs = "";
				contactEmail = db.getString("contactemail");
				email = db.getString("email");
				total_buy = db.getInt("total_buy");
				total_use = db.getInt("total_use");
				enddate = db.getString("end_date");
				company_name = db.getString("company_name");
				cs = db.getString("cs");
				if(db.getString("end_date")!= null && !db.getString("end_date").equals(""))
				{
					enddate=db.getString("end_date").substring(6,8)+"/"+db.getString("end_date").substring(4,6)+"/"+db.getString("end_date").substring(0,4);
				}
				if(email!=null && !email.equals("-"))
				{
					arrEmail = email.split(",");
				}
				if(contactEmail!=null && !contactEmail.equals("-"))
				{
					arrContactEmail = contactEmail.split(",");
				}
				if(arrEmail!=null && arrContactEmail!=null)
				{
					Set<String>listmail=new HashSet<String>();
					listmail.addAll(Arrays.asList(arrEmail));
					listmail.addAll(Arrays.asList(arrContactEmail));
					List<String> lists = new ArrayList<String>(listmail);
					Collections.sort(lists);
					for(int i=0 ;i<lists.size(); i++)
					{
						counter++;
						bodymail = contentMail(total_buy,total_use,enddate,company_name,cs,counter);
						sendmail(cs,lists.get(i),bodymail);
					}
				}
				else if(arrEmail!=null && arrContactEmail==null)
				{
					for(int i=0 ;i<arrEmail.length; i++)
					{
						counter++;
						bodymail = contentMail(total_buy,total_use,enddate,company_name,cs,counter);
						sendmail(cs,arrEmail[i],bodymail);
					}
				}
				else if(arrEmail==null && arrContactEmail!=null)
				{
					for(int i=0 ;i<arrContactEmail.length; i++)
					{
						counter++;
						bodymail = contentMail(total_buy,total_use,enddate,company_name,cs,counter);
						sendmail(cs,arrContactEmail[i],bodymail);
					}
				}
				
			}
			System.out.println("total sent : "+counter);
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
	
	public static String contentMail(int total_buy,int total_use,String enddate,String company_name,String cs,int counter)
	{
		String body = "";
		String csDetail[] = getCsDetail(cs);
			body="<!DOCTYPE html>"
					+"<html>"
					+"<head>"
					+"<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />"
					+"<title>Email Follow Jobs</title>"
					+"<meta name='viewport' content='width=device-width, initial-scale=1.0'/>"
					+"</head>"
					+"<style type='text/css'>"
					+"body{"
					+"margin: 0;"
					+"padding: 0;"
					+"background-color:#f1f2f3;"
					+"}"
					+"</style>"
					+"<body >"
					+"<table  bgcolor='#d9d9d9'  cellspacing='0' cellpadding='0'  align='center' border='0'  width='100%' style='font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif;font-size: 16px !important;padding:10px'>"
					+"<tr>"
					+"<td>"
					+"<table cellspacing='0' cellpadding='0'  align='center' border='0' bgcolor='#ffffff' width='600'  style='border-bottom: 2px solid #e7e7e7;'>"
					+"<tr align='center' valign='top'>"
					+"<td  width='100%' colspan='3'>"
					+"<table width='600' align='center' cellspacing='0'>"
					+"<tr>"
					+"<td width='100%' align='center'>"
					+"<img src='http://mail.jobinthailand.com/content/superMatch/images/header_email.jpg' width='100%'>"
					+"</td>"
					+"</tr>"
					+"</table>"
					+"</td>"
					+"</tr>"
					+"<tr align='center' valign='top'>"
					+"<td width='40' style='min-width: 40px'></td>"
					+"<td  width='510'>"
					+"<table width='510' align='center' cellspacing='0'>"
					+"<tr>"
					+"<td width='510' align='left' style='min-width: 510px'>"
					+"<span style='word-wrap: normal;width:510px'>"		    				
					+"<br>"
					+"<span style='font-size: 18px'>สวัสดีค่ะ</span><br><br>"
					+"บริษัท "+company_name+" ยังมีโควต้าการลงประกาศตำแหน่งงานกับ JOBTOPGUN<br> ในแพ็คเกจปัจจุบันอีก ";
			if(total_buy==99999)
			{
				body+="<span style='color:#0093d8'>ไม่จำกัด จาก แพ็คเกจ Unlimited </span>";
			}
			else
			{
				body+="<span style='color:#0093d8'> "+(total_buy-total_use)+" ตำแหน่ง จากทั้งหมด "+total_buy+" ตำแหน่ง </span>";
			}
				body+="ซึ่งคุณสามารถ<span style='color:#0093d8'> <br>ลงประกาศงานได้ถึงวันที่ "+enddate+"</span><br><br>"
					+"เราขอ<span style='color:#0093d8'>แนะนำให้คุณใช้โควต้าลงประกาศงานกับ JOBTOPGUN เพื่อหาคน <br>ให้ได้คนในช่วงนี้ </span>ซึ่งเป็นช่วงฤดูกาลหางาน เพราะไม่ว่าจะเป็นกลุ่มคนที่มี<br>ประสบการณ์หรือนักศึกษาจบใหม่ก็กำลังมองหางานกันอยู่ <span style='color:#0093d8'>"
					+"ส่งผลให้ตอนนี้มี<br>ใบสมัครเฉลี่ยอยู่ที่ 50-60 ใบ/ตำแหน่ง โอกาสที่คุณจะได้คนจึงเร็วกว่า</span><br><br>"
					+"คุณสามารถลงประกาศงานกับ JOBTOPGUN ได้ทันที ผ่านซอฟต์แวร์ <br>Super E-Recruit ของคุณเลย "
					+"<a href=\"http://superemployer.jobtopgun.com?utm_source=newsletter&utm_medium=email&utm_campaign=superE_raise_job_click\" target=\"_blank\">คลิกที่นี่ </a>หรือสอบถามการใช้งานและการประกาศ<br>ตำแหน่งงาน กรุณาติดต่อฝ่ายบริการลูกค้าได้ที่"
					+" 0-2711-1685"
					+"</span>"
					+"</td>"
					+"</tr>"
					+"<tr>"
					+"<td width='510' align='left'  style='min-width: 510px'>"
					+"<br><br>"
					+"<span style='font-family: serif;'>"
					+"หาคน ได้คนที่ JOBTOPGUN.com<br><br>"									
					+"Best Regards,<br>";
				if(csDetail!=null)
			    {
			    		body+=""+csDetail[0]+" "+csDetail[1]+"<br> ";
			    		if(csDetail[2]!=null)
			    		{
			    			body+=""+csDetail[2]+"<br> ";
			    		}
			    		body+="0-2711-1685 ";
			    		if(csDetail[3]!=null)
			    		{
			    			body+=" ต่อ ("+csDetail[3]+")";
			    		}
			    				
			    		body+="<br><a href='mailto:"+csDetail[0]+"@topgunthailand.com' target='_top'>"+csDetail[0]+"@topgunthailand.com</a></span> ";
			    				
			    }
				body+="</span>"
					+"</td>"
					+"</tr>"
					+"<tr>"
					+"<td width='100%' align='right'>"
					+"<table width='100%' align='center' cellpadding='0' cellspacing='0'  style='border:1px solid #ffffff;font-size:10px;color:#ffffff'>"
					+"<tr>"
					+"<th  width='180' bgcolor='#ffffff' style='color: #fff;border-right:1px solid #ffffff;'>&nbsp;</th>"
					+"<th bgcolor='#ffffff' style='color: #fff;border-right:1px solid #ffffff;'>&nbsp;</th>"
					+"<th bgcolor='#ffffff' style='color: #fff;border-right:1px solid #ffffff;'>&nbsp;</th>"
					+"<th bgcolor='#ffffff' style='color: #fff;border-right:1px solid #ffffff;'>&nbsp;</th>"
					+"</tr>"
					+"<tr>"
					+"<td style='border-right:1px solid #ffffff;border-bottom:1px solid #ffffff;'><br>&nbsp;</td>"
					+"<td align='center' style='border-right:1px solid #ffffff;border-bottom:1px solid #ffffff;'>&nbsp;</td>"
					+"<td align='center' style='border-right:1px solid #ffffff;border-bottom:1px solid #ffffff;'>&nbsp;</td>"
					+"<td align='center' style='border-bottom:1px solid #ffffff;'>&nbsp;</td>"
					+"</tr>"
					+"</table>"
					+"</td>"
					+"</tr>"
					+"</table>"
					+"</td>"
					+"<td width='40' style='min-width: 40px'></td>"
					+"</tr>"
					+"</table>"
					+"</td>"
					+"</tr>"
					+"</table>"
					+"<img src=\"http://www.google-analytics.com/collect?v=1&tid=UA-102207715-1&cid="+counter+"&t=event&ec=email&ea=open&el=noti_quota_job&cs=newsletter&cm=email&cn=superE_raise_job_open\">"
					+"</body>"
					+"</html>";
		
		return body;
	}
	
	public static  String[] getCsDetail(String cs)
	{
		DBManager db=null;
		String[] result = new String[4];
		String sql="";
		try
		{
			db= DBManagerFactory.create155DBManager();
			sql=" select a.name,a.lastname,a.title,a.phone_ext from topgun_name a  "
				+	" where a.username=? "
				+ 	" and a.del_flag=0 ";
		    db.createPreparedStatement(sql);
		    db.setString(1, cs);
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
	
	public static void sendmail(String cs,String mailto,String body)
	{
		MailManager mail=null;
		try
		{
			mail=new MailManager();
			mail.setSender(cs+"@topgunthailand.com");
			mail.setSubject("หาคนได้เร็วกว่าเดิม ลงประกาศงานเลยวันนี้!");
			//mail.setRecepient("winthai@topgunthailand.com");
			//mail.setRecepient("sawitree@topgunthailand.com");
			//mail.setCc("winthai3@gmail.com,hemmawan.w@gmail.com,sathiya@topgunthailand.com");
			mail.setRecepient(mailto);
			//System.out.println(mailto);
			mail.addHTML(body);
			mail.send();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}