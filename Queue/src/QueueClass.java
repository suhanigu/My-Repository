
public class QueueClass {

	
	static class circularqueue_usingarrays{
		int[] queue;
		int front;
		int rear;
		int size;
		
		circularqueue_usingarrays(int n){
			queue = new int[n];
			front = 0;
			rear = 0;
			size = 0;
		}
		
		public void add(int item){
			if(size ==  queue.length)
				return;
			queue[rear] = item;
			size++;
			rear = (rear+1)% queue.length;
		}
		
		public int peek(){
			return queue[front];
		}
		
		public int poll(){
			if(size == 0)
				return 0;
			size--;
			int n = queue[front];
			front = (front+1)%queue.length;
			return n;
		}
		
		public void print(){
			int i = 0;
			while(i < queue.length){
				System.out.println(queue[i]);
				i++;
			}
		}
	}
	
	
	public static void main(String[] args){
		circularqueue_usingarrays queue = new circularqueue_usingarrays(10);
		int i = 1;
		while(i<=10){
			queue.add(i);
			i++;
		}
		int p = queue.poll();
		int n = queue.peek();
		System.out.println("poll: " + p + "peek: " + n);
		queue.add(11);
		queue.add(12);
		System.out.println("peek: " + queue.peek());
		queue.print();
	}
	
}
