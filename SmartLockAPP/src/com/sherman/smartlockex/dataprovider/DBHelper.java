package com.sherman.smartlockex.dataprovider;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper  extends SQLiteOpenHelper{
	private static DBHelper mInstance = null;
	
	public static DBHelper getInstance(Context context){
		if (null == mInstance){
			mInstance = new DBHelper(context);	
		}
		return 	mInstance;
	}
	
	ArrayList<String> mTableList = new ArrayList<String>();

	private DBHelper(Context context) {
		super(context, SmartLockExDbDefine.g_SmartLockDbName, null, SmartLockExDbDefine.DATABASE_VERSION);

		mTableList.add(SmartLockExContentDefine.Lock.TABLE_NAME);
		mTableList.add(SmartLockExContentDefine.MessageDevice.TABLE_NAME);
		mTableList.add(SmartLockExContentDefine.MessageSystem.TABLE_NAME);
		mTableList.add(SmartLockExContentDefine.AuthorizeUser.TABLE_NAME);
		mTableList.add(SmartLockExContentDefine.Password.TABLE_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (null == db) {
			return;
		}

		db.execSQL(SmartLockExContentDefine.Lock.TABLE_CREATE);
		db.execSQL(SmartLockExContentDefine.MessageDevice.TABLE_CREATE);
		db.execSQL(SmartLockExContentDefine.MessageSystem.TABLE_CREATE);
		db.execSQL(SmartLockExContentDefine.AuthorizeUser.TABLE_CREATE);
		db.execSQL(SmartLockExContentDefine.Password.TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (null == db) {
			return;
		}
		
		for (int i = 0; i < mTableList.size(); i++){
    		db.execSQL("DROP TABLE IF EXISTS " + mTableList.get(i));
		}	
		onCreate(db);
	}
}
