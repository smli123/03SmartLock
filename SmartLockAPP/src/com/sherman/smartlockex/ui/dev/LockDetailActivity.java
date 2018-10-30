package com.sherman.smartlockex.ui.dev;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.internet.UDPReceiver;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.login.LoginActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class LockDetailActivity extends TitledActivity implements OnClickListener {
	private Context mContext = null;
	private SmartLockExLockHelper mLockHelper = null;
	
	private LinearLayout ll_authorize_area;
	private TextView tv_status;
	private TextView tv_log;
	
	private TextView tv_lock_open;
	private TextView tv_lock_close;
	
	private String mLockID = "";
	private SmartLockDefine mLock = null;

	private BroadcastReceiver mLoginRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.LOCK_NOTIFY_STATUS_BROADCAST)) {
					String moduleID = intent.getStringExtra("LOCKID");
					int status = intent.getIntExtra("STATUS", -1);
					int charge = intent.getIntExtra("CHARGE", 0);
					int userType = intent.getIntExtra("USERTYPE", 0);
					String memo = intent.getStringExtra("USERMEMO");
					if (moduleID.equals(mLockID) == true) {
						Message msg = new Message();
						msg.what = 0;
						msg.arg1 = status;
						updateHandler.sendMessage(msg);
					}
				}
			
			if (intent.getAction().equals(PubDefine.LOCK_OPENLOCK_BROADCAST)) {
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("STATUS", -1);
				if (moduleID.equals(mLockID) == true) {
					Message msg = new Message();
					msg.what = 0;
					msg.arg1 = status;
					updateHandler.sendMessage(msg);
				}
			}
		}
	};
	
	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				int status = msg.arg1;
				String str_status = SmartLockApplication.getInstance().getString(R.string.lock_detail_lockstatus_unknown);
				if (status == 0) {
					str_status = SmartLockApplication.getInstance().getString(R.string.lock_detail_lockstatus_closed);
				} else if (status == 1) {
					str_status = SmartLockApplication.getInstance().getString(R.string.lock_detail_lockstatus_opened);
				}
				 
				tv_status.setText(str_status);
				break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_lock,
				false);
		SmartLockApplication.resetTask();
		SmartLockApplication.getInstance().addActivity(this);
        mContext = this;
        
		setTitleLeftButton(R.string.smartlock_goback,
				R.drawable.title_btn_selector, this);
        
        mLockHelper = new SmartLockExLockHelper(mContext);
        
        Intent intent = getIntent();
        mLockID = intent.getStringExtra("LOCKID");
        
        if(mLockID.isEmpty() == false) {
        	mLock = mLockHelper.getSmartLock(mLockID);
        }
        
        initView();
        
        IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOCK_NOTIFY_STATUS_BROADCAST);
		filter.addAction(PubDefine.LOCK_OPENLOCK_BROADCAST);
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

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.titlebar_leftbutton :
			disconnectSocket();
			finish();
			break;
		case R.id.tv_lock_open:		// Open Lock
			setLock(1);
			break;
		case R.id.tv_lock_close:	// Close Lock
			setLock(0);
			break;
		}
	}
	
	private void disconnectSocket() {
		// TODO nothing...
		return;
	}
	
	private void initView() {
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_log = (TextView) findViewById(R.id.tv_log);
		tv_lock_open = (TextView) findViewById(R.id.tv_lock_open);
		tv_lock_close = (TextView) findViewById(R.id.tv_lock_close);
		ll_authorize_area = (LinearLayout) findViewById(R.id.ll_authorize_area);
		
		if (mLock.mRelation == 0) {
			ll_authorize_area.setVisibility(View.VISIBLE);
		} else {
			ll_authorize_area.setVisibility(View.GONE);
		}
		
		tv_lock_open.setOnClickListener(this);
		tv_lock_close.setOnClickListener(this);
		

		Message msg = new Message();
		msg.what = 0;
		msg.arg1 = mLock.mStatus;
		updateHandler.sendMessage(msg);
	}

	private void setLock(int i_status) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_OPEN_LOCK)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mLockID)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(i_status));

		sendMsg(true, sb.toString(), true);
	}
}