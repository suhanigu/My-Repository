
public class SyncClass {
	static int flag=0;
	
	public synchronized void printeven(int n){
		while(flag==0){
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		System.out.printf("printeven: %d\n", n);
		flag=0;
		notifyAll();	
	}

public synchronized void printodd(int n){
		while(flag==1){
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		System.out.printf("printodd: %d\n", n);
		flag=1;
		notifyAll();	
	}
}
