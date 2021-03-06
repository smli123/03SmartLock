package com.sherman.smartlockex.processhandler;

import java.util.ArrayList;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.content.Intent;
import android.os.Message;

public class SmartLockEventHandlerLockOpen extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOCK_OPENLOCK_BROADCAST);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		try{
			int code   = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (2 > buffer.length) {
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartLockApplication.getContext().getString(R.string.smartlock_oper_back2ap_fail));
		    	SmartLockApplication.getContext().sendBroadcast(mIntent);
				return;
			}			
			
			String LockID = buffer[3];
			mIntent.putExtra("LOCKID", buffer[3]);

			int status = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
			
			if (0 == code) {
				//success
				modifyDB(LockID, status);
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("STATUS", status);
				
			} else {
				//fail
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("STATUS", status);
		    	mIntent.putExtra("MESSAGE", 
	    				SmartLockApplication.getContext().getString(R.string.smartlock_oper_back2ap_fail));		    	
		    }
			SmartLockApplication.getContext().sendBroadcast(mIntent);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void modifyDB(String lockID, int status) {
		SmartLockExLockHelper mLockHelper = new SmartLockExLockHelper(SmartLockApplication.getContext());
		SmartLockDefine device = mLockHelper.get(lockID);
		device.mStatus = status;
		mLockHelper.modify(device);
		
		mLockHelper = null;
	}
}
