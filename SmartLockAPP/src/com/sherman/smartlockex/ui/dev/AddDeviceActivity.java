package com.sherman.smartlockex.ui.dev;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.task.__IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.EspNetUtil;
import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExContentDefine;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import java.lang.ref.WeakReference;
import java.util.List;

public class AddDeviceActivity extends TitledActivity implements OnClickListener {
    private static final String TAG = "EsptouchDemoActivity";

    private static final int REQUEST_PERMISSION = 0x01;
    
    private Context mContext = null;

    private TextView mApSsidTV;
    private TextView mApBssidTV;
    private EditText mApPasswordET;
    private EditText mDeviceCountET;
    private RadioGroup mPackageModeGroup;
    private TextView mMessageTV;
    private Button mConfirmBtn;
    
    private ImageView iv_show_password;
    private boolean b_ShowPassword = false;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
	private String mWIFIName = "";
	private String mWIFIPassword = "";

    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    private EsptouchAsyncTask4 mTask;

    private boolean mReceiverRegistered = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            assert wifiManager != null;

            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) == true) {
                WifiInfo wifiInfo;
                if (intent.hasExtra(WifiManager.EXTRA_WIFI_INFO)) {
                    wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                } else {
                    wifiInfo = wifiManager.getConnectionInfo();
                }
                onWifiChanged(wifiInfo);
            } else if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION) == true) {
                onWifiChanged(wifiManager.getConnectionInfo());
                onLocationChanged();
            }
            
            if (intent.getAction().equals(PubDefine.LOCK_ADDLOCK_BROADCAST)) {
				mHandler.sendEmptyMessage(1);
			}
        }
    };

    private boolean mDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState, R.layout.activity_add_device,
				false);
		SmartLockApplication.resetTask();
		SmartLockApplication.getInstance().addActivity(this);
        mContext = this;
        
        setTitleLeftButton(R.string.smartlock_goback,
				R.drawable.title_btn_selector, this);
        setTitle(SmartLockApplication.getInstance().getString(R.string.smartlock_add_lock));
        
        mSharedPreferences = getSharedPreferences("Activity_AddLock_" + PubStatus.getUserName(),
				Activity.MODE_PRIVATE);
        
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_device);

        mApSsidTV = (TextView) findViewById(R.id.ap_ssid_text);
        mApBssidTV = (TextView) findViewById(R.id.ap_bssid_text);
        mApPasswordET = (EditText) findViewById(R.id.ap_password_edit);
        mDeviceCountET = (EditText) findViewById(R.id.device_count_edit);
        mPackageModeGroup = (RadioGroup) findViewById(R.id.package_mode_group);
        mMessageTV = (TextView) findViewById(R.id.message);
        mConfirmBtn = (Button) findViewById(R.id.confirm_btn);
        
        iv_show_password = (ImageView) findViewById(R.id.iv_show_password);
        
        mConfirmBtn.setEnabled(false);
        mConfirmBtn.setOnClickListener(this);
        iv_show_password.setOnClickListener(this);
        
        TextView versionTV = (TextView) findViewById(R.id.version_tv);
        versionTV.setText(IEsptouchTask.ESPTOUCH_VERSION);

        if (isSDKAtLeastP()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {
                        Manifest.permission.ACCESS_COARSE_LOCATION
                };

                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
            } else {
                registerBroadcastReceiver();
            }

        } else {
            registerBroadcastReceiver();
        }
        
