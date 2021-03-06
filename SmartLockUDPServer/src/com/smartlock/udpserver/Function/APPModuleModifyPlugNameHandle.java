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

public class APPModuleModifyPlugNameHandle implements ICallFunction{
	

	/**********************************************************************************************************
	 * @name ModifyPlugName  status通用回应函数；所有对STATUS的变更，均由该函数响应 
	 * 			包含：1、继电器通/断开控制   2、USB控制  3、PARENT CTRL 4、
	 * @param 	strMsg: 命令字符串 
	 * 					格式：<cookie>,APPMODPLUG,<username>,<module_id>,<plugname>
	 * @RET 		<new_cookie>,APPMODPLUG, <username>,<module_id>,<code>,<plugname>，<原命令字符串>
	 * @return  boolean 是否成功
	 * @author lishimin
	 * @date 2016/06/23
	 * **********************************************************************************************************/
	public int call(Runnable thread_base, String strMsg) 
	{
		String strRet[] 			= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie			= strRet[0].trim();
		String strMsgHeader			= strRet[1].trim();
		String strUserName 			= strRet[2].trim();
		String strModuleId			= strRet[3].trim();
		String strNewModuleName		= strRet[4].trim();
	
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
			Vector<USER_MODULE> infos = dbMgr.QueryUserModuleByDevId(strModuleId);
			if(null == infos) {
				ResponseToAPP(strMsgHeader, strUserName, strModuleId, ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE);
				return ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE;
			}
			if (infos.size() == 0) {
				ResponseToAPP(strMsgHeader, strUserName, strModuleId, ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE);
				return ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE;
			}
			
			//被修改模块必须是已填加的模块
			MODULE_INFO module_info = dbMgr.QueryModuleInfo(strModuleId);
			if(null == module_info)
			{
				ResponseToAPP(strMsgHeader, strUserName, ServerRetCodeMgr.ERROR_CODE_MODULE_ID_UNREGISTERED);
				return ServerRetCodeMgr.ERROR_CODE_MODULE_ID_UNREGISTERED;			
			}
		
			/* 透传给模块 */
			try {	
//				/* 待模块返回 */
//				iRet = NotifyToModule(strMsg);
//				if (ServerRetCodeMgr.SUCCESS_CODE != iRet)
//				{
//					return iRet;
//				}
				
				// lishimin 
				dbMgr.UpdateModuleInfo_ModuleName(strModuleId, strNewModuleName);
				//返回信息给APP
				//必须把strMsg中的"#"去掉；
				strMsg = strMsg.substring(0, strMsg.indexOf("#"));
				
				for (int i = 0; i < infos.size(); i++) {
					String username = infos.get(i).getUserName();
					ResponseToAPP(strMsgHeader, username, ServerRetCodeMgr.SUCCESS_CODE, strMsg);
				}

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
	 * @author lishimin
	 * @date 2016/06/23
	 * **********************************************************************************************************/
	@Override
	public int resp(Runnable thread_base, String strMsg) {
		
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		
		// TODO Auto-generated method stub
		String strRet[] 	= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strNewCookie	= strRet[0].trim();
		int iRetCode = Integer.valueOf(strRet[4].trim());
		
		String strMsgHeader = strRet[1].trim();
		String strUserName 	= strRet[2].trim();
		String strModuleID	= strRet[3].trim();
		String strStatus = strRet[5].trim();		
		
		/* 更新COOKIE */
		ServerWorkThread.RefreshModuleCookie(strModuleID, strNewCookie);
		/* 刷新心跳状态 */
		ServerWorkThread.RefreshModuleAliveFlag(strModuleID, true);
		ServerWorkThread.RefreshModuleIP(strModuleID, thread.getSrcIP(), thread.getSrcPort());
		
		//更新数据库
		ServerDBMgr dbMgr = new ServerDBMgr();
		
		try
		{
			boolean bRet = dbMgr.UpdateModuleInfo_ModuleName(strModuleID, strStatus);
			if(!bRet)
			{
				ResponseToAPP(strMsgHeader, strUserName, ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION);
				return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;
			}
			
			//获取模块返回的返回码
			if(0 != iRetCode)
			{
				ResponseToAPP(strMsgHeader, strUserName, strModuleID, ServerRetCodeMgr.ERROR_CODE_MODULE_RET_ERROR);
				return ServerRetCodeMgr.ERROR_CODE_MODULE_RET_ERROR;
			}
			
			//给APP回复成功
			Vector<USER_MODULE> infos = dbMgr.QueryUserModuleByDevId(strModuleID);
			if(null != infos) {
				for (int i = 0; i < infos.size(); i++) {
					String username = infos.get(i).getUserName();
					ResponseToAPP(strMsgHeader, username, strModuleID, ServerRetCodeMgr.SUCCESS_CODE, strStatus);
				}
			}
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
