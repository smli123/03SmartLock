package com.sherman.smartlockex.processhandler;

import java.util.HashMap;
import java.util.Map;

import com.sherman.smartlockex.ui.common.PubDefine;


public class SmartLockMessage {
	// 透传命令
	public static final String CMD_SP_PASSTHROUGHT = "APPPASSTHROUGH";
	
	// 只跟服务器有交互的命令
	public static final String CMD_SP_REGISTER 		= "APPREG";
	public static final String CMD_SP_MODPWD 		= "APPMODPWD";
	public static final String CMD_SP_MODEMAIL 		= "APPMODEMAIL";
	public static final String CMD_SP_FINDPWD 		= "APPFINDPWD";
	public static final String CMD_SP_LOGIN_SERVER 	= "APPLOGIN";
	public static final String CMD_SP_LOGINOUT 		= "APPLOGOUT";
	public static final String CMD_SP_ADDLOCK 		= "APPADDLOCK";
	public static final String CMD_SP_DELLOCK 		= "APPDELLOCK";
	public static final String CMD_SP_MODYLOCK 		= "APPMODLOCK";
	public static final String CMD_SP_QRYLOCK 		= "APPQRYLOCK";
	
	public static final String CMD_SP_QRYAUTHORIZEUSER 	= "APPQRYAUTHORIZEUSER";
	public static final String CMD_SP_ADDAUTHORIZEUSER 	= "APPADDAUTHORIZEUSER";
	public static final String CMD_SP_DELAUTHORIZEUSER 	= "APPDELAUTHORIZEUSER";
	public static final String CMD_SP_QRYPASSWORD 		= "APPQRYPASSWORD";
	public static final String CMD_SP_ADDPASSWORD 		= "APPADDPASSWORD";
	public static final String CMD_SP_DELPASSWORD 		= "APPDELPASSWORD";
	
	// 模块交互命令
	public static final String CMD_SP_LOGIN_MODULE = "LOGIN"; // 登录模块的命令，用于直连方式登录

	// 跟模块有交互的命令
	public static final String CMD_SP_BACK2AP = "APPBACK2AP";
	public static final String CMD_SP_OPEN_LOCK = "APPLOCK_OPEN";

	/**设备通知消息头*/
	public static final String CMD_SP_NOTIFYONLINE = "NOTIFYONLINE";
	public static final String CMD_SP_NOTIFYLOCKSTATUS = "NOTIFY_STASTUS";
	public static final String CMD_SP_NOTIFYLOCKBELL = "NOTIFY_BELL";
	public static final String CMD_SP_NOTIFYLOCKALARM = "NOTIFY_ALARM";

	// -------------------------------------------------------
	public static final int EVT_SP_REGISTER = 10;
	public static final int EVT_SP_MODPWD = 11;
	public static final int EVT_SP_MODEMAIL = 12;
	public static final int EVT_SP_FINDPWD = 13;
	public static final int EVT_SP_LOGIN = 14;
	public static final int EVT_SP_LOGINOUT = 15;

	public static final int EVT_SP_ADDLOCK = 100;
	public static final int EVT_SP_DELLOCK = 101;
	public static final int EVT_SP_MODLOCK = 102;
	public static final int EVT_SP_QRYLOCK = 106;
	
	public static final int EVT_SP_QRYAUTHORIZEUSER = 107;
	public static final int EVT_SP_ADDAUTHORIZEUSER = 108;
	public static final int EVT_SP_DELAUTHORIZEUSER = 109;
	public static final int EVT_SP_QRYPASSWORD = 110;
	public static final int EVT_SP_ADDPASSWORD = 111;
	public static final int EVT_SP_DELPASSWORD = 112;
	
	public static final int EVT_SP_BACK2AP = 150;
	public static final int EVT_SP_OPEN_LOCK = 151;

	public static final int EVT_SP_NOTIFYONLINE = 200;
	public static final int EVT_SP_NOTIFYLOCKSTATUS = 201;
	public static final int EVT_SP_NOTIFYLOCKBELL = 202;
	public static final int EVT_SP_NOTIFYLOCKALARM = 203;

