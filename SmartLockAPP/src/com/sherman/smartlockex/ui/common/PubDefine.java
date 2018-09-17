package com.sherman.smartlockex.ui.common;

import java.net.Socket;

import android.view.View;

public class PubDefine {
	public static final int TEN_MINUTES = 600000; 	// 10分钟
	public static final int MAX_ALERT_TIMES = 20; 	// 最大告警次数
	public static final int SOCKET_TIMEOUT = 5000; 	// Socket超时 5秒
	public static int CmdStatus = -2; 				// 0:send, 1:receive, -1:timeout,
													// -2:Unknown
	public static View CtrlView = null;
	
	// Server Config
	public static final int WAIT_SER_RESPONSE = 6000; 		// 服务器超时时间
	public static final int WAIT_WIFI_RESPONSE = 3000; 		// 设备WIFI超时时间
	public static final String COMPANY_NAME = "sherman";
	public final static String SERVERIP_HANGZHOU = "121.41.19.6"; // 杭州服务器IP地址
	public final static String SERVERIP_SHENZHEN = "112.126.65.122"; // 深圳服务器IP地址
	public final static String SERVERIP_DEBUG = "192.168.0.102"; // 本地调试服务器IP地址
	public static String SERVER_HOST_NAME = SERVERIP_HANGZHOU;
	
	public static enum SmartPlug_Connect_Mode {
		Internet, WiFi
	};
	
	public static SmartPlug_Connect_Mode g_Connect_Mode = SmartPlug_Connect_Mode.Internet;
	
	// Broadcast Info
	public static final String SOCKET_CONNECT_FAIL_BROADCAST = "com.thingzdo.socket.connect.fail";
	public static final String INTENT_SET_ENABLED = "com.thingzdo.ui.setfunctionenable";
	public static final String INTENT_SET_DISABLED = "com.thingzdo.ui.setfunctiondisable";
	public static final String LOGIN_BROADCAST = "com.thingzdo.login.broadcast";
	public static final String PLUG_POWER_ACTION = "com.thingzdo.ctrl.plugpower";
	public static final String PLUG_STEELYARD_GETWEIGHT_ACTION = "com.thingzdo.ctrl.getweight";
	public static final String PLUG_STEELYARD_GETMAOPI_ACTION = "com.thingzdo.ctrl.getmaopi";
	public static final String PLUG_BACK2AP_ACTION = "com.thingzdo.ctrl.plugback2ap";
	public static final String PLUG_LIGHT_ACTION = "com.thingzdo.ctrl.pluglight";
	public static final String PLUG_USB_ACTION = "com.thingzdo.ctrl.plugusb";
	public static final String PLUG_PARENTCTRL_ACTION = "com.thingzdo.ctrl.plugparent";
	public static final String REGISTER_BROADCAST = "com.thingzdo.register.broadcast";
	public static final String LOGOUT_BROADCAST = "com.thingzdo.logout.broadcast";
	public static final String FINDPWD_BROADCAST = "com.thingzdo.findpwd.broadcast";
	public static final String PLUG_UPDATE = "com.thingzdo.smartplug.update";
	public static final String PLUG_ADD_TASK = "com.thingzdo.smartplug.plugaddone";

	public static final String PLUG_CURTAIN_ACTION = "com.thingzdo.ctrl.plugcurtain";
	public static final String PLUG_WINDOW_ACTION = "com.thingzdo.ctrl.plugwindow";
	public static final String PLUG_ALED_ACTION = "com.thingzdo.ctrl.plugaled";
	public static final String PLUG_AIRCON_ACTION = "com.thingzdo.ctrl.plugaircon";

	public static final String PLUG_BATTERY_QUERYENERGE_ACTION = "com.thingzdo.ctrl.plugbatteryqueryenerge";
	public static final String PLUG_BATTERY_QUERYTRAIL_ACTION = "com.thingzdo.ctrl.plugbatteryquerytrail";
	public static final String PLUG_BATTERY_NOTIFYENERGE_ACTION = "com.thingzdo.ctrl.plugbatterynotifyenerge";

	public static final String PLUG_QRYGONGLV = "com.thingzdo.ctrl.plugquerygonglv";
	public static final String PLUG_ENABLEENERGE = "com.thingzdo.ctrl.plugenableenerge";
	public static final String PLUG_QRYENERGE = "com.thingzdo.ctrl.plugqueryenerge";

