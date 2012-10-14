package edu.buffalo.cse.cse486_586.simpledynamo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
	// database
		private static MyDatabaseHelper database;

		private static final String AUTHORITY = "edu.buffalo.cse.cse486_586.simpledynamo.provider";

		private static final String BASE_PATH = "msgfile";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/" );
//------------------------------------------------------------------------------------------------------------
		ServerSocket serversock = null;
	    String cltmsg = "";
	    Thread myCommsThread = null;
	    Thread clickCommsThread = null;
	    
		 public int SERVERPORT = 10000;
		    private Socket socket;
		    public int client_no = 3;
		    private String serverIpAddress = "10.0.2.2";
		    
		    //my variables---------------------
		    String keyhash = "";
		    String display = "";
		    String[] all_info = {"5562","5556","5554","5558","5560"};
		    String[] all_info_send = {"11124","11112","11108","11116","11120"};
		    String[] all_hash = new String[5];
		    
		    static String own_port = "";
		    static int port = 0;
		    static int own_id = 0;
		    int time_flag = 0;
		    int coord_flag = 0;
		    
		    boolean qflag = false;
		    String globalkey = "";
		    String globalvalue = "";
		    
		@Override
		public boolean onCreate() {
			System.out.println("Oncreate of CP");
			database = new MyDatabaseHelper(getContext());
			database.getWritableDatabase();
			System.out.println("database created");
			this.myCommsThread = new Thread(new CommsThread());
	        this.myCommsThread.start();
			System.out.println("server thread started");
			TelephonyManager tel = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
			own_port = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		    port = Integer.parseInt(own_port) * 2; 
		       
		    System.out.println("own port found: " + own_port);
		    int i=0;
		    while(i<5){
		    	if(own_port.compareTo(all_info[i]) == 0){
		    		own_id = i;
		    		break;
		    	}
		    	i++;
		    }
		    System.out.println("own id found: " + own_id);
		       try {
				all_hash[0] = genHash("5562");
				all_hash[1] = genHash("5556");
				all_hash[2] = genHash("5554");
				all_hash[3] = genHash("5558");
				all_hash[4] = genHash("5560");
				
				new FailureCheckMessages().execute("");
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		       
		       
		       System.out.println("Returning from oncreate of CP");
			return false;
		}

		
		protected void onStop() {
	         try {
	              serversock.close();
	        	 System.out.println("stopping");
	         } catch (IOException e) {
	                 e.printStackTrace();
	         }
	    }
	   
		
		@Override
		public Cursor query(Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder) {

			if(selectionArgs==null)
			{
				//System.out.println("Selection args null");
				String querystr = "query:" + selection + ":" + port;
				try {
					query(querystr);
					while(true)
					{
						if(qflag == true)
						{
							qflag=false;
							System.out.println("finally qreply received");
							MatrixCursor cM = new MatrixCursor(new String[]{MyTable.PROVIDER_KEY,MyTable.PROVIDER_VALUE},1);
					 		cM.addRow(new String[]{globalkey,globalvalue});
				            cM.setNotificationUri(getContext().getContentResolver(),CONTENT_URI);
				            return cM;
						}
					}
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(MyTable.DATABASE_TABLE);
			
			String myquery = "";
			
			if(selection!=null)
				myquery = "Select " + projection[0] + "," + projection[1] + " from " + MyTable.DATABASE_TABLE  + " where " + MyTable.PROVIDER_KEY + "='" + selection + "';";
			else
				myquery = "Select * from " + MyTable.DATABASE_TABLE + ";";
			
			//System.out.println("query is: " + myquery);
			SQLiteDatabase db = database.getWritableDatabase();
			
			Cursor cursor = db.rawQuery(myquery,null);
			
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
			}
			return null;
		}

		@Override
		public String getType(Uri uri) {
			return null;
		}

		@Override
		public Uri insert(Uri uri, ContentValues values) {
			
			String key = (String) values.get(MyTable.PROVIDER_KEY);
			String value = (String) values.get(MyTable.PROVIDER_VALUE);
			
			if(value.contains(":"))
			{
				StringTokenizer steq = new StringTokenizer(value,":");
				String finalvalue = steq.nextToken();
				
				ContentValues keyValue = new ContentValues();

	        	String mykey = key;
	        	String myvalue = finalvalue;
      			keyValue.put(MyTable.PROVIDER_KEY,mykey);
      			keyValue.put(MyTable.PROVIDER_VALUE,myvalue); 
			SQLiteDatabase sqlDB = database.getWritableDatabase();
			
			long id = 0;
			Uri newuri = ContentUris.withAppendedId(uri, id);
			
			Cursor c = getrow(Integer.parseInt(mykey));
			if(c.moveToFirst()){
				System.out.println("Updating row for key: " + mykey);
				String whereclause = MyTable.PROVIDER_KEY + "='" + mykey + "'";
				id = sqlDB.update(MyTable.DATABASE_TABLE, keyValue, whereclause, null);
				System.out.println("Result of insertion in db: " + id);
				getContext().getContentResolver().notifyChange(uri, null);
				return newuri;
			}
			else{
				System.out.println("Adding new row for key: " + mykey);
				id = sqlDB.insert(MyTable.DATABASE_TABLE, null, keyValue);
				System.out.println("Result of insertion in db: " + id);
				getContext().getContentResolver().notifyChange(uri, null);
				return newuri;
			}
			}
			else
			{
				String str = key + ":" + value;
				System.out.println("Calling insert_conditions: " + str);
				try {
					insert_conditions(str);
					//System.out.println("Returned from insert conditions");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return uri;
		}

		@Override
		public int delete(Uri uri, String selection, String[] selectionArgs) {
			
			SQLiteDatabase sqlDB = database.getWritableDatabase();
			int rowsDeleted = 0;
			getContext().getContentResolver().notifyChange(uri, null);
			return rowsDeleted;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection,
				String[] selectionArgs) {

			SQLiteDatabase sqlDB = database.getWritableDatabase();
			int rowsUpdated = 0;
			final String DATABASE_CREATE = "create table " + MyTable.DATABASE_TABLE + "(" + MyTable.PROVIDER_KEY + " VARCHAR(5) ," + MyTable.PROVIDER_VALUE + " VARCHAR(20));";

			sqlDB.execSQL("DROP TABLE IF EXISTS " + MyTable.DATABASE_TABLE + ";");
			sqlDB.execSQL(DATABASE_CREATE);

			getContext().getContentResolver().notifyChange(uri, null);
			return rowsUpdated;
		}

		private void checkColumns(String[] projection) {
			String[] available = {
					MyTable.PROVIDER_KEY,
					MyTable.PROVIDER_VALUE };
			if (projection != null) {
				HashSet<String> requestedColumns = new HashSet<String>(
						Arrays.asList(projection));
				HashSet<String> availableColumns = new HashSet<String>(
						Arrays.asList(available));
				if (!availableColumns.containsAll(requestedColumns)) {
					throw new IllegalArgumentException(
							"Unknown columns in projection");
				}
			}
		}

		
		//---------------------------------------------------------------------------------------------------------
		
		public static String genHash(String input) throws NoSuchAlgorithmException {
	 		
	 		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
	 		byte[] sha1Hash = sha1.digest(input.getBytes());
	 		Formatter formatter = new Formatter();
	 		for (byte b : sha1Hash) {
	 		formatter.format("%02x", b);
	 		}
	 		return formatter.toString();
	 	}
		
		
		public void quorum_vote(){
			int i=0;
			String[] list = {"",""};
			for(i=1;i<3;i++){
				list[0] = "vote:" + all_info_send[own_id];
				list[1] = all_info_send[(own_id+i)%5];
				VoteMessages_insert(list);
			}
			System.out.println("Result of quorum vote: " + time_flag);
		}
		
		public Cursor getrow(int i) {
			Uri uri = MyContentProvider.CONTENT_URI;
		
	        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
	        
	        qBuilder.setTables(MyTable.DATABASE_TABLE);
	        
			String[] projection = new String[] { MyTable.PROVIDER_KEY, MyTable.PROVIDER_VALUE };
			String selection = Integer.toString(i);
			String[] selectionArgs = {"something"};
			String sortOrder = null;
			
			Cursor c = getContext().getContentResolver().query(uri, projection, selection, selectionArgs,sortOrder);	
			return c;
		}
		
		
		
		public String insert_conditions(String copy) throws NoSuchAlgorithmException
	 	{
	 		StringTokenizer steq = new StringTokenizer(copy,":");
            String key = steq.nextToken();
	 		String value = steq.nextToken();
         	String mykeyhash = genHash(key);
         	int i=0,flag=0;
         	String[] list = {"",""};
         	String send_to_port = "";
         	int index = 0;
         	ContentValues keyValue = new ContentValues();
         	
         	while(i<5)
         	{
         		if(mykeyhash.compareTo(all_hash[i]) > 0 && mykeyhash.compareTo(all_hash[(i+1)%5]) < 0)
         		{
         			flag=1;
         			send_to_port = all_info_send[(i+1)%5];
         			index = (i+1)%5;
         			break;
         		}
         		i++;
         	}
         	if(flag==0){
         		send_to_port = all_info_send[0];
         	}
         	
         	System.out.println("found coordinator for key: " + send_to_port);
         	
         	if(send_to_port.compareTo(Integer.toString(port)) == 0){
         		
         		value = value+":"+"insertnow";
				keyValue.put(MyTable.PROVIDER_KEY,key);
				keyValue.put(MyTable.PROVIDER_VALUE,value); 
				Uri newUri = getContext().getContentResolver().insert(MyContentProvider.CONTENT_URI,keyValue);
				int k = 2;
				while(k>0){
					list[0] = "direct:" + key + ":" + value;
					list[1] = all_info_send[(own_id+k)%5];
					k--;
					OutgoingMessages_insert(list);
				}
         		
         	}
         	
         	else{
         		
         	list[0] = "coordinator:" + all_info_send[own_id];
         	list[1] = send_to_port;
         	CoordinatorMessages_insert(list);
         	
         	if(coord_flag>=1){
         		coord_flag=0;
         		System.out.println("sending it to owner: " + send_to_port);
         		list[0] = "store:" + key + ":" + value + ":" + 2;
         		list[1] = all_info_send[(i+1)%5];
         		OutgoingMessages_insert(list);
         	}
         	else{
         		list[0] = "coordinator:" + all_info_send[own_id];
             	list[1] = all_info_send[(index+1)%5];
             	CoordinatorMessages_insert(list);
             	
             	if(coord_flag>=1){
             		coord_flag=0;
             		System.out.println("sending it to successor of owner: " + all_info[(index+1)%5]);
             		list[0] = "store:" + key + ":" + value + ":" + 1;
             		list[1] = all_info_send[(index+1)%5];
             		OutgoingMessages_insert(list);
             	}
             	else{
             		System.out.println("Quorum failed............not inserting anywhere");
             	}
         	}
         	}
         	
         	return null;
	 	}
		
		
		public void insert(String insert_str)
		{
				ContentValues keyValue = new ContentValues(); 
				int i=0;
		  		String[] list = {"",""};
		  		String storekey = "";
		  		String storevalue = "";
				StringTokenizer steq = new StringTokenizer(insert_str,":");
				String token = steq.nextToken();
				
				if(token.contains("store")){
					quorum_vote();
					
					if(time_flag >=1){
						time_flag=0;
						storekey = steq.nextToken();
						storevalue = steq.nextToken();
						storevalue = storevalue+":"+"insertnow";
						int k = Integer.parseInt(steq.nextToken());
         	   
						System.out.println("stored message");
         	   
						keyValue.put(MyTable.PROVIDER_KEY,storekey);
						keyValue.put(MyTable.PROVIDER_VALUE,storevalue); 
						Uri newUri = getContext().getContentResolver().insert(MyContentProvider.CONTENT_URI,keyValue);
         	   
         	 
						while(k>0){
							list[0] = "direct:" + storekey + ":" + storevalue;
							list[1] = all_info_send[(own_id+k)%5];
							OutgoingMessages_insert(list);
							k--;
						}
					}
					else {
						time_flag=0;
						System.out.println("Quorum failed while inserting...........hence no insertion");
					}
				}
         	  	else{
         	  		storekey = steq.nextToken();
					storevalue = steq.nextToken();
					storevalue = storevalue+":"+"insertnow";
     	   
					System.out.println("direct message");
     	   
					keyValue.put(MyTable.PROVIDER_KEY,storekey);
					keyValue.put(MyTable.PROVIDER_VALUE,storevalue); 
					Uri newUri = getContext().getContentResolver().insert(MyContentProvider.CONTENT_URI,keyValue);
         	  	}
		}
		
		
		
		public void query(String query_str) throws NoSuchAlgorithmException{
			
			StringTokenizer steq = new StringTokenizer(query_str,":");
			String query_check = steq.nextToken();
			String query_key = steq.nextToken();
			String mykeyhash = genHash(query_key);
         	int i=0,flag=0;
         	int index=0;
         	String send_to_port = "";
         	String[] list = {"",""};
         	
         	while(i<5)
         	{
         		if(mykeyhash.compareTo(all_hash[i]) > 0 && mykeyhash.compareTo(all_hash[(i+1)%5]) < 0)
         		{
         			flag=1;
         			send_to_port = all_info_send[(i+1)%5];
         			index = (i+1)%5;
         			break;
         		}
         		i++;
         	}
         	if(flag==0){
         		send_to_port = all_info_send[0];
         	}
         	
         	System.out.println("found coordinator for key: " + send_to_port);
         	
         	list[0] = "coordinator:" + all_info_send[own_id];
         	list[1] = send_to_port;
         	CoordinatorMessages_insert(list);
         	
         	if(coord_flag==1){
         		coord_flag=0;
         		System.out.println("sending it to owner: " + send_to_port);
         		list[0] = "q_done:" + query_key + ":" + all_info_send[own_id];
         		list[1] = all_info_send[(i+1)%5];
         		OutgoingMessages_insert(list);
         	}
         	else{
         		list[0] = "coordinator:" + all_info_send[own_id];
             	list[1] = all_info_send[(index+1)%5];
             	CoordinatorMessages_insert(list);
             	
             	if(coord_flag==1){
             		coord_flag=0;
             		System.out.println("sending it to successor: " + all_info[(index+1)%5]);
             		list[0] = "q_done:" + query_key + ":" + all_info_send[own_id];
             		list[1] = all_info_send[(index+1)%5];
             		OutgoingMessages_insert(list);
             	}
             	else{
             		System.out.println("Quorum failed............not inserting anywhere");
             		String queryreply = "qreply:" + query_key + ":" + "not found";
     	 	       list[0] = queryreply;
     	           list[1] = Integer.toString(port);
     	           OutgoingMessages_insert(list);
             	}
         	}
         	
		}
		
		
		
		public void query_done(String query_done){
			
			int i=0;
			String[] list = {"",""};
		
				StringTokenizer steq = new StringTokenizer(query_done,":");
     	 		String querydone = steq.nextToken();
     	 		String msgkey = steq.nextToken();
     	 		String srcport = steq.nextToken();
     	 		Cursor cursor = getrow(Integer.parseInt(msgkey));
	 	        while (cursor.moveToNext()) 
	 	        {
	 	        	String displaykey = cursor.getString(cursor.getColumnIndex(MyTable.PROVIDER_KEY));
	 	        	String displayvalue = cursor.getString(cursor.getColumnIndex(MyTable.PROVIDER_VALUE));
	 	        	display = displaykey + ":" + displayvalue;
	 			}
	 	        String queryreply = "qreply:" + display;
	 	        System.out.println("sending qreply " + display + "to " + srcport);
	 	       list[0] = queryreply;
	           list[1] = srcport;
	           OutgoingMessages_insert(list);
		}
	 	
		
		
		public void queryreply(String qreplymsg)
	 	{
	 		Message mesg = new Message();
	        mesg.what = SimpleDynamoActivity.MESG_ID;
	        System.out.println("query reply received : " + qreplymsg);
	 		StringTokenizer steq = new StringTokenizer(qreplymsg,":");
	 		String qreplycheck = steq.nextToken();
	 			globalkey = steq.nextToken();	
	 			globalvalue = steq.nextToken();
	 			qflag = true;
	 	}
		
		
		public Cursor gettable() {
	        
	        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

	        qBuilder.setTables(MyTable.DATABASE_TABLE);
	        
	        Message mesg = new Message();
	        mesg.what = SimpleDynamoActivity.MESG_ID;
	        
			Uri uri = MyContentProvider.CONTENT_URI;
			String[] projection = new String[] { MyTable.PROVIDER_KEY, MyTable.PROVIDER_VALUE };
			String selection = null;
			String[] selectionArgs = {"something"};
			String sortOrder = null;
			Cursor c = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
			return c;
			
		}
		
		
		public void send_keys(String upmsg) throws NoSuchAlgorithmException{
			StringTokenizer steq = new StringTokenizer(upmsg,":");
	 		String upcheck = steq.nextToken();
	 		String spcheck = steq.nextToken();
	 		Integer originator_id = Integer.parseInt(steq.nextToken());
	 		String key = "";
	 		String keyvaluesList = "";
	 		String[] list = {"",""};
	 		Cursor c = gettable();
	 		
	 		if(spcheck.contains("succ")){
	 			
	 			if (c.moveToFirst()) {
		            do {
		                key = c.getString(c.getColumnIndex(MyTable.PROVIDER_KEY));
		                if(genHash(key).compareTo(all_hash[(own_id+4)%5]) > 0 && genHash(key).compareTo(all_hash[(own_id+4)%5]) < 0){
		                	keyvaluesList += c.getString(c.getColumnIndex(MyTable.PROVIDER_KEY)) + ":" + c.getString(c.getColumnIndex(MyTable.PROVIDER_VALUE));
		                }
		            	
		            } while (c.moveToNext());
		        }
	 			System.out.println("keyvaluelist in sendkeys for succ: " + keyvaluesList);
	 				list[0] = "return:" + keyvaluesList;
					list[1] = all_info_send[originator_id];
					OutgoingMessages_insert(list);
	 			
	 		}
	 		else{
	 			
	 			if (c.moveToFirst()) {
		            do {
		                key = c.getString(c.getColumnIndex(MyTable.PROVIDER_KEY));
		                if(genHash(key).compareTo(all_hash[own_id]) > 0 && genHash(key).compareTo(all_hash[originator_id]) < 0){
		                	keyvaluesList += c.getString(c.getColumnIndex(MyTable.PROVIDER_KEY)) + ":" + c.getString(c.getColumnIndex(MyTable.PROVIDER_VALUE));
		                }
		            	
		            } while (c.moveToNext());
		        }
	 			System.out.println("keyvaluelist in sendkeys for pred: " + keyvaluesList);
	 				list[0] = "return:" + keyvaluesList;
	 				list[1] = all_info_send[originator_id];
	 				OutgoingMessages_insert(list);
	 		}
	 		
		}
		
		
		
		public void return_kvpair(String returnstr){
			
			ContentValues keyValue = new ContentValues(); 
			System.out.println("returning string: " + returnstr);
			StringTokenizer steq = new StringTokenizer(returnstr,":");
			steq.nextToken();
			String storekey = "";
			String storevalue = "";
			while(steq.hasMoreTokens()){
				storekey = steq.nextToken();
				storevalue = steq.nextToken();
				storevalue = storevalue+":"+"insertnow";
	   
				//System.out.println("stored message");
	   
				keyValue.put(MyTable.PROVIDER_KEY,storekey);
				keyValue.put(MyTable.PROVIDER_VALUE,storevalue); 
				Uri newUri = getContext().getContentResolver().insert(MyContentProvider.CONTENT_URI,keyValue);
			}
			
		}
		
		
	 	 class CommsThread implements Runnable {
	 		   Thread clickCommsThread = null;
	 	        public void run() {
	 	                Socket sock = null;
	 	                try {
	 	                        serversock = new ServerSocket(SERVERPORT);
	 	                } catch (IOException e) {
	 	                        e.printStackTrace();
	 	                }
	 	                
	 	                while (true) {
	 	                	sock=null;
	 	                        try {
	 	                        	if (sock == null)
	 	                                sock = serversock.accept();
	 	                        		
	 	                                BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	 	                                String str = input.readLine();
	 	                                String copy = str;
	 	                                System.out.println("String received at server: " + str);
	 	                                if(str==null)
	 	                                	continue;
	 	                               StringTokenizer steq = new StringTokenizer(str,":");
	 	                               String key = steq.nextToken();
	 	                               
	 	                              if(key.contains("vote")){
	 	                            	 PrintWriter out1 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
	 	                            	  String srcport = steq.nextToken();
	 	                            	  String list[] = {"",""};
	 	                            	  list[0] = "vote_reply";
	 	                		         out1.println(list[0]);
	 	                              }
	 	                              else if(key.contains("coordinator")){
	 	                            	 PrintWriter out2 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
	 	                            	 String srcport = steq.nextToken();
	 	                            	  String list[] = {"",""};
	 	                            	  list[0] = "coord_alive";
	 	                		         out2.println(list[0]);
	 	                              }
	 	                              else if(key.contains("store") || key.contains("direct")){
	 	                            	  insert(copy);
	 	                              } 
	 	                              else if(key.contains("query")){
	 	                            	  query(copy);
	 	                              }
	 	                              else if(key.contains("q_done")){
	 	                            	  query_done(copy);
	 	                              }
	 	                              else if(key.contains("qreply")){
	 	                            	  queryreply(copy);
	 	                              }
	 	                              else if(key.contains("quorum_failed")){
	 	                            	  queryreply(copy);
	 	                              }
	 	                              else if(key.contains("up")){
	 	                            	  System.out.println("up message received");
	 	                            	  send_keys(copy);
	 	                              }
	 	                              else if(key.contains("return")){
	 	                            	  return_kvpair(copy);
	 	                              }
	 	                              else{
	 	                            	  System.out.println("Do Nothing");
	 	                              }
	 	                               
	 	                              
	 	                        } catch (IOException e) {
	 	                                e.printStackTrace();
	 	                        } catch (NoSuchAlgorithmException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
	 	                }
	 	        }
	 	    }
	 	   
	 	 
	 	 
	 	public String OutgoingMessages_insert(String[] str){
	 		   
	 			   
	 			   //Socket socket = null;
	 			 
	 			   InetAddress serverAddr;
	 			  Socket osocket = new Socket();
	 		 		InetSocketAddress sa = new InetSocketAddress(serverIpAddress, Integer.parseInt(str[1])); 
	 		 		try {
	 					osocket.connect(sa,50);
	 				
	 					
	 		 		if(osocket.isConnected()){
	 		 			System.out.println("Sending message------------- " + str[0] + " to: " + str[1]);
	 		 		}
	 		 		else{
	 		 			System.out.println("Server down-----outgoingmsg: " + str[1]);
	 		 			return null;
	 		 		}
	 				   //System.out.println("Outgoing Message--------" + str[0]);
	 	              PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(osocket.getOutputStream())) , true);
	 	              out.println(str[0]);
	 	              
	 	              Log.d("Client", "Message sent to " + str[1]);
	 	              osocket.close();
	 	           } catch (UnknownHostException e) {
	 	              e.printStackTrace();
	 	           } catch (IOException e) {
	 	        	   System.out.println("IOException");
	 	        	   return null;
	 	              //e.printStackTrace();
	 	           } catch (Exception e) {
	 	              e.printStackTrace();
	 	           }
					return null;
	 			 
	 		   }
	 		  
	 	
	 	public String VoteMessages_insert(String[] str){
	 		   
			   
			   //Socket socket = null;
			 
			   InetAddress serverAddr;
			//try {
				Socket vsocket = new Socket();
		 		InetSocketAddress sa = new InetSocketAddress(serverIpAddress, Integer.parseInt(str[1])); 
		 		try {
					vsocket.connect(sa,50);
				
		 		if(vsocket.isConnected()){
		 			System.out.println("Asking to vote from: " + str[1]);
		 		}
		 		else{
		 			System.out.println("Server down-----votemsg: " + str[1]);
		 			return null;
		 		}
					vsocket.setSoTimeout(200);
				
	                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(vsocket.getOutputStream())), true);
	                BufferedReader br = new BufferedReader(new InputStreamReader(vsocket.getInputStream()));
	                out.println(str[0]);
	                
	                String input = br.readLine();
	                System.out.println("Voted: " + input);
	                if(input!=null)
	                	time_flag++;
	                
	                vsocket.close();
	           }catch(InterruptedIOException iioe){
	        	   //time_flag--;
	        	   System.out.println("Remote host timed out\n");
	           }
				catch (UnknownHostException e) {
	              e.printStackTrace();
	           } catch (IOException e) {
	        	   System.out.println("IOException");
	        	   return null;
	             // e.printStackTrace();
	           } catch (Exception e) {
	              e.printStackTrace();
	           }
				return null;
			 
		   }
	 	
	 	
	 	public String CoordinatorMessages_insert(String[] str){
	 		   
			   
			   
			   InetAddress serverAddr;
			//try {
					//System.out.println("Entering coordinator messages: " + str[1]);
				Socket cordsocket = new Socket();
		 		InetSocketAddress sa = new InetSocketAddress(serverIpAddress, Integer.parseInt(str[1])); 
		 		try {
					cordsocket.connect(sa,50);
				
		 		if(cordsocket.isConnected()){
		 			System.out.println("Sending message to: " + str[1] + "to check if alive or not");
		 		}
		 		else{
		 			System.out.println("Server down-------coordmsg: " + str[1]);
		 			return null;
		 		}
					//System.out.println("value of socket return value: " + socket);
					cordsocket.setSoTimeout(200);
				
					//System.out.println("Creating input/output socket stream");
	                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(cordsocket.getOutputStream())), true);
	                BufferedReader br = new BufferedReader(new InputStreamReader(cordsocket.getInputStream()));
	               // System.out.println("Sent message to coordinator:" + str[1]);
	                out.println(str[0]);
	                
	                String input = br.readLine();
	                System.out.println("Heartbeat status: " + input);
	                if(input!=null)
	                	coord_flag++;
	                cordsocket.close();
	           }catch(InterruptedIOException iioe){
	        	   //coord_flag--;
	        	   System.out.println("Remote host timed out\n");
	           }
				catch (UnknownHostException e) {
					System.out.println("Unknown host exception");
	              e.printStackTrace();
	           } catch (IOException e) {
	        	   System.out.println("IO exception");
	              //e.printStackTrace();
	              return null;
	           } catch (Exception e) {
	        	   System.out.println("General exception");
	              e.printStackTrace();
	           }
				return null;
			 
		   }
	 	  
	 	
	 	public String FailMessages(String[] list){
	 	
	 		//System.out.println("Entering fail messages");
	 		 Socket fsocket = new Socket();
	 		InetSocketAddress sa = new InetSocketAddress(serverIpAddress, Integer.parseInt(list[1])); 
	 		try {
				fsocket.connect(sa,50);
			
	 		if(fsocket.isConnected()){
	 			System.out.println("Sending failcheck message " + list[0] + " to: " + list[1]);
	 		}
	 		else{
	 			System.out.println("Server down-----failmsg: " + list[1]);
	 			return null;
	 		}
			   //System.out.println("Outgoing Message--------" + list[0]);
              PrintWriter fout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fsocket.getOutputStream())), true);
              fout.println(list[0]);
              
              fsocket.close();
			return null;
	 		} catch (IOException e) {
				// TODO Auto-generated catch block
	 			return null;
				//e.printStackTrace();
			}
	 	}
	 		   
	 	
	 	private class FailureCheckMessages extends AsyncTask<String, Void, String> {
			   
			   @Override
			   protected String doInBackground(String... params) {
				  
				   Socket fcsocket = null;
				   InetAddress serverAddr;
				   int i = 0;
				try {
					System.out.println("entered failurecheckmessages");
					String[] list = {"",""};
						list[0] = "up:" + "succ:" + own_id;
						list[1] = all_info_send[(own_id + 1)%5];
						FailMessages(list);

					for(i=3;i<5;i++){
							list[0] = "up:" + "pred:" + own_id;
							list[1] = all_info_send[(own_id + i)%5];
							FailMessages(list);
					}
					
				}catch (Exception e) {
		        	   System.out.println("General exception");
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
