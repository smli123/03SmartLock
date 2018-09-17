package com.sherman.smartlockex.ui.login;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.R.id;
import com.sherman.smartlockex.R.layout;
import com.sherman.smartlockex.R.menu;
import com.sherman.smartlockex.ui.smartlockex.SmartLockActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class LoginActivity extends Activity implements OnClickListener {
	private Handler mHandler = null;
	private Context mContext = null;
	
	private TextView tv_version;
	private ImageView iv_delete_username;
	private ImageView iv_delete_password;
	
	private EditText et_username;
	private EditText et_password;
	
	private Button btn_login;
	
	private TextView tv_register;
	private TextView tv_forget_password;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        
        initview();
        
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		initview();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
    
    private void initview() {
    	tv_version = (TextView)findViewById(R.id.tv_version);
    	tv_version.setText(getVersion(mContext));
    	
    	tv_version = (TextView)findViewById(R.id.tv_version);
    	iv_delete_username = (ImageView)findViewById(R.id.iv_delete_username);
    	iv_delete_password = (ImageView)findViewById(R.id.iv_delete_password);
    	
    	et_username = (EditText)findViewById(R.id.et_username);
    	et_password = (EditText)findViewById(R.id.et_password);
    	
    	btn_login = (Button)findViewById(R.id.btn_login);
    	
    	tv_register = (TextView)findViewById(R.id.tv_register);
    	tv_forget_password = (TextView)findViewById(R.id.tv_forget_password);
    	
    	tv_version.setOnClickListener(this);
    	btn_login.setOnClickListener(this);
    	tv_register.setOnClickListener(this);
    	tv_forget_password.setOnClickListener(this);
    	iv_delete_username.setOnClickListener(this);
    	iv_delete_password.setOnClickListener(this);
    }
    
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_version :
				net_version();
				break;
			case R.id.btn_login :
				net_login();
				break;
			case R.id.tv_register :
				net_register();
				break;
			case R.id.tv_forget_password :
				net_forget_password();
				break;
			case R.id.iv_delete_username :
				delete_username();
				break;
			case R.id.iv_delete_password :
				delete_password();
				break;
		}
	}
	
	private void net_version() {
		
	}
	
	private void net_login() {
		Intent act = new Intent();
		act.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		act.setClass(LoginActivity.this, SmartLockActivity.class);
		mContext.startActivity(act);
		finish();
	}
	
	private void net_register() {
		Intent act = new Intent();
		act.setClass(LoginActivity.this, RegisterActivity.class);
		mContext.startActivity(act);
	}
	
	private void net_forget_password() {
		Intent act = new Intent();
		act.setClass(LoginActivity.this, FindPwdActivity.class);
		mContext.startActivity(act);
	}

	private void delete_username() {
		et_username.setText("");
	}
	
	private void delete_password() {
		et_password.setText("");
	}
    
    // ��ȡ�汾��
	public static String getVersion(Context context)
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return context
					.getString(R.string.app_version_unknown);
		}
	}
    
}
