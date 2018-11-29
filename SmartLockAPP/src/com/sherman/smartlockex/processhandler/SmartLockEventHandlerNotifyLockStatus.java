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

public class SmartLockEventHandlerNotifyLockStatus extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOCK_NOTIFY_STATUS_BROADCAST);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		
		try{
			int code = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);
			if (2 > buffer.length) {
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("MESSAGE", 
		    				SmartLockApplication.getContext().getString(R.string.smartlock_oper_lock_fail));
		    	SmartLockApplication.getContext().sendBroadcast(mIntent);
				return;
			}			
			
			String moduleID = buffer[3];
			int status = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
			int charge = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+2]);
			int userType = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+3]);
			String memo = buffer[EVENT_MESSAGE_HEADER+4];
			
			if (0 == code) {
				modifyDB(moduleID, status, charge);
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("LOCKID", moduleID);
				mIntent.putExtra("STATUS", status);
				mIntent.putExtra("CHARGE", charge);
				mIntent.putExtra("USERTYPE", userType);
				mIntent.putExtra("USERMEMO", memo);
		    	
			} else {
		    	mIntent.putExtra("RESULT", code);
		    	mIntent.putExtra("STATUS", status);
		    	int resid = AppServerReposeDefine.getServerResponse(code);
		    	if (0 != resid) {
		    		mIntent.putExtra("MESSAGE", SmartLockApplication.getContext().getString(resid));
		    	} else {
		    		mIntent.putExtra("MESSAGE", 
		    				SmartLockApplication.getContext().getString(R.string.smartlock_oper_lock_fail));
		    	}
		    }
			SmartLockApplication.getContext().sendBroadcast(mIntent);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void modifyDB(String lockID, int status, int charge) {
		SmartLockExLockHelper mLockHelper = new SmartLockExLockHelper(SmartLockApplication.getContext());
		SmartLockDefine device = mLockHelper.get(lockID);
		device.mStatus = status;
		device.mCharge = charge;
		mLockHelper.modify(device);
		
		mLockHelper = null;
	}
}
