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
	public static final String COMPANY_NAME = "Sherman";
	public static final String DEVICE_SSID_PRE = "Thingzdo";
	public final static String SERVERIP_HANGZHOU = "121.41.19.6"; // 杭州服务器IP地址
	public final static String SERVERIP_DEBUG = "192.168.3.9"; // 本地调试服务器IP地址
	public static String SERVER_HOST_NAME = SERVERIP_DEBUG;
	
	public static enum SmartPlug_Connect_Mode {
		Internet, WiFi
	};
	
	public static SmartPlug_Connect_Mode g_Connect_Mode = SmartPlug_Connect_Mode.Internet;
	
	// Broadcast Info
	public static final String SOCKET_CONNECT_FAIL_BROADCAST = "com.sherman.socket.connect.fail";
	public static final String LOGIN_BROADCAST = "com.sherman.login.broadcast";
	
	public static final String REGISTER_BROADCAST = "com.sherman.register.broadcast";
	public static final String LOGOUT_BROADCAST = "com.sherman.logout.broadcast";
	public static final String FINDPWD_BROADCAST = "com.sherman.findpwd.broadcast";

	public static final String LOCK_QRYLOCK_BROADCAST = "com.sherman.smartlock.update";
	public static final String LOCK_ADDLOCK_BROADCAST = "com.sherman.smartlock.plugaddone";
	public static final String LOCK_DELETELOCK_BROADCAST = "com.sherman.smartlock.delete";
	public static final String LOCK_MODIFY_LOCKNAME_BROADCAST = "com.sherman.smartlock.modifyname";

	public static final String USER_MODIFY_PASSWORD_BROADCAST = "com.sherman.smartlock.modifypassword";
	public static final String USER_MODIFY_EMAIL_BROADCAST = "com.sherman.smartlock.modifyemail";

	public static final String LOCK_BACK2AP_BROADCAST = "com.sherman.smartlock.ctrl.plugback2ap";
	public static final String LOCK_OPENLOCK_BROADCAST = "com.sherman.smartlock.ctrl.openlock";
	
	// SmartLock
	public static final String LOCK_NOTIFY_ONLINE_BROADCAST = "com.sherman.smartlock.notifyonline";
	public static final String LOCK_NOTIFY_STATUS_BROADCAST = "com.sherman.smartlockex.lock.status";
	public static final String LOCK_NOTIFY_BELL_BROADCAST = "com.sherman.smartlockex.lock.bell";
	public static final String LOCK_NOTIFY_ALARM_BROADCAST = "com.sherman.smartlockex.lock.alarm";

	// 设备类型
	public static final String DEVICE_UNKNOWN 			= "UNKNOWN"; 		// 0_
	public static final String DEVICE_SMART_LOCK 		= "SmartLock"; 		// 1_ 智能门锁
	public static final String DEVICE_SMART_LOCK_II 	= "SmartLockII"; 	// 2_ 智能门锁II

	public static boolean DEBUG_VERSION = false;

	public static Socket global_tcp_socket = null;
	public static String global_local_ip = "";

	public static int SERVER_PORT = 6000;
	public static int LOCAL_PORT = 5002;
	public static int MODULE_PORT = 5003;
	
	public static boolean g_First_Login = false;

}
