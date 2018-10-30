package com.sherman.smartlockex.internet;

import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.StringUtils;

import android.os.Handler;

/*
 * 发命令的代理，根据连接类型选择是UDP、WIFI直连、互联网发送命令
 */
public class SendMsgProxy {
	public static void sendCtrlMsg(boolean containCookie, String msg, Handler timeoutHandler) {
    	String message = msg;
    	if (PubDefine.g_Connect_Mode != PubDefine.SmartLock_Connect_Mode.Internet) {
    		message = message.substring(3,  message.length());
    	}
    	if (null == PubStatus.g_userCookie || PubStatus.g_userCookie.isEmpty()) {
    		PubStatus.g_userCookie = PubFunc.getTimeString();
    	}
    	if (true == containCookie) {
    		message = PubStatus.g_userCookie + "," + msg;
    	} else {
    		message = "0," + msg;
    	}
   		message = message + StringUtils.PACKAGE_END_SYMBOL;
		
		if (PubDefine.g_Connect_Mode == PubDefine.SmartLock_Connect_Mode.Internet) {
			UDPClient.getInstance().send(message, timeoutHandler);
		} else if (PubDefine.g_Connect_Mode == PubDefine.SmartLock_Connect_Mode.WiFi) {
			new SendCmdToSocketRunnable(timeoutHandler, message).run();
		}
	}
	

	public static void sendCtrlMsgBin(boolean containCookie, byte[] msgBin, Handler timeoutHandler) {
    	byte[] messageBin = null;
//    	if (PubDefine.g_Connect_Mode != PubDefine.SmartPlug_Connect_Mode.Internet) {
//    		messageBin = new byte[msgBin.length - 2];
//    		for (int i = 0; i < msgBin.length - 2; i++) {
//    			messageBin[i] = msgBin[i + 2];
//    		}
//    	} else {
//    		messageBin = new byte[msgBin.length];
//    		for (int i = 0; i < msgBin.length; i++) {
//    			messageBin[i] = msgBin[i];
//    		}
//    	}
    	
    	if (null == PubStatus.g_userCookie || PubStatus.g_userCookie.isEmpty()) {
    		PubStatus.g_userCookie = PubFunc.getTimeString();
    	}
    	
    	int i = 0;
    	if (true == containCookie) {
    		messageBin = new byte[PubStatus.g_userCookie.getBytes().length + 1 + msgBin.length];
    		for (i = 0; i < PubStatus.g_userCookie.getBytes().length; i++) {
    			messageBin[i] = (PubStatus.g_userCookie.getBytes())[i];
    		}
    		messageBin[i++] = ','; 

    		for (int j = 0; j < msgBin.length; j++) {
    			messageBin[i++] = msgBin[j];
    		}
    	} else {
    		messageBin = new byte[msgBin.length];
    		for (i = 0; i < msgBin.length; i++) {
    			messageBin[i] = msgBin[i];
    		}
    	}
		
		if (PubDefine.g_Connect_Mode == PubDefine.SmartLock_Connect_Mode.Internet) {
			UDPClient.getInstance().sendBin(messageBin, timeoutHandler);
		} else if (PubDefine.g_Connect_Mode == PubDefine.SmartLock_Connect_Mode.WiFi) {
			new SendCmdToSocketRunnable(timeoutHandler, messageBin).run();
		}
	}

	
}
