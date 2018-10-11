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
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (null == db) {
			return;
		}
		
		db.execSQL(SmartLockExContentDefine.Lock.TABLE_CREATE);
		
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
