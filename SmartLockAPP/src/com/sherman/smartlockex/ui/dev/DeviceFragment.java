package com.sherman.smartlockex.ui.dev;

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

import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.common.SmartLockFragment;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import com.sherman.smartlockex.ui.util.MyAlertDialog;
import com.sherman.smartlockex.ui.util.RefreshableView;
import com.sherman.smartlockex.ui.util.RefreshableView.PullToRefreshListener;
import com.sherman.smartlockex.R;

public class DeviceFragment extends SmartLockFragment
		implements
			View.OnClickListener {
	private SmartLockExLockHelper mLockHelper = null;
	private String mFocusLockId = "0";
	
	private ListView mDevList = null;
	private RefreshableView mRefreshableView = null;

	private static DeviceFragment mFragment = null;

	private String mNewPlugName = "";
	private MyAlertDialog mModifyDlg = null;

	public static DeviceFragment newInstance() {
		if (null == mFragment) {
			mFragment = new DeviceFragment();
		}
		return mFragment;
	}

	public static void delete() {
		if (null != mFragment) {
			mFragment = null;
		}
	}

	private BroadcastReceiver mLoadPlugReveiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != mProgress) {
				mProgress.dismiss();
			}
			if (intent.getAction().equals(PubDefine.LOCK_OPENLOCK_BROADCAST)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				int code = intent.getIntExtra("RESULT", 0);
				int status = intent.getIntExtra("STATUS", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (code) {
					case 0 :
						SmartLockDefine lock = mLockHelper
								.getSmartLock(mFocusLockId);
						if (null != lock) {
							lock.mStatus = status;
							if (0 < mLockHelper.modifySmartLock(lock)) {
								doBackgroundLoad();
							}
						}

						break;
					default :
						PubFunc.thinzdoToast(mContext, message);
						break;
				}
			}

			if (intent.getAction().equals(PubDefine.LOCK_ADDLOCK_BROADCAST)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				if (0 == ret) {
					qryLocksFromServer();
				}
			}

			if (intent.getAction().equals(PubDefine.LOCK_QRYLOCK_BROADCAST)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				if (0 == ret) {
					mLockHelper.clearSmartLock();
				}
				if (null != message && !message.isEmpty()) {
					PubFunc.thinzdoToast(mContext, message);
				}
				doBackgroundLoad();
			}
			
			if (intent.getAction().equals(PubDefine.LOCK_DELETELOCK_BROADCAST)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
					case 0 :
//						if (true == mLockHelper.deleteSmartLock(mFocusLockId)) {
							doBackgroundLoad();
//						}

						break;
					default :
						PubFunc.thinzdoToast(mContext, message);
						break;
				}
			}
			if (intent.getAction().equals(PubDefine.LOCK_MODIFY_LOCKNAME_BROADCAST)) {
				if (null != mProgress) {
					mProgress.dismiss();
				}
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				timeoutHandler.removeCallbacks(timeoutProcess);
				SmartLockDefine item = mLockHelper.getSmartLock(mFocusLockId);
				switch (ret) {
					case 0 :
						item.mName = mNewPlugName;
						if (0 < mLockHelper.modifySmartLock(item)) {
							doBackgroundLoad();
						}

						break;
					default :
						PubFunc.thinzdoToast(mContext, message);
						break;
				}
			}
			if (intent.getAction().equals(PubDefine.LOCK_NOTIFY_STATUS_BROADCAST)) {
				String message = intent.getStringExtra("MESSAGE");
				int ret = intent.getIntExtra("RESULT", 0);
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("STATUS", -1);
				int charge = intent.getIntExtra("CHARGE", 0);
				int userType = intent.getIntExtra("USERTYPE", 0);
				String memo = intent.getStringExtra("USERMEMO");
				SmartLockDefine item = mLockHelper.getSmartLock(moduleID);
				switch (ret) {
				case 0 :
					item.mStatus = status;
					item.mCharge = charge;
					if (0 < mLockHelper.modifySmartLock(item)) {
						doBackgroundLoad();
					}
					break;
				default :
					PubFunc.thinzdoToast(mContext, message);
					break;
			}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
		mLockHelper = new SmartLockExLockHelper(mContext);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOCK_QRYLOCK_BROADCAST);
		filter.addAction(PubDefine.LOCK_MODIFY_LOCKNAME_BROADCAST);
		filter.addAction(PubDefine.LOCK_DELETELOCK_BROADCAST);
		filter.addAction(PubDefine.LOCK_ADDLOCK_BROADCAST);
		filter.addAction(PubDefine.LOCK_NOTIFY_STATUS_BROADCAST);
		filter.addAction(PubDefine.LOCK_NOTIFY_ONLINE_BROADCAST);
		mContext.registerReceiver(mLoadPlugReveiver, filter);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				qryLocksFromServer();
			}
		}, 1);
	}

	private Handler mTimeoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (0 == msg.what) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				if (null != mRefreshableView) {
					mRefreshableView.finishRefreshing();
				}
			}
		}
	};

	private void qryLocksFromServer() {
		registerTimeoutHandler(mTimeoutHandler);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_QRYLOCK)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName);

		sendMsg(true, sb.toString(), true);
	}

	private void setPlugsOffline() {
		mLockHelper.setAllLocksOffline();
		doBackgroundLoad();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (mFragmentView == null) {
			mFragmentView = inflater.inflate(R.layout.fragment_dev, container,
					false);
		}
		
		mDevList = (ListView) mFragmentView.findViewById(R.id.dev_list);

		mRefreshableView = (RefreshableView) mFragmentView
				.findViewById(R.id.refreshable_view);
		mRefreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				qryLocksFromServer();
			}
		}, 0);
		
		// Test for all
