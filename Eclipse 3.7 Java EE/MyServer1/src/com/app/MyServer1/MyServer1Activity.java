package com.app.MyServer1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.*;

import java.util.LinkedList;
import java.util.Random;

import com.app.MyServer1.R;
import com.app.MyServer1.R.id;
import com.app.MyServer1.R.layout;


public class MyServer1Activity extends Activity {
   ServerSocket serversock = null;
   String cltmsg = "";
   Thread myCommsThread = null;
   Thread clickCommsThread = null;
   Thread mysequencer_thread = null;
   private Button bt;
   private Button bt_total;
   private TextView tv;
   protected static final int MESG_ID = 0x1337;
 
   public int SERVERPORT = 10000;
   private Socket socket;
   public int client_no = 3;
   private String serverIpAddress = "10.0.2.2";
   
   public int[] REDIRECTED_SERVERPORT = new int[client_no];
   
   String own_port;
   Integer port; 
   Integer id ;
   String own_id ;
   int msg_id = 0;
   int order = 0;
   
   String str = null;
   
   LinkedList<String> totalq = new LinkedList<String>();
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
 	   REDIRECTED_SERVERPORT[1] = 11118;
 	   REDIRECTED_SERVERPORT[2] = 11116;
 		   
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bt = (Button) findViewById(R.id.myButtoncausal);
        bt_total = (Button) findViewById(R.id.myButtontotal);
        tv = (TextView) findViewById(R.id.myTextView);
 
      tv.setText("Server listening on port: " + SERVERPORT);
          
      TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
      own_port = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
      port = Integer.parseInt(own_port);
      id = (port - 5554)/2;
      own_id = Integer.toString(id);
     // tv.setText("Server listening on port: " + SERVERPORT+ " AVD running on port: "+port);
      
          if(port == 5554)
          {
        	  //System.out.println("port is: "+port);
        	  tv.setText("AVD port: "+port);
        	  this.mysequencer_thread = new Thread(new sequencer_thread());
              this.mysequencer_thread.start();
              this.myCommsThread = new Thread(new CommsThread());
              this.myCommsThread.start();
          }
          else
          {
        	  System.out.println("port is: "+port);
        	  this.myCommsThread = new Thread(new CommsThread());
              this.myCommsThread.start();
          }
      
      bt.setOnClickListener(new OnClickListener() {
    	  Thread clickCommsThread = null;
    	  
          public void onClick(View v) {
        	  new OutGoingMessages().execute("");
          }
       });
      
