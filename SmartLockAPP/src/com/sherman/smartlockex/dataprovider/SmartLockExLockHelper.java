package com.sherman.smartlockex.dataprovider;

import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.SmartLockDefine;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class SmartLockExLockHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "LockHelper";
	
	public SmartLockExLockHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}
	
	private SmartLockDefine getData(Cursor cur) {
		SmartLockDefine user = new SmartLockDefine();
    	while (cur.moveToNext()){
    		user.mID = cur.getInt(SmartLockExContentDefine.Lock.ID_COLUMN);
    		user.mUserName = cur.getString(SmartLockExContentDefine.Lock.USER_NAME_COLUMN);
    		user.mName = cur.getString(SmartLockExContentDefine.Lock.LOCK_NAME_COLUMN);
    		user.mStatus = cur.getInt(SmartLockExContentDefine.Lock.LOCK_STATUS_COLUMN);
    		user.mOnline = cur.getInt(SmartLockExContentDefine.Lock.LOCK_ONLINE_COLUMN) == 1 ? true : false;
    		user.mAddress = cur.getString(SmartLockExContentDefine.Lock.LOCK_ADDRESS_COLUMN);
    		user.mType = cur.getString(SmartLockExContentDefine.Lock.LOCK_TYPE_COLUMN);
    		user.mCharge = cur.getInt(SmartLockExContentDefine.Lock.LOCK_CHARGE_COLUMN);
    	}		
		return user;	
	}
	

	public SmartLockDefine getLock(){
		if (null == mContentResolver) {
			return null; 
		}
		SmartLockDefine user = null;
		String where = "";
		String order = SmartLockExContentDefine.Lock._ID  + " asc";
    	Cursor cur = mContentResolver.query(SmartLockExContentDefine.Lock.ALL_CONTENT_URI, 
    			                            null, 
				                            where, 
				                            null, 
				                            order);
    	
    	
    	if (null != cur) {
    		user = getData(cur);
    		cur.close();
    	}
		return user;
    }	
    

    public SmartLockDefine getLock(String lockName){
    	SmartLockDefine user = new SmartLockDefine();
		if (null == mContentResolver) {
			return null; 
		}    	
    	
		String where = SmartLockExContentDefine.Lock.LOCK_NAME + "='" + lockName + "'";
    	    	
    	Cursor cur = mContentResolver.query(SmartLockExContentDefine.Lock.ALL_CONTENT_URI, 
                                            null, 
                                            where, 
                                            null, 
                                            null);
    	if (null != cur) {
    		if (cur.moveToFirst() == false) {
            	cur.close();
            	return null;    			
    		}
    		user.mID = cur.getInt(SmartLockExContentDefine.Lock.ID_COLUMN);
    		user.mUserName = cur.getString(SmartLockExContentDefine.Lock.USER_NAME_COLUMN);
    		user.mName = cur.getString(SmartLockExContentDefine.Lock.LOCK_NAME_COLUMN);
    		user.mStatus = cur.getInt(SmartLockExContentDefine.Lock.LOCK_STATUS_COLUMN);
    		user.mOnline = cur.getInt(SmartLockExContentDefine.Lock.LOCK_ONLINE_COLUMN) == 1 ? true : false;
    		user.mAddress = cur.getString(SmartLockExContentDefine.Lock.LOCK_ADDRESS_COLUMN);
    		user.mType = cur.getString(SmartLockExContentDefine.Lock.LOCK_TYPE_COLUMN);
    		user.mCharge = cur.getInt(SmartLockExContentDefine.Lock.LOCK_CHARGE_COLUMN);
        	cur.close();
        	return user;         	
    	} else {
        	return null;     		
    	}
    }
    

    public long addLock(SmartLockDefine item){
		if (null == mContentResolver) {
			return 0; 
		} 
		if (null == item) {
			return 0;
		}
		
		deleteLock(item.mName);
			
    	ContentValues values = new ContentValues();
    	values.put(SmartLockExContentDefine.Lock.USER_NAME, item.mUserName);
    	values.put(SmartLockExContentDefine.Lock.LOCK_NAME, item.mName);
    	values.put(SmartLockExContentDefine.Lock.LOCK_STATUS, item.mStatus);
    	values.put(SmartLockExContentDefine.Lock.LOCK_ONLINE, item.mOnline ? "1" : "0");
    	values.put(SmartLockExContentDefine.Lock.LOCK_ADDRESS, item.mAddress);
    	values.put(SmartLockExContentDefine.Lock.LOCK_TYPE, item.mType);
    	values.put(SmartLockExContentDefine.Lock.LOCK_CHARGE, item.mCharge);
    	
    	Uri uri = mContentResolver.insert(SmartLockExContentDefine.Lock.ALL_CONTENT_URI, values);
    	PubFunc.log(TAG, "Insert a record");
    	
    	if (null == uri) {
    		return 0;
    	}
        try {
    	    long id = ContentUris.parseId(uri);
    	    return id;
        } catch(Exception e) {
        	Log.e(TAG, e.getMessage());
        	return 0;
        }
    }
    
    public boolean deleteLock(String lockName){
		if (null == mContentResolver) {
			return false; 
		}
    	String where = SmartLockExContentDefine.Lock.LOCK_NAME + "='" + lockName + "'"; 
    	int count = mContentResolver.delete(SmartLockExContentDefine.Lock.ALL_CONTENT_URI, where, null);
    	return count > 0 ? true : false;
    }
       

    public void clearLock(){
		if (null != mContentResolver) {
			PubFunc.log(TAG, "Clear users");
			mContentResolver.delete(SmartLockExContentDefine.Lock.ALL_CONTENT_URI, null, null); 
		}
    }
    
    public int modifyLock(SmartLockDefine item){
		if (null == mContentResolver) {
			return -1; 
		} 
		if (null == item) {
			return -1;
		}
			
    	ContentValues values = new ContentValues();
    	values.put(SmartLockExContentDefine.Lock.USER_NAME, item.mUserName);
    	values.put(SmartLockExContentDefine.Lock.LOCK_NAME, item.mName);
    	values.put(SmartLockExContentDefine.Lock.LOCK_STATUS, item.mStatus);
    	values.put(SmartLockExContentDefine.Lock.LOCK_ONLINE, item.mOnline ? "1" : "0");
    	values.put(SmartLockExContentDefine.Lock.LOCK_ADDRESS, item.mAddress);
    	values.put(SmartLockExContentDefine.Lock.LOCK_TYPE, item.mType);
    	values.put(SmartLockExContentDefine.Lock.LOCK_CHARGE, item.mCharge);
    	
    	String where = null;
    	if (!item.mName.isEmpty()) {
    		where = SmartLockExContentDefine.Lock.LOCK_NAME + "='" + item.mName + "'";
    	}
    	
    	int index = mContentResolver.update(SmartLockExContentDefine.Lock.ALL_CONTENT_URI, 
    			                         values, 
    			                         where, 
    			                         null);
    	PubFunc.log(TAG, "Update a record");
        return index;
    } 
}