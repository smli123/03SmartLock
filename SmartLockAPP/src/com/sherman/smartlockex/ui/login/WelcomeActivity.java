package com.sherman.smartlockex.ui.login;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.R.id;
import com.sherman.smartlockex.R.layout;
import com.sherman.smartlockex.R.menu;

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
//        //��title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);    
//        //ȫ��    
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,      
//                       WindowManager.LayoutParams. FLAG_FULLSCREEN);
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
// 		it.putExtra("AUTO_LOGIN", DEFAULT_AUTOLOGIN);
// 		it.putExtra("LOGIN_SHOW", "true");
 		it.setClass(WelcomeActivity.this, LoginActivity.class);
 		startActivity(it);
 		finish();
     }
}
