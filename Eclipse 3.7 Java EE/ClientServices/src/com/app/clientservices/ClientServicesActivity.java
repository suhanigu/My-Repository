package com.app.clientservices;

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
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ClientServicesActivity extends Activity {
	private String serverIpAddress = "10.0.2.2";
	private Button bt;
	private Socket socket;
	   private TextView tv;
	   public static final int SERVERPORT = 4001;
	   public static final int REDIRECTED_SERVERPORT = 8000;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bt = (Button) findViewById(R.id.myButton);
        tv = (TextView) findViewById(R.id.myTextView);
        tv.setText("Nothing from client yet");
			
			 InetAddress serverAddr;
				try {
					serverAddr = InetAddress.getByName(serverIpAddress);
					   socket = new Socket(serverAddr, REDIRECTED_SERVERPORT);
				          System.out.println("Connected to server");
				          tv.setText("Connected to server");
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			bt.setOnClickListener(new OnClickListener() {
		          public void onClick(View v) {
		             try {
		            	 tv.setText("Click event listener outside thread");
		                EditText et = (EditText) findViewById(R.id.EditText01);
		                String str = et.getText().toString();
		                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
		                out.println(str);
		                Log.d("Client", "Client sent message");
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
		       });
			
			while(true)
				startService(new Intent(this, startService.class));
		      
    }
}

