package com.sherman.smartlockex.dataprovider;

import java.util.ArrayList;

import com.sherman.smartlockex.ui.common.PasswordDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.SmartLockDefine;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class SmartLockExPasswordHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "PasswordHelper";
	
	public SmartLockExPasswordHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}
	
	// 获得数据
	private ArrayList<PasswordDefine> getData(Cursor cur) {
		ArrayList<PasswordDefine> devs = new ArrayList<PasswordDefine>();
		while (cur.moveToNext()) {
			PasswordDefine item = new PasswordDefine();
			item.mID = cur
					.getInt(SmartLockExContentDefine.Password.ID_COLUMN);
			item.mIndex = cur
					.getInt(SmartLockExContentDefine.Password.INDEX_ID_COLUMN);
			item.mPasswordID = cur
					.getInt(SmartLockExContentDefine.Password.PASSWORD_ID_COLUMN);
			item.mLockID = cur
					.getString(SmartLockExContentDefine.Password.LOCK_ID_COLUMN);
			item.mUserName = cur
					.getString(SmartLockExContentDefine.Password.USER_NAME_COLUMN);
			item.mType = cur
					.getInt(SmartLockExContentDefine.Password.PASSWORD_TYPE_COLUMN);
			item.mBeginTime = cur
					.getString(SmartLockExContentDefine.Password.BEGIN_TIIME_COLUMN);
			item.mEndTime = cur
					.getString(SmartLockExContentDefine.Password.END_TIIME_COLUMN);
			item.mMemo = cur
					.getString(SmartLockExContentDefine.Password.MEMO_COLUMN);

			devs.add(item);
		}
		return devs;
	}

	// 获得指定用户名下所有插座
	public ArrayList<PasswordDefine> getAll(String devID) {
		ArrayList<PasswordDefine> devs = new ArrayList<PasswordDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartLockExContentDefine.Password.LOCK_ID
				+ " = '" + devID + "'";
		String order = SmartLockExContentDefine.Password._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.Password.ALL_CONTENT_URI, null,
				where, null, order);

		if (null != cur) {
			devs = getData(cur);
			cur.close();
		}

		return devs;
	}

	// 获得指定插座信息
	public PasswordDefine get(String devID, String id) {
		PasswordDefine item = null;
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartLockExContentDefine.Password.LOCK_ID + "='"
				+ devID + "' AND " + SmartLockExContentDefine.Password.PASSWORD_ID + "='"
				+ id + "'";

		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.Password.ALL_CONTENT_URI, null,
				where, null, null);
		if (null != cur) {
			while (cur.moveToNext()) {
				item = new PasswordDefine();
				
				item.mID = cur
						.getInt(SmartLockExContentDefine.Password.ID_COLUMN);
				item.mIndex = cur
						.getInt(SmartLockExContentDefine.Password.INDEX_ID_COLUMN);
				item.mPasswordID = cur
						.getInt(SmartLockExContentDefine.Password.PASSWORD_ID_COLUMN);
				item.mLockID = cur
						.getString(SmartLockExContentDefine.Password.LOCK_ID_COLUMN);
				item.mUserName = cur
						.getString(SmartLockExContentDefine.Password.USER_NAME_COLUMN);
				item.mType = cur
						.getInt(SmartLockExContentDefine.Password.PASSWORD_TYPE_COLUMN);
				item.mBeginTime = cur
						.getString(SmartLockExContentDefine.Password.BEGIN_TIIME_COLUMN);
				item.mEndTime = cur
						.getString(SmartLockExContentDefine.Password.END_TIIME_COLUMN);
				item.mMemo = cur
						.getString(SmartLockExContentDefine.Password.MEMO_COLUMN);
				break;
			}
			cur.close();
			return item;
		} else {
			return null;
		}
	}

	// 增加新插座
	public long add(PasswordDefine item) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == item) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.Password.PASSWORD_ID,
				item.mPasswordID);
		values.put(SmartLockExContentDefine.Password.INDEX_ID,
				item.mIndex);
		values.put(SmartLockExContentDefine.Password.LOCK_ID,
				item.mLockID);
		values.put(SmartLockExContentDefine.Password.USER_NAME,
				item.mUserName);
		values.put(SmartLockExContentDefine.Password.PASSWORD_TYPE,
				item.mType);
		values.put(SmartLockExContentDefine.Password.BEGIN_TIIME,
				item.mBeginTime);
		values.put(SmartLockExContentDefine.Password.END_TIIME,
				item.mEndTime);
		values.put(SmartLockExContentDefine.Password.MEMO,
				item.mMemo);
		
		Uri uri = mContentResolver
				.insert(SmartLockExContentDefine.Password.ALL_CONTENT_URI,
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
		String where = SmartLockExContentDefine.Password.LOCK_ID + "='"
				+ devID + "' AND " + SmartLockExContentDefine.Password.PASSWORD_ID + "='"
				+ id + "'";
		int count = mContentResolver.delete(
				SmartLockExContentDefine.Password.ALL_CONTENT_URI, where,
				null);
		return count > 0 ? true : false;
	}

	// 删除所有插座
	public void clear() {
		if (null != mContentResolver) {
			PubFunc.log(TAG, "Clear call lock");
			mContentResolver.delete(
					SmartLockExContentDefine.Password.ALL_CONTENT_URI,
					null, null);
		}
	}

	// 修改插座
	public int modify(PasswordDefine item) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == item) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.Password.PASSWORD_ID,
				item.mPasswordID);
		values.put(SmartLockExContentDefine.Password.INDEX_ID,
				item.mIndex);
		values.put(SmartLockExContentDefine.Password.LOCK_ID,
				item.mLockID);
		values.put(SmartLockExContentDefine.Password.USER_NAME,
				item.mUserName);
		values.put(SmartLockExContentDefine.Password.PASSWORD_TYPE,
				item.mType);
		values.put(SmartLockExContentDefine.Password.BEGIN_TIIME,
				item.mBeginTime);
		values.put(SmartLockExContentDefine.Password.END_TIIME,
				item.mEndTime);
		values.put(SmartLockExContentDefine.Password.MEMO,
				item.mMemo);

		String where = SmartLockExContentDefine.Password.LOCK_ID + "='"
				+ item.mLockID + "'" + SmartLockExContentDefine.Password.PASSWORD_ID + "='"
				+ item.mPasswordID + "'";
		int index = mContentResolver.update(
				SmartLockExContentDefine.Password.ALL_CONTENT_URI,
				values, where, null);
		PubFunc.log(TAG, "Update a lock");
		return index;
	}
	
}