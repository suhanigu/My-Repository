package edu.buffalo.cse.cse486_586.simpledynamo;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.net.Uri;
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
//import android.widget.EditText;
import android.widget.TextView;

public class SimpleDynamoActivity extends Activity {
    /** Called when the activity is first created. */
   
	Thread myCommsThread = null;
    Thread clickCommsThread = null;
    private Button bt_put1;
    private Button bt_put2;
    private Button bt_put3;
    private Button bt_get;
    private Button bt_dump;
    private static TextView tv;
    protected static final int MESG_ID = 0x1337;
    
    
    public int SERVERPORT = 10000;
    private Socket socket;
    public int client_no = 2;
    private String serverIpAddress = "10.0.2.2";

    static String display = "";
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();  
  	   StrictMode.setThreadPolicy(policy);
  	 super.onCreate(savedInstanceState);
  	   
         setContentView(R.layout.main);
         bt_put1 = (Button) findViewById(R.id.myButtonput1);
         bt_put2 = (Button) findViewById(R.id.myButtonput2);
         bt_put3 = (Button) findViewById(R.id.myButtonput3);
         bt_get = (Button) findViewById(R.id.myButtonget);
         bt_dump = (Button) findViewById(R.id.myButtondump);
         tv = (TextView) findViewById(R.id.myTextView);
  
   System.out.println("set content view");
       tv.setText("Server listening on port: " + SERVERPORT);
           
       
       
       
       tv.setText("AVD port: " + MyContentProvider.own_port);
       
      getContentResolver().update(MyContentProvider.CONTENT_URI,null,null,null);
   
       bt_put1.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
        	   String value = "put1";
        	   new IncomingMessages().execute(value);
           }
        });
       
       bt_put2.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
        	   String value = "put2";
        	   new IncomingMessages().execute(value);
           }
        });
       
       bt_put3.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
        	   String value = "put3";
        	   new IncomingMessages().execute(value);
           }
        });
       
       bt_get.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
        	   new GetMessages().execute("");
           }
        });
       
       bt_dump.setOnClickListener(new OnClickListener() {
           private Thread myCommsThread;

		public void onClick(View v) {
        	   this.myCommsThread = new Thread(new CommsThread());
   	        this.myCommsThread.start();
           }
        });
    }
    
    @Override
    protected void onStop() {
         super.onStop();
		 System.out.println("stopping");
    }
   
    static Handler myUpdateHandler = new Handler() {
 	    public void handleMessage(Message mesg) {
 	            tv.setText(display);
 	            super.handleMessage(mesg);
 	    }
 	};
 	
	 class CommsThread implements Runnable {
		   Thread clickCommsThread = null;
	        public void run() {
	        	 Message mesg = new Message();
	             mesg.what = MESG_ID;

	             display = gettable();
	             myUpdateHandler.sendMessage(mesg);
	 	              
	 	           Log.d("Client", "Dump Completed");
	        }
	 }
 	
 	 
 	 
 	private class IncomingMessages extends AsyncTask<String, Void, String> {
		   
		   @Override
		   protected String doInBackground(String... values) {
			   //getContentResolver().update(MyContentProvider.CONTENT_URI,null,null,null);
			   int msg_no;
	    try {       
	        for(msg_no=0;msg_no<10;msg_no++){
	        	ContentValues keyValue = new ContentValues();
	        	String key = Integer.toString(msg_no);
	        	String value = values[0]+msg_no;
      			keyValue.put(MyTable.PROVIDER_KEY,key);
      			keyValue.put(MyTable.PROVIDER_VALUE,value); 
      			System.out.println("Inserting element: " + msg_no);
      			getContentResolver().insert(MyContentProvider.CONTENT_URI,keyValue);
      			
	              try {
	  				Thread.currentThread();
					Thread.sleep(3000);
	  			  } catch (InterruptedException e) {
	  				e.printStackTrace();
	  			  }
	              
	        	}
	        
	    }
	            catch (Exception e) {
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

 	
 	private class GetMessages extends AsyncTask<String, Void, String> {
		   
		   @Override
		   protected String doInBackground(String... values) {
			   //getContentResolver().update(MyContentProvider.CONTENT_URI,null,null,null);
			   int msg_no;
	    try {       
	        
	        for(msg_no=0;msg_no<10;msg_no++){
	        	
	        	String keyvaluesList = "";
	        	System.out.println("Querying element: " + msg_no);
	        	Cursor c = getContentResolver().query(
	        			MyContentProvider.CONTENT_URI,
	        			   null,
	        			   Integer.toString(msg_no),
	        			   null,
	        			   null
	        			);
	        	
	        	System.out.println("cursor returned");
	        	if (c.moveToFirst()) {
	                do {
	                	
	                	keyvaluesList += c.getString(c.getColumnIndex(MyTable.PROVIDER_KEY)) + ":" + c.getString(c.getColumnIndex(MyTable.PROVIDER_VALUE));
	                } while (c.moveToNext());
	            }
	        	
	        	 Message mesg = new Message();
	             mesg.what = MESG_ID;
	    		System.out.println("displaying: " + keyvaluesList);
	    		display = keyvaluesList;
	    		myUpdateHandler.sendMessage(mesg);
	        try {
				Thread.currentThread();
				Thread.sleep(3000);
			  } catch (InterruptedException e) {
				e.printStackTrace();
			  }
	        }
	        
	    }
	            catch (Exception e) {
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
 	

 	public String gettable() {
        
		String keyvaluesList = "";
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        qBuilder.setTables(MyTable.DATABASE_TABLE);
        
        Message mesg = new Message();
        mesg.what = SimpleDynamoActivity.MESG_ID;
        
		Uri uri = MyContentProvider.CONTENT_URI;
		String[] projection = new String[] { MyTable.PROVIDER_KEY, MyTable.PROVIDER_VALUE };
		String selection = null;
		String[] selectionArgs = {"something"};
		String sortOrder = null;
		Cursor c = getBaseContext().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
		int count=0;
		if (c.moveToFirst()) {
            do {
            	count++;
                keyvaluesList += c.getString(c.getColumnIndex(MyTable.PROVIDER_KEY)) + ":" + c.getString(c.getColumnIndex(MyTable.PROVIDER_VALUE));
            } while (c.moveToNext());
        }
		
		return keyvaluesList;
	}
 	
	public static void mycphandler(String display2) {
		// TODO Auto-generated method stub
		Message mesg = new Message();
        mesg.what = SimpleDynamoActivity.MESG_ID;
        display = display2;
		myUpdateHandler.sendMessage(mesg);
	}
 	

 	 
}