//        addLock("b4:e6:2d:0b:dd:20");
    }
    
	private void saveData() {
		mWIFIName = mApSsidTV.getText().toString();
		mWIFIPassword = mApPasswordET.getText().toString();

		editor = mSharedPreferences.edit();
		editor.putString(mWIFIName, mWIFIPassword);
		editor.commit();
	}

	private void loadData() {
		mWIFIName = mApSsidTV.getText().toString();
		mWIFIPassword = mSharedPreferences.getString(mWIFIName, "");
	}
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        saveData();

        mDestroyed = true;
        if (mReceiverRegistered) {
            unregisterReceiver(mReceiver);
        }
    }

    @SuppressLint("NewApi") 
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!mDestroyed) {
                        registerBroadcastReceiver();
                    }
                }
                break;
        }
    }


    private boolean isSDKAtLeastP() {
        return Build.VERSION.SDK_INT >= 28;
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        if (isSDKAtLeastP()) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        }
        filter.addAction(PubDefine.LOCK_ADDLOCK_BROADCAST);
        registerReceiver(mReceiver, filter);
        mReceiverRegistered = true;
    }

    @SuppressLint("NewApi") 
    private void onWifiChanged(WifiInfo info) {
        if (info == null) {
            mApSsidTV.setText("");
            mApSsidTV.setTag(null);
            mApBssidTV.setTag("");
            mMessageTV.setText("");
            mConfirmBtn.setEnabled(false);

            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(AddDeviceActivity.this)
                        .setMessage("Wifi disconnected or changed")
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        } else {
            String ssid = info.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            mApSsidTV.setText(ssid);
            mApSsidTV.setTag(ByteUtil.getBytesByString(ssid));
            byte[] ssidOriginalData = EspUtils.getOriginalSsidBytes(info);
            mApSsidTV.setTag(ssidOriginalData);

            String bssid = info.getBSSID();
            mApBssidTV.setText(bssid);
            
            // 加载WIFI路由器的密码
            loadData();
            mApPasswordET.setText(mWIFIPassword);

            mConfirmBtn.setEnabled(true);
            mMessageTV.setText("");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                int frequence = info.getFrequency();
//                if (frequence > 4900 && frequence < 5900) {
//                    // Connected 5G wifi. Device does not support 5G
//                    mMessageTV.setText(R.string.wifi_5g_message);
//                }
//            }
        }
    }

    private void onLocationChanged() {
        boolean enable;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            enable = false;
        } else {
            boolean locationGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean locationNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            enable = locationGPS || locationNetwork;
        }

        if (!enable) {
            mMessageTV.setText(R.string.location_disable_message);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mConfirmBtn) {
            byte[] ssid = mApSsidTV.getTag() == null ? ByteUtil.getBytesByString(mApSsidTV.getText().toString())
                    : (byte[]) mApSsidTV.getTag();
            byte[] password = ByteUtil.getBytesByString(mApPasswordET.getText().toString());
            byte [] bssid = EspNetUtil.parseBssid2bytes(mApBssidTV.getText().toString());
            byte[] deviceCount = mDeviceCountET.getText().toString().getBytes();
            byte[] broadcast = {(byte) (mPackageModeGroup.getCheckedRadioButtonId() == R.id.package_broadcast
                    ? 1 : 0)};

            if(mTask != null) {
                mTask.cancelEsptouch();
            }
            mTask = new EsptouchAsyncTask4(this);
            mTask.execute(ssid, bssid, password, deviceCount, broadcast);
        } else if (v.getId() == R.id.titlebar_leftbutton) {
        	finish();
        } else if (v.getId() == R.id.iv_show_password) {
        	if (b_ShowPassword == false) {
        		iv_show_password.setImageResource(R.drawable.smp_password_show);
        	} else {
        		iv_show_password.setImageResource(R.drawable.smp_password_hide);
        	}
        	b_ShowPassword = ! b_ShowPassword;
        	setPasswordEye(mApPasswordET);
        }
    }
    
  //设置密码可见和不可见
    private void setPasswordEye(EditText editText) {
        if (EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == editText.getInputType()) {
            //如果不可见就设置为可见
            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            //如果可见就设置为不可见
            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
        editText.setSelection(editText.getText().toString().length());
    }

    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // Add SmartLock in Server
                String newMac = transMac(result.getBssid());

                Message msg = new Message();
                msg.what = 0;
                msg.obj = newMac;
                mHandler.sendMessage(msg);

                String text = newMac + SmartLockApplication.getInstance().getString(R.string.connected_to_wifi_router);
                Toast.makeText(AddDeviceActivity.this, text,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private String transMac(String mac) {
    	StringBuilder newMac = new StringBuilder(mac);
    	newMac.insert(2, ":");
    	newMac.insert(5, ":");
    	newMac.insert(8, ":");
    	newMac.insert(11, ":");
    	newMac.insert(14, ":");
    	
    	return newMac.toString();
    }

    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], Void, List<IEsptouchResult>> {
        private WeakReference<AddDeviceActivity> mActivity;

        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;

        EsptouchAsyncTask4(AddDeviceActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        void cancelEsptouch() {
            cancel(true);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mResultDialog != null) {
                mResultDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        @Override
        protected void onPreExecute() {
            Activity activity = mActivity.get();
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage(SmartLockApplication.getInstance().getString(R.string.connecting_to_wifi_router));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (__IEsptouchTask.DEBUG) {
                            Log.i(TAG, "progress dialog back pressed canceled");
                        }
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            synchronized (mLock) {
                                if (__IEsptouchTask.DEBUG) {
                                    Log.i(TAG, "progress dialog cancel button canceled");
                                }
                                if (mEsptouchTask != null) {
                                    mEsptouchTask.interrupt();
                                }
                            }
                        }
                    });
            mProgressDialog.show();
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            AddDeviceActivity activity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                byte[] broadcastData = params[4];
                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
                Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
                mEsptouchTask.setEsptouchListener(activity.myListener);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            AddDeviceActivity activity = mActivity.get();
            mProgressDialog.dismiss();
            mResultDialog = new AlertDialog.Builder(activity)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
            mResultDialog.setCanceledOnTouchOutside(false);
            if (result == null) {
                mResultDialog.setMessage(SmartLockApplication.getInstance().getString(R.string.esptouch_task_failed_port_is_used));
                mResultDialog.show();
                return;
            }

            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    for (IEsptouchResult resultInList : result) {
                        sb.append("Esptouch success, bssid = ")
                                .append(resultInList.getBssid())
                                .append(", InetAddress = ")
                                .append(resultInList.getInetAddress().getHostAddress())
                                .append("\n");
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's ")
                                .append(result.size() - count)
                                .append(" more result(s) without showing\n");
                    }
                    mResultDialog.setMessage(sb.toString());
                } else {
                    mResultDialog.setMessage(SmartLockApplication.getInstance().getString(R.string.esptouch_task_failed));
                }

                mResultDialog.show();
            }

            activity.mTask = null;
        }
    }
    
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					String strMac = (String)msg.obj;
					addLock(strMac);
					break;
				case 1 :
					finish();
					break;
				default :
					break;
			}
		};
	};
    
	private void addLock(String strMac) {
		String strDevName = "DefaultName";
		String strDevID = strMac;
		
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_ADDLOCK)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(strDevName)	// DevName
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(strDevID)	// DevID
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(strMac)		// Mac
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(SmartLockExContentDefine.Lock.RELATION_MASTER);	// Relation

		sendMsg(true, sb.toString(), true);
	}
}
