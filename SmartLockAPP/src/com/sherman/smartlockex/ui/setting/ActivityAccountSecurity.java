package com.sherman.smartlockex.ui.setting;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ActivityAccountSecurity extends TitledActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_setting_account_mgr, false);
		SmartLockApplication.resetTask();
		SmartLockApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartlock_goback, R.drawable.title_btn_selector, this);
		setTitle(R.string.app_account_security);
		
		RelativeLayout layoutModifyPwd = (RelativeLayout) findViewById(R.id.lay_modify_pwd);
		layoutModifyPwd.setOnClickListener(this);
		
		RelativeLayout layoutModifyEmail = (RelativeLayout) findViewById(R.id.lay_modify_email);
		layoutModifyEmail.setOnClickListener(this);	
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TextView txtEmail = (TextView)findViewById(R.id.txt_modify_email);
		txtEmail.setText(PubStatus.g_userEmail);		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (view.getId()) {
		case R.id.titlebar_leftbutton:
			finish();
			break;
        case R.id.lay_modify_pwd:
        	intent = new Intent();
        	intent.setClass(this, ActivityModifyPasswd.class);
        	startActivity(intent);
        	break;
        case R.id.lay_modify_email:
        	intent = new Intent();
        	intent.setClass(this, ActivityModifyEmail.class);
        	startActivity(intent);
        	break;	
		}
	}
}
