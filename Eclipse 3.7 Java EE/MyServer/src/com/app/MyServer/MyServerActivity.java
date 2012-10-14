package com.app.MyServer;

import com.app.MyServer.MyOwnContentProvider;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.*;

import java.util.LinkedList;
import java.util.Random;

import com.app.MyServer.R;
import com.app.MyServer.R.id;
import com.app.MyServer.R.layout;

public class MyServerActivity extends Activity {
   ServerSocket serversock = null;
   String cltmsg = "";
   Thread myCommsThread = null;
   Thread clickCommsThread = null;
   Thread mysequencer_thread = null;
   private Button bt;
   private Button bt_total;
   private Button btnormal;
   private TextView tv;
   protected static final int MESG_ID = 0x1337;
   
   
   public int SERVERPORT = 10000;
   private Socket socket;
   public int client_no = 4;
   private String serverIpAddress = "10.0.2.2";
   
   public int[] REDIRECTED_SERVERPORT = new int[client_no];
   
   String own_port;
   Integer port; 
   Integer id ;
   String own_id ;
   int msg_id = 0;
   int order = 0;
   int test2_flag = 0;
   int counter_test2 = 0;
   String strhbq=null;
   
   boolean islocked = false;
   
   String str = null;
   
  // LinkedList<String> totalq = new LinkedList<String>();
   Queue<String> totalq = new LinkedList<String>();
   LinkedList<String> causalq = new LinkedList<String>();
   Queue<String> sequencerq = new LinkedList<String>();
   Queue<String> holdbackq = new LinkedList<String>();
   LinkedList<String> seqdeliverq = new LinkedList<String>();
   int[] causal_check = new int[client_no];
   
   
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
       
       StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();  
 	   StrictMode.setThreadPolicy(policy);
 	  int i=0;
 	  while(i<client_no)
 	   {
 		   causal_check[i]=0;
 		   i++;
 	   }
 	  
 	   REDIRECTED_SERVERPORT[0] = 11108;
 	   REDIRECTED_SERVERPORT[1] = 11112;
 	   REDIRECTED_SERVERPORT[2] = 11116;
 	   REDIRECTED_SERVERPORT[3] = 11120;
 	   //REDIRECTED_SERVERPORT[4] = 11124;

 	   
 	   
 	   super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bt = (Button) findViewById(R.id.myButtoncausal);
        bt_total = (Button) findViewById(R.id.myButtontotal);
        btnormal = (Button) findViewById(R.id.myButton);
        tv = (TextView) findViewById(R.id.myTextView);
 
        
      tv.setText("Server listening on port: " + SERVERPORT);
          
      TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
      own_port = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
      port = Integer.parseInt(own_port);
      id = (port - 5554)/2;
      own_id = Integer.toString(id);
      
     getContentResolver().update(MyOwnContentProvider.CONTENT_URI,null,null,null);
     
//     Message mesg = new Message();
//     mesg.what = MESG_ID;
//     myUpdateHandler.sendMessage(mesg);
      
          if(port == 5554)
          {
        	  tv.setText("AVD port: "+port);
        	  this.mysequencer_thread = new Thread(new sequencer_thread());
              this.mysequencer_thread.start();
              this.myCommsThread = new Thread(new CommsThread());
              this.myCommsThread.start();
          }
          else
          {
        	  islocked = true;
        	  System.out.println("port is: "+port);
        	  this.myCommsThread = new Thread(new CommsThread());
              this.myCommsThread.start();
          }
      
      bt.setOnClickListener(new OnClickListener() {
    	  Thread clickCommsThread = null;
          public void onClick(View v) {
        	  test2_flag=1;
        	  new OutGoingMessages_causal().execute("");
          }
       });
      
      bt_total.setOnClickListener(new OnClickListener() {
    	  Thread clickCommsThread = null;
    	  
          public void onClick(View v) {
        	  test2_flag=0;
        	  new OutGoingMessages_total().execute("");
          }
       });
      
