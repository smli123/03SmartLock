package com.sherman.smartlockex.ui.dev;

import java.util.ArrayList;
import java.util.List;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class AddSocketActivity2 extends TitledActivity
		implements
			OnClickListener {
	private WifiManager wifiManager;
	private List<ScanResult> listData;
	private String mSSId = "";
	private String mPlugId = "";

	// 给Wifi直连模式使用的存储变量
	private String mStored_SSID = "";
	private String mStored_PLUGID = "";

	private SmartLockDefine mWifiDevice = null;
	private ListView mWifiListView = null;
	private String esc_activity = "";

	private SharedPreferences mWifiInfo = null;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (null != mProgress) {
				mProgress.dismiss();
			}
			if (msg.what == 0) {
				PubFunc.thinzdoToast(AddSocketActivity2.this,
						getString(R.string.smartlock_oper_noscanplug));
			} else {
				AddSocketActivity2.this.BindListData();
			}
		}
	};

	// Wifi 直连模式下，SSID和PlugID
	private void saveWiFiStore() {
		SharedPreferences.Editor editor = mWifiInfo.edit();
		editor.putString("WIFI_PLUGSSID", mStored_SSID);
		editor.putString("WIFI_PLUGID", mStored_PLUGID);
		editor.commit();
	}

	private void loadWiFiStore() {
		mStored_SSID = mWifiInfo.getString("WIFI_PLUGSSID", "");
		mStored_PLUGID = mWifiInfo.getString("WIFI_PLUGID", "0");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_add_socket2, false);
		SmartLockApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartlock_goback,
				R.drawable.title_btn_selector, this);
		setTitleRightButton(R.string.smartlock_refresh,
				R.drawable.title_btn_selector, this);
		this.setTitle(getString(R.string.smartlock_oper_devices));
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (wifiManager != null && !wifiManager.isWifiEnabled()) {
			PubFunc.thinzdoToast(this,
					getString(R.string.smartlock_oper_openwifi));
			wifiManager.setWifiEnabled(true);
		}
		mPlugId = "0";
		mWifiListView = (ListView) findViewById(R.id.listView);

		SmartLockApplication.resetTask();
		Intent intent = getIntent();
		esc_activity = intent.getStringExtra("SOURCE");

		mWifiInfo = getSharedPreferences("WiFiStore", Activity.MODE_PRIVATE);
		loadWiFiStore();
	}

	private void doSearchPlug() {
		listData = new ArrayList<ScanResult>();
		listData.clear();
		BindListData();

		mProgress = PubFunc.createProgressDialog(this,
				getString(R.string.smartlock_oper_scanning), false);
		AsyncTask<Void, Void, Void> searchPlug = new AsyncTask<Void, Void, Void>() {

			private boolean filterScanResult(List<ScanResult> list) {
				if (list == null) {
					return false;
				}
				listData.clear();
				for (int i = 0; list != null && i < list.size(); ++i) {
					if (!list.get(i).SSID.startsWith(PubDefine.DEVICE_SSID_PRE)) {
						continue;
					}
					String ssid = list.get(i).SSID;
					if (true == isSSIDExist(listData, ssid)) {
						continue;
					}

					listData.add(list.get(i));
				}

				return listData.size() == 0 ? false : true;
			}

			private boolean isSSIDExist(final List<ScanResult> list,
					final String ssid) {
				for (int i = 0; i < listData.size(); ++i) {
					if (listData.get(i).SSID.equals(ssid)) {
						return true;
					}
				}
				return false;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mProgress.show();
			}

			@Override
			protected void onCancelled(Void result) {
				super.onCancelled(result);
				if (null != mProgress) {
					mProgress.dismiss();
				}
			}
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (null != mProgress) {
					mProgress.dismiss();
				}
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				int count = 0;
				List<ScanResult> wifiList = null;
				listData.clear();
				do {
					try {
						boolean b_result = wifiManager.startScan();
						PubFunc.log(b_result
								? "Scan Wifi is succeed."
								: "Scan Wifi is Fail.");

						wifiList = wifiManager.getScanResults();
						if (null != wifiList) {
							PubFunc.log("Scan Result Size is "
									+ String.valueOf(wifiList.size()));
						}

						if (null != wifiList && 0 < wifiList.size()) {
							if (true == filterScanResult(wifiList)) {
								break;
							}
						}
						count++;
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (count < 1);
				// filterScanResult(wifiList);
				if (listData.size() == 0) {
					handler.sendEmptyMessage(0);
				} else {
					handler.sendEmptyMessage(1);
				}
				return null;
			}
		};
		searchPlug.execute();
	}
	public void BindListData() {
		mWifiListView.setAdapter(new MyAdapter(this, listData));
	}

	private void logout() {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_LOGINOUT)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName);
		sendMsg(true, sb.toString(), false);
	}

	public class AddOnClickListener implements OnClickListener {

		public AddOnClickListener(String ssid0) {
			mSSId = ssid0;
		}
		@Override
		public void onClick(View view) {
			TextView ssidView = (TextView) view.findViewById(R.id.textView);
			mSSId = ssidView.getText().toString();
			final Intent intent = new Intent();
			if (PubDefine.SmartPlug_Connect_Mode.Internet == PubDefine.g_Connect_Mode) {
//				intent.setClass(AddSocketActivity2.this,
//						AddSocketActivity3.class);
//				intent.putExtra("SSID", mSSId);
//				ListView listView = (ListView) findViewById(R.id.listView);
//				intent.putExtra("PLUGCOUNT", listView.getCount());
//				intent.putExtra("CONNECT_MODE",
//						"PubDefine.SmartPlug_Connect_Mode.Internet");
//				startActivity(intent);
//				finish();
//				/*
//				 * new MyAlertDialog(AddSocketActivity2.this).builder()
//				 * .setCancelable(true)
//				 * .setMsg(getString(R.string.smartplug_mgr_logout))
//				 * .setPositiveButton
//				 * (AddSocketActivity2.this.getString(R.string.smartplug_ok),
//				 * new View.OnClickListener(){
//				 * 
//				 * @Override public void onClick(View view) { logout();
//				 * 
//				 * intent.setClass(AddSocketActivity2.this,
//				 * AddSocketActivity3.class); intent.putExtra("SSID", mSSId);
//				 * ListView listView = (ListView) findViewById(R.id.listView);
//				 * intent.putExtra("PLUGCOUNT", listView.getCount());
//				 * intent.putExtra("CONNECT_MODE",
//				 * "PubDefine.SmartPlug_Connect_Mode.Internet");
//				 * startActivity(intent); finish(); } })
//				 * .setNegativeButton(AddSocketActivity2
//				 * .this.getString(R.string.smartplug_cancel), new
//				 * View.OnClickListener(){
//				 * 
//				 * @Override public void onClick(View arg0) {
//				 * 
//				 * } }) .show();
//				 */

			} else if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) { // 2��
				mStored_SSID = mSSId;
				connectToPlug(mStored_SSID, wifiConnect);
			}
		}
	}

	private void connectToPlug(final String ssid, final Handler handler) {
		WifiAdmin wifiAdmin = new WifiAdmin(this);
		if (null != mProgress) {
			mProgress.dismiss();
			mProgress = null;
		}
		mProgress = PubFunc.createProgressDialog(AddSocketActivity2.this,
				getString(R.string.smartlock_oper_connect_plug), false);

		if (!wifiAdmin.isWifiEnabled()) {
			wifiAdmin.openWifi();
		}
		// lishimin 针对【重选】，切换到插座的SSID；
		WifiConfiguration plugConfig = wifiAdmin.CreateWifiInfo(ssid, "", 1);
		wifiAdmin.addNetwork(plugConfig);

		mProgress.show();

		// lishimin 建立连接： 默认值： 192.168.4.1:6002
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SmartLockWifiMgr.createWifiSocket(handler, "", 0);
			}
		}, 6000); // 需要有个等待获取 IP地址的时间；
	}

	public class MyAdapter extends BaseAdapter {

		LayoutInflater inflater;
		List<ScanResult> list;

		public MyAdapter(Context context, List<ScanResult> list) {
			// TODO Auto-generated constructor stub
			this.inflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_plug_list, parent,
						false);
			}
			ScanResult scanResult = list.get(position);
			TextView textView = (TextView) convertView
					.findViewById(R.id.textView);
			RelativeLayout layout = (RelativeLayout) convertView
					.findViewById(R.id.layout_scanplug);
			textView.setText(scanResult.SSID);
			AddOnClickListener listener = new AddOnClickListener(
					scanResult.SSID);

			convertView.findViewById(R.id.imageViewAdd);

			// Device Type Image
			ImageView img_devicetype = (ImageView) convertView
					.findViewById(R.id.img_devicetype);

			String deviceType = "1";
			String productType = "1";
			
			img_devicetype.setImageResource(R.drawable.smp_lock_small);
			layout.setOnClickListener(listener);
			
			return convertView;
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.titlebar_leftbutton :
				finish();
				break;
			case R.id.titlebar_rightbutton :
				doSearchPlug();
				break;
			default :
				break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartLockApplication.resetTask();

		doSearchPlug();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}

	private Handler wifiConnect = new Handler() {
		public void handleMessage(Message msg) {
			if (null != mProgress) {
				mProgress.dismiss();
				mProgress = null;
			}

			if (-1 == msg.what) {
				// lishimin Modify Hint the error msg.
				String tmp = (String) msg.obj;
				PubFunc.thinzdoToast(AddSocketActivity2.this, tmp);

			} else if (0 == msg.what) {
				PubFunc.thinzdoToast(AddSocketActivity2.this,
						getString(R.string.smartlock_oper_connect_fail));

			} else {
				mWifiDevice = (SmartLockDefine) msg.obj;
				mPlugId = mWifiDevice.mLockID;

				mStored_SSID = mSSId;
				mStored_PLUGID = mPlugId;
				saveWiFiStore();

				// SharedPreferences WifiInfo =
				// getSharedPreferences("WiFiStore", Activity.MODE_PRIVATE);
				// SharedPreferences.Editor editor = WifiInfo.edit();
				// editor.putString("SSID", mSSId);
				// editor.putString("PLUGID", mPlugId);
				// editor.commit();

				// // 重新启动APP程序
				// Intent intent = getBaseContext().getPackageManager()
				// .getLaunchIntentForPackage(getBaseContext().getPackageName());
				// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// intent.putExtra("AUTOLOGIN", true);
				// startActivity(intent);
				// finish();

				new UpdateTableTask(mSSId).execute();
			}
		};
	};

	public class UpdateTableTask extends AsyncTask<Void, Void, Void> {
		private String mSSID_update = "";
		public UpdateTableTask(String ssid) {
			mSSID_update = ssid;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			addNewLockToDB();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent();
			intent.putExtra("PLUGID", mPlugId);
			intent.putExtra("PLUGIP", ""); // IP是 插座作为AP自动分配的；
			intent.putExtra("PLUG_SSID", mSSID_update);
			intent.putExtra("ONLINE", true);

			String deviceType = "1";
			String productType = "1";

			intent.setClass(AddSocketActivity2.this,
						LockDetailActivity.class);

			startActivity(intent);
			finish();
		}

	};

	private boolean addNewLockToDB() {
		SmartLockExLockHelper plugProvider = new SmartLockExLockHelper(this);
		plugProvider.clearSmartLock();

		SmartLockDefine plug = new SmartLockDefine();
		plug.mUserName = PubStatus.g_CurUserName;
		plug.mName = mSSId;
		plug.mLockID = mPlugId;
		plug.mOnline = true;
		plug.mType = "1";
		
		plugProvider.addSmartLock(plug);

		plugProvider = null;
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
