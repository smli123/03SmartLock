package com.sherman.smartlockex.processhandler;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import android.content.Intent;
import android.os.Message;

public class SmartLockEventHandlerLogout extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOGOUT_BROADCAST);	
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
		mIntent.putExtra("LOGOUT", ret);
    	SmartLockApplication.getContext().sendBroadcast(mIntent);
	}
}
