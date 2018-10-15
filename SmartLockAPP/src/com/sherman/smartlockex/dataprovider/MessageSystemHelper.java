package com.sherman.smartlockex.dataprovider;

import java.util.ArrayList;

import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.MessageSystemDefine;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class MessageSystemHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "MessageSystemHelper";
	
	public MessageSystemHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}

	// 获得数据
	private ArrayList<MessageSystemDefine> getData(Cursor cur) {
		ArrayList<MessageSystemDefine> devs = new ArrayList<MessageSystemDefine>();
		while (cur.moveToNext()) {
			MessageSystemDefine item = new MessageSystemDefine();
			item.mID = cur
					.getInt(SmartLockExContentDefine.MessageSystem.ID_COLUMN);
			item.mMessageID = cur
					.getString(SmartLockExContentDefine.MessageSystem.MESSAGE_ID_COLUMN);
			item.mUserName = cur
					.getString(SmartLockExContentDefine.MessageSystem.USER_NAME_COLUMN);
			item.mDeviceID = cur
					.getString(SmartLockExContentDefine.MessageSystem.DEVICE_ID_COLUMN);
			item.mDeviceName = cur
					.getString(SmartLockExContentDefine.MessageSystem.DEVICE_NAME_COLUMN);
			item.mOperType = cur
					.getInt(SmartLockExContentDefine.MessageSystem.OPER_TYPE_COLUMN);
			item.mDetail = cur
					.getString(SmartLockExContentDefine.MessageSystem.DETAIL_COLUMN);

			devs.add(item);
		}
		return devs;
	}

	// 获得数据
	private MessageSystemDefine getFistData(Cursor cur) {
		while (cur.moveToNext()) {
			MessageSystemDefine item = new MessageSystemDefine();
			item.mID = cur
					.getInt(SmartLockExContentDefine.MessageSystem.ID_COLUMN);
			item.mMessageID = cur
					.getString(SmartLockExContentDefine.MessageSystem.MESSAGE_ID_COLUMN);
			item.mUserName = cur
					.getString(SmartLockExContentDefine.MessageSystem.USER_NAME_COLUMN);
			item.mDeviceID = cur
					.getString(SmartLockExContentDefine.MessageSystem.DEVICE_ID_COLUMN);
			item.mDeviceName = cur
					.getString(SmartLockExContentDefine.MessageSystem.DEVICE_NAME_COLUMN);
			item.mOperType = cur
					.getInt(SmartLockExContentDefine.MessageSystem.OPER_TYPE_COLUMN);
			item.mDetail = cur
					.getString(SmartLockExContentDefine.MessageSystem.DETAIL_COLUMN);
			
			return item;

		}
		return null;
	}

	// 获得指定用户名下所有插座
	public ArrayList<MessageSystemDefine> getAllMessage(String user) {
		ArrayList<MessageSystemDefine> devs = new ArrayList<MessageSystemDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartLockExContentDefine.MessageSystem.USER_NAME
				+ " = '" + user + "'";
		String order = SmartLockExContentDefine.MessageSystem._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.MessageSystem.ALL_CONTENT_URI, null,
				where, null, order);

		if (null != cur) {
			devs = getData(cur);
			cur.close();
		}

		return devs;
	}

	// 获得指定用户名下所有插座
	public MessageSystemDefine getNewMessage(String user) {
		MessageSystemDefine item = null;
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartLockExContentDefine.MessageSystem.USER_NAME
				+ " = '" + user + "'";
		String order = SmartLockExContentDefine.MessageSystem._ID + " desc";
		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.MessageSystem.ALL_CONTENT_URI, null,
				where, null, order);

		if (null != cur) {
			item = getFistData(cur);
			cur.close();
		}

		return item;
	}

	// 获得指定插座信息
	public MessageSystemDefine getMessage(String id) {
		MessageSystemDefine item = null;
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartLockExContentDefine.MessageSystem.MESSAGE_ID + "='"
				+ id + "'";

		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.MessageSystem.ALL_CONTENT_URI, null,
				where, null, null);
		if (null != cur) {
			while (cur.moveToNext()) {

				item = new MessageSystemDefine();
				item.mID = cur
						.getInt(SmartLockExContentDefine.MessageSystem.ID_COLUMN);
				item.mMessageID = cur
						.getString(SmartLockExContentDefine.MessageSystem.MESSAGE_ID_COLUMN);
				item.mUserName = cur
						.getString(SmartLockExContentDefine.MessageSystem.USER_NAME_COLUMN);
				item.mDeviceID = cur
						.getString(SmartLockExContentDefine.MessageSystem.DEVICE_ID_COLUMN);
				item.mDeviceName = cur
						.getString(SmartLockExContentDefine.MessageSystem.DEVICE_NAME_COLUMN);
				item.mOperType = cur
						.getInt(SmartLockExContentDefine.MessageSystem.OPER_TYPE_COLUMN);
				item.mDetail = cur
						.getString(SmartLockExContentDefine.MessageSystem.DETAIL_COLUMN);

				break;
			}
			cur.close();
			return item;
		} else {
			return null;
		}
	}

	// 增加新插座
	public long addMessage(MessageSystemDefine item) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == item) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.MessageSystem.MESSAGE_ID,
				item.mMessageID);
		values.put(SmartLockExContentDefine.MessageSystem.USER_NAME,
				item.mUserName);
		values.put(SmartLockExContentDefine.MessageSystem.DEVICE_ID,
				item.mDeviceID);
		values.put(SmartLockExContentDefine.MessageSystem.DEVICE_NAME,
				item.mDeviceName);
		values.put(SmartLockExContentDefine.MessageSystem.OPER_TYPE,
				item.mOperType);
		values.put(SmartLockExContentDefine.MessageSystem.DETAIL,
				item.mDetail);

		Uri uri = mContentResolver
				.insert(SmartLockExContentDefine.MessageSystem.ALL_CONTENT_URI,
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
	public boolean deleteMessage(String id) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartLockExContentDefine.MessageSystem.MESSAGE_ID + "='"
				+ id + "'";
		int count = mContentResolver.delete(
				SmartLockExContentDefine.MessageSystem.ALL_CONTENT_URI, where,
				null);
		return count > 0 ? true : false;
	}

	// 删除所有插座
	public void clearMessage() {
		if (null != mContentResolver) {
			PubFunc.log(TAG, "Clear call messsage device");
			mContentResolver.delete(
					SmartLockExContentDefine.MessageSystem.ALL_CONTENT_URI,
					null, null);
		}
	}

	// 修改插座
	public int modifyMessage(MessageSystemDefine item) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == item) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.MessageSystem.MESSAGE_ID,
				item.mMessageID);
		values.put(SmartLockExContentDefine.MessageSystem.USER_NAME,
				item.mUserName);
		values.put(SmartLockExContentDefine.MessageSystem.DEVICE_ID,
				item.mDeviceID);
		values.put(SmartLockExContentDefine.MessageSystem.DEVICE_NAME,
				item.mDeviceName);
		values.put(SmartLockExContentDefine.MessageSystem.OPER_TYPE,
				item.mOperType);
		values.put(SmartLockExContentDefine.MessageSystem.DETAIL,
				item.mDetail);

		String where = SmartLockExContentDefine.MessageSystem.MESSAGE_ID + "='"
				+ item.mMessageID + "'";
		int index = mContentResolver.update(
				SmartLockExContentDefine.MessageSystem.ALL_CONTENT_URI,
				values, where, null);
		PubFunc.log(TAG, "Update a message device");
		return index;
	}
}