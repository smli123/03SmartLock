package com.sherman.smartlockex.processhandler;

import java.util.HashMap;
import java.util.Map;

import com.sherman.smartlockex.ui.common.PubDefine;


public class SmartLockMessage {
	// 只跟服务器有交互的命令
	public static final String CMD_SP_REGISTER = "APPREG";
	public static final String CMD_SP_MODPWD = "APPMODPWD";
	public static final String CMD_SP_MODEMAIL = "APPMODEMAIL";
	public static final String CMD_SP_FINDPWD = "APPFINDPWD";
	public static final String CMD_SP_LOGIN_SERVER = "APPLOGIN";
	public static final String CMD_SP_LOGIN_MODULE = "LOGIN"; // 登录模块的命令，用于直连方式登录
	public static final String CMD_SP_LOGINOUT = "APPLOGOUT";
	public static final String CMD_SP_ADDPLUG = "APPADDPLUG";
	public static final String CMD_SP_DELPLUG = "APPDELPLUG";
	public static final String CMD_SP_MODYPLUG = "APPMODPLUG";
	public static final String CMD_SP_QRYPLUG = "APPQRYPLUG";

	// 跟模块有交互的命令
	public static final String CMD_SP_POWER = "APPPOWER";
	public static final String CMD_SP_LIGHT = "APPLIGHT";
	public static final String CMD_SP_USB = "APPUSB";
	public static final String CMD_SP_BACK2AP = "APPBACK2AP";

	// 服务器主动通知的命令
	public static final String CMD_SP_NOTIFYONLINE = "APPNOTIFYONLINE";
	public static final String CMD_SP_NOTIFYPOWER = "APPNOTIFYPOWER";
	public static final String CMD_SP_NOTIFYLIGHT = "APPNOTIFYLIGHT";
	public static final String CMD_SP_NOTIFYUSB = "APPNOTIFYUSB";

	public static final String CMD_SP_NOTIFYUPGRADEAP = "APPNOTIFYUPGRADEAP";

	// -------------------------------------------------------
	public static final int EVT_SP_REGISTER = 10;
	public static final int EVT_SP_MODPWD = 11;
	public static final int EVT_SP_MODEMAIL = 12;
	public static final int EVT_SP_FINDPWD = 13;

	public static final int EVT_SP_LOGIN = 50;
	public static final int EVT_SP_LOGINOUT = 51;

	public static final int EVT_SP_ADDPLUG = 100;
	public static final int EVT_SP_DELPLUG = 101;
	public static final int EVT_SP_MODYPLUG = 102;
	public static final int EVT_SP_QRYPLUG = 106;
	public static final int EVT_SP_POWER = 108;
	public static final int EVT_SP_LIGHT = 109;
	public static final int EVT_SP_USB = 113;

	public static final int EVT_SP_BACK2AP = 119;

	public static final int EVT_SP_NOTIFYONLINE = 200;
	public static final int EVT_SP_NOTIFYPOWER = 201;
	public static final int EVT_SP_NOTIFYLIGHT = 202;
	public static final int EVT_SP_NOTIFYUSB = 203;
	
	public static final int EVT_SP_NOTIFYUPGRADEAP = 204;

