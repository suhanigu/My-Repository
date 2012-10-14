package com.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;


public class ClientAndroidActivity extends Activity {
    /** Called when the activity is first created. */
	
	 private Button bt;
	 private TextView tv;
	    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    bt = (Button) findViewById(R.id.myButton);
    tv = (TextView) findViewById(R.id.myTextView);
    }
    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

public void listenSocket(){
//Create socket connection
   try{
     socket = new Socket("10.0.2.2", 8000);
   } catch (UnknownHostException e) {
	     System.out.println("Unknown host: 10.0.2.2");
	     System.exit(1);
	   } catch  (IOException e) {
	     System.out.println("No I/O");
	     System.exit(1);
	   }
   
   bt.setOnClickListener(new OnClickListener() {
       public void onClick(View v) {
          try {
             EditText et = (EditText) findViewById(R.id.EditText01);
             String str = et.getText().toString();
             System.out.println("string in client: " + str);
     out = new PrintWriter(socket.getOutputStream(), 
                 true);
     in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
     out.println(str);
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
}
}
