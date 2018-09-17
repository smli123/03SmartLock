package com.sherman.smartlockex.processhandler;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.content.Intent;
import android.os.Message;

public class SmartLockEventHandlerRegister extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.REGISTER_BROADCAST);
	
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
		
	    if (0 == ret) {
	    	//success
	    	mIntent.putExtra("RESULT", 0);
	    	SmartLockApplication.getContext().sendBroadcast(mIntent);	    		
	    } else {	
	    	//fail
	    	mIntent.putExtra("RESULT", ret);
	    	PubFunc.log("error:" + String.valueOf(ret));
	    	int resid = AppServerReposeDefine.getServerResponse(ret);
	    	PubFunc.log(String.valueOf(resid));
	    	if (0 != resid) {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartLockApplication.getContext().getString(resid));
	    	} else {
	    		mIntent.putExtra("MESSAGE", 
	    				SmartLockApplication.getContext().getString(R.string.smartlock_ctrl_userregister_fail));
	    	}
	    	SmartLockApplication.getContext().sendBroadcast(mIntent);	    	
	    }		
	}
}
