package com.sherman.smartlockex.processhandler;

import android.content.Intent;
import android.os.Message;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class SmartLockEventHandlerHeart  extends SmartLockEventHandler{
	Intent mIntent = new Intent(PubDefine.HEART_BROADCAST);
	
	@Override
	public void handleMessage(Message msg) {
		PubStatus.g_heartRecvCount += 1;
		
		String[] buffer = (String[]) msg.obj;
		try{
			int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (0 == ret) {
		    	mIntent.putExtra("RESULT", 0);
		    	if (buffer.length >= 2) {
		    		mIntent.putExtra("MESSAGE", buffer[EVENT_MESSAGE_HEADER+1]);
		    	} else {
		    		mIntent.putExtra("MESSAGE", "");
		    	}
		    	SmartLockApplication.getContext().sendBroadcast(mIntent);
			} else {
		    	mIntent.putExtra("RESULT", 1);
		    	int resid = AppServerReposeDefine.getServerResponse(ret);
		    	if (0 != resid) {
		    		mIntent.putExtra("MESSAGE", SmartLockApplication.getContext().getString(resid));
		    	} else {
		    		mIntent.putExtra("MESSAGE", getUnknowErrorMsg());
		    		
		    	}
		    	SmartLockApplication.getContext().sendBroadcast(mIntent);	    	
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Debug
//		String info = "recv heart. count:" + PubStatus.g_heartRecvCount;
//		PubFunc.thinzdoToast(SmartLockApplication.getContext(), info);
	}
}
