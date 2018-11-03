package com.sherman.smartlockex.dataprovider;

import java.util.ArrayList;

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

	// 获得数据
	private ArrayList<SmartLockDefine> getData(Cursor cur) {
		ArrayList<SmartLockDefine> devs = new ArrayList<SmartLockDefine>();
		while (cur.moveToNext()) {
			SmartLockDefine item = new SmartLockDefine();
			item.mID = cur
					.getInt(SmartLockExContentDefine.Lock.ID_COLUMN);
			item.mLockID = cur
					.getString(SmartLockExContentDefine.Lock.LOCK_ID_COLUMN);
			item.mUserName = cur
					.getString(SmartLockExContentDefine.Lock.USER_NAME_COLUMN);
			item.mName = cur
					.getString(SmartLockExContentDefine.Lock.LOCK_NAME_COLUMN);
			item.mStatus = cur
					.getInt(SmartLockExContentDefine.Lock.LOCK_STATUS_COLUMN);
			item.mOnline = cur
					.getInt(SmartLockExContentDefine.Lock.LOCK_ONLINE_COLUMN) == 1 ? true : false;
			item.mAddress = cur
					.getString(SmartLockExContentDefine.Lock.LOCK_ADDRESS_COLUMN);
			item.mVersion = cur
					.getString(SmartLockExContentDefine.Lock.LOCK_VERSION_COLUMN);
			item.mType = cur
					.getString(SmartLockExContentDefine.Lock.LOCK_TYPE_COLUMN);
			item.mCharge = cur
					.getInt(SmartLockExContentDefine.Lock.LOCK_CHARGE_COLUMN);
			item.mRelation = cur
					.getInt(SmartLockExContentDefine.Lock.LOCK_RELATION_COLUMN);

			devs.add(item);
		}
		return devs;
	}

	// 获得指定用户名下所有插座
	public ArrayList<SmartLockDefine> getAll(String user) {
		ArrayList<SmartLockDefine> devs = new ArrayList<SmartLockDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartLockExContentDefine.Lock.USER_NAME
				+ " = '" + user + "'";
		String order = SmartLockExContentDefine.Lock._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.Lock.ALL_CONTENT_URI, null,
				where, null, order);

		if (null != cur) {
			devs = getData(cur);
			cur.close();
		}

		return devs;
	}

	// 获得指定插座信息
	public SmartLockDefine get(String id) {
		SmartLockDefine device = null;
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartLockExContentDefine.Lock.LOCK_ID + "='"
				+ id + "'";

		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.Lock.ALL_CONTENT_URI, null,
				where, null, null);
		if (null != cur) {
			while (cur.moveToNext()) {
				device = new SmartLockDefine();
				device.mID = cur
						.getInt(SmartLockExContentDefine.Lock.ID_COLUMN);
				device.mLockID = cur
						.getString(SmartLockExContentDefine.Lock.LOCK_ID_COLUMN);
				device.mUserName = cur
						.getString(SmartLockExContentDefine.Lock.USER_NAME_COLUMN);
				device.mName = cur
						.getString(SmartLockExContentDefine.Lock.LOCK_NAME_COLUMN);
				device.mStatus = cur
						.getInt(SmartLockExContentDefine.Lock.LOCK_STATUS_COLUMN);
				device.mOnline = cur
						.getInt(SmartLockExContentDefine.Lock.LOCK_ONLINE_COLUMN) == 1 ? true : false;
				device.mAddress = cur
						.getString(SmartLockExContentDefine.Lock.LOCK_ADDRESS_COLUMN);
				device.mVersion = cur
						.getString(SmartLockExContentDefine.Lock.LOCK_VERSION_COLUMN);
				device.mType = cur
						.getString(SmartLockExContentDefine.Lock.LOCK_TYPE_COLUMN);
				device.mCharge = cur
						.getInt(SmartLockExContentDefine.Lock.LOCK_CHARGE_COLUMN);
				device.mRelation = cur
						.getInt(SmartLockExContentDefine.Lock.LOCK_RELATION_COLUMN);
				break;
			}
			cur.close();
			return device;
		} else {
			return null;
		}
	}

	// 增加新插座
	public long add(SmartLockDefine device) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == device) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.Lock.LOCK_ID,
				device.mLockID);
		values.put(SmartLockExContentDefine.Lock.USER_NAME,
				device.mUserName);
		values.put(SmartLockExContentDefine.Lock.LOCK_NAME,
				device.mName);
		values.put(SmartLockExContentDefine.Lock.LOCK_STATUS,
				device.mStatus);
		values.put(SmartLockExContentDefine.Lock.LOCK_ONLINE,
				device.mOnline == true ? 1 : 0);
		values.put(SmartLockExContentDefine.Lock.LOCK_ADDRESS,
				device.mAddress);
		values.put(SmartLockExContentDefine.Lock.LOCK_VERSION,
				device.mVersion);
		values.put(SmartLockExContentDefine.Lock.LOCK_TYPE,
				device.mType);
		values.put(SmartLockExContentDefine.Lock.LOCK_CHARGE,
				device.mCharge);
		values.put(SmartLockExContentDefine.Lock.LOCK_RELATION,
				device.mRelation);

		Uri uri = mContentResolver
				.insert(SmartLockExContentDefine.Lock.ALL_CONTENT_URI,
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
	public boolean delete(String id) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartLockExContentDefine.Lock.LOCK_ID + "='"
				+ id + "'";
		int count = mContentResolver.delete(
				SmartLockExContentDefine.Lock.ALL_CONTENT_URI, where,
				null);
		return count > 0 ? true : false;
	}

	// 删除所有插座
	public void clear() {
		if (null != mContentResolver) {
			PubFunc.log(TAG, "Clear call lock");
			mContentResolver.delete(
					SmartLockExContentDefine.Lock.ALL_CONTENT_URI,
					null, null);
		}
	}

	// 修改插座
	public int modify(SmartLockDefine device) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == device) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.Lock.LOCK_ID,
				device.mLockID);
		values.put(SmartLockExContentDefine.Lock.USER_NAME,
				device.mUserName);
		values.put(SmartLockExContentDefine.Lock.LOCK_NAME,
				device.mName);
		values.put(SmartLockExContentDefine.Lock.LOCK_STATUS,
				device.mStatus);
		values.put(SmartLockExContentDefine.Lock.LOCK_ONLINE,
				device.mOnline);
		values.put(SmartLockExContentDefine.Lock.LOCK_ADDRESS,
				device.mAddress);
		values.put(SmartLockExContentDefine.Lock.LOCK_VERSION,
				device.mVersion);
		values.put(SmartLockExContentDefine.Lock.LOCK_TYPE,
				device.mType);
		values.put(SmartLockExContentDefine.Lock.LOCK_CHARGE,
				device.mCharge);
		values.put(SmartLockExContentDefine.Lock.LOCK_RELATION,
				device.mRelation);

		String where = SmartLockExContentDefine.Lock.LOCK_ID + "='"
				+ device.mLockID + "'";
		int index = mContentResolver.update(
				SmartLockExContentDefine.Lock.ALL_CONTENT_URI,
				values, where, null);
		PubFunc.log(TAG, "Update a lock");
		return index;
	}

	// 判断用户名下的指定ID的插座是否存在
	public boolean isLockExist(final String username, final String lockname) {
		if (null == mContentResolver) {
			Log.e(TAG, "mContentResolver=null");
			return true;
		}

		String where = SmartLockExContentDefine.Lock.USER_NAME + "='"
				+ username + "' AND "
				+ SmartLockExContentDefine.Lock.LOCK_NAME + "='"
				+ lockname + "'";
		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.Lock.ALL_CONTENT_URI, null,
				where, null, null);
		if (null == cur || 0 == cur.getCount()) {
			cur.close();
			return false;
		}

		cur.close();
		return true;
	}

	public void setAllLocksOffline() {
		if (null == mContentResolver) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.Lock.LOCK_ONLINE, false);
		values.put(SmartLockExContentDefine.Lock.LOCK_STATUS, 0);
		values.put(SmartLockExContentDefine.Lock.LOCK_CHARGE, 0);

		mContentResolver.update(
				SmartLockExContentDefine.Lock.ALL_CONTENT_URI,
				values, null, null);
		PubFunc.log(TAG, "Update setAllLocksOffline");
	}
}