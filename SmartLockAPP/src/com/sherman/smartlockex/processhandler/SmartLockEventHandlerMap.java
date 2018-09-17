package com.sherman.smartlockex.processhandler;

public class SmartLockEventHandlerMap {
	private int mEvent = -1;
	private SmartLockEventHandler mHandler = null;
	
	public SmartLockEventHandlerMap(int event, SmartLockEventHandler handler){
		mEvent = event;
		mHandler = handler;
	}
	
	public int getEvent(){
		return mEvent;
	}
	
	public SmartLockEventHandler getHandler(){
		return mHandler;
	}
};
