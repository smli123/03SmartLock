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

public class ModuleLockCtrlMsgHandle implements ICallFunction{
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
		int iStatus					= Integer.valueOf(strRet[4].trim());
	
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
			Vector<USER_MODULE> info = dbMgr.QueryUserModuleByDevId(strModuleId);
			if(null == info)
			{
				ResponseToAPP(strMsgHeader, strUserName, strModuleId, ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE);
				return ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE;
			}
			if(info.size() == 0)
			{
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
				/* 待模块返回 */
				
				String moduleCommand = String.format("%s,%s,%s,%s,%d#", strCookie, ServerCommDefine.LOCK_CTRL_MSG_HEADER, strUserName, strModuleId, iStatus);
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
		String strMsgHeader = strRet[1].trim();
		String strUserName 	= strRet[2].trim();
		String strModuleID	= strRet[3].trim();
		int iRetCode 	= Integer.valueOf(strRet[4].trim());
		String strLockStatus	= strRet[5].trim();

		/* 更新COOKIE */
		ServerWorkThread.RefreshModuleCookie(strModuleID, strNewCookie);
		/* 刷新心跳状态 */
		ServerWorkThread.RefreshModuleAliveFlag(strModuleID, true);
		ServerWorkThread.RefreshModuleIP(strModuleID, thread.getSrcIP(), thread.getSrcPort());

		if (iRetCode == 0) {
			//更新数据库
			ServerDBMgr dbMgr = new ServerDBMgr();
			
			try
			{
				dbMgr.UpdateModuleInfo_Status(strModuleID, Integer.valueOf(strLockStatus));
				
	//			//给APP回复成功： 只返回给发送者，不对。
	//			ResponseToAPP(ServerCommDefine.LOCK_CTRL_MSG_HEADER, strUserName, strModuleID, ServerRetCodeMgr.SUCCESS_CODE, strLockStatus);
				
				// 给所有APP用户返回信息
				Vector<USER_MODULE> info = dbMgr.QueryUserModuleByDevId(strModuleID);
				
				if (info != null) {
					for (int i = 0; i < info.size(); i++) {
						String username = info.get(i).getUserName();
						
						NotifyToAPP(username, strModuleID, ServerCommDefine.APP_LOCK_CTRL_MSG_HEADER, 
								ServerRetCodeMgr.SUCCESS_CODE, strLockStatus);
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
		return ServerRetCodeMgr.SUCCESS_CODE;
	}
}
