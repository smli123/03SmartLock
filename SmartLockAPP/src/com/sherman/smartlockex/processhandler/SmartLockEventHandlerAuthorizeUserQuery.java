package com.sherman.smartlockex.processhandler;

import java.util.ArrayList;

import com.sherman.smartlockex.dataprovider.SmartLockExAuthorizeUserHelper;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.ui.common.AuthorizeUserDefine;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.content.Intent;
import android.os.Message;

public class SmartLockEventHandlerAuthorizeUserQuery extends SmartLockEventHandler {
	private Intent mIntent = new Intent(PubDefine.LOCK_QRYAUTHORIZEUSER_BROADCAST);
	
	private ArrayList<AuthorizeUserDefine> items  = new ArrayList<AuthorizeUserDefine>();
	
	@Override
	public void handleMessage(Message msg) {
		items.clear();
		String[] buffer = (String[]) msg.obj;
		
		int code = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]); 
		if (0 != code) {
	    	return;
		}
		
		String devID = buffer[3];
		mIntent.putExtra("LOCKID", devID);
		
		int lockCount = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
		if (0 == lockCount) {
			SmartLockExAuthorizeUserHelper mHelper = new SmartLockExAuthorizeUserHelper(SmartLockApplication.getContext());
			mHelper.clear();
			
			mIntent.putExtra("RESULT", 0);
			mIntent.putExtra("MESSAGE", "");
			SmartLockApplication.getContext().sendBroadcast(mIntent);
			return;
		}
		
		int i_header = 2 + EVENT_MESSAGE_HEADER;
		String[] infors = new String[buffer.length - i_header];
		for (int i = i_header; i < buffer.length; i++) {
			infors[i - i_header] = buffer[i];
		}
		
		int baseIdx = 0;
		for (int i = 0; i < lockCount; i++) {
			AuthorizeUserDefine item = new AuthorizeUserDefine();
		    item.mIndex 		= i + 1;
		    item.mAuthorizeID   = 0;	//Integer.valueOf(infors[baseIdx + 0]);
		    item.mLockID   		= infors[baseIdx + 0];
		    item.mUserName  	= infors[baseIdx + 1];
		    item.mUserStatus 	= Integer.valueOf(infors[baseIdx + 2]);
		    
		    items.add(item);
		    
		    baseIdx = baseIdx + 3;
		}
		
		add2DB(items, devID);
		
		mIntent.putExtra("RESULT", 0);
		mIntent.putExtra("MESSAGE", "");
		SmartLockApplication.getContext().sendBroadcast(mIntent);
	}
	
	private void add2DB(ArrayList<AuthorizeUserDefine> locks, String devID) {
		SmartLockExAuthorizeUserHelper mHelper = new SmartLockExAuthorizeUserHelper(SmartLockApplication.getContext());
		mHelper.clear(devID);

		int i = 0, j = 0;
		for (i = 0; i < locks.size(); i++) {
			long id = mHelper.add(locks.get(i));
		}
		
		mHelper = null;
	}
}
