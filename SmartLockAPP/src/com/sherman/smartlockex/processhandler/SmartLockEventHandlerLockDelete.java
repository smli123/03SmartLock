package com.sherman.smartlockex.processhandler;

import android.content.Intent;
import android.os.Message;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class SmartLockEventHandlerLockDelete extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOCK_DELETELOCK_BROADCAST);
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
		
		if (0 == ret) {
	    	mIntent.putExtra("RESULT", 0);

	    	SmartLockExLockHelper mLockHelper = new SmartLockExLockHelper(SmartLockApplication.getContext());
	    	String moduleID = buffer[3];
	    	boolean result = mLockHelper.delete(moduleID);
	    	
	    	SmartLockApplication.getContext().sendBroadcast(mIntent);
		} else {
	    	mIntent.putExtra("RESULT", 1);
	    	int resid = AppServerReposeDefine.getServerResponse(ret);
	    	if (0 == resid) {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartLockApplication.getContext().getString(R.string.smartlock_ctrl_delete_fail));
	    	} else {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartLockApplication.getContext().getString(resid));
	    	}
	    	SmartLockApplication.getContext().sendBroadcast(mIntent);	    	
	    }		
	}
}