	public static Map<String, Integer> mEventCommand = new HashMap<String, Integer>();
	static {
		mEventCommand.put(CMD_SP_REGISTER, EVT_SP_REGISTER);
		mEventCommand.put(CMD_SP_MODPWD, EVT_SP_MODPWD);
		mEventCommand.put(CMD_SP_MODEMAIL, EVT_SP_MODEMAIL);
		mEventCommand.put(CMD_SP_FINDPWD, EVT_SP_FINDPWD);
		mEventCommand.put(CMD_SP_LOGIN_SERVER, EVT_SP_LOGIN);
		mEventCommand.put(CMD_SP_LOGINOUT, EVT_SP_LOGINOUT);
		
		mEventCommand.put(CMD_SP_ADDLOCK, EVT_SP_ADDLOCK);
		mEventCommand.put(CMD_SP_DELLOCK, EVT_SP_DELLOCK);
		mEventCommand.put(CMD_SP_MODYLOCK, EVT_SP_MODLOCK);
		mEventCommand.put(CMD_SP_QRYLOCK, EVT_SP_QRYLOCK);
		
		mEventCommand.put(CMD_SP_QRYAUTHORIZEUSER, EVT_SP_QRYAUTHORIZEUSER);
		mEventCommand.put(CMD_SP_ADDAUTHORIZEUSER, EVT_SP_ADDAUTHORIZEUSER);
		mEventCommand.put(CMD_SP_DELAUTHORIZEUSER, EVT_SP_DELAUTHORIZEUSER);
		mEventCommand.put(CMD_SP_QRYPASSWORD, EVT_SP_QRYPASSWORD);
		mEventCommand.put(CMD_SP_ADDPASSWORD, EVT_SP_ADDPASSWORD);
		mEventCommand.put(CMD_SP_DELPASSWORD, EVT_SP_DELPASSWORD);
		
		mEventCommand.put(CMD_SP_BACK2AP, EVT_SP_BACK2AP);
		mEventCommand.put(CMD_SP_OPEN_LOCK, EVT_SP_OPEN_LOCK);
		
		mEventCommand.put(CMD_SP_NOTIFYONLINE, EVT_SP_NOTIFYONLINE);
		
		// SmartLock
		mEventCommand.put(CMD_SP_NOTIFYLOCKSTATUS, EVT_SP_NOTIFYLOCKSTATUS);
		mEventCommand.put(CMD_SP_NOTIFYLOCKBELL, EVT_SP_NOTIFYLOCKBELL);
		mEventCommand.put(CMD_SP_NOTIFYLOCKALARM, EVT_SP_NOTIFYLOCKALARM);

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
		mCommandAction.put(CMD_SP_MODPWD, PubDefine.USER_MODIFY_PASSWORD_BROADCAST);
		mCommandAction.put(CMD_SP_MODEMAIL, PubDefine.USER_MODIFY_EMAIL_BROADCAST);
		mCommandAction.put(CMD_SP_FINDPWD, PubDefine.FINDPWD_BROADCAST);
		mCommandAction.put(CMD_SP_LOGIN_SERVER, PubDefine.LOGIN_BROADCAST);
		mCommandAction.put(CMD_SP_LOGINOUT, PubDefine.LOGOUT_BROADCAST);
		
		mCommandAction.put(CMD_SP_ADDLOCK, PubDefine.LOCK_ADDLOCK_BROADCAST);
		mCommandAction.put(CMD_SP_DELLOCK, PubDefine.LOCK_DELETELOCK_BROADCAST);
		mCommandAction.put(CMD_SP_MODYLOCK, PubDefine.LOCK_MODIFY_LOCKNAME_BROADCAST);
		mCommandAction.put(CMD_SP_QRYLOCK, PubDefine.LOCK_QRYLOCK_BROADCAST);
		
		mCommandAction.put(CMD_SP_QRYAUTHORIZEUSER, PubDefine.LOCK_QRYAUTHORIZEUSER_BROADCAST);
		mCommandAction.put(CMD_SP_ADDAUTHORIZEUSER, PubDefine.LOCK_ADDAUTHORIZEUSER_BROADCAST);
		mCommandAction.put(CMD_SP_DELAUTHORIZEUSER, PubDefine.LOCK_DELAUTHORIZEUSER_BROADCAST);
		
		mCommandAction.put(CMD_SP_QRYPASSWORD, PubDefine.LOCK_QRYPASSWORD_BROADCAST);
		mCommandAction.put(CMD_SP_ADDPASSWORD, PubDefine.LOCK_ADDPASSWORD_BROADCAST);
		mCommandAction.put(CMD_SP_DELPASSWORD, PubDefine.LOCK_DELPASSWORD_BROADCAST);
		
		mCommandAction.put(CMD_SP_BACK2AP, PubDefine.LOCK_BACK2AP_BROADCAST);
		mCommandAction.put(CMD_SP_OPEN_LOCK, PubDefine.LOCK_OPENLOCK_BROADCAST);

		mCommandAction.put(CMD_SP_NOTIFYONLINE, PubDefine.LOCK_NOTIFY_ONLINE_BROADCAST);
		
		// SmartLock
		mCommandAction.put(CMD_SP_NOTIFYLOCKSTATUS, PubDefine.LOCK_NOTIFY_STATUS_BROADCAST);
		mCommandAction.put(CMD_SP_NOTIFYLOCKBELL, PubDefine.LOCK_NOTIFY_BELL_BROADCAST);
		mCommandAction.put(CMD_SP_NOTIFYLOCKALARM, PubDefine.LOCK_NOTIFY_ALARM_BROADCAST);

	};

	public static String getAction(final String cmd) {
		return mCommandAction.get(cmd);
	}
}
