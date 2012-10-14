package com.server.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.io.*;
import java.net.*;



public class ServerAndroidActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = (TextView) findViewById(R.id.TextView01);
        tv.setText("Nothing from client yet..................");
    }
    
    ServerSocket server = null;
    Socket client = null;
    BufferedReader in = null;
    PrintWriter out = null;
    String line;
    String mClientMsg = "";
    protected static final int MSG_ID = 0x1337;
    
public void listenSocket(){
  try{
    server = new ServerSocket(4000); 
  } catch (IOException e) {
    System.out.println("Could not listen on port 8000");
    System.exit(-1);
  }

//listenSocketSocketserver.acceptSocket
  try{
    client = server.accept();
  } catch (IOException e) {
    System.out.println("Accept failed: 8000");
    System.exit(-1);
  }

//listenSocketBufferedReaderclientPrintWriter
  try{
   in = new BufferedReader(new InputStreamReader(
                           client.getInputStream()));
   out = new PrintWriter(client.getOutputStream(), 
                         true);
  } catch (IOException e) {
    System.out.println("Read failed");
    System.exit(-1);
  }

  Handler myUpdateHandler = new Handler() {
      public void handleMessage(Message msg) {
              switch (msg.what) {
              case MSG_ID:
                      TextView tv = (TextView) findViewById(R.id.TextView01);
                      tv.setText(mClientMsg);
                      break;
              default:
                      break;
              }
              super.handleMessage(msg);
      }
 };


//listenSocket
    while(true){
    	try{
        line = in.readLine();
        Message m = new Message();
        m.what = MSG_ID;
        mClientMsg = line;
        myUpdateHandler.sendMessage(m);
        TextView tv = (TextView) findViewById(R.id.TextView01);
        tv.setText(line);
//Send data back to client
        out.println(line);
      } catch (IOException e) {
        System.out.println("Read failed");
        System.exit(-1);
      }
    }
}
}