//		testInsertInfo();
		
		return mFragmentView;
	}
	
	private void testInsertInfo() {
		SmartLockDefine item = new SmartLockDefine();
		item.mLockID = "642001";
		item.mUserName = "lishimin";
		item.mName = "TestOK1";
		item.mStatus = 1;
		item.mOnline = true;
		item.mAddress = "33:44:65:00:FF";
		item.mType = "1";
		item.mCharge = 81;
		item.mRelation = 0;
		mLockHelper.addSmartLock(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		SmartLockApplication.resetTask();
		PubFunc.log("ControlFragment onResume");
		doBackgroundLoad();
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContext.unregisterReceiver(mLoadPlugReveiver);
	}

	private void doBackgroundLoad() {
		AsyncTask<Void, Void, ArrayList<SmartLockDefine>> loadData = new AsyncTask<Void, Void, ArrayList<SmartLockDefine>>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected ArrayList<SmartLockDefine> doInBackground(Void... arg0) {
				ArrayList<SmartLockDefine> locks = mLockHelper
						.getAllSmartLock(PubStatus.g_CurUserName);

				return locks;
			}

			@Override
			protected void onPostExecute(ArrayList<SmartLockDefine> result) {
				super.onPostExecute(result);
				if (null != result) {
					AdapterDevlist adapter = new AdapterDevlist(mContext,
							result, mPressHandler);
					mDevList.setAdapter(adapter);
				}

				if (null != mRefreshableView) {
					mRefreshableView.finishRefreshing();
				}
			}

		};
		loadData.execute();
	}

	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			setPlugsOffline();
		};
	};

	private Handler mPressHandler = new Handler() {
		public void handleMessage(Message msg) {
			mFocusLockId = (String) msg.obj;
			SmartLockDefine plug = mLockHelper.getSmartLock(mFocusLockId);
			PubFunc.log("mFocusLockId=" + mFocusLockId);
			if (0 == msg.what) { 		// 修改模块名字
				if (null != plug) {
					modifyName(plug.mName);
				}
			}
			if (1 == msg.what) { 		// 删除模块
				if (null != plug) {
					String str_delete = SmartLockApplication.getContext().getString(R.string.smartlock_ctrl_delete);
					String str_ok = SmartLockApplication.getContext().getString(R.string.smartlock_confirm);
					String str_cancel = SmartLockApplication.getContext().getString(R.string.smartlock_cancel);
					final MyAlertDialog dialog = new MyAlertDialog(mContext)
							.builder().setTitle(str_delete)
							.setNegativeButton(str_cancel, new OnClickListener() {
								@Override
								public void onClick(View v) {

								}
							});
					dialog.setPositiveButton(str_ok, new OnClickListener() {
						@Override
						public void onClick(View v) {
							deletePlug(mFocusLockId);
						}
					});
					dialog.show();

				}
			}
		};
	};

	View.OnClickListener modifyClick = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (null == mModifyDlg) {
				return;
			}
			String text = mModifyDlg.getResult();
			if (!text.isEmpty()) {
				mNewPlugName = mModifyDlg.getResult();

				// 校验 NewPlugName：中英文占用的字节数必须小于20（最大20个byte）
				if (mNewPlugName.getBytes().length > 20) {
					PubFunc.thinzdoToast(
							mContext,
							getString(R.string.smartlock_ctrl_mod_plugname_length_too_long));
					return;
				}

				if (true == mLockHelper.isLockExist(PubStatus.g_CurUserName,
						mNewPlugName)) {
					PubFunc.thinzdoToast(mContext,
							getString(R.string.smartlock_ctrl_samename_exist));
					return;
				}

				mProgress = PubFunc.createProgressDialog(mContext,
						getString(R.string.smartlock_modify_name), false);
				mProgress.show();

				StringBuffer sb = new StringBuffer();
				sb.append(SmartLockMessage.CMD_SP_MODYLOCK)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(PubStatus.g_CurUserName)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(mFocusLockId)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(mNewPlugName);
				sendMsg(true, sb.toString(), true);
			}
		}
	};

	private void modifyName(String name) {
		mModifyDlg = new MyAlertDialog(mContext);
		mModifyDlg
				.builder()
				.setCancelable(true)
				.setTitle(getString(R.string.smartlock_modify_name))
				.setEditText(name)
				.setPositiveButton(mContext.getString(R.string.smartlock_ok),
						modifyClick)
				.setNegativeButton(
						mContext.getString(R.string.smartlock_cancel),
						new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {

							}

						}).show();
	}

	private void deletePlug(String plugId) {
		mErrorMsg = getString(R.string.smartlock_ctrl_delete_fail);
		mProgress = PubFunc.createProgressDialog(mContext,
				mContext.getString(R.string.smartlock_ctrl_delete), false);
		mProgress.show();

		if (mFocusLockId.equalsIgnoreCase("0")) {
			int a = 0;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_DELLOCK)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(plugId);
		sendMsg(true, sb.toString(), true);
	}
}
