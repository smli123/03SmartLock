package com.sherman.smartlockex.processhandler;

import java.util.ArrayList;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * 消息注册和分发
 * */
public class SmartLockEventHandler extends Handler {
	public static final int EVENT_START_ACTIVITY = 0;
	public static final int EVENT_FINISH_ACTIVITY = 1;
	public static final int EVENT_MESSAGE_HEADER = 4;

	public interface IActivityDestroyListener {
		public void onActivityDestroy(Activity act);
	}

	private static SmartLockEventHandler s_Me = null;
	private ArrayList<SmartLockEventHandlerMap> mEventHandlerMaps = new ArrayList<SmartLockEventHandlerMap>();

	public static SmartLockEventHandler getInstance() {
		if (s_Me == null) {
			s_Me = new SmartLockEventHandler();
		}
		
		return s_Me;
	}

	/*
	 * 根据事件编码获得对应的处理Handler
	 */
	public SmartLockEventHandler getTheEventHandler(int event) {
		SmartLockEventHandler eventHandler = null;
		for (SmartLockEventHandlerMap eventHandlerMap : mEventHandlerMaps) {
			if (eventHandlerMap.getEvent() == event) {
				eventHandler = eventHandlerMap.getHandler();
				break;
			}
		}

		return eventHandler;
	}

	@Override
	public void handleMessage(Message msg) {
		do {
			Looper.prepare();
			int event = msg.what;
			// PubFunc.log("received MESSAGE.what=" + event);
			SmartLockEventHandler eventHandler = getTheEventHandler(event);
			if (eventHandler != null) {
				eventHandler.handleMessage(msg);
			} else {
				PubFunc.log("Message: invalid Event! MSG:" + event);
			}
			Looper.loop();
		} while (false);
	}

	public void init() {
		// PubFunc.log("init MyCallEventHandler!!!");
		registerEvent();
	}

	private void registerEvent() {
		mEventHandlerMaps
				.add(new SmartLockEventHandlerMap(
						SmartLockMessage.EVT_SP_LOGIN,
						new SmartLockEventHandlerLogin()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_LOGINOUT,
				new SmartLockEventHandlerLogout()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_REGISTER,
				new SmartLockEventHandlerRegister()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_MODPWD,
				new SmartLockEventHandlerModifyPwd()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_MODEMAIL,
				new SmartLockEventHandlerModifyEmail()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_FINDPWD,
				new SmartLockEventHandlerResetPwd()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_ADDPLUG,
				new SmartLockEventHandlerAddPlug()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_DELPLUG,
				new SmartLockEventHandlerDeletePlug()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_MODYPLUG,
				new SmartLockEventHandlerModifyPlugName()));

		mEventHandlerMaps
				.add(new SmartLockEventHandlerMap(
						SmartLockMessage.EVT_SP_POWER,
						new SmartLockEventHandlerPower()));

		mEventHandlerMaps
				.add(new SmartLockEventHandlerMap(
						SmartLockMessage.EVT_SP_LIGHT,
						new SmartLockEventHandlerLight()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_BACK2AP,
				new SmartLockEventHandlerBACK2AP()));

		// usb
		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_USB, new SmartLockEventHandlerUSB()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_NOTIFYONLINE,
				new SmartLockEventHandlerNotifyOnline()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_NOTIFYPOWER,
				new SmartLockEventHandlerNotifyPower()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_NOTIFYLIGHT,
				new SmartLockEventHandlerNotifyLight()));

		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_NOTIFYUPGRADEAP,
				new SmartLockEventHandlerNotifyUpgradeAP()));

		// SmartLock
		mEventHandlerMaps.add(new SmartLockEventHandlerMap(
				SmartLockMessage.EVT_SP_NOTIFYLOCKSTATUS,
				new SmartLockEventHandlerNotifyLockStatus()));

	}

	protected void log(String logString) {
		Log.d(getClass().getName(), logString);
	}

	protected String getUnknowErrorMsg() {
		return SmartLockApplication
				.getContext()
				.getString(R.string.app_response_login_fail);
	}
}
