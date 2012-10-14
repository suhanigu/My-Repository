package com.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class DS_Project0Activity extends Activity {

	public Handler handler;
	EditText port_no, typemsg;
	Button p_accept, sendmsg;
	TextView tv1;
	public static final String IP_Server = "10.0.2.2";
	public int Port_Server = 5000;
	String msgrecieved = new String("Server1");
	String msgsent = new String("Client1");
	public int LOCAL_PORT;
	ServerSocket serverSocket;
	Socket client;
	BufferedWriter out;
	
	

	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        port_no = (EditText) findViewById (R.id.port);
        p_accept = (Button) findViewById (R.id.port_accept);
        
        tv1 = (TextView) findViewById (R.id.tv);
       
        typemsg = (EditText) findViewById (R.id.message);
        sendmsg = (Button) findViewById (R.id.send);
        
    
        
        handler = new Handler();
        
        try {
			Runnable r1 = new MyServer();
			Thread t1 = new Thread(r1);
			t1.start();
		} catch (Exception e) {
			e.printStackTrace();

			Log.d("Calling server thread", "Calling server thread");
		}
	
        p_accept.setOnClickListener(port_acceptOnClickListener);
		}
	
	
	class MyServer implements Runnable {

		public void run() {

			try {

				serverSocket = new ServerSocket(Port_Server);
				Log.d("Server Activity", "Server socket created");
			}catch(Exception e){ 
				e.printStackTrace();
			}
			while (true){	
			try{
				
					client = serverSocket.accept();
					Log.d("Server Activity", "Server is Receiving from Client");
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					Log.d("Recieving ", "Recieving from Socket of Server");
					do{
					msgrecieved = in.readLine();
					
					Log.d("Recieved", "stored in msg recived");
					handler.post(new Runnable() {
								public void run() {
							
											tv1.append("\n Recieved : "+ msgrecieved);
											Log.d("Recieved", "recieved whatever sent from Client");

													}
												});				
					}while (true);
			} catch (Exception e) {

				Log.e("Server Activity", "Error in Server", e);

			}}

		}

	}

	class MyClient implements Runnable {

		public void run() {

			try {

				Socket csocket = new Socket("10.0.2.2", LOCAL_PORT);
				Log.d("Server Side","Socket after Iniating created");
				out = new BufferedWriter(new OutputStreamWriter(
						csocket.getOutputStream()));
				Log.d("Buffer Out", "Buffered Writer Created");
				
				
				try{
					
					Log.d("onclicklistener","just before sendmessage2server");	
					sendmsg.setOnClickListener(sendmessage2server);
					Log.d("button click", "after click messagetoserver button");
				
				}catch (Exception e){
					Log.d("Out", "error in sending from client");
				}
				
				Log.i("Client Activity", "Sent msg is : " + msgsent);
				

				

			} catch (IOException e) {

				e.printStackTrace();

			}

		}
	}

	
	private OnClickListener sendmessage2server = new OnClickListener() {
		public void onClick(View v) {
			try{
				handler.post(new Runnable() {
				public void run() {
					Log.d("button Activity", "before onclick on taking msg");
					msgsent = typemsg.getText().toString();
					tv1.append("Sent : " + msgsent);
					typemsg.setText("");
					Log.d("button Activity", "after clicking msgsent is"+ msgsent);
					try{
						Log.d("Just", "just before sending msgto socket");
						out.write(msgsent);
						msgsent = "";
						out.flush();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			});
			
			
			}catch(Exception e){
				e.printStackTrace();
				
			}
		}
	};

	private OnClickListener port_acceptOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			handler.post(new Runnable() {
				public void run() {
					Log.d("button Activity", "before onclick on taking port");
					LOCAL_PORT = Integer.parseInt(port_no.getText().toString());
					Log.d("buttonActivity", "after onclick on taking port");

				}
			});
			Log.d("threaD Activity", "Instance of runnablebefore");
			Runnable r2 = new MyClient();
			Log.d("thread Activity", "after CREATING RUNNABLE");
			Thread t2 = new Thread(r2);
			Log.d("THREAd Activity", "after creATING thread object");
			t2.start();
			Log.d("thread", "thread started");


			
			
			
		}
	};
}
