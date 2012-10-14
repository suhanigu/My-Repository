import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class powof {

	
	public static int power_rec(int x, int n){
		int i = 0;
		int result = x;
		if(n==0)
			return 1;
		if(n==1)
			return x;
		
//		while(i<Math.log(n)){
			result = power_rec(result, n/2);
//			i++;
//		}
		
		if(n%2==0)
			result = result*result;
		else
			result = result*result*x;
		
		return result;
	}
	
	public static double power_iter(double x, int n){
		double odd = 1.0;
		boolean sign = false;
		double num = x;
		int pow = n;
		if(x<0){
			num = -x;
			sign = true;
		}
		if(n<0){
			n = -n;
		}
		
		System.out.println("number: " + x + "power: " + pow);
		while(n>1){
			num = num*num;
			n = n/2;
			System.out.println("num: " + num + "n: " + n);
		}
		if(pow%2!=0 && sign==true)
			num = num*x;
		if(pow%2!=0)
			num = num*x;
		
		System.out.println("Result is: " + num);
		
		if(pow<0)
			return 1/num;
		else
			return num;
	}
	
	public static void main(String[] args) throws IOException{
//		String str = "";
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		str = br.readLine();
		int res = power_rec(10,5);
//		double res = power_iter(10,-5);
		System.out.println(res);
		
	}
}
