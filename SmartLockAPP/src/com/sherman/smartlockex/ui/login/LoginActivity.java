package com.sherman.smartlockex.ui.login;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.R.id;
import com.sherman.smartlockex.R.layout;
import com.sherman.smartlockex.internet.UDPReceiver;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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


public class LoginActivity extends TitledActivity implements OnClickListener {
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
	
	private String mEmail = "";
	
	private static UDPReceiver mUDPServer = null;
	

	private BroadcastReceiver mLoginRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.LOGIN_BROADCAST)
					&& true == PubDefine.g_First_Login) {
				PubDefine.g_Connect_Mode = PubDefine.SmartLock_Connect_Mode.Internet;

				if (null != mProgress) {
					mProgress.dismiss();
				}
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 0);
				PubFunc.log("Message: app login. ret:=" + String.valueOf(ret));
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
					case 0 :
						mEmail = message;
						SmartLockApplication.setLogined(true);

						updateHandler.sendEmptyMessage(0);

						break;
					default :
						SmartLockApplication.setLogined(false);
						PubFunc.thinzdoToast(LoginActivity.this, message);
						break;
				}
			}

//			if (intent.getAction().equals(PubDefine.LOGOUT_BROADCAST)) {
//				int ret = intent.getIntExtra("LOGOUT", 0);
//				PubFunc.log("Message: app logout. ret:=" + String.valueOf(ret));
//
//				if (1 == ret) {
//					new MyAlertDialog(SmartPlugApplication.getContext())
//							.builder()
//							.setMsg("Forced to Quit")
//							.setPositiveButton(
//									mContext.getString(R.string.smartplug_ok),
//									new View.OnClickListener() {
//										@Override
//										public void onClick(View arg0) {
//
//										}
//									}).setCancelable(false).show();
//				}
			}
	};
	
	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			Intent act = new Intent();
			act.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			act.setClass(LoginActivity.this, SmartLockActivity.class);
			mContext.startActivity(act);
			finish();
		};
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        
        // 启动UDP端口监听线程
 		if (null == mUDPServer) {
 			mUDPServer = new UDPReceiver(connectHandler);
 			new Thread(mUDPServer).start();
 		}
        
        initview();
        
        IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOGIN_BROADCAST);
		registerReceiver(mLoginRev, filter);
    }
    
	private Runnable login_runnable = new Runnable() {
		@Override
		public void run() {
			connectHandler.sendEmptyMessage(1);
		}
	};

	private Handler connectHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				PubDefine.g_Connect_Mode = PubDefine.SmartLock_Connect_Mode.Internet;
				onlineLogin();
			} else {
				if (null != mProgress) {
					mProgress.dismiss();
				}
				PubFunc.thinzdoToast(mContext,
						mContext.getString(R.string.login_cmd_socket_timeout));
				timeoutHandler.removeCallbacks(timeoutProcess);
			}
		};
	};

	private void onlineLogin() {
		String userName = et_username.getText().toString().trim();
		String password = et_password.getText().toString().trim();
		if (!userName.isEmpty() && !password.isEmpty()) {

			String version = PubFunc.getAppVersion();

			PubDefine.g_First_Login = true;
			StringBuffer sb = new StringBuffer();
			sb.append(SmartLockMessage.CMD_SP_LOGIN_SERVER)
					.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(userName)
					.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(version)
					.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
					.append(password);

			PubStatus.g_userPwd = password;
			sendMsg(true, sb.toString(), true);
			
			// Only For Test, it must delete
			PubStatus.g_userEmail = "smli123@163.com";
		}
	}
    
    @Override
	protected void onResume() {
		super.onResume();
		
		initview();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mLoginRev);
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
		new Thread(login_runnable).start();
		
//		Intent act = new Intent();
//		act.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		act.setClass(LoginActivity.this, SmartLockActivity.class);
//		mContext.startActivity(act);
//		finish();
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
	
	
	