package com.sherman.smartlockex.ui.setting;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.sherman.smartlockex.ui.common.SmartLockFragment;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import com.sherman.smartlockex.ui.util.MyAlertDialog;
import com.sherman.smartlockex.ui.util.RefreshableView;
import com.sherman.smartlockex.ui.util.RefreshableView.PullToRefreshListener;
import com.sherman.smartlockex.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.smartplug.PubStatus;

public class SettingFragment extends SmartLockFragment
		implements
			View.OnClickListener {
	
	private TextView tvUserName = null;
	private Button mBtnLogout = null;

	private static SettingFragment mFragment = null;

	public static SettingFragment newInstance() {
		if (null == mFragment) {
			mFragment = new SettingFragment();
		}
		return mFragment;
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

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				qryPlugsFromServer();
			}
		}, 1);
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
		RelativeLayout layout1 = (RelativeLayout) mFragmentView
				.findViewById(R.id.detail_person_num_layout);
		layout1.setOnClickListener(this);

		RelativeLayout layoutFeedback = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_mgr_feedback);
		layoutFeedback.setOnClickListener(this);

		RelativeLayout layoutUpdate = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_mgr_update);
		layoutUpdate.setOnClickListener(this);

		RelativeLayout lay_scene_management = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_scene_management);
		lay_scene_management.setOnClickListener(this);
		if (PubDefine.RELEASE_VERSION == true && false) {
			lay_scene_management.setVisibility(View.GONE);
		}

		RelativeLayout lay_update_debug = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_update_debug);
		lay_update_debug.setOnClickListener(new View.OnClickListener() {
			// 需要监听几次点击事件数组的长度就为几
			// 如果要监听双击事件则数组长度为2，如果要监听3次连续点击事件则数组长度为3...
			long[] mHints = new long[5]; // 初始全部为0
			@Override
			public void onClick(View v) {
				// 将mHints数组内的所有元素左移一个位置
				System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
				// 获得当前系统已经启动的时间
				mHints[mHints.length - 1] = SystemClock.uptimeMillis();
				if (SystemClock.uptimeMillis() - mHints[0] <= 500) {
					// Toast.makeText(mContext, "当你点击三次之后才会出现",
					// Toast.LENGTH_SHORT)
					// .show();
					update(1); // DEBUG
				}
			}
		});

		RelativeLayout layouthelp = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_mgr_help);
		layouthelp.setOnClickListener(this);

		// -- For Debug Version
		final RelativeLayout layoutiat = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_mgr_iat);
		layoutiat.setOnClickListener(this);

		RelativeLayout layoutconfig = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_mgr_config);
		layoutconfig.setOnClickListener(this);

		RelativeLayout layoutdueros = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_dueros_config);
		layoutdueros.setOnClickListener(this);

		if (true == PubDefine.RELEASE_VERSION || true) {
			layoutiat.setVisibility(View.GONE);
			layoutconfig.setVisibility(View.GONE);
			layoutdueros.setVisibility(View.GONE);
			((RelativeLayout) mFragmentView.findViewById(R.id.layout_items3))
					.setVisibility(View.GONE);
		}

		RelativeLayout layoutAboutus = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_mgr_about);
		layoutAboutus.setOnClickListener(this);

		RelativeLayout layoutShare = (RelativeLayout) mFragmentView
				.findViewById(R.id.lay_mgr_share);
		layoutShare.setOnClickListener(this);

		TextView layoutLogout = (TextView) mFragmentView
				.findViewById(R.id.detail_user_logout);
		layoutLogout.setOnClickListener(this);

		tvUserName = (TextView) mFragmentView
				.findViewById(R.id.detail_user_name);
		if (PubStatus.g_CurUserName == null
				|| PubStatus.g_CurUserName.isEmpty()) {
			tvUserName.setText("Test");
		} else {
			tvUserName.setText(PubStatus.g_CurUserName);
		}

		mBtnLogout = (Button) mFragmentView.findViewById(R.id.login_out);
		mBtnLogout.setOnClickListener(this);
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
}