	public static Map<String, Integer> mEventCommand = new HashMap<String, Integer>();
	static {
		mEventCommand.put(CMD_SP_REGISTER, EVT_SP_REGISTER);
		mEventCommand.put(CMD_SP_MODPWD, EVT_SP_MODPWD);
		mEventCommand.put(CMD_SP_MODEMAIL, EVT_SP_MODEMAIL);
		mEventCommand.put(CMD_SP_FINDPWD, EVT_SP_FINDPWD);
		mEventCommand.put(CMD_SP_LOGIN_SERVER, EVT_SP_LOGIN);
		mEventCommand.put(CMD_SP_LOGINOUT, EVT_SP_LOGINOUT);
		mEventCommand.put(CMD_SP_ADDPLUG, EVT_SP_ADDPLUG);
		mEventCommand.put(CMD_SP_DELPLUG, EVT_SP_DELPLUG);
		mEventCommand.put(CMD_SP_MODYPLUG, EVT_SP_MODYPLUG);
		mEventCommand.put(CMD_SP_QRYPLUG, EVT_SP_QRYPLUG);
		mEventCommand.put(CMD_SP_POWER, EVT_SP_POWER);
		mEventCommand.put(CMD_SP_LIGHT, EVT_SP_LIGHT);
		mEventCommand.put(CMD_SP_BACK2AP, EVT_SP_BACK2AP);
		mEventCommand.put(CMD_SP_USB, EVT_SP_USB);
		mEventCommand.put(CMD_SP_NOTIFYONLINE, EVT_SP_NOTIFYONLINE);
		mEventCommand.put(CMD_SP_NOTIFYPOWER, EVT_SP_NOTIFYPOWER);
		mEventCommand.put(CMD_SP_NOTIFYLIGHT, EVT_SP_NOTIFYLIGHT);
		mEventCommand.put(CMD_SP_NOTIFYUPGRADEAP, EVT_SP_NOTIFYUPGRADEAP);

	};

	/*
	 * 根据命令码获得对应的事件编码 由于发到模块的命令前加了APP前缀，而模块返回的命令码没有这个前缀，对于直连或摇一摇，需要进行适配
	 */
	public static int getEvent(final String cmd) {

		for (String key : mEventCommand.keySet()) {
			if (key.equals(cmd)) {
				return mEventCommand.get(cmd);
			}
		}

		for (String key : mEventCommand.keySet()) {
			if (key.equals("APP" + cmd)) {
				return mEventCommand.get("APP" + cmd);
			}
		}

		return -1;
	}

	public static Map<String, String> mCommandAction = new HashMap<String, String>();
	static {
		mCommandAction.put(CMD_SP_REGISTER, PubDefine.REGISTER_BROADCAST);
		mCommandAction.put(CMD_SP_MODPWD, PubDefine.USER_MODIFY_PASSWORD);
		mCommandAction.put(CMD_SP_MODEMAIL, PubDefine.USER_MODIFY_EMAIL);
		mCommandAction.put(CMD_SP_FINDPWD, PubDefine.FINDPWD_BROADCAST);
		mCommandAction.put(CMD_SP_LOGIN_SERVER, PubDefine.LOGIN_BROADCAST);
		mCommandAction.put(CMD_SP_LOGINOUT, PubDefine.LOGOUT_BROADCAST);
		mCommandAction.put(CMD_SP_ADDPLUG, PubDefine.PLUG_ADD_TASK);
		mCommandAction.put(CMD_SP_DELPLUG, PubDefine.PLUG_DELETE);
		mCommandAction.put(CMD_SP_MODYPLUG, PubDefine.PLUG_MODIFY_PLUGNAME);
		mCommandAction.put(CMD_SP_QRYPLUG, PubDefine.PLUG_UPDATE);
		mCommandAction.put(CMD_SP_POWER, PubDefine.PLUG_POWER_ACTION);
		mCommandAction.put(CMD_SP_LIGHT, PubDefine.PLUG_LIGHT_ACTION);
		mCommandAction.put(CMD_SP_BACK2AP, PubDefine.PLUG_BACK2AP_ACTION);

		// mCommandAction.put(CMD_SP_BELL, EVT_SP_BELL);
		mCommandAction.put(CMD_SP_USB, PubDefine.PLUG_USB_ACTION);
		mCommandAction.put(CMD_SP_NOTIFYONLINE, PubDefine.PLUG_NOTIFY_ONLINE);
		mCommandAction.put(CMD_SP_NOTIFYPOWER, PubDefine.PLUG_NOTIFY_POWER);
		mCommandAction.put(CMD_SP_NOTIFYLIGHT, PubDefine.PLUG_NOTIFY_LIGHT);

	};

	public static String getAction(final String cmd) {
		return mCommandAction.get(cmd);
	}
}
