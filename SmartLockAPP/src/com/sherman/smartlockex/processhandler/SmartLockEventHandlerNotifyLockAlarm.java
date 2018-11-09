package com.sherman.smartlockex.processhandler;

import android.content.Intent;
import android.os.Message;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.MessageDeviceHelper;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
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
			
			if (0 == code) {
				MessageDeviceDefine item = new MessageDeviceDefine();
				item.mMessageID = buffer[5];
				item.mUserName = buffer[2];
				item.mDeviceID = moduleID;
				
				SmartLockExLockHelper  helper = new SmartLockExLockHelper(SmartLockApplication.getContext());
				item.mDeviceName = helper.get(moduleID).mName;
				
				item.mMessageTime = buffer[6];
				item.mMessageType = Integer.valueOf(buffer[7]);
				item.mMessageData = Integer.valueOf(buffer[8]);
				item.mUserType = Integer.valueOf(buffer[9]);
				item.mDetail = buffer[10];
				item.mMarked = false;
				
				addMessage(item);

				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("LOCKNAME", item.mDeviceName);
				mIntent.putExtra("ALARMDATA", item.mMessageData);
			} else {
		    	mIntent.putExtra("RESULT", code);
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
