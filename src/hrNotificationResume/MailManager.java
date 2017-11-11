package hrNotificationResume;
import java.io.File;
import java.net.InetAddress;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


/**
* MailManager is a utility class for sending mail 
* support UTF-8 message encoding 
* and mail with file attachment
*  
* @author Mr.Anuwat Palasak
* @version 1.0 
*/
public class MailManager 
{
	
	private InternetAddress sender = new InternetAddress();
	private String recepient = null;
	private  String cc = null;
	private String subject = null;
	private Multipart multipart = null;
	private Properties prop=null;
	private Session session=null;
	private Message message=null;
	private String mailReturn="";
	private String senderName;
	
	String encode=null;
	
	
	
	public String getSenderName() 
	{
		return senderName;
	}

	public void setSenderName(String senderName) 
	{
		this.senderName = senderName;
	}

	public String getMailReturn() 
	{
		return mailReturn;
	}

	public void setMailReturn(String mailReturn) 
	{
		this.mailReturn = mailReturn;
	}

	/**
	* Constructor method 
	*/ 
	public MailManager() 
	{			
		this.multipart = new MimeMultipart();	
		sender = new InternetAddress();
		recepient = null;
		cc = null;
		subject = null;
		prop=null;
		session=null;
		message=null;					
	}
	
	public MailManager(String encode) 
	{			
		this.multipart = new MimeMultipart();	
		this.sender = new InternetAddress();
		this.recepient = null;
		this.cc = null;
		this.subject = null;
		this.prop=null;
		this.session=null;
		this.message=null;				
		this.encode=encode;
	}
	

	/**
	* Set recipient email address
	* @param recepient Email address of recipient
	*/ 	
	public void setRecepient(String recepient) 
	{
		this.recepient=recepient;
	}

	/**
	* Get recipient email address
	* @return Email address of recipient
	*/ 	
	public String getRecepient() 
	{
		return recepient;
	}

	/**
	* Set sender email address
	* @param sender Email address of sender
	*/ 	
	public void setSender(String sender) 
	{
		this.sender.setAddress(sender);
	}

	/**
	* Get sender email address
	* @return Email address of sender
	*/ 	
	public String getSender() 
	{
		return sender.getAddress();
	}
	/**
	* Get mail subject
	* @return Mail subject
	*/ 	
	public String getSubject() 
	{
		return subject;
	}

	/**
	* Set mail subject
	* @param subject mail subject
	*/ 	
	public void setSubject(String subject) 
	{
		this.subject = subject;
	}	

	/**
	* Get The "Cc" (carbon copy) recipients.
	* @return The "Cc" (carbon copy) recipients.
	*/ 	
	public String getCc() 
	{
		return this.cc;
	}

	/**
	* Set The "Cc" (carbon copy) recipients.
	* @param cc The "Cc" (carbon copy) recipients.
	*/ 	
	public void setCc(String cc) 
	{
		this.cc = cc;		
	}
	
	/**
	* Add mail body by use html format
	* @param html mail body with html format
	*/ 	
	public void addHTML(String html) throws Exception
	{
		BodyPart htmlPart = new MimeBodyPart();
		htmlPart.setDisposition(Part.INLINE);
		//htmlPart.setDisposition(Part.ATTACHMENT);
		if(this.encode!=null)
		{
			htmlPart.setContent(html, "text/html; charset="+this.encode);
		}
		else
		{
			htmlPart.setContent(html, "text/html; charset=utf-8");
		}
		this.multipart.addBodyPart(htmlPart);
	}
	/**
	* Add mail body by image, this image will show inline in mail message 
	* @param file image filename with real path  
	*/ 	
	public void addImage(String file) throws Exception
	{
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(file.substring(file.lastIndexOf(File.separator)+1));
		messageBodyPart.setDisposition(Part.INLINE);
		this.multipart.addBodyPart(messageBodyPart);
		
	}	
	/**
	* Add mail body by image, this image will show inline in mail message 
	* @param file image filename with real path  
	*/ 	
	public void addImage(String file,boolean isInline,String cid) throws Exception
	{
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(file.substring(file.lastIndexOf(File.separator)+1));
		if(isInline)
		{
			messageBodyPart.setDisposition(Part.INLINE);
		}
		messageBodyPart.setHeader("Content-ID","<"+cid+">");
		this.multipart.addBodyPart(messageBodyPart);
	}
	
	/**
	* Add file attachment for sending mail 
	* @param file attachment filename with real path  
	* @param fileName Name of attachment file for display in mail message 
	* 		 normally is attachment filename without path  
	*/ 	
	public void addAttachment(String file,String fileName) throws Exception
	{
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileName);	
		messageBodyPart.setDisposition(Part.ATTACHMENT);	
		this.multipart.addBodyPart(messageBodyPart);
	}
	
	public void addHTMLFile(String file,String fileName) throws Exception
	{
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setHeader("Content-Type","text/html; charset=\"UTF-8\"");
		messageBodyPart.setHeader("Content-Transfer-Encoding", "quoted-printable");
		messageBodyPart.setFileName(fileName);	
		messageBodyPart.setDisposition(Part.ATTACHMENT);	
		this.multipart.addBodyPart(messageBodyPart);
	}	
	/**
	* Send mail  
	*/ 	
	public int send()
	{
		int result=0;
		try
		{
			prop = new Properties();
			if((this.mailReturn!=null) && (!this.mailReturn.equals("")))
			{
				prop.put("mail.smtp.from", this.mailReturn);  
			}
			String ip=InetAddress.getLocalHost().getHostAddress();
			//if(ip.indexOf("10.1.1.")!=-1)
			{
				prop.put("mail.smtp.host","203.146.249.140");
			}
			session = Session.getInstance(prop);
			message = new MimeMessage(session);	
			
			if(this.senderName!=null && this.sender!=null)
			{
				message.setHeader("From",this.senderName+" <"+this.sender+">");
			}
			else if(this.sender!=null)
			{
				message.setFrom(this.sender);
			}			
			
			if(this.recepient!=null)
			{
				if(this.recepient!=null)
				{
					StringTokenizer token = new StringTokenizer(this.recepient,",");
					while(token.hasMoreTokens())
					{
						String buf = token.nextToken().trim();
						if(!buf.equals(""))
						{
							InternetAddress adr = new InternetAddress(buf);
							message.addRecipient(Message.RecipientType.TO,adr);
						}
					}
				}				
			}			
			if(this.subject!=null)
			{		
				if(this.encode!=null)
				{
					message.setSubject(MimeUtility.encodeText(this.subject, this.encode, "Q"));
				}
				else
				{
					message.setSubject(MimeUtility.encodeText(this.subject, "UTF-8", "Q"));
				}
			}						
			if(this.cc!=null)
			{
				StringTokenizer token = new StringTokenizer(this.cc,",");
				while(token.hasMoreTokens())
				{
					String buf = token.nextToken().trim();
					if(!buf.equals(""))
					{						
						InternetAddress adr = new InternetAddress(buf);
						message.addRecipient(Message.RecipientType.CC,adr);
					}
				}
			}
			message.setContent(this.multipart);
			Transport.send(message);
			System.out.println("mail: "+this.getRecepient());
			result=1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
}

