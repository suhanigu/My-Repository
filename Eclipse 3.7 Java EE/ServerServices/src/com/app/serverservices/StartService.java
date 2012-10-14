package com.app.serverservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

public class StartService extends Service {
	
	private String serverIpAddress = "10.0.2.2";
	private Button bt;
	private Socket socket;
	private ServerSocket ss =null;
	   private TextView tv;
	   public static final int SERVERPORT = 4000;
	   public static final int REDIRECTED_SERVERPORT = 8000;
	   
	   @Override
	   public IBinder onBind(Intent intent) {
	      return null;
	   }

	   @Override
	   public void onCreate() {
		   Socket s = null;
           try {
                   ss = new ServerSocket(SERVERPORT );
           } catch (IOException e) {
                   e.printStackTrace();
           }
           while (true) {
           	s=null;
                  // Message m = new Message();
                  // m.what = MSG_ID;
                   try {
                           if (s == null)
                           {
                                   s = ss.accept();
                                   //tv.setText("Accepted the connection from: 8000");
                           }
                           BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                           String st = null;
                           st = input.readLine();
                           System.out.println("output: "+ st);                              
                   } catch (IOException e) {
                       e.printStackTrace();
               }
       }
	   
			
	      //code to execute when the service is first created
	   }

	   @Override
	   public void onDestroy() {
	      //code to execute when the service is shutting down
	   }

	   @Override
	   public int onStartCommand(Intent intent, int flags, int startid) {
	      //code to execute when the service is starting up
		   Socket s = null;
           try {
                   ss = new ServerSocket(SERVERPORT );
           } catch (IOException e) {
                   e.printStackTrace();
           }
           while (true) {
           	s=null;
                  // Message m = new Message();
                  // m.what = MSG_ID;
                   try {
                           if (s == null)
                           {
                                   s = ss.accept();
                                   //tv.setText("Accepted the connection from: 8000");
                           }
                           BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                           String st = null;
                           st = input.readLine();
                           System.out.println("output: "+ st);                              
                   } catch (IOException e) {
                       e.printStackTrace();
               }
       }
	   }
	}
