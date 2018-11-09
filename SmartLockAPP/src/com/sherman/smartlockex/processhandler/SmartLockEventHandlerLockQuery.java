package com.sherman.smartlockex.processhandler;

import java.util.ArrayList;

import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.content.Intent;
import android.os.Message;

public class SmartLockEventHandlerLockQuery extends SmartLockEventHandler {
	private Intent mIntent = new Intent(PubDefine.LOCK_QRYLOCK_BROADCAST);
	
	private ArrayList<SmartLockDefine> locks  = new ArrayList<SmartLockDefine>();
	
	@Override
	public void handleMessage(Message msg) {
		locks.clear();
		String[] buffer = (String[]) msg.obj;
		
		int code = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]); 
		if (0 != code) {
	    	return;
		}
		int lockCount = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
		if (0 == lockCount) {
			SmartLockExLockHelper mLockHelper = new SmartLockExLockHelper(SmartLockApplication.getContext());
			mLockHelper.clear();
			
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
			SmartLockDefine item = new SmartLockDefine();
		    item.mUserName = PubStatus.g_CurUserName;
		    item.mLockID   = infors[baseIdx + 0];
		    item.mName  	= infors[baseIdx + 1];
		    item.mAddress 	= infors[baseIdx + 2];
		    item.mVersion 	= infors[baseIdx + 3];
		    item.mType	 	= infors[baseIdx + 4];
		    item.mOnline  	= Integer.parseInt(infors[baseIdx + 5]) == 1 ? true : false;
		    item.mStatus = Integer.parseInt(infors[baseIdx + 6]);
		    item.mCharge = Integer.parseInt(infors[baseIdx + 7]);
		    item.mRelation = Integer.parseInt(infors[baseIdx + 8]);
		    locks.add(item);
		    
		    baseIdx = baseIdx + 9;
		}
		
		add2DB(locks);
		
		mIntent.putExtra("RESULT", 0);
		mIntent.putExtra("MESSAGE", "");
		SmartLockApplication.getContext().sendBroadcast(mIntent);
	}
	
	private void add2DB(ArrayList<SmartLockDefine> locks) {
		SmartLockExLockHelper mLockHelper = new SmartLockExLockHelper(SmartLockApplication.getContext());
		mLockHelper.clear();

		int i = 0, j = 0;
		for (i = 0; i < locks.size(); i++) {
			long id = mLockHelper.add(locks.get(i));
		}
		
		mLockHelper = null;
	}
}
