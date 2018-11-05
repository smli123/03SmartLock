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
		public static final String LOCK_RELATION = "Relation";

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
		public static final int LOCK_RELATION_COLUMN = 10;
		
		public static final int RELATION_MASTER = 0;
		public static final int RELATION_SLAVE = 1;

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
				+ LOCK_CHARGE + " integer default 0, "
				+ LOCK_RELATION + " integer default 0);";

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
	    public int  	mOperData;
	    public int  	mUserType;
	    public int  	mMarked;
	    public String 	mDetail;
	    public String 	mMessageTime;
		
		// 列名属性
		public static final String MESSAGE_ID = "MessageID";
		public static final String USER_NAME = "UserName";
		public static final String DEVICE_ID = "DeviceID";
		public static final String DEVICE_NAME = "DeviceName";
		public static final String OPER_TYPE = "OperType";
		public static final String OPER_DATA = "OperData";
		public static final String USER_TYPE = "UserType";
		public static final String MARKED = "Marked";
		public static final String DETAIL = "Detail";
		public static final String MESSAGE_TIME = "MessageTime";

		// 列数
		public static final int ID_COLUMN = 0;
		public static final int MESSAGE_ID_COLUMN = 1;
		public static final int USER_NAME_COLUMN = 2;
		public static final int DEVICE_ID_COLUMN = 3;
		public static final int DEVICE_NAME_COLUMN = 4;
		public static final int OPER_TYPE_COLUMN = 5;
		public static final int OPER_DATA_COLUMN = 6;
		public static final int USER_TYPE_COLUMN = 7;
		public static final int MARKED_COLUMN = 8;
		public static final int DETAIL_COLUMN = 9;
		public static final int MESSAGE_TIME_COLUMN = 10;
		
		// create table
		public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" 
				+ _ID + " integer primary key autoincrement, " 
				+ MESSAGE_ID + " text default '', "  
				+ USER_NAME + " text default '', " 
				+ DEVICE_ID + " text default '', " 
				+ DEVICE_NAME + " text default '', " 
				+ OPER_TYPE + " integer default 0, "
				+ OPER_DATA + " integer default 0, "
				+ USER_TYPE + " integer default 0, "
				+ MARKED + " integer default 0, "
				+ DETAIL + " text default '', "
				+ MESSAGE_TIME + " text default '');";

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

	public final static class AuthorizeUser implements BaseColumns {
		public static final String AUTHORITY = "com.sherman.dataprovider.authorizeuserprovider";

		public static final String ALL_RECORD = "smartlockexauthorizes";
		public static final String ONE_RECORD = "smartlockexauthorize";

		public static final Uri ALL_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ALL_RECORD);
		public static final Uri ONE_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ONE_RECORD);

		public static final String TABLE_NAME = "AuthorizeUser";
		
		// 列名属性
		public static final String INDEX_ID = "IndexID";
		public static final String AUTHORIZE_ID = "AuthorizeID";
		public static final String LOCK_ID = "LockID";
		public static final String USER_NAME = "UserName";
		public static final String USER_STATUS = "UserStatus";

		// 列数
		public static final int ID_COLUMN = 0;
		public static final int INDEX_ID_COLUMN = 1;
		public static final int AUTHORIZE_ID_COLUMN = 2;
		public static final int LOCK_ID_COLUMN = 3;
		public static final int USER_NAME_COLUMN = 4;
		public static final int USER_STATUS_COLUMN = 5;
		
		// create table
		public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" 
				+ _ID + " integer primary key autoincrement, " 
				+ INDEX_ID + " integer default 0, "
				+ AUTHORIZE_ID + " integer default 0, "
				+ LOCK_ID + " text default '', "
				+ USER_NAME + " text default '', " 
				+ USER_STATUS + " integer default 0);";

		public static final String TRIGGER_LOCK_DELETE = "Trigger_Locks";
		public final static String SQL_TRIGER_LOCK_DELETE = " CREATE TRIGGER ["
				+ TRIGGER_LOCK_DELETE + "]" + " AFTER DELETE ON [" + TABLE_NAME
				+ "]" + " BEGIN" + " delete from "
				+ AuthorizeUser.TABLE_NAME + " where "
				+ AuthorizeUser.USER_STATUS + " = old." + USER_STATUS + ";"
				+ " END;";
		
		public final static String DEFAULT_SORT_ORDER = _ID + " desc";
	}

	public final static class Password implements BaseColumns {
		public static final String AUTHORITY = "com.sherman.dataprovider.passwordprovider";

		public static final String ALL_RECORD = "smartlockexpasswords";
		public static final String ONE_RECORD = "smartlockexpassword";

		public static final Uri ALL_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ALL_RECORD);
		public static final Uri ONE_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + ONE_RECORD);

		public static final String TABLE_NAME = "Password";
		
		// 列名属性
		public static final String INDEX_ID = "IndexID";
		public static final String PASSWORD_ID = "PasswordID";
		public static final String LOCK_ID = "LockID";
		public static final String USER_NAME = "UserName";
		public static final String PASSWORD_TYPE = "Password_Type";
		public static final String PASSWORD_TEXT = "Password_Text";
		public static final String BEGIN_TIIME = "Begin_Time";
		public static final String END_TIIME = "End_Time";
		public static final String MEMO = "Memo";

		// 列数
		public static final int ID_COLUMN = 0;
		public static final int INDEX_ID_COLUMN = 1;
		public static final int PASSWORD_ID_COLUMN = 2;
		public static final int LOCK_ID_COLUMN = 3;
		public static final int USER_NAME_COLUMN = 4;
		public static final int PASSWORD_TYPE_COLUMN = 5;
		public static final int PASSWORD_TEXT_COLUMN = 6;
		public static final int BEGIN_TIME_COLUMN = 7;
		public static final int END_TIME_COLUMN = 8;
		public static final int MEMO_COLUMN = 9;

		// create table
		public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" 
				+ _ID + " integer primary key autoincrement, " 
				+ INDEX_ID + " integer default 0, "
				+ PASSWORD_ID + " integer default 0, "
				+ LOCK_ID + " text default '', "  
				+ USER_NAME + " text default '', " 
				+ PASSWORD_TYPE + " integer default 0, "
				+ PASSWORD_TEXT + " text default '', "
				+ BEGIN_TIIME + " text default '', "
				+ END_TIIME + " text default '', "
				+ MEMO + " text default '' );";

		public static final String TRIGGER_LOCK_DELETE = "Trigger_Locks";
		public final static String SQL_TRIGER_LOCK_DELETE = " CREATE TRIGGER ["
				+ TRIGGER_LOCK_DELETE + "]" + " AFTER DELETE ON [" + TABLE_NAME
				+ "]" + " BEGIN" + " delete from "
				+ Password.TABLE_NAME + " where "
				+ Password.PASSWORD_ID + " = old." + PASSWORD_ID + ";"
				+ " END;";
		
		public final static String DEFAULT_SORT_ORDER = _ID + " desc";
	}

	
}
