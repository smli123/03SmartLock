package com.sherman.smartlockex.ui.login;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.R.id;
import com.sherman.smartlockex.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class WelcomeActivity extends Activity {
	private Handler mHandler = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        mHandler = new Handler();
        
        mHandler.postDelayed(startRunnable, 1000);
    }
    
    private Runnable startRunnable = new Runnable() {

 		@Override
 		public void run() {
 			start();
 		}
     };
     
     private void start() {
 		Intent it = new Intent();
 		it.setClass(WelcomeActivity.this, LoginActivity.class);
 		startActivity(it);
 		finish();
     }
}
