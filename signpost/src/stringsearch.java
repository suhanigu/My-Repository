import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;


public class stringsearch {

	 private static String text;
	  private static String pattern;
	  private static String replacewith;
	  private static int[] failure;
	  private static int matchPoint;
	  
	  
	  public static int findmatch(String textcopy) {
	    int j = 0;
	    if (textcopy.length() == 0) return -1;
	    
	    for (int i = 0; i < textcopy.length(); i++) {
	      while (j > 0 && pattern.charAt(j) != textcopy.toLowerCase().charAt(i)) {
	        j = failure[j - 1];
	      }
	      if (pattern.charAt(j) == textcopy.toLowerCase().charAt(i)) { j++; }
	      if (j == pattern.length()) {
	        matchPoint = i - pattern.length() + 1;
	        return matchPoint;
	      }
	    }
	    return -1;
	  }
	  
	  
	  private static void failurefunction() {
	    int j = 0;
	    failure = new int[pattern.length()];
	    failure[0] = 0;
	    for (int i = 1; i < pattern.length(); i++) {
	      while (j > 0 && pattern.charAt(j) != pattern.charAt(i)) { 
	        j = failure[j - 1];
	      }
	      if (pattern.charAt(j) == pattern.charAt(i)) { 
	        j++; 
	      }
	      failure[i] = j;
	    }
	  }

	  private static void replace(int pos){
		  int len = replacewith.length();
		  text = text.substring(0, pos) + replacewith + text.substring(pos+len);
	  }
	  
	  private static String reverse(){
		  int len = text.length() - 1;
		  int i = 0;
		  char[] c = text.toCharArray();
		  while(i<=len)
		  {
			  char temp = c[i];
			  c[i] = c[len];
			  c[len] = temp;
			  len--;
			  i++;
		  }
		  String rev = new String(c);
		  StringBuilder result = new StringBuilder();
		  StringTokenizer st = new StringTokenizer(rev," ");
		    while (st.hasMoreTokens()) {
		        StringBuilder curtoken = new StringBuilder(st.nextToken());
		        result.append(curtoken.reverse() + " ");
		    }
		    String resultstring = result.toString();
		    return resultstring;
		  
	  }
	  
	  private static void evaluation(){
		  String copy = text;
		  int i = text.length();
		  int j = 0;
		  int resultsum = 0;
		  int[] result = new int[text.length()/pattern.length()];
		  failurefunction();
		  while(i > 0)
		  {
		  result[j] = findmatch(copy);
		  resultsum += result[j];
		  if(result[j] != -1)
		  {
			if(result[j] + pattern.length() < copy.length()){
			if(result[j]!=0 && copy.charAt(result[j]-1)==' ' && copy.charAt(result[j] + pattern.length())==' ')
			{
				if(j!=0)
					replace(resultsum + pattern.length()*j);
				else
					replace(result[j]);
		  	}
		  	else if (result[j]==0 && copy.charAt(result[j] + pattern.length())==' ')
		  	{
				  if(j!=0)
					  replace(resultsum + pattern.length()*j);
				  else
					  replace(result[j]);
		  	}
			}
			else{
				if(result[j]!=0 && copy.charAt(result[j]-1)==' ')
				{
					if(j!=0)
						replace(resultsum + pattern.length()*j);
					else
						replace(result[j]);
			  	}
			  	else if (result[j]==0)
			  	{
					  if(j!=0)
						  replace(resultsum + pattern.length()*j);
					  else
						  replace(result[j]);
			  	}
			}
		  }
//		  System.out.println("Result: " + result[j]);
//		  System.out.println("Replaced text: " + text);
		  i = copy.length() - result[j] - pattern.length();
		  if(copy.length() > result[j] + pattern.length())
		  {
			  copy = copy.substring(result[j] + pattern.length());
//			  System.out.println("Copy: " + copy);
		  }
		  j++;
		  }
		  String revtext = reverse();
		  System.out.println("Replaced and Reversed text: " + revtext);
	  }
	  
	  public static void main(String[] args){
		  String inputstring = "";
		  System.out.println("Enter Text, pattern and replacement with ',' as identifier: ");
		  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		  try{
			  inputstring = br.readLine();
		  }catch (IOException ioe) {
		         System.out.println("IO error trying to read your input!");
		         System.exit(1);
		      }
		  
		  int i = 0;
		  String[] result = new String[3];
		  StringTokenizer st = new StringTokenizer(inputstring,",");
		    while (st.hasMoreTokens()) {
		        StringBuilder curtoken = new StringBuilder(st.nextToken());
		        result[i] = curtoken.toString();
		        i++;
		    }
		   
		  text = result[0];
		  pattern = result[1];
		  replacewith = result[2];
		  
		  evaluation();
	  }
}

