package com.sherman.smartlockex.ui.common;

public class PubStatus {
	public enum LoginState{
		LOGIN_ONLINE,
		LOGIN_OFFLINE
	};
	
	private static LoginState mLoginState;
	
	public static String g_CurUserName;
	public static String g_userEmail;
	public static String g_userPwd;
	public static String g_userCookie;
	public static String g_moduleId;
	public static String g_moduleType;
	
	public static int g_heartSendCount = 0;
	public static int g_heartRecvCount = 0;
	
	public static void setLoginState(LoginState state) {
		mLoginState = state;	
	}
	
	public static LoginState getLoginState() {
	    return mLoginState;	
	}
	
	public static String getUserName() {
		if (null == g_CurUserName || g_CurUserName.isEmpty()) {
			return "Sherman";
		}

		return g_CurUserName;
	}
}
