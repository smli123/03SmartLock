package com.sherman.smartlockex.processhandler;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.content.Intent;
import android.os.Message;

public class SmartLockEventHandlerAddLock extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOCK_ADDLOCK_BROADCAST);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		try{
			int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);		
			if (0 == ret) {
		    	mIntent.putExtra("RESULT", 0);
		    	SmartLockApplication.getContext().sendBroadcast(mIntent);
			} else {
				PubFunc.log("add plug fail,ret=" + String.valueOf(ret));
		    	mIntent.putExtra("RESULT", 1);
		    	int resid = AppServerReposeDefine.getServerResponse(ret);
		    	if (0  != resid) {
		    		mIntent.putExtra("MESSAGE", SmartLockApplication.getContext().getString(resid));
		    	} else {
		    		mIntent.putExtra("MESSAGE", SmartLockApplication.getContext().getString(R.string.smartlock_add_fail));
		    	}
		    	SmartLockApplication.getContext().sendBroadcast(mIntent);	    	
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
