package com.sherman.smartlockex.ui.dev;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExAuthorizeUserHelper;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.dataprovider.SmartLockExPasswordHelper;
import com.sherman.smartlockex.internet.UDPReceiver;
import com.sherman.smartlockex.processhandler.SmartLockMessage;
import com.sherman.smartlockex.ui.common.AuthorizeUserDefine;
import com.sherman.smartlockex.ui.common.PasswordDefine;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.common.StringUtils;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.login.LoginActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import com.sherman.smartlockex.ui.util.MyAlertDialog;

public class LockDetailActivity extends TitledActivity implements OnClickListener {
	private Context mContext = null;
	private SmartLockExLockHelper mLockHelper = null;
	private SmartLockExAuthorizeUserHelper mAuthorizeHelper = null;
	private SmartLockExPasswordHelper mPasswordHelper = null;
	
	private LinearLayout ll_administrator_area;
	private TextView tv_status;
	private TextView tv_log;
	
	private TextView tv_lock_open;
	private TextView tv_lock_close;
	
	private ImageView iv_authorize_add;
	private ImageView iv_password_management_add;
	
	private ListView lv_authorize;
	private ListView lv_password_management;
	
	private String mLockID = "";
	private SmartLockDefine mLock = null;
	
	private MyAlertDialog mAddUserDlg = null;

	private BroadcastReceiver mLoginRev = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != mProgress) {
				mProgress.dismiss();
			}
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

			if (intent.getAction().equals(PubDefine.LOCK_QRYAUTHORIZEUSER_BROADCAST)) {
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("RESULT", -1);
				if (status == 0) {
					if (moduleID.equals(mLockID) == true) {
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = status;
						updateHandler.sendMessage(msg);
					}
				} else {
					String message = intent.getStringExtra("MESSAGE");
					PubFunc.thinzdoToast(mContext, message);
				}
			}
			if (intent.getAction().equals(PubDefine.LOCK_ADDAUTHORIZEUSER_BROADCAST)) {
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("RESULT", -1);
				if (status == 0) {
					if (moduleID.equals(mLockID) == true) {
						Message msg = new Message();
						msg.what = 2;
						msg.arg1 = status;
						updateHandler.sendMessage(msg);
					}
				} else {
					String message = intent.getStringExtra("MESSAGE");
					PubFunc.thinzdoToast(mContext, message);
				}
			}
			if (intent.getAction().equals(PubDefine.LOCK_DELAUTHORIZEUSER_BROADCAST)) {
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("RESULT", -1);
				if (status == 0) {
					if (moduleID.equals(mLockID) == true) {
						Message msg = new Message();
						msg.what = 3;
						msg.arg1 = status;
						updateHandler.sendMessage(msg);
					}
				} else {
					String message = intent.getStringExtra("MESSAGE");
					PubFunc.thinzdoToast(mContext, message);
				}
			}
			if (intent.getAction().equals(PubDefine.LOCK_QRYPASSWORD_BROADCAST)) {
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("RESULT", -1);
				if (status == 0) {
					if (moduleID.equals(mLockID) == true) {
						Message msg = new Message();
						msg.what = 4;
						msg.arg1 = status;
						updateHandler.sendMessage(msg);
					}
				} else {
					String message = intent.getStringExtra("MESSAGE");
					PubFunc.thinzdoToast(mContext, message);
				}
			}
			if (intent.getAction().equals(PubDefine.LOCK_ADDPASSWORD_BROADCAST)) {
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("RESULT", -1);
				if (status == 0) {
					if (moduleID.equals(mLockID) == true) {
						Message msg = new Message();
						msg.what = 5;
						msg.arg1 = status;
						updateHandler.sendMessage(msg);
					}
				} else {
					String message = intent.getStringExtra("MESSAGE");
					PubFunc.thinzdoToast(mContext, message);
				}
			}
			if (intent.getAction().equals(PubDefine.LOCK_DELPASSWORD_BROADCAST)) {
				String moduleID = intent.getStringExtra("LOCKID");
				int status = intent.getIntExtra("RESULT", -1);
				if (status == 0) {
					if (moduleID.equals(mLockID) == true) {
						Message msg = new Message();
						msg.what = 6;
						msg.arg1 = status;
						updateHandler.sendMessage(msg);
					}
				} else {
					String message = intent.getStringExtra("MESSAGE");
					PubFunc.thinzdoToast(mContext, message);
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
			case 1:		// Query AuthorizeUser
				updateAuthorize();
				break;
			case 2:		// ADD AuthorizeUser
				queryAuthorize(mLockID);
				break;
			case 3:		// DEL AuthorizeUser
				queryAuthorize(mLockID);
				break;
			case 4:		// Query Password
				updatePassword();
				break;
			case 5:		// ADD Password
				queryPassword(mLockID);
				break;
			case 6:		// DEL Password
				queryPassword(mLockID);
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
        mAuthorizeHelper = new SmartLockExAuthorizeUserHelper(mContext);
        mPasswordHelper = new SmartLockExPasswordHelper(mContext);
        
        Intent intent = getIntent();
        mLockID = intent.getStringExtra("LOCKID");
        
        if(mLockID.isEmpty() == false) {
        	mLock = mLockHelper.get(mLockID);
        }
        
        initView();
        
        IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.LOCK_NOTIFY_STATUS_BROADCAST);
		filter.addAction(PubDefine.LOCK_OPENLOCK_BROADCAST);
		filter.addAction(PubDefine.LOCK_QRYAUTHORIZEUSER_BROADCAST);
		filter.addAction(PubDefine.LOCK_ADDAUTHORIZEUSER_BROADCAST);
		filter.addAction(PubDefine.LOCK_DELAUTHORIZEUSER_BROADCAST);
		filter.addAction(PubDefine.LOCK_QRYPASSWORD_BROADCAST);
		filter.addAction(PubDefine.LOCK_ADDPASSWORD_BROADCAST);
		filter.addAction(PubDefine.LOCK_DELPASSWORD_BROADCAST);
		registerReceiver(mLoginRev, filter);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				queryAuthorize(mLockID);
			}
		}, 500);

