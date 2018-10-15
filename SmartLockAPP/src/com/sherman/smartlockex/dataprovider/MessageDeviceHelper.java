package com.sherman.smartlockex.dataprovider;

import java.util.ArrayList;

import com.sherman.smartlockex.ui.common.MessageDeviceDefine;
import com.sherman.smartlockex.ui.common.PubFunc;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class MessageDeviceHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "MessageDeviceHelper";
	
	public MessageDeviceHelper(Context context){
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}

	// 获得数据
	private ArrayList<MessageDeviceDefine> getData(Cursor cur) {
		ArrayList<MessageDeviceDefine> devs = new ArrayList<MessageDeviceDefine>();
		while (cur.moveToNext()) {
			MessageDeviceDefine item = new MessageDeviceDefine();
			item.mID = cur
					.getInt(SmartLockExContentDefine.MessageDevice.ID_COLUMN);
			item.mMessageID = cur
					.getString(SmartLockExContentDefine.MessageDevice.MESSAGE_ID_COLUMN);
			item.mUserName = cur
					.getString(SmartLockExContentDefine.MessageDevice.USER_NAME_COLUMN);
			item.mDeviceID = cur
					.getString(SmartLockExContentDefine.MessageDevice.DEVICE_ID_COLUMN);
			item.mDeviceName = cur
					.getString(SmartLockExContentDefine.MessageDevice.DEVICE_NAME_COLUMN);
			item.mOperType = cur
					.getInt(SmartLockExContentDefine.MessageDevice.OPER_TYPE_COLUMN);
			item.mDetail = cur
					.getString(SmartLockExContentDefine.MessageDevice.DETAIL_COLUMN);

			devs.add(item);
		}
		return devs;
	}
	
	// 获得数据
	private MessageDeviceDefine getFistData(Cursor cur) {
		while (cur.moveToNext()) {
			MessageDeviceDefine item = new MessageDeviceDefine();
			item.mID = cur
					.getInt(SmartLockExContentDefine.MessageDevice.ID_COLUMN);
			item.mMessageID = cur
					.getString(SmartLockExContentDefine.MessageDevice.MESSAGE_ID_COLUMN);
			item.mUserName = cur
					.getString(SmartLockExContentDefine.MessageDevice.USER_NAME_COLUMN);
			item.mDeviceID = cur
					.getString(SmartLockExContentDefine.MessageDevice.DEVICE_ID_COLUMN);
			item.mDeviceName = cur
					.getString(SmartLockExContentDefine.MessageDevice.DEVICE_NAME_COLUMN);
			item.mOperType = cur
					.getInt(SmartLockExContentDefine.MessageDevice.OPER_TYPE_COLUMN);
			item.mDetail = cur
					.getString(SmartLockExContentDefine.MessageDevice.DETAIL_COLUMN);
			
			return item;

		}
		return null;
	}

	// 获得指定用户名下所有插座
	public ArrayList<MessageDeviceDefine> getAllMessage(String user) {
		ArrayList<MessageDeviceDefine> devs = new ArrayList<MessageDeviceDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartLockExContentDefine.MessageDevice.USER_NAME
				+ " = '" + user + "'";
		String order = SmartLockExContentDefine.MessageDevice._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.MessageDevice.ALL_CONTENT_URI, null,
				where, null, order);

		if (null != cur) {
			devs = getData(cur);
			cur.close();
		}

		return devs;
	}
	

	// 获得指定用户名下所有插座
	public MessageDeviceDefine getNewMessage(String user) {
		MessageDeviceDefine item = null;
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartLockExContentDefine.MessageDevice.USER_NAME
				+ " = '" + user + "'";
		String order = SmartLockExContentDefine.MessageDevice._ID + " desc";
		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.MessageDevice.ALL_CONTENT_URI, null,
				where, null, order);

		if (null != cur) {
			item = getFistData(cur);
			cur.close();
		}

		return item;
	}

	// 获得指定插座信息
	public MessageDeviceDefine getMessage(String id) {
		MessageDeviceDefine item = null;
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartLockExContentDefine.MessageDevice.MESSAGE_ID + "='"
				+ id + "'";

		Cursor cur = mContentResolver.query(
				SmartLockExContentDefine.MessageDevice.ALL_CONTENT_URI, null,
				where, null, null);
		if (null != cur) {
			while (cur.moveToNext()) {

				item = new MessageDeviceDefine();
				item.mID = cur
						.getInt(SmartLockExContentDefine.MessageDevice.ID_COLUMN);
				item.mMessageID = cur
						.getString(SmartLockExContentDefine.MessageDevice.MESSAGE_ID_COLUMN);
				item.mUserName = cur
						.getString(SmartLockExContentDefine.MessageDevice.USER_NAME_COLUMN);
				item.mDeviceID = cur
						.getString(SmartLockExContentDefine.MessageDevice.DEVICE_ID_COLUMN);
				item.mDeviceName = cur
						.getString(SmartLockExContentDefine.MessageDevice.DEVICE_NAME_COLUMN);
				item.mOperType = cur
						.getInt(SmartLockExContentDefine.MessageDevice.OPER_TYPE_COLUMN);
				item.mDetail = cur
						.getString(SmartLockExContentDefine.MessageDevice.DETAIL_COLUMN);

				break;
			}
			cur.close();
			return item;
		} else {
			return null;
		}
	}

	// 增加新插座
	public long addMessage(MessageDeviceDefine item) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == item) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.MessageDevice.MESSAGE_ID,
				item.mMessageID);
		values.put(SmartLockExContentDefine.MessageDevice.USER_NAME,
				item.mUserName);
		values.put(SmartLockExContentDefine.MessageDevice.DEVICE_ID,
				item.mDeviceID);
		values.put(SmartLockExContentDefine.MessageDevice.DEVICE_NAME,
				item.mDeviceName);
		values.put(SmartLockExContentDefine.MessageDevice.OPER_TYPE,
				item.mOperType);
		values.put(SmartLockExContentDefine.MessageDevice.DETAIL,
				item.mDetail);

		Uri uri = mContentResolver
				.insert(SmartLockExContentDefine.MessageDevice.ALL_CONTENT_URI,
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
		String where = SmartLockExContentDefine.MessageDevice.MESSAGE_ID + "='"
				+ id + "'";
		int count = mContentResolver.delete(
				SmartLockExContentDefine.MessageDevice.ALL_CONTENT_URI, where,
				null);
		return count > 0 ? true : false;
	}

	// 删除所有插座
	public void clearMessage() {
		if (null != mContentResolver) {
			PubFunc.log(TAG, "Clear call messsage device");
			mContentResolver.delete(
					SmartLockExContentDefine.MessageDevice.ALL_CONTENT_URI,
					null, null);
		}
	}

	// 修改插座
	public int modifyMessage(MessageDeviceDefine item) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == item) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(SmartLockExContentDefine.MessageDevice.MESSAGE_ID,
				item.mMessageID);
		values.put(SmartLockExContentDefine.MessageDevice.USER_NAME,
				item.mUserName);
		values.put(SmartLockExContentDefine.MessageDevice.DEVICE_ID,
				item.mDeviceID);
		values.put(SmartLockExContentDefine.MessageDevice.DEVICE_NAME,
				item.mDeviceName);
		values.put(SmartLockExContentDefine.MessageDevice.OPER_TYPE,
				item.mOperType);
		values.put(SmartLockExContentDefine.MessageDevice.DETAIL,
				item.mDetail);

		String where = SmartLockExContentDefine.MessageDevice.MESSAGE_ID + "='"
				+ item.mMessageID + "'";
		int index = mContentResolver.update(
				SmartLockExContentDefine.MessageDevice.ALL_CONTENT_URI,
				values, where, null);
		PubFunc.log(TAG, "Update a message device");
		return index;
	}
}