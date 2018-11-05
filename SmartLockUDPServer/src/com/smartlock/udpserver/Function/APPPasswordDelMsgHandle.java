package com.smartlock.udpserver.Function;

import java.util.Vector;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.MODULE_INFO;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_MODULE;

public class APPPasswordDelMsgHandle implements ICallFunction{
	/**********************************************************************************************************
	 * @name ParentCtrlRespHandle  status通用回应函数；所有对STATUS的变更，均由该函数响应 
	 * 			包含：1、继电器通/断开控制   2、USB控制  3、PARENT CTRL 4、
	 * @param 	strMsg: 命令字符串 
	 * 					格式：<cookie>,PARENTCTRL,<username>,<module_id>,<ctrl>
	 * @RET 		<new_cookie>,PARENTCTRL, <username>,<module_id>,<code>,<status>，<原命令字符串>
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
		
		ServerDBMgr dbMgr = new ServerDBMgr();
		
		try
		{
			/* 透传给模块 */
			try {
				String strNEResult = "";
				for (int i = 4; i < strRet.length; i++) {
					strNEResult += strRet[i];
					if (i < strRet.length - 1) {
						strNEResult += ",";
					}
				}
				/* 待模块返回 */
				String moduleCommand = String.format("%s,%s,%s,%s,%s#", strCookie, ServerCommDefine.DEL_PASSWORD_MSG_HEADER, strUserName, strModuleId, strNEResult);
				ResponseToModule(strModuleId, moduleCommand);
				
				return ServerRetCodeMgr.SUCCESS_CODE;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogWriter.WriteExceptionLog(LogWriter.SELF, e);
			}

			return ServerRetCodeMgr.ERROR_COMMON;	
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
	/**********************************************************************************************************
	 * @name ParentCtrlRespHandle  status通用回应函数；所有对STATUS的变更，均由该函数响应 
	 * 			包含：1、继电器通/断开控制   2、USB控制  3、PARENT CTRL 4、
	 * @param 	strMsg: 命令字符串 
	 * 					格式：<cookie>,PARENTCTRL,<username>,<module_id>,<ctrl>
	 * @RET 		<new_cookie>,PARENTCTRL, <username>,<module_id>,<code>,<status>，<原命令字符串>
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @date 2015/04/07
	 * **********************************************************************************************************/
	@Override
	public int resp(Runnable thread_base, String strMsg) {
		
ServerWorkThread thread = (ServerWorkThread)thread_base;
		
		// TODO Auto-generated method stub
		String strRet[] 	= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strNewCookie	= strRet[0].trim();
		String strMsgHeader = ServerCommDefine.APP_DEL_PASSWORD_MSG_HEADER;
		String strUserName 	= strRet[2].trim();
		String strModuleID	= strRet[3].trim();
		int iRetCode = Integer.valueOf(strRet[4].trim());
		
		String strNEResult = "";
		for (int i = 5; i < strRet.length; i++) {
			strNEResult += strRet[i];
			if (i < strRet.length - 1) {
				strNEResult += ",";
			}
		}
		
		/* 更新COOKIE */
		ServerWorkThread.RefreshModuleCookie(strModuleID, strNewCookie);
		/* 刷新心跳状态 */
		ServerWorkThread.RefreshModuleAliveFlag(strModuleID, true);
		ServerWorkThread.RefreshModuleIP(strModuleID, thread.getSrcIP(), thread.getSrcPort());
		
		//更新数据库
		ServerDBMgr dbMgr = new ServerDBMgr();
		
		try
		{
			//获取模块返回的返回码
			if(0 != iRetCode)
			{
				ResponseToAPP(strMsgHeader, strUserName, strModuleID, ServerRetCodeMgr.ERROR_CODE_MODULE_RET_ERROR);
				return ServerRetCodeMgr.ERROR_CODE_MODULE_RET_ERROR;
			}
			
			//给APP回复成功
			ResponseToAPP(strMsgHeader, strUserName, strModuleID, ServerRetCodeMgr.SUCCESS_CODE, strNEResult);
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