//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				queryPassword(mLockID);
//			}
//		}, 1000);
		
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
		case R.id.iv_authorize_add:
			addAuthorizeUserName("");
			break;
		case R.id.iv_password_management_add:
			addPassword();
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
		ll_administrator_area = (LinearLayout) findViewById(R.id.ll_administrator_area);
		
		if (mLock.mRelation == 0) {
			ll_administrator_area.setVisibility(View.VISIBLE);
		} else {
			ll_administrator_area.setVisibility(View.GONE);
		}
		
		iv_authorize_add = (ImageView) findViewById(R.id.iv_authorize_add);
		iv_password_management_add = (ImageView) findViewById(R.id.iv_password_management_add);
		
		tv_lock_open.setOnClickListener(this);
		tv_lock_close.setOnClickListener(this);
		iv_authorize_add.setOnClickListener(this);
		iv_password_management_add.setOnClickListener(this);
		
		lv_authorize = (ListView) findViewById(R.id.lv_authorize);
		lv_password_management= (ListView) findViewById(R.id.lv_password_management);
		
		updateAuthorize();
		updatePassword();
		
		Message msg = new Message();
		msg.what = 0;
		msg.arg1 = mLock.mStatus;
		updateHandler.sendMessage(msg);
	}
	
	private void addAuthorizeUserName(String name) {
		mAddUserDlg = new MyAlertDialog(mContext);
		mAddUserDlg
				.builder()
				.setCancelable(true)
				.setTitle(getString(R.string.smartlock_add_authorize_user))
				.setEditText(name)
				.setPositiveButton(mContext.getString(R.string.smartlock_ok),
						addAuthorizeUserClick)
				.setNegativeButton(
						mContext.getString(R.string.smartlock_cancel),
						new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {

							}

						}).show();
	}
	
	private void addPassword() {
		Intent intent = new Intent();
		intent.setClass(LockDetailActivity.this, LockPasswordAddAvtivity.class);
		intent.putExtra("LOCKID", mLockID);
		mContext.startActivity(intent);
	}
	
	View.OnClickListener addAuthorizeUserClick = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (null == mAddUserDlg) {
				return;
			}
			String text = mAddUserDlg.getResult();
			if (!text.isEmpty()) {
				String shareUserName = mAddUserDlg.getResult();

				// 校验 NewPlugName：中英文占用的字节数必须小于20（最大20个byte）
				if (shareUserName.getBytes().length > 20) {
					PubFunc.thinzdoToast(
							mContext,
							getString(R.string.smartlock_ctrl_mod_plugname_length_too_long));
					return;
				}

				if (true == mAuthorizeHelper.isExist(mLockID, shareUserName)) {
					PubFunc.thinzdoToast(mContext,
							getString(R.string.smartlock_ctrl_samename_exist));
					return;
				}

				mProgress = PubFunc.createProgressDialog(mContext,
						getString(R.string.smartlock_add_authorize_user), false);
				mProgress.show();

				StringBuffer sb = new StringBuffer();
				sb.append(SmartLockMessage.CMD_SP_ADDAUTHORIZEUSER)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(PubStatus.g_CurUserName)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(mLockID)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append(shareUserName)
						.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
						.append("0");		// 预留位，暂时为0
				sendMsg(true, sb.toString(), true);
			}
		}
	};


	private void updateAuthorize() {
		ArrayList<AuthorizeUserDefine> items = mAuthorizeHelper
				.getAll(mLockID);
		AdapterAuthorizeList adapter = new AdapterAuthorizeList(mContext,
				items, mPressHandler);
		lv_authorize.setAdapter(adapter);
	}

	private void updatePassword() {
		ArrayList<PasswordDefine> items = mPasswordHelper
				.getAll(mLockID);
		AdapterPasswordManagement adapter = new AdapterPasswordManagement(mContext,
				items, mPressHandler);
		lv_password_management.setAdapter(adapter);
	}
	
	private Handler mPressHandler = new Handler() {
		public void handleMessage(Message msg) {
			final String id = (String)msg.obj;
			
			if (1 == msg.what) { 		// 删除授权用户
				AuthorizeUserDefine item = mAuthorizeHelper.getByUserName(mLockID, id);
				
				if (null != item) {
					String str_delete = SmartLockApplication.getContext().getString(R.string.smartlock_ctrl_delete_authorize);
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
							deleteAuthorize(mLockID, id);
						}
					});
					dialog.show();
				}
			}
			
			if (2 == msg.what) { 		// 删除临时密码
				PasswordDefine item = mPasswordHelper.get(mLockID, id);
				if (null != item) {
					String str_delete = SmartLockApplication.getContext().getString(R.string.smartlock_ctrl_delete_password);
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
							deletePassword(mLockID, id);
						}
					});
					dialog.show();
				}
			}
		};
	};

	private void queryAuthorize(String moduleID) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_QRYAUTHORIZEUSER)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mLockID);

		sendMsg(true, sb.toString(), true);
	}
	
	private void deleteAuthorize(String moduleID, String shareusername) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_DELAUTHORIZEUSER)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mLockID)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(shareusername));

		sendMsg(true, sb.toString(), true);
	}

	private void queryPassword(String moduleID) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_QRYPASSWORD)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mLockID)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append("0");

		sendMsg(true, sb.toString(), true);
	}
	
	private void deletePassword(String moduleID, String id) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartLockMessage.CMD_SP_DELPASSWORD)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mLockID)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(id));

		sendMsg(true, sb.toString(), true);
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