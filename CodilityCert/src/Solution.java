
public class Solution {

	public static void caesar_cipher(StringBuilder str, int n){
		if(str.length() == 0)
			return;
		int i = 0;
		while(i<str.length()){
			if((str.charAt(i)>='a' && str.charAt(i)<='z')){ 
				int x = (int)(str.charAt(i)) + n;
				System.out.println("Entering loop 1 " + x);
				if(x > 122)
					str.setCharAt(i, (char)((int)str.charAt(i) + n - 26));
				else
					str.setCharAt(i, (char)((int)str.charAt(i) + n));
			}
			else if((str.charAt(i)>='A' && str.charAt(i)<='Z')){
				int x = (int)(str.charAt(i)) + n;
				System.out.println("Entering loop 2 " + x);
				if(x > 90)
					str.setCharAt(i, (char)((int)str.charAt(i) + n - 26));
				else
					str.setCharAt(i, (char)((int)str.charAt(i) + n));
			}
			i++;
		}
		
		System.out.println("String after cipher: " + str);
	}
	
	public static void main(String[] args){
		StringBuilder string = new StringBuilder("Zoozle");
		caesar_cipher(string, 1);
	}
}
