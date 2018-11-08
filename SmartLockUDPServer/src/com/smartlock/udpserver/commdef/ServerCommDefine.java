package com.smartlock.udpserver.commdef;

public class ServerCommDefine {
	/** 透传命令 **/
	public final static String APP_PASSTHROUGH_MSG_HEADER	= "APPPASSTHROUGH";			//Server不做处理，透传APP发过来的命令给MOUDLE

	/**APP通用消息头*/
	public final static String APP_REGUSER_MSG_HEADER 		= "APPREG";				//注册
	public final static String APP_MOD_PWD_MSG_HEADER		= "APPMODPWD";			//修改密码
	public final static String APP_MOD_EMAIL_MSG_HEADER		= "APPMODEMAIL";		//修改邮箱
	public final static String APP_RSTPWD_MSG_HEADER		= "APPFINDPWD";			//重置用户密码
	public final static String APP_LOGIN_MSG_HEADER 		= "APPLOGIN";			//LOGIN
	public final static String APP_LOGOUT_MSG_HEADER		= "APPLOGOUT";			//LOGOUT
	public final static String APP_HEART_MSG_HEADER			= "APPHEART";			//APP心跳包消息
	public final static String APP_ADD_PLUG_MSG_HEADER		= "APPADDLOCK";
	public final static String APP_DEL_PLUG_MSG_HEADER		= "APPDELLOCK";
	public final static String APP_MOD_PLUG_MSG_HEADER		= "APPMODLOCK";
	public final static String APP_QRY_PLUG_MSG_HEADER		= "APPQRYLOCK";
	
	public final static String APP_QRY_AUTHORIZEUSER_MSG_HEADER		= "APPQRYAUTHORIZEUSER";
	public final static String APP_ADD_AUTHORIZEUSER_MSG_HEADER		= "APPADDAUTHORIZEUSER";
	public final static String APP_DEL_AUTHORIZEUSER_MSG_HEADER		= "APPDELAUTHORIZEUSER";
	public final static String APP_QRY_PASSWORD_MSG_HEADER			= "APPQUERY_PASSWORD";
	public final static String APP_ADD_PASSWORD_MSG_HEADER			= "APPCREATE_PASSWORD";
	public final static String APP_DEL_PASSWORD_MSG_HEADER			= "APPDELETE_PASSWORD";
	
	/* APP智能门锁 */
	public final static String APP_BACK2AP_CTRL_MSG_HEADER	= "APPBACK2AP";			//设备初始化
	public final static String APP_LOCK_CTRL_MSG_HEADER		= "APPLOCK_OPEN";		//开锁关锁操作
	
	/**设备通知消息头*/
	public final static String APP_NOTIFY_ONLINE_MSG_HEADER	= "NOTIFYONLINE";	//上线
	public final static String NOTIFY_LOCK_STATUS			= "NOTIFY_STASTUS";		//推送门锁状态
	public final static String NOTIFY_BELL_MSG_HEADER		= "NOTIFY_BELL";		//推送门锁门铃
	public final static String NOTIFY_NOTIFY_ALARM			= "NOTIFY_ALARM";		//推送门锁告警
	
	/**设备消息头*/
	public final static String MODULE_ADJUST_TIME_MSG_HEADER	= "ADJTIME";		//设备时间校准消息
	public final static String MODULE_LOGIN_MSG_HEADER			= "LOGIN";			//设备LOGIN
	public final static String MODULE_HEART_MSG_HEADER			= "HEART";			//设备心跳包消息
	public final static String LOCK_CTRL_MSG_HEADER				= "LOCK_OPEN";		//门锁打开关闭操作
	public final static String BACK2AP_CTRL_MSG_HEADER			= "BACK2AP";		//设备初始化
	public final static String QRY_PASSWORD_MSG_HEADER			= "QUERY_PASSWORD";
	public final static String ADD_PASSWORD_MSG_HEADER			= "CREATE_PASSWORD";
	public final static String DEL_PASSWORD_MSG_HEADER			= "DELETE_PASSWORD";
	
	public final static String MOD_PLUG_MSG_HEADER		= "MODLOCK";
	
	/** 转发器功能  **/
	public final static String TRANSMIT_HEARBEAT_MSG_HEADER		= "TRANSMIT_HEARBEAT";
	public final static String TRANSMIT_TRANS_MSG_HEADER		= "TRANSMIT_TRANS";
	
	/**模块状态标识*/
	public final static int MODULE_OFF_LINE		= 0;	//模块离线状态
	public final static int MODULE_ON_LINE 		= 1;	//模块在线状态
	
	/**模块门锁状态*/
	public final static int MODULE_STATUS_LOCK_OFF	= 0;	//门锁关闭
	public final static int MODULE_STATUS_LOCK_ON	= 1;	//门锁打开
	
	/**命令分隔符*/
	public final static String CMD_SPLIT_STRING			= "[,#]";
	/**默认模块ID*/
	public final static String DEFAULT_MODULE_ID 	= "0";
	/**默认COOKIE*/
	public final static String DEFAULT_COOKIE		= "0";
}
