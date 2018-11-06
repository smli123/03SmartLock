package com.sherman.smartlockex.processhandler;

import android.content.Intent;
import android.os.Message;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class SmartLockEventHandlerNotifyOnline extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOCK_NOTIFY_ONLINE_BROADCAST);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		
		String devID = buffer[3];
		int online = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
		
		updateDB(devID, online);
		
		mIntent.putExtra("LOCKID", devID);
		mIntent.putExtra("ONLINE", online);
		SmartLockApplication.getContext().sendBroadcast(mIntent);
	}
	
	private void updateDB(String devID, int online) {
		SmartLockExLockHelper helper = new SmartLockExLockHelper(SmartLockApplication.getContext());
		SmartLockDefine item = helper.get(devID);
		item.mOnline = (online == 1) ? true : false;
		helper.modify(item);
		
		helper = null;
	}
}