	public static final String PLUG_AIRCON_ADDIRSCENE_ACTION = "com.thingzdo.ctrl.plugairconaddirscene";
	public static final String PLUG_AIRCON_MODIFYIRSCENE_ACTION = "com.thingzdo.ctrl.plugairconmodifyirscene";
	public static final String PLUG_AIRCON_DELIRSCENE_ACTION = "com.thingzdo.ctrl.plugaircondelirscene";
	public static final String PLUG_AIRCON_ENABLEIRSCENE_ACTION = "com.thingzdo.ctrl.plugairconenalbeirscene";
	public static final String PLUG_AIRCON_QUERYIRSCENE_ACTION = "com.thingzdo.ctrl.plugairconqueryirscene";

	public static final String PLUG_ADDSCENE_ACTION = "com.thingzdo.ctrl.plugairconaddscene";
	public static final String PLUG_DELSCENE_ACTION = "com.thingzdo.ctrl.plugaircondelscene";
	public static final String PLUG_QUERYSCENE_ACTION = "com.thingzdo.ctrl.plugairconqueryscene";
	public static final String PLUG_APPLYSCENE_ACTION = "com.thingzdo.ctrl.plugairconapplyscene";

	// 空调红外遥控
	public static final String PLUG_AIRCON_IRDATA_ACTION = "com.thingzdo.ctrl.irdata";
	public static final String PLUG_AIRCON_SERVER_ACTION = "com.thingzdo.ctrl.airconserver";
	// 电视红外遥控
	public static final String PLUG_TV_IRDATA_ACTION = "com.thingzdo.ctrl.tv.irdata";
	public static final String PLUG_TV_SERVER_ACTION = "com.thingzdo.ctrl.tvserver";

	public static final String PLUG_ADD_TIMERTASK = "com.thingzdo.smartplug.addtimertask";
	public static final String PLUG_MOD_TIMERTASK = "com.thingzdo.smartplug.modifytimertask";
	public static final String PLUG_DEL_TIMERTASK = "com.thingzdo.smartplug.deltimertask";

	public static final String SETTINGS_TIMELESS_BROADCAST = "com.thingzdo.settings.timeless";
	public static final String SETTINGS_GPRS_BROADCAST = "com.thingzdo.settings.gprs";
	public static final String SETTINGS_MAINTAIN_BROADCAST = "com.thingzdo.settings.maintain";

	public static final String PLUG_MODIFY_PLUGNAME = "com.thingzdo.smartplug.modifyname";
	public static final String PLUG_DELETE = "com.thingzdo.smartplug.delete";
	public static final String PLUG_SETTIMER_ENABLED = "com.thingzdo.smartplug.settimerenabled";

	public static final String USER_MODIFY_PASSWORD = "com.thingzdo.smartplug.modifypassword";
	public static final String USER_MODIFY_EMAIL = "com.thingzdo.smartplug.modifyemail";

	public static final String PLUG_NOTIFY_POWER = "com.thingzdo.smartplug.notifypower";
	public static final String PLUG_NOTIFY_LIGHT = "com.thingzdo.smartplug.notifylight";
	public static final String PLUG_NOTIFY_ONLINE = "com.thingzdo.smartplug.notifyonline";
	public static final String PLUG_NOTIFYTIMER = "com.thingzdo.smartplug.notifytimer";
	public static final String PLUG_RGB_ACTION = "com.thingzdo.ctrl.rgb";

	public static final String PLUG_SHAKE_FAIL_ACTION = "com.thingzdo.shakeshake.fail";

	public static final String PLUG_NOTIFY_CURTAIN = "com.thingzdo.smartplug.notifycurtain";
	public static final String PLUG_NOTIFY_KETTLE = "com.thingzdo.smartplug.notifykettle";
	public static final String PLUG_NOTIFY_UPGRADEAP = "com.thingzdo.smartplug.notifyupgradeap";

	public static final String CONFIG_MODIFY_SPEEK_TIMER = "com.thingzdo.smartplug.config.speek";

	// 设备类型
	public static final String DEVICE_UNKNOWN = "UNKNOWN"; // 0_
	public static final String DEVICE_SMART_LOCK = "SmartLock"; // 1_ 智能门锁
	public static final String DEVICE_SMART_LOCK_II = "SmartLockII"; // 2_ 智能门锁II

	public static boolean DEBUG_VERSION = false;

	public static Socket global_tcp_socket = null;
	public static String global_local_ip = "";

	public static int SERVER_PORT = 6000;
	public static int LOCAL_PORT = 5002;
	public static int MODULE_PORT = 5003;
	
	public static boolean g_First_Login = false;

}
