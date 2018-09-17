package com.smartlock.udpserver.Function;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ModuleUpgradeOnLineMgr;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;

public class UpgradeReEndRspHandle implements ICallFunction{
	
	@Override
	public int call(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**********************************************************************************************************
	 * @name UpgradeModuleReEndRspHandle 模块通知服务器其已收到END消息
	 * @param 模块回复：cookiID,UPGRADEREEND,usrname,moduleID,0#
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @date 2015/04/10
	 * **********************************************************************************************************/
	public int resp(Runnable thread_base, String strMsg) 
	{
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		String strRet[] 	= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strNewCookie	= strRet[0].trim();
		String strUserName	= strRet[2].trim();
		String strDevId		= strRet[3].trim();
		
		/* 通知模块，服务器收到END响应 */
		String strResp = strMsg.replace(ServerCommDefine.MODULE_UPGRADE_REEND_MSG_HEADER, ServerCommDefine.MODULE_UPGRADE_END_MSG_HEADER);
		ResponseToModule(strDevId, strResp);
		
		LogWriter.WriteErrorLog(LogWriter.SELF, String.format("(%s) resend END command: [%s]", strDevId, strResp));
		
		return ServerRetCodeMgr.SUCCESS_CODE;
	}

}
