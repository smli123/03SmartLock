package com.sherman.smartlockex.processhandler;

import android.content.Intent;
import android.os.Message;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class SmartLockEventHandlerNotifyLockStatus extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOCK_NOTIFY_STATUS);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		
		int status = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
		mIntent.putExtra("LOCKID", PubStatus.g_moduleId);
		mIntent.putExtra("STATUS", status);
		SmartLockApplication.getContext().sendBroadcast(mIntent);
	}
}
