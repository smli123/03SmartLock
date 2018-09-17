package com.smartlock.udpserver.Function;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;

public class ModuleSetTcpUdpMsgHandle implements ICallFunction{
	/**********************************************************************************************************
	 * @name SetTcpUdpHandle 设置TCP UDP协议类型
	 * @param 	strMsg: 命令字符串 
	 * 					格式：COOKIE,TCPUDP,username,moduleID,mode#
	 * @RET 		<new_cookie>,TCPUDP, <username>,<module_id>,<code>
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @date 2015/04/07
	 * **********************************************************************************************************/
	public int call(Runnable thread_base, String strMsg) 
	{
		String strRet[] 			= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie			= strRet[0].trim();
		String strMsgHeader			= strRet[1].trim();
		String strUserName 			= strRet[2].trim();
		String strModuleId			= strRet[3].trim();
	
		/* 校验参数合法性 */
		int iRet = CheckAppCmdValid(strUserName, strCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			ResponseToAPP(strMsgHeader, strUserName, strModuleId, iRet);
			return iRet;
		}
		
		/* 透传给模块 */
		try {
			/* 待模块返回 */
			iRet = NotifyToModule(strMsg);
			if (ServerRetCodeMgr.SUCCESS_CODE != iRet)
			{
				return iRet;
			}
			
			return ServerRetCodeMgr.SUCCESS_CODE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e);
		}

		return ServerRetCodeMgr.ERROR_COMMON;	
	}

	/**********************************************************************************************************
	 * @name SetTcpUdpHandle 设置TCP UDP协议类型
	 * @param 	strMsg: 命令字符串 
	 * 					格式：COOKIE,TCPUDP,username,moduleID,mode#
	 * @RET 		<new_cookie>,TCPUDP, <username>,<module_id>,<code>
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @date 2015/04/07
	 * **********************************************************************************************************/
	@Override
	public int resp(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		String strRet[] 	= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strNewCookie	= strRet[0].trim();
		int iRetCode = Integer.valueOf(strRet[4].trim());
		
		String strMsgHeader = strRet[6].trim();
		String strUserName 	= strRet[7].trim();
		String strModuleId	= strRet[8].trim();
		
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		
		/* 更新COOKIE */
		ServerWorkThread.RefreshModuleCookie(strModuleId, strNewCookie);
		/* 刷新心跳状态 */
		ServerWorkThread.RefreshModuleAliveFlag(strModuleId, true);
		
		ServerWorkThread.RefreshModuleIP(strModuleId, thread.getSrcIP(), thread.getSrcPort());
		
		//获取模块返回的返回码
		if(0 != iRetCode)
		{
			ResponseToAPP(strMsgHeader, strUserName, strModuleId, ServerRetCodeMgr.ERROR_CODE_MODULE_RET_ERROR);
			return ServerRetCodeMgr.ERROR_CODE_MODULE_RET_ERROR;
		}	
		
		//给APP回复成功
		ResponseToAPP(strMsgHeader, strUserName, strModuleId, ServerRetCodeMgr.SUCCESS_CODE);
		return ServerRetCodeMgr.SUCCESS_CODE;
	}
}
