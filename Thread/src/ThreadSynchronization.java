
public class ThreadSynchronization {
	
	public static class deadlock extends Thread{
		
		public Object lock1 = new Object();
		public Object lock2 = new Object();
		
		public void method1(){
			synchronized(lock1){
				delay(500);
				System.out.println("method1: " + Thread.currentThread().getName());
				synchronized(lock2){
					System.out.println("Executing method1");
				}
			}
		}
		
		public void method2(){
			synchronized(lock2){
				delay(500);
				System.out.println("method2: " + Thread.currentThread().getName());
				synchronized(lock1){
					System.out.println("Executing method2");
				}
			}
		}
		
		public void delay(long timeinmillisec){
			try{
				Thread.sleep(timeinmillisec);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		public void run(){
			method1();
			method2();
		}
	}
	
	public static void main(String[] args){
		deadlock thread1 = new deadlock();
		deadlock thread2 = new deadlock();
		
		thread1.start();
		thread2.start();
	}
}
