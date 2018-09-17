package com.sherman.smartlockex.processhandler;

import android.content.Intent;
import android.os.Message;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class SmartLockEventHandlerNotifyLight extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_NOTIFY_LIGHT);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		if ((EVENT_MESSAGE_HEADER+2) != buffer.length){
			return;
		}
		
		String plugid = buffer[EVENT_MESSAGE_HEADER+0];
		int light = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
		mIntent.putExtra("PLUGID", plugid);
		mIntent.putExtra("LIGHT", light);
		SmartLockApplication.getContext().sendBroadcast(mIntent);
	}

}
