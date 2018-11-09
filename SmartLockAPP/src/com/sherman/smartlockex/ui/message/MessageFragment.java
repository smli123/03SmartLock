package com.sherman.smartlockex.ui.message;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sherman.smartlockex.dataprovider.MessageDeviceHelper;
import com.sherman.smartlockex.dataprovider.MessageSystemHelper;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.MessageDeviceDefine;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockFragment;
import com.sherman.smartlockex.ui.dev.LockDetailActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import com.sherman.smartlockex.ui.util.MyAlertDialog;
import com.sherman.smartlockex.ui.util.RefreshableView;
import com.sherman.smartlockex.ui.util.RefreshableView.PullToRefreshListener;
import com.sherman.smartlockex.R;

public class MessageFragment extends SmartLockFragment
		implements
			View.OnClickListener {
	
	private MessageDeviceHelper deviceHelper = null;
	private MessageSystemHelper systemHelper = null;
	private RelativeLayout rl_message_device;
	private RelativeLayout rl_message_system;
	
	private TextView tv_message_device_new_info;
	private TextView tv_message_system_new_info;

	private String mFocusLockId = "0";

//	private RefreshableView mRefreshableView = null;

	private static MessageFragment mFragment = null;

	public static MessageFragment newInstance() {
		if (null == mFragment) {
			mFragment = new MessageFragment();
		}
		return mFragment;
	}

	public static void delete() {
		if (null != mFragment) {
			mFragment = null;
		}
	}
	
	private BroadcastReceiver mLoginRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.LOCK_NOTIFY_ALARM_BROADCAST)) {
					String lockName = intent.getStringExtra("LOCKNAME");
					int iAlarmData = intent.getIntExtra("ALARMDATA", 0);
					String alarmData = PubFunc.getStringMessageData(iAlarmData);
					
					tv_message_device_new_info.setText("[" + lockName + "] " + alarmData);
				}
			}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
		
		deviceHelper = new MessageDeviceHelper(mContext);
		systemHelper = new MessageSystemHelper(mContext);
		
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				qryMessagesFromServer();
//			}
//		}, 1);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOCK_NOTIFY_ALARM_BROADCAST);
		mContext.registerReceiver(mLoginRev, filter);
	}

	private Handler mTimeoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (0 == msg.what) {
				timeoutHandler.removeCallbacks(timeoutProcess);
			}
		}
	};

	private void qryMessagesFromServer() {
		registerTimeoutHandler(mTimeoutHandler);
//
//		StringBuffer sb = new StringBuffer();
//		sb.append(SmartLockMessage.CMD_SP_QRYPLUG)
//				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
//				.append(PubStatus.g_CurUserName);
//
//		sendMsg(true, sb.toString(), true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (mFragmentView == null) {
			mFragmentView = inflater.inflate(R.layout.fragment_message, container,
					false);
		}
		
		initView();

		return mFragmentView;
	}
	
	private void initView() {
		rl_message_device = (RelativeLayout)mFragmentView.findViewById(R.id.rl_message_device);
		rl_message_system = (RelativeLayout) mFragmentView.findViewById(R.id.rl_message_system);
		tv_message_device_new_info = (TextView) mFragmentView.findViewById(R.id.tv_message_device_new_info);
		tv_message_system_new_info = (TextView) mFragmentView.findViewById(R.id.tv_message_system_new_info);
		
		rl_message_device.setOnClickListener(this);
		rl_message_system.setOnClickListener(this);
		
		// 设置为最新的额消息
		MessageDeviceDefine item = deviceHelper.getNewMessage(PubStatus.g_CurUserName);
		if (item ==  null) {
			tv_message_device_new_info.setText(SmartLockApplication.getInstance().getString(R.string.smartlock_no_message_device));
		} else {
			String lockName = item.mDeviceName;
			String alarmData = PubFunc.getStringMessageData(item.mMessageData);
			tv_message_device_new_info.setText("[" + lockName + "] " + alarmData);
		}
	}
	
	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			
		};
	};

	@Override
	public void onResume() {
		super.onResume();
		SmartLockApplication.resetTask();
		
		initView();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()) {
		case R.id.rl_message_device:
			intent.setClass(mContext, ActivityMessageDevice.class);
			break;
		case R.id.rl_message_system:
			intent.setClass(mContext, ActivityMessageSystem.class);
			break;
		}
		
		mContext.startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.unregisterReceiver(mLoginRev);
	}

	private Handler mPressHandler = new Handler() {
		public void handleMessage(Message msg) {
			
		};
	};
}