      bt_total.setOnClickListener(new OnClickListener() {
    	  Thread clickCommsThread = null;
    	  
          public void onClick(View v) {
        	  
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
	            switch (mesg.what) {
	            case MESG_ID:
	                    TextView tv = (TextView) findViewById(R.id.myTextView);
	                    tv.setText(cltmsg);
	                    break;
	            default:
	                    break;
	            }
	            super.handleMessage(mesg);
	    }
	};



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
                           
                     		   StringTokenizer st = new StringTokenizer(str,":");
                     		   String incoming_msg = st.nextToken();
                     		   Integer incoming_port = Integer.parseInt(st.nextToken());
                     		   Integer incoming_id = Integer.parseInt(st.nextToken());
                     		   String msgno = st.nextToken();
                     		   if(st.hasMoreTokens()){
                     		   orderid = st.nextToken(); 
                     		  System.out.println("order id is: "+ orderid);}
                     		  
                     		   System.out.println(msgno + "------" + own_id);           
                     		   
                     		  if(orderid==null){
                     			  if(port == 5554)
                                      sequencerq.add(str_copy);
                                  else
                                  seqdeliverq.add(str_copy);
                     			   
                     		   StringTokenizer st1 = new StringTokenizer(msgno,".");
                     		   while(st1.hasMoreTokens()){
                     		   incoming_msgno[m]=st1.nextToken(); 
                     		   m++; }
                     		   
                                System.out.println("causal check------" + own_id);
                                System.out.println("Causal check of peer id "+incoming_id+"is"+causal_check[incoming_id]+"but message contains id"+incoming_msgno[incoming_id]);
                               
                            	   if(incoming_id == Integer.parseInt(own_id))
                            	   {
                            		   System.out.println("Printing message Causal------"+ own_id);
                      				  //causal_check[incoming_id]++; 
                					      cltmsg = incoming_msg;
                                       myUpdateHandler.sendMessage(mesg);
                            	   }
                            	   else{
                            		   start:
                     		   if(((causal_check[incoming_id]+1) == (Integer.parseInt(incoming_msgno[incoming_id]))))
                     		   {
                     			   
                     			   System.out.println("Satisfying causal check------" + own_id);
                     			   while(k<client_no){
                     			   if(k!=incoming_id)
                     			   {
                     				   inmsg = Integer.parseInt(incoming_msgno[k]);
                     				   if(causal_check[k] >= inmsg){
                     					   chk++;
                     				   }
                     				
                     			   }
                     			  k++;	   
                   				  System.out.println("Value of k is: "+k);
                     			   }
                     			   k=0;
                     			   System.out.println("Value of check is: "+chk);
                     			   if(chk==(client_no-1)){
                     				   System.out.println("Printing message causal again------" + own_id);
                     				  causal_check[incoming_id]++; 
                     				  if(totalq.isEmpty())
                     				  causalq.add(str_copy);
                     				  else
                     				  {
                     					  if(totalq.contains(str_copy))
                     					  {
                     						  int position = totalq.indexOf(str_copy);
                     						  totalq.remove(position);
                     						 cltmsg = incoming_msg;
                                           myUpdateHandler.sendMessage(mesg);  
                     					  }
                     				  }
                     			   }
                     			   if(!holdbackq.isEmpty()){
                     				   String hd = holdbackq.peek();
                     				  StringTokenizer steq = new StringTokenizer(hd,":");
                           		   String incoming_msg1 = steq.nextToken();
                           		   Integer incoming_port1 = Integer.parseInt(steq.nextToken());
                           		   Integer incoming_id1 = Integer.parseInt(steq.nextToken());
                           		   String msgno1 = steq.nextToken();
                           		   System.out.println("causal vector again" + msgno1 + "------" + own_id);                     		   
                           		   StringTokenizer steq1 = new StringTokenizer(msgno1,".");
                           		   m=0;
                           		   while(steq1.hasMoreTokens()){
                           		   incoming_msgno[m]=steq1.nextToken(); 
                           		   m++; }
                           		   k = 0;
                           		   inmsg = 0;
                           		   chk=0;
                           		   
                     			   if(incoming_id1 == incoming_id){
                     				  if(((causal_check[incoming_id]+1) == (Integer.parseInt(incoming_msgno[incoming_id])))){
                     				   str = holdbackq.remove();                    				   
                            		   break start;
                     			   } }
                     					   
                     		   }
                     		   }
                     		   else{
                     			  holdbackq.add(str);
                            	   }
                            	   }
                     		   }
                     		  
                     		   else
                     		   {
                     			   //total message processing
                     			  if(!seqdeliverq.isEmpty()){
                     				  
                     				  System.out.println("Seq delivery queue not empty------"+ own_id);
                    				  StringTokenizer steq = new StringTokenizer(str_copy,":");
                          		   String incoming_msg1 = steq.nextToken();
                          		   String incoming_port1 = steq.nextToken();
                          		   String incoming_id1 = steq.nextToken();
                          		   String msgno1 = steq.nextToken();
                          		   String str_recovery = incoming_msg1 + ":" + incoming_port1 + ":" + incoming_id1 + ":" + msgno1;
                          		   String orderid1 = steq.nextToken();
                          		   System.out.println("causal vector again in total queue" + msgno1 + "------" + own_id );                     		   
                          		   StringTokenizer steq1 = new StringTokenizer(msgno1,".");
                          		   m=0;
                          		   while(steq1.hasMoreTokens()){
                          		   incoming_msgno1[m]=steq1.nextToken(); 
                          		   m++; }
                          		   k = 0;
                          		   inmsg = 0;
                          		   chk=0;
                     			   int indexof = 0;
                          		 if(seqdeliverq.contains(str_recovery))
                				  {
                          			 System.out.println("Recovered string found------"+own_id);
                					  indexof = seqdeliverq.indexOf(str_recovery);
                				  }
               				   String hd = seqdeliverq.get(indexof);
               				   seqdeliverq.remove(indexof);
               				   
           				  StringTokenizer steq2 = new StringTokenizer(hd,":");
                 		   String incoming_msg2 = steq2.nextToken();
                 		   Integer incoming_port2 = Integer.parseInt(steq2.nextToken());
                 		   Integer incoming_id2 = Integer.parseInt(steq2.nextToken());
                 		   String msgno2 = steq2.nextToken();
                 		   System.out.println("causal vector again in recovery queue" + msgno2 + "------" + own_id);                     		   
                 		   StringTokenizer steq3 = new StringTokenizer(msgno2,".");
                 		   m=0;
                 		   while(steq3.hasMoreTokens()){
                 		   incoming_msgno[m]=steq3.nextToken(); 
                 		   m++; }
                 		   k = 0;
                 		   inmsg = 0;
                 		   chk=0;
               				   
                          		   if(Integer.parseInt(incoming_id1) == incoming_id2)
                          		   {
                          			 if(causalq.isEmpty())
                          				 totalq.add(str_recovery); 
                        				  else
                        				  {
                        					  if(causalq.contains(str_recovery))
                        					  {
                        						  int position = causalq.indexOf(str_recovery);
                        						  causalq.remove(position);
                        						 cltmsg = incoming_msg1;
                                              myUpdateHandler.sendMessage(mesg);  
                        					  }
                        				  }
                          		 
                          		   }
                     		   } orderid = null;
                     		   }

                                
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
    }
   
   
   class sequencer_thread implements Runnable {
	   //Thread clickCommsThread = null;
        public void run() {
        	while(true){
        	System.out.println("Sequencer thread started------"+own_id);
 		   
 		   if(!sequencerq.isEmpty()){
 			   String total_msg = sequencerq.remove();

 		   int clt_loop;
 		   String multicast_msg = total_msg + ":" + Integer.toString(order);
 		   System.out.println("Sequencer queue not empty hence entered------"+own_id);
 		   totalq.add(multicast_msg);
 		   for(clt_loop=0;clt_loop<client_no;clt_loop++){
 			   if(clt_loop != Integer.parseInt(own_id)){
 		   Socket socket = null;
 		 
 		   
 		   InetAddress serverAddr;
 		try {
 			serverAddr = InetAddress.getByName(serverIpAddress);
 			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT[clt_loop]);
 	          System.out.println("Connected to server in sequencer------"+own_id);

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
 		   }} order++;
 		   }
        }
        }
   }
   
//private class sequencer extends AsyncTask<String, Void, String> {
//	   
//	   @Override
//	   protected String doInBackground(String... params) {
//		   System.out.println("Sequencer thread started");
//		   
//		   if(!sequencerq.isEmpty()){
//			   String total_msg = sequencerq.remove();
//
//		   int clt_loop;
//		   String multicast_msg = total_msg + ":" + Integer.toString(order);
//		   System.out.println("Sequencer queue not empty hence entered");
//		   totalq.add(multicast_msg);
//		   for(clt_loop=0;clt_loop<client_no;clt_loop++){
//			   if(clt_loop != Integer.parseInt(own_id)){
//		   Socket socket = null;
//		 
//		   
//		   InetAddress serverAddr;
//		try {
//			serverAddr = InetAddress.getByName(serverIpAddress);
//			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT[clt_loop]);
//	          System.out.println("Connected to server in sequencer");
//
//	          str = multicast_msg;
//              PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
//              out.println(str);
//              
//              Log.d("Client", "Message sent by client in sequencer");
//              socket.close();
//           } catch (UnknownHostException e) {
//              tv.setText("Error1");
//              e.printStackTrace();
//           } catch (IOException e) {
//              tv.setText("Error2");
//              e.printStackTrace();
//           } catch (Exception e) {
//              tv.setText("Error3");
//              e.printStackTrace();
//           }
//		   }} order++;
//		   }
//	     return null;
//	   }
//	  
//	   @Override
//	   protected void onPostExecute(String result) {
//	     // execution of result of Long time consuming operation
//	   }
//	  
//	   @Override
//	   protected void onPreExecute() {
//	   // Things to be done before execution of long running operation. For example showing ProgessDialog
//	   }
//
//	   @Override
//	   protected void onProgressUpdate(Void... values) {
//	       // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
//	    }
//	 }
//  
   
   private class OutGoingMessages extends AsyncTask<String, Void, String> {
	   
	   @Override
	   protected String doInBackground(String... params) {
		   
		   int clt_loop;
		   causal_check[Integer.parseInt(own_id)]++;
		   for(clt_loop=0;clt_loop<client_no;clt_loop++){
		   Socket socket = null;
		 
		   String multicast_msg = ":" + own_port + ":" + own_id;
		   InetAddress serverAddr;
		try {
			serverAddr = InetAddress.getByName(serverIpAddress);
			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT[clt_loop]);
	          System.out.println("Connected to server"+own_id);
              EditText et = (EditText) findViewById(R.id.EditText01);
              String str = et.getText().toString();
              
              System.out.println("Client causal vector contains"+causal_check[Integer.parseInt(own_id)]);
              	
              int causal;
              String str1=null;
              for(causal=0;causal<client_no;causal++)
              {
            	  if(str1==null)
            		  str1 = Integer.toString(causal_check[causal]);
            	  else
            	  str1 = str1 + "." + Integer.toString(causal_check[causal]);
              }
              str = str + multicast_msg + ":" + str1;
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
}