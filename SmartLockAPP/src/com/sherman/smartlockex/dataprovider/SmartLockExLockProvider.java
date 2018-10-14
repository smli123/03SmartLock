package com.sherman.smartlockex.dataprovider;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class SmartLockExLockProvider extends ContentProvider {
	private static final String TAG= "LockProvider"; 

	private DBHelper dbHelper;
	private SQLiteDatabase gotaDB;
	private Context mContenxt; 
	
	public static final int ACTIONS         = 1;
	public static final int ACTION          = 2;
	
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;	
	static{
		uriMatcher.addURI(SmartLockExContentDefine.Lock.AUTHORITY, SmartLockExContentDefine.Lock.ALL_RECORD, ACTIONS);
		uriMatcher.addURI(SmartLockExContentDefine.Lock.AUTHORITY, SmartLockExContentDefine.Lock.ONE_RECORD + "/#", ACTION);
	}	
	
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		int count = 0;
		gotaDB = DBHelper.getInstance(getContext()).getWritableDatabase();
		if (null == gotaDB) {
			return 0;
		}		
		
		String strID;
		switch (uriMatcher.match(uri)) {
			case ACTIONS:
				count = gotaDB.delete(SmartLockExContentDefine.Lock.TABLE_NAME,  where, selectionArgs);
				break;
			case ACTION:
				strID = uri.getPathSegments().get(1);
				count = gotaDB.delete(SmartLockExContentDefine.Lock.TABLE_NAME, 
						SmartLockExContentDefine.Lock._ID + "=" + strID
						+ (!TextUtils.isEmpty(where) ? " AND ("+ where + ")" : ""),
						selectionArgs);
				break;
			default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		mContenxt.getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
	    case ACTIONS:
	        return SmartLockExDbDefine.CONTENT_TYPE;
	    case ACTION:
	    	return SmartLockExDbDefine.CONTENT_ITEM_TYPE;
	    default:
	        throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (uriMatcher.match(uri) != ACTIONS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		gotaDB = DBHelper.getInstance(getContext()).getWritableDatabase();
		if (null == gotaDB) {
			return null;
		}		
		
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		
		String tableName = "";
		switch (uriMatcher.match(uri)) {
		    case ACTIONS:
		    	tableName = SmartLockExContentDefine.Lock.TABLE_NAME; 
			    break;
		    default: 
			    break;		
		}
		try{
		    long rowId = gotaDB.insert(tableName, SmartLockExContentDefine.Lock._ID, values);
		    if (rowId > 0) {
			    Uri carInfoUri = ContentUris.withAppendedId(uri, rowId);
			    ContentResolver resovler = mContenxt.getContentResolver();
			    resovler.notifyChange(carInfoUri, null);
			    return carInfoUri;
		    } 
		    return null;
		} catch(Exception e) {
			Log.e( TAG + "insert", e.getMessage());	
			return null;
		}
		
	}

	@Override
	public boolean onCreate() {
		mContenxt = getContext();
		
		dbHelper = DBHelper.getInstance(getContext());
		gotaDB = dbHelper.getWritableDatabase();
		boolean result = (gotaDB == null)? false : true;
		return result;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String orderBy = null;
		Cursor cursor = null;
		String groupBy = null;
		gotaDB = DBHelper.getInstance(getContext()).getReadableDatabase();
		if (null == gotaDB) {
			return null;
		}
		
		switch (uriMatcher.match(uri)) {
			case ACTIONS:
				cursor = gotaDB.query(true, 
			              SmartLockExContentDefine.Lock.TABLE_NAME.toLowerCase(), 
			              projection, 
			              null, 
			              selectionArgs, 
			              groupBy,
			              null, 
			              sortOrder, 
			              null);
//				cursor = gotaDB.query(true, 
//			              SmartLockExContentDefine.Lock.TABLE_NAME.toLowerCase(), 
//			              projection, 
//			              selection, 
//			              selectionArgs, 
//			              groupBy,
//			              null, 
//			              sortOrder, 
//			              null);
				break;
			case ACTION:
				long id = ContentUris.parseId(uri);
				String where = SmartLockExContentDefine.Lock._ID + "=" + id;
				if (null != selection && !"".equals(selection)){
					where = where + " and " + selection;
				}
							
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = SmartLockExContentDefine.Lock._ID;
				} else {
					orderBy = sortOrder;
				}
				
				cursor = gotaDB.query(SmartLockExContentDefine.Lock.TABLE_NAME.toLowerCase(), 
						              projection, 
						              where, 
						              selectionArgs, 
						              null, 
						              null, 
						              orderBy);
				
				break;
			default: 
				break;
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] selectionArgs) {
		int count;
		gotaDB = DBHelper.getInstance(getContext()).getWritableDatabase();
		if (null == gotaDB) {
			return 0;
		}		
		
		switch (uriMatcher.match(uri)) {
			case ACTIONS:
				count = gotaDB.update(SmartLockExContentDefine.Lock.TABLE_NAME, values, where, selectionArgs);
				break;
			case ACTION:
				String strID = uri.getPathSegments().get(1);
				count = gotaDB.update(SmartLockExContentDefine.Lock.TABLE_NAME, values,
						SmartLockExContentDefine.Lock._ID + "=" + strID
						+ (!TextUtils.isEmpty(where) ? " AND ("+ where + ")" : ""), 
						selectionArgs);
				break;
			default: 
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		mContenxt.getContentResolver().notifyChange(uri, null);
		return count;
	}

}
