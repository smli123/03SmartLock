package com.smartlock.udpserver.Function;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_MODULE;

public class NofityBellMsgHandle implements ICallFunction{
	/**********************************************************************************************************
	 * @name ModuleBellOnMsgHandle 通用命令发送处理句柄
	 * @param 	strMsg: 命令字符串 
	 * 					格式：<cookie>,BELLON,<username>,<module_id>
	 * @RET 		不需要模块响应
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @date 2015/04/07
	 * **********************************************************************************************************/
	public int call(Runnable thread_base, String strMsg) 
	{
		String strRet[] 	= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie	= strRet[0].trim();
		String strMsgHeader	= strRet[1].trim();
		String strUserName 	= strRet[2].trim();
		String strModuleId	= strRet[3].trim();
		
		/* 获取用户线程 */
		ServerWorkThread thread = (ServerWorkThread)thread_base;
				
		/* 校验参数合法性 */
		int iRet = CheckAppCmdValid(strUserName, strCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			ResponseToAPP(strMsgHeader, strUserName, strModuleId, iRet);
			return iRet;
		}
		
		/* 透传给模块 */
		try {
			NotifyToModule(strMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e);
			
			LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s:%d)\t App(%s) Fail to BellOn of module(%s). ", 
					thread.getSrcIP(),thread.getSrcPort(),strUserName,strModuleId));
			
			return ServerRetCodeMgr.ERROR_COMMON;
		}

		LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s:%d)\t App(%s) Succeed to BellOn of module(%s). ", 
				thread.getSrcIP(),thread.getSrcPort(),strUserName,strModuleId));
		
		return ServerRetCodeMgr.SUCCESS_CODE;
	}

	@Override
	public int resp(Runnable thread_base, String strMsg) {
		String strRet[] 		= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strNewCookie		= strRet[0].trim();
		String strUserName		= strRet[2].trim();
		String strDevId			= strRet[3].trim();
		String strRetCode			= strRet[4].trim();
		
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		
		/* 校验参数合法性 */
		int iRet = CheckModuleCmdValid(strDevId, strNewCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			//ResponseToModule(strNewCookie, strMsgHeader, strUserName, strDevId, iRet);
			//return iRet;
		}
		
		/* 更新COOKIE */
		ServerWorkThread.RefreshAppCookie(strUserName, strNewCookie);
		/* 刷新心跳状态 */
		ServerWorkThread.RefreshModuleAliveFlag(strDevId, true);
		ServerWorkThread.RefreshModuleIP(strDevId, thread.getSrcIP(), thread.getSrcPort());
		
		ServerDBMgr dbMgr = new ServerDBMgr();
		
		try
		{
			USER_MODULE info = dbMgr.QueryUserModuleByDevId(strDevId);
			
			NotifyToAPP(info.getUserName(),strDevId, ServerCommDefine.NOTIFY_BELL_MSG_HEADER, 
					ServerRetCodeMgr.SUCCESS_CODE,  strRetCode);
			
			//通知模块通知已收到
			ResponseToModule(strDevId, String.format("%s#", ServerCommDefine.NOTIFY_BELL_MSG_HEADER));
			return ServerRetCodeMgr.SUCCESS_CODE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServerRetCodeMgr.ERROR_COMMON;
		}
		finally
		{
			dbMgr.Destroy();
		}
	}	
}
