import java.util.ArrayList;


public class StackOps {
	
	static class Stack{
		ArrayList<Integer> stack = new ArrayList<Integer>();
		int top;
		
		Stack(){
			top = 0;
		}
		
		public void push(int n){
			stack.add(n);
			top++;
		}
		public int pop(){
			int value = stack.remove(top-1);
			top--;
			return value;
		}
	}
	
	static Stack init_stack = new Stack();
	
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
		
		int val = recursive_push(stack);
		reverse_rec(stack);
		stack.push(val);
		
	}
	
	public static int recursive_push(Stack stack){
		int temp = stack.pop();
		if(stack.top==0)
			return temp;
		else{
		int val = recursive_push(stack);
		stack.push(temp);
		return val;
		}
		
	}
	
	public static void main(String[] args){
		createstack(init_stack);
		printstack(init_stack);
		createstack(init_stack);
		reverse_rec(init_stack);
		printstack(init_stack);
	}
}
