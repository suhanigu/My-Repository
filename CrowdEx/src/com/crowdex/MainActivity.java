package com.crowdex;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private static TextView tv1;
	private static TextView tv2;
	private static Button bt1;
	private static Button bt2;
	private static ImageSwitcher IS;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView1);
		bt1 = (Button) findViewById(R.id.button1);
		bt2 = (Button) findViewById(R.id.button2);
//		IS = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
		
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
		
		tv1.setText("Start Bidding");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }

    
}
