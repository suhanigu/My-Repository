import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class StringClass {

	public static int count = 1;
	
	public static int isPalindrome(String str){
		int i = 0;
		while(i<str.length()/2){
			System.out.println(i);
			if(str.charAt(i) != str.charAt(str.length()-i-1))
				return 0;
			i++;
		}
		return 1;
	}
	
	public static void removechar(String str, char ch){
		int i = 0;
		while(i<str.length()){
			if(str.charAt(i) == ch)
				str = str.substring(0, i) + str.substring(i+1);
			i++;
		}
		System.out.println("Deleted character string is: " + str);
	}
	
	public static void permutation_recursive(String prefix, String str){
		int i = 0;
		if(str.length()<1){
			System.out.println(prefix + str);
			System.out.println(count);
			count++;
		}
		while(i<str.length()){
			permutation_recursive(prefix+str.charAt(i), str.substring(0,i) + str.substring(i+1));
			i++;
		}
	}
	
	public static void permutation_iterative(String str){
		
	}
	
	public static String reverse_recursive(String str){
		if(str==null || str.length()<=1)
			return str;
		
		return reverse_recursive(str.substring(1)) + str.charAt(0);
	}
	
	public static void reverse_words(String str){
		int i=0;
		int n = 0;
		int end,start;
		String word = "";
		String reversed = reverse_recursive(str);
		System.out.println("Reversed string is: " + reversed);
		while(n<reversed.length()){
			start = n;
			while(i<reversed.length() && reversed.charAt(i)!=' ' && reversed.charAt(i)!='\0'){
				word = word + reversed.charAt(i);
				i++;
				n++;
			}
			System.out.println("word: " + word);
			end = i;
			word = reverse_recursive(word);
			reversed = reversed.substring(0,start) + word + reversed.substring(end);
			System.out.println("reversal in process: " + reversed);
			word = "";
			i++;
			n++;
		}
		System.out.println("Reversed words: " + reversed);
	}
	
	public static String sort_String(String str){
	    int i = 0;
	    char key;
	    char[] st = str.toCharArray();
	    for(int j=2; j<str.length(); j++){
	        key = st[j];
	        i = j-1;
	            while(i>0 && ((int)st[i] > (int)key)){
	                st[i+1] = st[i];
	                i--;
	            }
	        st[i+1] = key; 
	    }
	    
	    return String.valueOf(st);
	}
	
	public static void main(String[] args){
		String str = null;
		System.out.println("Enter String: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			str = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = sort_String(str);
		System.out.println(res);
//		reverse_words(str);
//		String rev_str = reverse_recursive(str);
//		System.out.println(rev_str);
//		permutation_iterative(str);
//		permutation_recursive("",str);
//		removechar(str, 'a');
//		int res = isPalindrome(str);
//		System.out.println("IS Palindrome: " + res);
	}
}