      btnormal.setOnClickListener(new OnClickListener() {
    	//  Thread clickCommsThread = null;
    	  
          public void onClick(View v) {
        	  test2_flag=0;
        	  new OutGoingMessages().execute("");
          }
       });
   }
   @Override
   protected void onStop() {
        super.onStop();
        try {
                serversock.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
   }
  
   Handler myUpdateHandler = new Handler() {
	    public void handleMessage(Message mesg) {
	    	
	    	System.out.println("Message handler called");
	    	int totalq_size = totalq.size();
	    	int causalq_size = causalq.size();
	    	int s = 0;
	    	int totalq_count=0;
	    	int position;
	            	while(totalq_size>0){
	            		totalq_size--;
	            		String str_recovery = totalq.peek();
	       				  System.out.println("Total queue not empty: " + str_recovery);
	            		for(s=0;s<causalq_size;s++)
	            		{
	            			if(str_recovery.equals(causalq.get(s))){
	            				totalq_count++;
	            				position=s;
	            			
	            		System.out.println("found" + str_recovery + "in causal queue");
  						  totalq.remove();
  						  causalq.remove(position);
  						  causalq_size--;
  						  //totalq_size--;  
  						
  						StringTokenizer steq2 = new StringTokenizer(str_recovery,"-");
              		   String incoming_msg = steq2.nextToken();
              		   Integer incoming_port = Integer.parseInt(steq2.nextToken());
              		   Integer incoming_id = Integer.parseInt(steq2.nextToken());
              		   String msgno = steq2.nextToken();
  						  
  						 cltmsg += incoming_msg;
	            	
//	            	Cursor resultCursor = getContentResolver().query(
//	            			MyTodoContentProvider.CONTENT_URI,
//	            		    null,
//	            		    "10",
//	            		    null,
//	            		    null
//	            		);
  						 System.out.println("Printing to AVD.........................");
	                    TextView tv = (TextView) findViewById(R.id.myTextView);
	                    tv.setText(cltmsg);
	                    ContentValues keyValue = new ContentValues();
						String mykey = Integer.toString(incoming_id) + "." + causal_check[incoming_id]; 
	                    keyValue.put(OwnTable.PROVIDER_KEY,mykey);
	                    keyValue.put(OwnTable.PROVIDER_VALUE,incoming_msg); 
 						 Uri newUri = getContentResolver().insert(
 		            		    MyOwnContentProvider.CONTENT_URI,
 		            		    keyValue
 		            		);
  					  }
	            		}
	            		
	            		if(totalq_count>0){
	            			while(totalq_count>0){
	            		totalq_size--;
	            		totalq_count--;
	            		}
	            		}
//	            		else
//	            			totalq_size--;
	            		
	            super.handleMessage(mesg);
	    }
	    }
	};


	static class theLock extends Object {
	   }
	   static public theLock lockObject = new theLock();
	   
   class CommsThread implements Runnable {
	   Thread clickCommsThread = null;
        public void run() {
                Socket sock = null;
                try {
                        serversock = new ServerSocket(SERVERPORT );
                } catch (IOException e) {
                        e.printStackTrace();
                }
                
                while (true) {
                	sock=null;
                        try {
                                if (sock == null)
                                        sock = serversock.accept();
                                       
                                BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                                str = input.readLine();
                               String str_copy = str;
                                int k = 0;
                      		   int inmsg = 0;
                      		 int m=0;
                      		int chk=0;
                      		Message mesg = new Message();
                            mesg.what = MESG_ID;
                            String[] incoming_msgno = new String[client_no];
                            String[] incoming_msgno1 = new String[client_no];
                            String orderid = null;
                            String causal_msg=null;
                            String causal_msgid=null;
                            String test2_identifier = null;
                            String orderid_dup=null;
                            
                            //synchronized(lockObject){
                            System.out.println("server received data: " + str);
                     		   StringTokenizer st = new StringTokenizer(str,"-");
                     		   String incoming_msg = st.nextToken();
                     		   StringTokenizer st_causal = new StringTokenizer(incoming_msg,":");
                     		   int count_token = st_causal.countTokens();
                     		   Integer incoming_port = Integer.parseInt(st.nextToken());
                     		   Integer incoming_id = Integer.parseInt(st.nextToken());
                     		   String msgno = st.nextToken();
                     		   
                     		  synchronized(lockObject){
                     		   if(st.hasMoreTokens()){
                     		   orderid = st.nextToken();
                     		   if(orderid==orderid_dup)
                     			   orderid=null;
                     		   orderid_dup=orderid;
                     		   st = null;
                     		   System.out.println("order id extracted on first stage is: "+ orderid);
                     		   }
                     		   else{
                     			   orderid=null;}
                     		  }
                            
                     		   System.out.println("msgno extracted on first stage is: " + msgno + "------" + own_id);   
                     		   
                     		  if(orderid==null){
                     			 if(port == 5554){
                                     sequencerq.add(str_copy);
                                     System.out.println("Added to sequencer");
                    			  }
                     		   StringTokenizer st1 = new StringTokenizer(msgno,".");
                     		   while(st1.hasMoreTokens()){
                     		   incoming_msgno[m]=st1.nextToken(); 
                     		   m++; }
                     		   
                     		  if(st_causal.hasMoreTokens() && (count_token >= 2))
                    		   {
                     			  System.out.println("Entering test2");
                     			  test2_identifier = st_causal.nextToken();
                     			  if(test2_identifier.equals("$")){
                     				 System.out.println("Entering test2 actual");
                    		   causal_msg = st_causal.nextToken();
                    		   causal_msgid = st_causal.nextToken();
                    		   System.out.println("causal_msgid: " + causal_msgid);
                    		   Integer causal_id = (Integer.parseInt(causal_msg) - 5554)/2;
                    		   
                    		   
                    		  if(Integer.parseInt(own_id)==((causal_id % client_no)+1)%client_no)
                    		   {
                    			  if(counter_test2 <1){
                    			   System.out.println("Executing test2 ");
                    			  new OutGoingMessages_causal().execute("");
                    			  counter_test2++;
                    		   }
                    			  else
                    			  counter_test2 = 0;
                    		   }
                     			  }
                    		   }
                     			  
                     		  
                    
                     		  int causal_vector=0;
                     		 System.out.println("Causal Vector is: ");
                     		  for(causal_vector=0;causal_vector<client_no;causal_vector++)
                     		  {
                     			  System.out.println(causal_check[causal_vector]);
                     		  }
                                System.out.println("Causal check of peer id "+incoming_id+"is"+causal_check[incoming_id]+"but message contains id"+incoming_msgno[incoming_id]);
                               String noorder_recovery = incoming_msg + "-" + incoming_port + "-" + incoming_id + "-" + msgno ;
                                 seqdeliverq.add(noorder_recovery);
                                 
                            	   if(incoming_id == Integer.parseInt(own_id))
                            	   {
                            		   
                            		   System.out.println("Incoming id equals ownid " + own_id);
                					      
                         						  causalq.add(noorder_recovery);
                         						  myUpdateHandler.sendMessage(mesg);
                         				  }
                            	   
                            	   else{
                            		   //start:
                     		   if(((causal_check[incoming_id]+1) == (Integer.parseInt(incoming_msgno[incoming_id]))))
                     		   {
                     			  k=0;
                     			   while(k<client_no){
                     			   if(k!=incoming_id)
                     			   {
                     				   inmsg = Integer.parseInt(incoming_msgno[k]);
                     				   if(causal_check[k] >= inmsg){
                     					   chk++;
                     				   }
                     				
                     			   }
                     			  k++;                     			  
                     			   }
                     			   
                     			   if(chk==(client_no-1)){
                     				  System.out.println("Causal condition satisfied ");
                     				// synchronized (lockObject) {
                     				  causal_check[incoming_id]++; 
                     				 
                     						  causalq.add(noorder_recovery);
                     						 myUpdateHandler.sendMessage(mesg);
                     				  
                     			   
                     			   while(!holdbackq.isEmpty()){
                     				   System.out.println("hold back queue peeked");
                     				   String hd = holdbackq.peek();
                     				  StringTokenizer steq = new StringTokenizer(hd,"-");
                           		   String incoming_msg1 = steq.nextToken();
                           		   Integer incoming_port1 = Integer.parseInt(steq.nextToken());
                           		   Integer incoming_id1 = Integer.parseInt(steq.nextToken());
                           		   String msgno1 = steq.nextToken();                 		   
                           		   StringTokenizer steq1 = new StringTokenizer(msgno1,".");
                           		   m=0;
                           		   while(steq1.hasMoreTokens()){
                           		   incoming_msgno1[m]=steq1.nextToken(); 
                           		   m++; }
                           		   k = 0;
                           		   inmsg = 0;
                           		   chk=0;
                           		   
                     				if(((causal_check[incoming_id1]+1) == (Integer.parseInt(incoming_msgno[incoming_id1]))))
                          		   {
                     					
                       				 noorder_recovery = incoming_msg1 + "-" + incoming_port1 + "-" + incoming_id1 + "-" + msgno1 ;
                          			  k=0;
                          			   while(k<client_no){
                          			   if(k!=incoming_id1)
                          			   {
                          				   inmsg = Integer.parseInt(incoming_msgno1[k]);
                          				   if(causal_check[k] >= inmsg){
                          					   chk++;
                          				   }
                          			   }
                          			  k++;                     			  
                          			   }
                          			   
                          			   if(chk==(client_no-1)){
                          				 strhbq = holdbackq.remove(); 
                          				  System.out.println("Removing from holdbackqueue: " + strhbq);
                          				// synchronized (lockObject) {
                          				  causal_check[incoming_id1]++; 
                          				  //}
                         				  System.out.println("Causal condition satisfied for hbq");
                          				 causalq.add(noorder_recovery);
                          				myUpdateHandler.sendMessage(mesg);
                 				  }
                          		   }
                     				else
                     					break;
                     		   }
                     			   }
                     			  else{
                         			  holdbackq.add(str_copy);
                         			 System.out.println("Adding into holdbackqueue causal vector is not less: " + str_copy);
                         			 //break;
                                	   }
                     		   }
                     		   else{
                     			  holdbackq.add(str_copy);
                     			 System.out.println("Adding into holdbackqueue: " + str_copy);
                            	   }
                            	   }
                     		   }
                     		  
                     		   else
                     		   {
                     			   orderid=null;
                     			   System.out.println("total algo: " + str_copy);
                    				  StringTokenizer steq = new StringTokenizer(str_copy,"-");
                          		   String incoming_msg1 = steq.nextToken();
                          		   String incoming_port1 = steq.nextToken();
                          		   String incoming_id1 = steq.nextToken();
                          		   String msgno1 = steq.nextToken();
                          		   String str_recovery = incoming_msg1 + "-" + incoming_port1 + "-" + incoming_id1 + "-" + msgno1 ;
                          		 
                        						  totalq.add(str_recovery);
                        						  myUpdateHandler.sendMessage(mesg);
                        						  
                    		   }
                     		  System.out.println("Message processing complete");
                 
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
    }
   
   
   class sequencer_thread implements Runnable {
        public void run() {
        	
        	while(true){
 		   
 		   if(!sequencerq.isEmpty()){
 			   
 			   String total_msg = sequencerq.remove();

 		   int clt_loop;
 		   String multicast_msg = total_msg + "-" + Integer.toString(order);
 		   
 		   for(clt_loop=0;clt_loop<client_no;clt_loop++){
 		   Socket socket = null;
 		 
 		   
 		   InetAddress serverAddr;
 		try {
 			serverAddr = InetAddress.getByName(serverIpAddress);
 			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT[clt_loop]);

 	          
 	          str = multicast_msg;
               PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
               out.println(str);
               
               
               Log.d("Client", "Message sent by client in sequencer------"+own_id);
               socket.close();
            } catch (UnknownHostException e) {
               tv.setText("Error1");
               e.printStackTrace();
            } catch (IOException e) {
               tv.setText("Error2");
               e.printStackTrace();
            } catch (Exception e) {
               tv.setText("Error3");
               e.printStackTrace();
            }
 		   
 		   } 
 		   order++;
 		   }
        }
        }
   }
     
   
   private class OutGoingMessages extends AsyncTask<String, Void, String> {
	   
	   @Override
	   protected String doInBackground(String... params) {
		   
		   int clt_loop;
		   causal_check[Integer.parseInt(own_id)]++;
		   int causal;
           String str1=null;
           for(causal=0;causal<client_no;causal++)
           {
         	  if(str1==null)
         		  str1 = Integer.toString(causal_check[causal]);
         	  else
         	  str1 = str1 + "." + Integer.toString(causal_check[causal]);
           }
		   for(clt_loop=0;clt_loop<client_no;clt_loop++){
		   Socket socket = null;
		 
		   String multicast_msg = "-" + own_port + "-" + own_id;
		   InetAddress serverAddr;
		try {
			serverAddr = InetAddress.getByName(serverIpAddress);
			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT[clt_loop]);
	          //System.out.println("Connected to server"+own_id);
              EditText et = (EditText) findViewById(R.id.EditText01);
              String str = et.getText().toString();
              str = str + multicast_msg + "-" + str1;
              PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
              out.println(str);
              
              Log.d("Client", "Message sent by client");
              socket.close();
           } catch (UnknownHostException e) {
              tv.setText("Error1");
              e.printStackTrace();
           } catch (IOException e) {
              tv.setText("Error2");
              e.printStackTrace();
           } catch (Exception e) {
              tv.setText("Error3");
              e.printStackTrace();
           }
		   }
	     return null;
	   }
	  
	   @Override
	   protected void onPostExecute(String result) {
	     // execution of result of Long time consuming operation
	   }
	  
	   @Override
	   protected void onPreExecute() {
	   // Things to be done before execution of long running operation. For example showing ProgessDialog
	   }

	   @Override
	   protected void onProgressUpdate(Void... values) {
	       // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
	    }
	 }
   
   
private class OutGoingMessages_total extends AsyncTask<String, Void, String> {
	   
	   @Override
	   protected String doInBackground(String... params) {
		   
		   test2_flag=0;
		   int clt_loop;
		   int msgcnt=0;
		   while(msgcnt<client_no){
			   int causal;
	             
			  // synchronized (lockObject) {
		   causal_check[Integer.parseInt(own_id)]++;
		   String str1=null;
           for(causal=0;causal<client_no;causal++)
           {
         	  if(str1==null)
         		  str1 = Integer.toString(causal_check[causal]);
         	  else
         	  str1 = str1 + "." + Integer.toString(causal_check[causal]);
           }
		   for(clt_loop=0;clt_loop<client_no;clt_loop++){
		   Socket socket = null;
		 
		   String multicast_msg = "-" + own_port + "-" + own_id;
		   InetAddress serverAddr;
		try {
			serverAddr = InetAddress.getByName(serverIpAddress);
			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT[clt_loop]);
              String str = own_port + ":" + Integer.toString(causal_check[Integer.parseInt(own_id)]);
              	
              
              str = str + multicast_msg + "-" + str1;
              PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
              out.println(str);
              
              Log.d("Client", "Message sent by client in total");
              socket.close();
           } catch (UnknownHostException e) {
              tv.setText("Error1");
              e.printStackTrace();
           } catch (IOException e) {
              tv.setText("Error2");
              e.printStackTrace();
           } catch (Exception e) {
              tv.setText("Error3");
              e.printStackTrace();
           }
		   }
		   msgcnt++;}
	     return null;
	   }
	  
	   @Override
	   protected void onPostExecute(String result) {
	     // execution of result of Long time consuming operation
	   }
	  
	   @Override
	   protected void onPreExecute() {
	   // Things to be done before execution of long running operation. For example showing ProgessDialog
	   }

	   @Override
	   protected void onProgressUpdate(Void... values) {
	       // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
	    }
	 }

private class OutGoingMessages_causal extends AsyncTask<String, Void, String> {
	   
	   @Override
	   protected String doInBackground(String... params) {
		   
		   test2_flag=1;
		   int clt_loop;
		   try {
				Thread.currentThread().sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   causal_check[Integer.parseInt(own_id)]++;
		   int causal;
           String str1=null;
           for(causal=0;causal<client_no;causal++)
           {
         	  if(str1==null)
         		  str1 = Integer.toString(causal_check[causal]);
         	  else
         	  str1 = str1 + "." + Integer.toString(causal_check[causal]);
           }
		   for(clt_loop=0;clt_loop<client_no;clt_loop++){
		   Socket socket = null;
		 
		   String multicast_msg = "-" + own_port + "-" + own_id;
		   InetAddress serverAddr;
		try {
			serverAddr = InetAddress.getByName(serverIpAddress);
			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT[clt_loop]);
	          String str = "$" + ":" + own_port + ":" + Integer.toString(causal_check[Integer.parseInt(own_id)]);
           	
          
           str = str + multicast_msg + "-" + str1;
           PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
           out.println(str);
           
           Log.d("Client", "Message sent by client in causal");
           socket.close();
        } catch (UnknownHostException e) {
           tv.setText("Error1");
           e.printStackTrace();
        } catch (IOException e) {
           tv.setText("Error2");
           e.printStackTrace();
        } catch (Exception e) {
           tv.setText("Error3");
           e.printStackTrace();
        }
		   }
		   //counter_test2=0;
	     return null;
	   }
	  
	   @Override
	   protected void onPostExecute(String result) {
	     // execution of result of Long time consuming operation
	   }
	  
	   @Override
	   protected void onPreExecute() {
	   // Things to be done before execution of long running operation. For example showing ProgessDialog
	   }

	   @Override
	   protected void onProgressUpdate(Void... values) {
	       // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
	    }
	 }
}
   
   
   
   
   

