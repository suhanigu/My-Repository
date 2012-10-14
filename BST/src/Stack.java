import java.util.ArrayList;


public class Stack {
	
		ArrayList<Object> stack = new ArrayList<Object>();
		int top;
		
		Stack(){
			top = 0;
		}
		
		public void push(Object n){
			stack.add(n);
			top++;
		}
		public Object pop(){
			Object o = stack.remove(top-1);
			top--;
			return o;
		}
	
	
	public static void createstack(Stack stack){
		int i = 1;
		while(i<=10){
			stack.push(i);
			i++;
		}
	}
	
	public static void printstack(Stack stack){
		while(stack.top>0){
			System.out.println(stack.pop());
		}
	}
	
	public static void reverse_rec(Stack stack){
		
		if(stack.top==0)
			return;
		
		Object val = recursive_push(stack);
		reverse_rec(stack);
		stack.push(val);
		
	}
	
	public static Object recursive_push(Stack stack){
		Object temp = stack.pop();
		if(stack.top==0)
			return temp;
		else{
		Object val = recursive_push(stack);
		stack.push(temp);
		return val;
		}
	}
}
