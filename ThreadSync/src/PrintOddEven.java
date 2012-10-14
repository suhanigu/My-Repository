
public class PrintOddEven extends Thread{
		public Object printnum = new Object();
		public int flag = 0;
		public boolean isEven;
		public SyncClass sync;
		
		public PrintOddEven(SyncClass syncobj,boolean oe){
			isEven = oe;
			sync = syncobj;
		}
		
		public void run(){
			int j = (isEven==true?2:1);
			while(j<=10){
				if(isEven == true)
					sync.printeven(j);
				else
					sync.printodd(j);
				j=j+2;
			}
		}

		public static void main(String[] args){
			SyncClass syncO = new SyncClass();
			
			PrintOddEven eventhread = new PrintOddEven(syncO, true);
			PrintOddEven oddthread = new PrintOddEven(syncO, false);
			
			oddthread.start();
			eventhread.start();
		}
}

