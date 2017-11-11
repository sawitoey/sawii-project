package hrNotificationResume;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {

	
		   public static void main(String args[]) {
		      String a[] = { "suthasinee@nsk.com" };
		      String b[] = { "suthasinee@nsk.com","siriporn-h@nsk.com" };
		      List list = new ArrayList(Arrays.asList(a));
		      list.addAll(Arrays.asList(b));
		      Object[] c = list.toArray();
		      System.out.println(Arrays.toString(c));
		   }
		
}
