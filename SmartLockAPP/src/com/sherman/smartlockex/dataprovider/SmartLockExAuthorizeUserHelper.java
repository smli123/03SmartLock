package com.sherman.smartlockex.dataprovider;

import java.util.ArrayList;

import com.sherman.smartlockex.ui.common.AuthorizeUserDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.SmartLockDefine;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class SmartLockExAuthorizeUserHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "PasswordHelper";
	
	public SmartLockExAuthorizeUserHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}
	
	// 获得数据
	private ArrayList<AuthorizeUserDefine> getData(Cursor cur) {
		ArrayList<AuthorizeUserDefine> devs = new ArrayList<AuthorizeUserDefine>();
		while (cur.moveToNext()) {
			AuthorizeUserDefine item = new AuthorizeUserDefine();
			item.mID = cur
					.getInt(SmartLockExContentDefine.AuthorizeUser.ID_COLUMN);
			item.mAuthorizeID = cur
					.getInt(SmartLockExContentDefine.AuthorizeUser.AUTHORIZE_ID_COLUMN);
			item.mLockID = cur
					.getString(SmartLockExContentDefine.AuthorizeUser.LOCK_ID_COLUMN);
			item.mUserName = cur
					.getString(SmartLockExContentDefine.AuthorizeUser.USER_NAME_COLUMN);
			item.mUserStatus = cur
					.getInt(SmartLockExContentDefine.AuthorizeUser.USER_STATUS_COLUMN);

			devs.add(item);
		}
		return devs;
	}

	// 获得指定用户名下所有插座
	public ArrayList<AuthorizeUserDefine> getAll(String devID) {
		ArrayList<AuthorizeUserDefine> devs = new ArrayList<AuthorizeUserDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartLockExContentDefine.AuthorizeUser.LOCK_ID
				+ " = '" + devID + "'";
		String order = SmartLockExContentDefine.AuthorizeUser._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.AuthorizeUser.ALL_CONTENT_URI, null,
				where, null, order);

		if (null != cur) {
			devs = getData(cur);
			cur.close();
		}

		return devs;
	}

	// 获得指定插座信息
	public AuthorizeUserDefine get(String devID, String id) {
		AuthorizeUserDefine item = null;
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartLockExContentDefine.AuthorizeUser.LOCK_ID + "='"
				+ devID + "' AND " + SmartLockExContentDefine.AuthorizeUser.AUTHORIZE_ID + "='"
				+ id + "'";

		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.AuthorizeUser.ALL_CONTENT_URI, null,
				where, null, null);
		if (null != cur) {
			while (cur.moveToNext()) {
				item = new AuthorizeUserDefine();
				
				item.mID = cur
						.getInt(SmartLockExContentDefine.AuthorizeUser.ID_COLUMN);
				item.mAuthorizeID = cur
						.getInt(SmartLockExContentDefine.AuthorizeUser.AUTHORIZE_ID_COLUMN);
				item.mLockID = cur
						.getString(SmartLockExContentDefine.AuthorizeUser.LOCK_ID_COLUMN);
				item.mUserName = cur
						.getString(SmartLockExContentDefine.AuthorizeUser.USER_NAME_COLUMN);
				item.mUserStatus = cur
						.getInt(SmartLockExContentDefine.AuthorizeUser.USER_STATUS_COLUMN);

				break;
			}
			cur.close();
			return item;
		} else {
			return null;
		}
	}

	// 增加新插座
	public long add(AuthorizeUserDefine item) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == item) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.AuthorizeUser.AUTHORIZE_ID,
				item.mAuthorizeID);
		values.put(SmartLockExContentDefine.AuthorizeUser.LOCK_ID,
				item.mLockID);
		values.put(SmartLockExContentDefine.AuthorizeUser.USER_NAME,
				item.mUserName);
		values.put(SmartLockExContentDefine.AuthorizeUser.USER_STATUS,
				item.mUserStatus);
		
		Uri uri = mContentResolver
				.insert(SmartLockExContentDefine.AuthorizeUser.ALL_CONTENT_URI,
						values);
		PubFunc.log(TAG, "Insert a record");

		if (null == uri) {
			return 0;
		}
		try {
			long id = ContentUris.parseId(uri);
			return id;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return 0;
		}
	}

	// 删除指定的插座
	public boolean delete(String devID, String id) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartLockExContentDefine.AuthorizeUser.LOCK_ID + "='"
				+ devID + "' AND " + SmartLockExContentDefine.AuthorizeUser.AUTHORIZE_ID + "='"
				+ id + "'";
		int count = mContentResolver.delete(
				SmartLockExContentDefine.AuthorizeUser.ALL_CONTENT_URI, where,
				null);
		return count > 0 ? true : false;
	}

	// 删除所有插座
	public void clear() {
		if (null != mContentResolver) {
			PubFunc.log(TAG, "Clear call lock");
			mContentResolver.delete(
					SmartLockExContentDefine.AuthorizeUser.ALL_CONTENT_URI,
					null, null);
		}
	}

	// 修改插座
	public int modify(AuthorizeUserDefine item) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == item) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.AuthorizeUser.AUTHORIZE_ID,
				item.mAuthorizeID);
		values.put(SmartLockExContentDefine.AuthorizeUser.LOCK_ID,
				item.mLockID);
		values.put(SmartLockExContentDefine.AuthorizeUser.USER_NAME,
				item.mUserName);
		values.put(SmartLockExContentDefine.AuthorizeUser.USER_STATUS,
				item.mUserStatus);
		
		Uri uri = mContentResolver
				.insert(SmartLockExContentDefine.AuthorizeUser.ALL_CONTENT_URI,
						values);

		String where = SmartLockExContentDefine.AuthorizeUser.LOCK_ID + "='"
				+ item.mLockID + "' AND " + SmartLockExContentDefine.AuthorizeUser.AUTHORIZE_ID + "='"
				+ item.mAuthorizeID + "'";
		int index = mContentResolver.update(
				SmartLockExContentDefine.AuthorizeUser.ALL_CONTENT_URI,
				values, where, null);
		PubFunc.log(TAG, "Update a lock");
		return index;
	}
	
}