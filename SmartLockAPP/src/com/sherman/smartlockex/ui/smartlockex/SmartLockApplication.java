package com.sherman.smartlockex.ui.smartlockex;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.sherman.smartlockex.processhandler.SmartLockEventHandler;
import com.sherman.smartlockex.ui.login.LoginActivity;
import com.sherman.smartlockex.ui.util.AppTimeoutTask;
import com.sherman.smartlockex.R;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.View;

public class SmartLockApplication extends Application {
    private static SmartLockApplication sMe;
    private static boolean mLogined = false;
    
    private static Timer mCheckTimer = null;
    private static AppTimeoutTask mTimeoutTask = null;
    
    public static int mCounter = 0;
    private final static long TIMEOUT = 3600000;  //60分钟，若60分钟内没有操作，APP便自动�??出�??
    
    private static boolean mFirstUse = true;
    
    private List<Activity> mList = new ArrayList<Activity>(); 
    
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	sMe = this;
    	mCheckTimer = new Timer();
    	mFirstUse = false;
    	
    	SmartLockEventHandler.getInstance().init();
    	
    	mSharedPreferences = getSharedPreferences("SmartPlug", Activity.MODE_PRIVATE);
		loadAPPConfig();
		
	}
	
	private void loadAPPConfig() {
//		PubDefine.THINGZDO_HOST_NAME = mSharedPreferences.getString("serverip", PubDefine.SERVERIP_HANGZHOU);

	}
    
    @Override
    public void onTerminate() {
    	// TODO Auto-generated method stub
    	super.onTerminate();
    }
    
    public static SmartLockApplication getInstance() {
    	return sMe;
    }
    
	public static Context getContext(){
		return getInstance().getApplicationContext();
	}    
    
    public static boolean isLogined() {
    	return mLogined; 
    }
    
    public static void setLogined(boolean isLogined) {
    	mLogined = isLogined; 
    	if (mLogined) {
    		mCounter = 0;
    	}
    }
    
    public static void setFirstUse(boolean isFirstUse) {
    	mFirstUse = isFirstUse;	
    }
    
    public static boolean getFirstUse() {
    	return mFirstUse;
    }
    
    // add Activity  
    public void addActivity(Activity activity) { 
        mList.add(activity); 
    } 
    
    public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null && !(activity instanceof LoginActivity))
                    activity.finish(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    } 
    
    public void finishSmartLockActivity() {
        try { 
            for (Activity activity : mList) { 
                if ((activity instanceof SmartLockActivity)) {
                    activity.finish();
                    break;
                }
                
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        }     	
    }
    
    
    public void onLowMemory() { 
        super.onLowMemory();     
        System.gc(); 
    }   
    
    public static void resetTask() {
    	if (null != mCheckTimer) {
    		mCheckTimer.cancel();
    	}
    	if (null != mTimeoutTask) {
    		mTimeoutTask.cancel();
    	}
    	
    	mCheckTimer = null;
    	mTimeoutTask = null;
    	mCheckTimer = new Timer();
    	mTimeoutTask = new AppTimeoutTask();
    	mCheckTimer.schedule(mTimeoutTask, TIMEOUT, TIMEOUT);
    }
    
    public static void closeTask() {
    	if (null != mCheckTimer) {
    		mCheckTimer.cancel();
    	}
    	if (null != mTimeoutTask) {
    		mTimeoutTask.cancel();
    	}
    	
    	mCheckTimer = null;
    	mTimeoutTask = null;
    }
   
}
