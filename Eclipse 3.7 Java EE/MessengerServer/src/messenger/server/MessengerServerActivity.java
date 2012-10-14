package messenger.server;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.*;
import java.net.*;


public class MessengerServerActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     // Capture our button from layout
        Button button = (Button)findViewById(R.id.button1);
        // Register the onClick listener with the implementation above
        button.setOnClickListener(this);
    }
   // public static void main(String[] args) throws IOException {
public void onClick(View v) { 
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket("10.0.2.15", 5554);
            
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: android");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: taranis.");
            System.exit(1);
        }

	BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
	String userInput;

	try {
		while ((userInput = stdIn.readLine()) != null) {
		    out.println(userInput);
		    System.out.println("echo: " + in.readLine());
		}

	out.close();
	in.close();
	stdIn.close();
	echoSocket.close();
    }
 catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
}
}