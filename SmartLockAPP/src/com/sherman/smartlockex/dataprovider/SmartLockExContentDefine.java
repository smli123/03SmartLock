package com.sherman.smartlockex.dataprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class SmartLockExContentDefine {

	public final static class Lock implements BaseColumns {
		public static final String AUTHORITY = "com.sherman.dataprovider.smartlockprovider";

		public static final String ALL_RECORD = "smartlockexlocks";
		public static final String ONE_RECORD = "smartlockexlock";

		public static final Uri ALL_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ALL_RECORD);
		public static final Uri ONE_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ONE_RECORD);

		public static final String TABLE_NAME = "Lock";
		
		// 列名属性
		public static final String LOCK_ID = "LockID";
		public static final String USER_NAME = "UserName";
		public static final String LOCK_NAME = "LockName";
		public static final String LOCK_STATUS = "Lock_Status";
		public static final String LOCK_ONLINE = "Online";
		public static final String LOCK_ADDRESS = "Address";
		public static final String LOCK_VERSION = "Version";
		public static final String LOCK_TYPE = "DeviceType";
		public static final String LOCK_CHARGE = "Charge";

		// 列数
		public static final int ID_COLUMN = 0;
		public static final int LOCK_ID_COLUMN = 1;
		public static final int USER_NAME_COLUMN = 2;
		public static final int LOCK_NAME_COLUMN = 3;
		public static final int LOCK_STATUS_COLUMN = 4;
		public static final int LOCK_ONLINE_COLUMN = 5;
		public static final int LOCK_ADDRESS_COLUMN = 6;
		public static final int LOCK_VERSION_COLUMN = 7;
		public static final int LOCK_TYPE_COLUMN = 8;
		public static final int LOCK_CHARGE_COLUMN = 9;

		// create table
		public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" 
				+ _ID + " integer primary key autoincrement, " 
				+ LOCK_ID + " text default '', "  
				+ USER_NAME + " text default '', " 
				+ LOCK_NAME + " text default '', " 
				+ LOCK_STATUS + " integer default 0, "
				+ LOCK_ONLINE + " integer default 0, "
				+ LOCK_ADDRESS + " text default '', "
				+ LOCK_VERSION + " text default '', "
				+ LOCK_TYPE + " text default '', "
				+ LOCK_CHARGE + " integer default 0);";

		public static final String TRIGGER_LOCK_DELETE = "Trigger_Locks";
		public final static String SQL_TRIGER_LOCK_DELETE = " CREATE TRIGGER ["
				+ TRIGGER_LOCK_DELETE + "]" + " AFTER DELETE ON [" + TABLE_NAME
				+ "]" + " BEGIN" + " delete from "
				+ Lock.TABLE_NAME + " where "
				+ Lock.LOCK_NAME + " = old." + LOCK_STATUS + ";"
				+ " END;";
		
		public final static String DEFAULT_SORT_ORDER = _ID + " desc";
	}


	public final static class MessageDevice implements BaseColumns {
		public static final String AUTHORITY = "com.sherman.dataprovider.messagedeviceprovider";

		public static final String ALL_RECORD = "messagedevices";
		public static final String ONE_RECORD = "messagedevice";

		public static final Uri ALL_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ALL_RECORD);
		public static final Uri ONE_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ONE_RECORD);

		public static final String TABLE_NAME = "MessageDevice";
		
		
		public int    	mID;
		public String 	mMessageID;
		public String 	mUserName;
		public String 	mDeviceID;
		public String 	mDeviceName;
	    public int  	mOperType;
	    public String 	mDetail;
		
		// 列名属性
		public static final String MESSAGE_ID = "MessageID";
		public static final String USER_NAME = "UserName";
		public static final String DEVICE_ID = "DeviceID";
		public static final String DEVICE_NAME = "DeviceName";
		public static final String OPER_TYPE = "OperType";
		public static final String DETAIL = "Detail";

		// 列数
		public static final int ID_COLUMN = 0;
		public static final int MESSAGE_ID_COLUMN = 1;
		public static final int USER_NAME_COLUMN = 2;
		public static final int DEVICE_ID_COLUMN = 3;
		public static final int DEVICE_NAME_COLUMN = 4;
		public static final int OPER_TYPE_COLUMN = 5;
		public static final int DETAIL_COLUMN = 6;

		// create table
		public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" 
				+ _ID + " integer primary key autoincrement, " 
				+ MESSAGE_ID + " text default '', "  
				+ USER_NAME + " text default '', " 
				+ DEVICE_ID + " text default '', " 
				+ DEVICE_NAME + " text default '', " 
				+ OPER_TYPE + " integer default 0, "
				+ DETAIL + " text default '');";

		public final static String DEFAULT_SORT_ORDER = _ID + " desc";
	}

	public final static class MessageSystem implements BaseColumns {
		public static final String AUTHORITY = "com.sherman.dataprovider.messagesystemprovider";

		public static final String ALL_RECORD = "messagesystems";
		public static final String ONE_RECORD = "messagesystem";

		public static final Uri ALL_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ALL_RECORD);
		public static final Uri ONE_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ONE_RECORD);

		public static final String TABLE_NAME = "MessageSystem";
		
		
		public int    	mID;
		public String 	mMessageID;
		public String 	mUserName;
		public String 	mDeviceID;
		public String 	mDeviceName;
	    public int  	mOperType;
	    public String 	mDetail;
		
		// 列名属性
		public static final String MESSAGE_ID = "MessageID";
		public static final String USER_NAME = "UserName";
		public static final String DEVICE_ID = "DeviceID";
		public static final String DEVICE_NAME = "DeviceName";
		public static final String OPER_TYPE = "OperType";
		public static final String DETAIL = "Detail";

		// 列数
		public static final int ID_COLUMN = 0;
		public static final int MESSAGE_ID_COLUMN = 1;
		public static final int USER_NAME_COLUMN = 2;
		public static final int DEVICE_ID_COLUMN = 3;
		public static final int DEVICE_NAME_COLUMN = 4;
		public static final int OPER_TYPE_COLUMN = 5;
		public static final int DETAIL_COLUMN = 6;

		// create table
		public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" 
				+ _ID + " integer primary key autoincrement, " 
				+ MESSAGE_ID + " text default '', "  
				+ USER_NAME + " text default '', " 
				+ DEVICE_ID + " text default '', " 
				+ DEVICE_NAME + " text default '', " 
				+ OPER_TYPE + " integer default 0, "
				+ DETAIL + " text default '');";

		public final static String DEFAULT_SORT_ORDER = _ID + " desc";
	}

	
}
