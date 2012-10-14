package chatprogram;

import java.io.*;
import java.net.*;


public class ChatClass {
	
	static Socket connection = null;
	static ServerSocket socket = null;
	static ClientThread th[] = new ClientThread[1];
	static ServerThread sth[] = new ServerThread[1];
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
			String line = "";
			String lineclient = "";
			socket = new ServerSocket(4000);
			Socket client = null;
			
		while(true)
		{
			System.out.println("started chat program..................");
			try {
				client = new Socket("localhost",4001) ;

				BufferedReader in = null;
				PrintWriter out = null;
				out = new PrintWriter(
					      client.getOutputStream(), true);
					in = new BufferedReader(
					     new InputStreamReader(
					     client.getInputStream()));
					
				BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in));
String userInput;

while ((userInput = stdIn.readLine()) != null) {
out.println(userInput);
System.out.println("echo: " + in.readLine());
} }
			catch (IOException e)
			{
			connection = socket.accept();
			System.out.println("accepted chat program..................");
			while(connection!=null)
			{
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			//DataOutputStream outbuf = new DataOutputStream(connection.getOutputStream());
			PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(System.out));
			
			BufferedReader stdIn = new BufferedReader(
                    new InputStreamReader(System.in));
String userInput;
while(true)
{
line = in.readLine();
userInput = stdIn.readLine();
if(line!=null)
System.out.println("Text from cleint: " + line);
//while ((userInput = stdIn.readLine()) != null) {
else if(userInput != null)
out.println(userInput);
else
{
	System.out.println("nothing recvd");
}
//System.out.println("echo: " + in.readLine());
//}
}
//String outputLine, inputLine;
//while ((inputLine = in.readLine()) != null)
//{   
//outputLine = inputLine;
//out.println(outputLine);
//if (outputLine.equals("Bye."))
//break;
//}
//			line = inbuf.readLine();
//			while(line!=null){
//				System.out.println("Text Received: "+ line);	
//				outbuf.writeBytes(line);
//				line = inbuf.readLine();
//				}
			}	}
		}
		}	
		
}
	
	class ClientThread extends Thread {
		ClientThread t[];
		Socket clientsocket = null;
		BufferedReader inbufclient = null;
		PrintStream printclient = null;
		
		public ClientThread(Socket clientSocket, ClientThread[] t){
			this.clientsocket=clientSocket;
		        this.t=t;
		    }	
		
		public void run()
		{
			
			try {
				clientsocket = new Socket("localhost",4001);
				if(clientsocket != null)
			System.out.println("socket 4001 connection accepted");
			String lineclient = "";

			//DataOutputStream outbufclient = new DataOutputStream(clientsocket.getOutputStream());
			inbufclient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
			//DataInputStream inclient = new DataInputStream(clientsocket.getInputStream());
			printclient = new PrintStream(clientsocket.getOutputStream());
			lineclient = inbufclient.readLine();
			t[0].printclient.println("line received: "+ lineclient);
			System.out.println("Text received from client: "+ lineclient);
			//outbufclient.writeBytes("text from client");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	class ServerThread extends Thread {
			ServerThread t[];
			ServerSocket serversocket = null;
			Socket clientconn = null;
			BufferedReader inbufclient = null;
			PrintStream printclient = null;
			
			public ServerThread(ServerSocket serverSocket, Socket clientConn, ServerThread[] t){
				this.serversocket=serverSocket;
				this.clientconn = clientConn;
			        this.t=t;
			    }	
			
			public void run()
			{
				
				try {
					
					clientconn = serversocket.accept();
					//clientconn_global = clientconn;
				
//				String lineclient = "";
//
//				//DataOutputStream outbufclient = new DataOutputStream(clientsocket.getOutputStream());
//				inbufclient = new BufferedReader(new InputStreamReader(serversocket.getInputStream()));
//				//DataInputStream inclient = new DataInputStream(clientsocket.getInputStream());
//				printclient = new PrintStream(serversocket.getOutputStream());
//				lineclient = inbufclient.readLine();
//				t[0].printclient.println("line received: "+ lineclient);
//				System.out.println("Text received from client: "+ lineclient);
//				//outbufclient.writeBytes("text from client");
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}	

	



