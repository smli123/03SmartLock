package com.sherman.smartlockex.ui.dev;

import javax.mail.Quota.Resource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.dataprovider.SmartLockExPasswordHelper;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;


public class LockPasswordAddAvtivity extends TitledActivity implements OnClickListener {
	private Context mContext = null;
	private SmartLockExLockHelper mLockHelper = null;
	private SmartLockExPasswordHelper mPasswordHelper = null;
	
	private RadioButton rb_type_temp;
	private RadioButton rb_type_forever;
	private int i_password_type = 2;	// 0: all 1: 永久密码   2：临时密码
	
	private EditText et_username;
	private EditText et_password;
	private TextView tv_begintime;
	private TextView tv_endtime;
	private EditText et_memo;
	
	private ImageView iv_username_del;
	private ImageView iv_password_del;
	private ImageView iv_begintime_del;
	private ImageView iv_endtime_del;
	private ImageView iv_memo_del;
	
	private Button btn_ok;
	
	private String mLockID = "";
	private SmartLockDefine mLock = null;
	
	private BroadcastReceiver mLoginRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != mProgress) {
				mProgress.dismiss();
			}
			if (intent.getAction().equals(PubDefine.LOCK_ADDPASSWORD_BROADCAST)) {
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("RESULT", -1);
				if (status == 0) {
					if (moduleID.equals(mLockID) == true) {
						Message msg = new Message();
						msg.what = 0;
						msg.arg1 = status;
						mHandler.sendMessage(msg);
					}
				} else {
					String message = intent.getStringExtra("MESSAGE");
					PubFunc.thinzdoToast(mContext, message);
				}
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				finish();
				break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_password_add,
				false);
		SmartLockApplication.resetTask();
		SmartLockApplication.getInstance().addActivity(this);
        mContext = this;
        
		setTitleLeftButton(R.string.smartlock_goback,
				R.drawable.title_btn_selector, this);
        
		mLockHelper = new SmartLockExLockHelper(mContext);
        mPasswordHelper = new SmartLockExPasswordHelper(mContext);
        
        Intent intent = getIntent();
        mLockID = intent.getStringExtra("LOCKID");
        
        if(mLockID.isEmpty() == false) {
        	mLock = mLockHelper.get(mLockID);
        }
        
        initView();
        
        IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOCK_ADDPASSWORD_BROADCAST);
		registerReceiver(mLoginRev, filter);
		
    }
    
    @Override
    protected void onResume () {
    	super.onResume();
    	
    	updateUI();
    }
    
    private void updateUI() {
    	setTitle(mLock.mName);
    }

	private void disconnectSocket() {
		return;
	}
	
	private void initView() {
		rb_type_temp = (RadioButton) findViewById(R.id.rb_type_temp);
		rb_type_forever = (RadioButton) findViewById(R.id.rb_type_forever);
		updateTypeRadio();
		
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		tv_begintime = (TextView) findViewById(R.id.tv_begintime);
		tv_endtime = (TextView) findViewById(R.id.tv_endtime);
		et_memo = (EditText) findViewById(R.id.et_memo);
		
		iv_username_del = (ImageView) findViewById(R.id.iv_username_del);
		iv_password_del = (ImageView) findViewById(R.id.iv_password_del);
		iv_begintime_del = (ImageView) findViewById(R.id.iv_begintime_del);
		iv_endtime_del = (ImageView) findViewById(R.id.iv_endtime_del);
		iv_memo_del = (ImageView) findViewById(R.id.iv_memo_del);
		
		String begintime = PubFunc.getTimeString("yyyy-MM-dd 00:00:00");
		String endtime = PubFunc.getTimeString("yyyy-MM-dd 23:59:59");
		tv_begintime.setText(begintime);
		tv_endtime.setText(endtime);
		
		btn_ok = (Button) findViewById(R.id.btn_ok);
		
		iv_begintime_del.setVisibility(View.GONE);
		iv_endtime_del.setVisibility(View.GONE);
		
		rb_type_temp.setOnClickListener(this);
		rb_type_forever.setOnClickListener(this);
		
		tv_begintime.setOnClickListener(this);
		tv_endtime.setOnClickListener(this);
		
		iv_username_del.setOnClickListener(this);
		iv_password_del.setOnClickListener(this);
		iv_memo_del.setOnClickListener(this);
		
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.titlebar_leftbutton :
			disconnectSocket();
			finish();
			break;
		case R.id.btn_ok:		// Add Password
			addPassword();
			break;
		case R.id.iv_username_del:
			et_username.setText("");
			break;
		case R.id.iv_password_del:
			et_password.setText("");
			break;
		case R.id.iv_memo_del:
			et_memo.setText("");
			break;
		case R.id.tv_begintime:
			break;
		case R.id.tv_endtime:
			break;
		case R.id.rb_type_temp:
			i_password_type = 0;
			updateTypeRadio();
			break;
		case R.id.rb_type_forever:
			i_password_type = 1;
			updateTypeRadio();
			break;
		}
	}
	
	private void updateTypeRadio() {
		 if (i_password_type == 1) {		// 永久密码
			rb_type_temp.setChecked(false);
			rb_type_forever.setChecked(true);
		} else if (i_password_type == 2) {	// 临时密码
			rb_type_temp.setChecked(true);
			rb_type_forever.setChecked(false);
		}
	}

	private void addPassword() {
		// Cookie,CREATE_PASSWORD,user_name,设备ID, [密码序号], [密码用户] , [密码类型], [密码内容],[开始时间],[结束时间], [密码备注]#
		int passwordID = mPasswordHelper.getMaxID(mLockID);
		String username = et_username.getText().toString();
		String password = et_password.getText().toString();
		String begintime = tv_begintime.getText().toString();
		String endtime = tv_endtime.getText().toString();
		String memo = et_memo.getText().toString();
		
		SmartLockApplication me = SmartLockApplication.getInstance();
		if (username.isEmpty()) {
			PubFunc.thinzdoToast(mContext, me.getString(R.string.password_username_isnotnull));
		}
		
		if (password.isEmpty()) {
			PubFunc.thinzdoToast(mContext, me.getString(R.string.password_password_isnotnull));
		}
		
//		if (i_password_type == 1) {	// 永久模式
//			begintime = "0";
//			endtime = "0";
//		}
		
		if (memo.isEmpty()) {
			memo = "default";
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_ADDPASSWORD)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mLockID)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(passwordID))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(username)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(i_password_type))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(password)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(begintime)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(endtime)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(memo);

		sendMsg(true, sb.toString(), true);
	}
	
}