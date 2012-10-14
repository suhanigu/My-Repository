import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;


class Solution {
  public static int nesting ( String S ) {
	  Stack<Character> stack = new Stack<Character>();
	  int i = 0;
	  char[] str = S.toCharArray();
	  if(S == null)
		  return 1;
	  while(i<S.length()){
		  if(str[i] == '('){
			  stack.push('(');
		  }
		  else if(str[i] == ')'){
			  if(stack.isEmpty())
				  return 0;
			  else
				  stack.pop();
		  }
		  i++;
	  }
	  if(i==S.length() && stack.isEmpty())
		  return 1;
	  else
		  return 0;
  }
  
  public static void main(String[] args){
	  String inputstring = "";
	  System.out.println("Enter String: ");
	  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	  try{
		  inputstring = br.readLine();
	  }catch (IOException ioe) {
	         System.out.println("IO error trying to read your input!");
	         System.exit(1);
	  }
	  int result = nesting(inputstring);
	  System.out.println("Result is: " + result);
  }
}