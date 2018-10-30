package com.sherman.smartlockex.processhandler;

import android.content.Intent;
import android.os.Message;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.MessageDeviceHelper;
import com.sherman.smartlockex.ui.common.MessageDeviceDefine;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.smartlockex.AppServerReposeDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class SmartLockEventHandlerNotifyLockAlarm extends SmartLockEventHandler {
	Intent mIntent = new Intent(PubDefine.LOCK_NOTIFY_ALARM_BROADCAST);
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
			
			if (0 == code) {
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("LOCKID", moduleID);
				mIntent.putExtra("STATUS", status);
		    	
				MessageDeviceDefine item = new MessageDeviceDefine();
				item.mMessageID = buffer[4];
				item.mUserName = buffer[2];
				item.mDeviceID = buffer[3];
				item.mDeviceName = buffer[4];
				item.mOperType = Integer.valueOf(buffer[5]);
				item.mOperData = Integer.valueOf(buffer[6]);
				item.mUserType = Integer.valueOf(buffer[7]);
				item.mDetail = buffer[8];
				item.mMarked = false;
				
				addMessage(item);
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
	
	private boolean addMessage(MessageDeviceDefine item) {
		MessageDeviceHelper  helper = new MessageDeviceHelper(SmartLockApplication.getContext());
		long i = helper.addMessage(item);
		
		return i > 0 ? true : false;
	}
}
