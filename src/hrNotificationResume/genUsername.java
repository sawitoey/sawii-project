package hrNotificationResume;

public class genUsername {

	public static void main(String[] args) {
		
		  String u1 = "sawii7";
		  String p1 = "sawii7";
		  
		  u1=Base64Coder.encodeString(u1);
		  for (int i = 0;i < 3 ; i++){   
		   p1 = Base64Coder.encodeString(p1);
		  }
		  System.out.println("username is "+u1);
		  System.out.println("password is"+p1);
		    System.out.println("password is"+p1);
		  // Test

	}

}
