package com.app.ServerClient;
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
import java.util.Random;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ServerClientActivity extends Activity {
   ServerSocket serversock = null;
   String cltmsg = "";
   Thread myCommsThread = null;
   Thread clickCommsThread = null;
   private Button bt;
   private TextView tv;
   protected static final int MESG_ID = 0x1337;
   public int SERVERPORT;
   private Socket socket;
   private String serverIpAddress = "10.0.2.2";
   public int REDIRECTED_SERVERPORT;
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
       
       StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();  
 	   StrictMode.setThreadPolicy(policy);
 	   
 	  Random randomGenerator = new Random();
	   for (int idx = 1; idx <= 2; ++idx){
	     int randomInt = randomGenerator.nextInt(1000);
	     if(idx==1)
	    	 SERVERPORT = randomInt + 7000;
	     else
	    	 REDIRECTED_SERVERPORT = randomInt + 7000;
	   }
 	   
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bt = (Button) findViewById(R.id.myButton);
        tv = (TextView) findViewById(R.id.myTextView);
        tv.setText("Server listening on port: " + SERVERPORT + "Client connecting on port: " + REDIRECTED_SERVERPORT);

          this.myCommsThread = new Thread(new CommsThread());
          this.myCommsThread.start();

      bt.setOnClickListener(new OnClickListener() {
    	  Thread clickCommsThread = null;
          public void onClick(View v) {
             new LongOperation().execute("");
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
        public void run() {
                Socket sock = null;
                try {
                        serversock = new ServerSocket(SERVERPORT );
                } catch (IOException e) {
                        e.printStackTrace();
                }
                while (true) {
                	sock=null;
                        Message mesg = new Message();
                        mesg.what = MESG_ID;
                        try {
                                if (sock == null)
                                {
                                        sock = serversock.accept();
                                   
                                }
                                BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                                String str = null;
                                str = input.readLine();
                                                              
                                cltmsg = str;
                                
                                myUpdateHandler.sendMessage(mesg);
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
    }
   
private class LongOperation extends AsyncTask<String, Void, String> {
	   
	   @Override
	   protected String doInBackground(String... params) {
		   Socket socket = null;
		   InetAddress serverAddr;
		try {
			serverAddr = InetAddress.getByName(serverIpAddress);
			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT);
	          System.out.println("Connected to server");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   try {
          	 
              EditText et = (EditText) findViewById(R.id.EditText01);
              String str = et.getText().toString();
              PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
              out.println(str);
            
              Log.d("Client", "Message sent by client");
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