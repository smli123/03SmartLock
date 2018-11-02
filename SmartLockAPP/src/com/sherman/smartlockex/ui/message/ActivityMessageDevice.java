package com.sherman.smartlockex.ui.message;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.MessageDeviceHelper;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.MessageDeviceDefine;
import com.sherman.smartlockex.ui.common.MessageSystemDefine;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.dev.AdapterDevlist;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import com.sherman.smartlockex.ui.util.MyAlertDialog;

public class ActivityMessageDevice extends TitledActivity implements OnClickListener {
	private Context mContext = null;
	private MessageDeviceHelper mMessageHelper = null;
	
	private ListView lv_message;
	private ArrayList<MessageDeviceDefine> mMessage_list = new  ArrayList<MessageDeviceDefine>(); 
	
	private SmartLockApplication app = SmartLockApplication.getInstance();
	
	private BroadcastReceiver mLoginRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.LOCK_NOTIFY_ALARM_BROADCAST)) {
				initView();
				}
			}
	};
	
	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				int status = msg.arg1;
//				String str_status = "未知状态";
//				if (status == 0) {
//					str_status = "门锁已经打开";
//				} else if (status == 1) {
//					str_status = "门锁已经关闭";
//				}
//				 
//				tv_status.setText(str_status);
				break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_message_device,
				false);
		SmartLockApplication.resetTask();
		SmartLockApplication.getInstance().addActivity(this);
        mContext = this;

		setTitleLeftButton(R.string.smartlock_goback,
				R.drawable.title_btn_selector, this);
		setTitleRightButton(R.string.smartlock_clear_message,
				R.drawable.title_btn_selector, this);
        
        mMessageHelper = new MessageDeviceHelper(mContext);
        
        initView();
        
//        testInsertInfo();
        
        IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOCK_NOTIFY_ALARM_BROADCAST);
		registerReceiver(mLoginRev, filter);
    }
    
    
    private void testInsertInfo() {
		MessageDeviceDefine item = new MessageDeviceDefine();
		item.mMessageID = "100";
		item.mUserName = "lishimin";
		item.mDeviceID = "641001";
		item.mDeviceName = "TestOK1";
		item.mMessageType = 1;
		item.mDetail = "【开锁成功】";
		
		mMessageHelper.addMessage(item);
	}
    
    @Override
    protected void onResume () {
    	super.onResume();
    	
    	initView();
    	updateUI();
    }
    
    private void updateUI() {
    	setTitle(app.getString(R.string.smartlock_message_device));
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.titlebar_leftbutton :
			disconnectSocket();
			finish();
			break;
		case R.id.titlebar_rightbutton :
			
			new MyAlertDialog(mContext)
			.builder()
			.setMsg(this.getString(R.string.smartlock_clear_message_confirm))
			.setPositiveButton(this.getString(R.string.smartlock_ok),
					new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mMessageHelper.clearMessage();
					initView();
				}
			})
			.setNegativeButton(
					this.getString(R.string.smartlock_cancel),
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {

						}
					}).setCancelable(true).show();
			break;
		}
	}
	
	private void disconnectSocket() {
		return;
	}
	
	private void initView() {
		lv_message = (ListView) findViewById(R.id.lv_message);
		
		getAllMessage();
		
		MessageDevicelistAdapter adapter = new MessageDevicelistAdapter(mContext, mMessage_list);
		lv_message.setAdapter(adapter);
	}
	
	private void getAllMessage() {
		mMessage_list.clear();
		mMessage_list = mMessageHelper.getAllMessage(PubStatus.getUserName());
	}

	private void query_Message() {
//		StringBuffer sb = new StringBuffer();
//		sb.append(SmartLockMessage.CMD_SP_OPER_LOCK)
//				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//				.append(PubStatus.getUserName())
//				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//				.append(mLockID)
//				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//				.append(String.valueOf(i_status));
//
//		sendMsg(true, sb.toString(), true);
	}
}