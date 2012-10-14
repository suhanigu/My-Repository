import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Solution {
  public static int magnitudePole( int[] A ) {
	  int i = 0;
	  int max = A[0];
	  int index = 0;
	  
	  while(i<A.length){
		  if(A[i]>=max){
			  max = A[i];
			  index = i;
		  }
		  i++;
	  }
	  for(int j = index;j<A.length;j++){
		  if(max > A[j]){
			  System.out.println("Return -1");
			  return -1;
		  }
	  }
	  System.out.println("print i: " + index);
	return 0;
    
  }
  
  public static void main(String[] args){
	  int[] array = new int[100000];
	  
	  String inputstring = "";
	  System.out.println("Enter Number: ");
	  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	  try{
		  inputstring = br.readLine();
	  }catch (IOException ioe) {
	         System.out.println("IO error trying to read your input!");
	         System.exit(1);
	      }
	  char[] arr = inputstring.toCharArray();
	  for (int i=0; i < arr.length; i++) {
	        array[i] = arr[i] - 48;
	    }
	  int i = magnitudePole(array);
	  System.out.println("Return value: " + i);
  }
}