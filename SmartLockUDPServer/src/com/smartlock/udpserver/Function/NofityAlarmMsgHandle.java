package com.smartlock.udpserver.Function;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.MESSAGE_DEVICE;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_MODULE;

public class NofityAlarmMsgHandle implements ICallFunction{
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
		String strMsgHeader	= strRet[1].trim();
		String strUserName		= strRet[2].trim();
		String strDevId			= strRet[3].trim();
		int iMessageID			= Integer.valueOf(strRet[4].trim());
		String messageTime		= strRet[5].trim();
		int iType				= Integer.valueOf(strRet[6].trim());
		int iData				= Integer.valueOf(strRet[7].trim());
		int iUserType			= Integer.valueOf(strRet[8].trim());
		String strMemo			= strRet[9].trim();
		
		Timestamp dtMessageTime = transString(messageTime);
		
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
			Vector<USER_MODULE> info = dbMgr.QueryUserModuleByDevId(strDevId);
			if(null == info)
			{
				ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE);
				return ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE;
			}
			if(info.size() == 0)
			{
				ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE);
				return ServerRetCodeMgr.ERROR_CODE_USER_NOT_OWN_MODULE;
			}
			
			// 写库
			if (!dbMgr.InsertMessageDevice(new MESSAGE_DEVICE(iMessageID, strDevId, strUserName, iType, iData, iUserType, strMemo, dtMessageTime))) {
				LogWriter.WriteErrorLog(LogWriter.SELF, String.format("(%s)db operation failed. [MessageDevice]", strDevId));
				return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;
			}
			
			// 通过APP用户
			String strResult = strRet[4].trim() + "," + strRet[5].trim() + "," + strRet[6].trim() + "," + strRet[7].trim() + "," + strRet[8].trim() + "," + strRet[9].trim();
			for (int i = 0; i < info.size(); i++) {
				String username = info.get(i).getUserName();
				if (info.get(i).getCtrlMode() == USER_MODULE.PRIMARY) {	// 只推送管理用户
					NotifyToAPP(username,strDevId, ServerCommDefine.NOTIFY_NOTIFY_ALARM, 
							ServerRetCodeMgr.SUCCESS_CODE, strResult);
				}
			}
			
			//通知模块通知已收到
			String moduleCommand = String.format("%s,%s,%s,%s,%d#", strNewCookie, ServerCommDefine.NOTIFY_NOTIFY_ALARM, strUserName, strDevId, 0);
			ResponseToModule(strDevId, moduleCommand);
			
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
	
	private Timestamp transString(String message_time) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());  
        try {  
        	ts = Timestamp.valueOf(message_time);  
            System.out.println(ts);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return ts;
	}
}
