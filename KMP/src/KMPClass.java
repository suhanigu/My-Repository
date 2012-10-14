
public class KMPClass {
	
	private static String text;
	private static String pattern;
	private static int[] failure;
	private static int matchPoint;
	
	public static void failurefunction(){
		
		int j = 0;
		int i;
		failure = new int[pattern.length()];
		for(i=1;i<pattern.length();i++){
			while(j>0 && pattern.charAt(j)!=pattern.charAt(i)){
				j = failure[j-1];
			}
			if(pattern.charAt(j) == pattern.charAt(i)){
				j++;
			}
			failure[i] = j;
		}
	}
	
	  public static boolean match() {
		    int j = 0;
		    if (text.length() == 0) return false;
		    
		    for (int i = 0; i < text.length(); i++) {
		      while (j > 0 && pattern.charAt(j) != text.charAt(i)) {
		        j = failure[j - 1];
		      }
		      if (pattern.charAt(j) == text.charAt(i)) { j++; }
		      if (j == pattern.length()) {
		        matchPoint = i - pattern.length() + 1;
		        return true;
		      }
		    }
		    return false;
		  }
	
	public static void main(String[] args){
		pattern = "ababac";
		text = "kjlkjklerabcdablkereabcdabferew";
		failurefunction();
		boolean result = match();
		int i = 0;
		while(i<failure.length){
			System.out.println(failure[i]);
			i++;
		}
		System.out.println("First occurrence at: " + matchPoint);
	}
}
