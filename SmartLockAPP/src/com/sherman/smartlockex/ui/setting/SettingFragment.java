package com.sherman.smartlockex.ui.setting;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockFragment;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import com.sherman.smartlockex.ui.util.MyAlertDialog;
import com.sherman.smartlockex.ui.util.RefreshableView;
import com.sherman.smartlockex.ui.util.RefreshableView.PullToRefreshListener;
import com.sherman.smartlockex.R;

public class SettingFragment extends SmartLockFragment
		implements
			View.OnClickListener {
	
	private TextView tv_username = null;
	
	private RelativeLayout rl_person_info;
	private RelativeLayout rl_mgr_update;
	private RelativeLayout rl_mgr_feedback;
	private RelativeLayout rl_mgr_help;
	private RelativeLayout rl_mgr_about;
	
	private Button btn_logout = null;

	private static SettingFragment mFragment = null;
	private Handler mHandler = null;

	public static SettingFragment newInstance() {
		if (null == mFragment) {
			mFragment = new SettingFragment();
		}
		return mFragment;
	}
	
	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	public static void delete() {
		if (null != mFragment) {
			mFragment = null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		
	}
	
	private Handler mTimeoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (0 == msg.what) {
				timeoutHandler.removeCallbacks(timeoutProcess);
			}
		}
	};

	private void qryPlugsFromServer() {
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
			mFragmentView = inflater.inflate(R.layout.fragment_setting, container,
					false);
		}
		
		initView();

		return mFragmentView;
	}
	
	private void initView() {
		tv_username = (TextView) mFragmentView.findViewById(R.id.tv_username);
		if (PubStatus.g_CurUserName == null
				|| PubStatus.g_CurUserName.isEmpty()) {
			tv_username.setText("Test");
		} else {
			tv_username.setText(PubStatus.g_CurUserName);
		}

		rl_person_info = (RelativeLayout) mFragmentView
				.findViewById(R.id.rl_person_info);
		rl_mgr_update = (RelativeLayout) mFragmentView
				.findViewById(R.id.rl_mgr_update);
		rl_mgr_feedback = (RelativeLayout) mFragmentView
				.findViewById(R.id.rl_mgr_feedback);
		rl_mgr_help = (RelativeLayout) mFragmentView
				.findViewById(R.id.rl_mgr_help);
		rl_mgr_about = (RelativeLayout) mFragmentView
				.findViewById(R.id.rl_mgr_about);
		
		btn_logout = (Button) mFragmentView
				.findViewById(R.id.btn_logout);

		rl_person_info.setOnClickListener(this);
		rl_mgr_update.setOnClickListener(this);
		rl_mgr_feedback.setOnClickListener(this);
		rl_mgr_help.setOnClickListener(this);
		rl_mgr_about.setOnClickListener(this);

		btn_logout.setOnClickListener(this);
	}

	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
		};
	};

	@Override
	public void onResume() {
		super.onResume();
		SmartLockApplication.resetTask();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()) {
		case R.id.rl_person_info :
			intent = new Intent();
			intent.setClass(this.getActivity(),
					ActivityAccountSecurity.class);
			startActivity(intent);
			break;
		case R.id.rl_mgr_update:
			this.update(0);
			break;
		case R.id.rl_mgr_feedback:
			intent = new Intent();
			intent.setClass(this.getActivity(),
					ActivityFeedback.class);
			startActivity(intent);
			break;
		case R.id.rl_mgr_help:
			intent = new Intent();
			intent.setClass(this.getActivity(),
					ActivityHelp.class);
			startActivity(intent);
			break;
		case R.id.rl_mgr_about:
			intent = new Intent();
			intent.setClass(this.getActivity(),
					ActivityAboutUs.class);
			startActivity(intent);
			break;
		case R.id.btn_logout:
			logout();
			break;
		}
	}
	
	public void update(int verType) {
		// 使用网址进行下载更新
		// downloadAppFile();

		// 自定义自动更新机制
		checkUpdate(verType);
	}

	private void checkUpdate(int verType) {
		UpdateManager update = new UpdateManager(mContext, verType);
		update.XMLFile_UpdateCheck();

	}

	private void downloadAppFile() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri
				.parse("http://fx.thingzdo.com/download/SmartPlug_UDP.apk");
		intent.setData(content_url);
		startActivity(intent);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private Handler mPressHandler = new Handler() {
		public void handleMessage(Message msg) {
			
		};
	};
	
	private void logout() {
		mHandler.sendEmptyMessage(0);
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_LOGINOUT)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName);
		sendMsg(true, sb.toString(), false);
	}
}
