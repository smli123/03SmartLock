package com.sherman.smartlockex.processhandler;

import android.content.Intent;
import android.os.Message;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class SmartLockEventHandlerPasswordDelete extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOCK_DELPASSWORD_BROADCAST);
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;

		String devID = buffer[3];
		mIntent.putExtra("LOCKID", devID);
		
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
		if (0 == ret) {
	    	mIntent.putExtra("RESULT", 0);
	    	SmartLockApplication.getContext().sendBroadcast(mIntent);
		} else {
	    	mIntent.putExtra("RESULT", 1);
	    	int resid = AppServerReposeDefine.getServerResponse(ret);
	    	if (0 == resid) {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartLockApplication.getContext().getString(R.string.smartlock_ctrl_delete_authorize_fail));
	    	} else {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartLockApplication.getContext().getString(resid));
	    	}
	    	SmartLockApplication.getContext().sendBroadcast(mIntent);	    	
	    }		
	}
}
