package com.smartlock.udpserver.Function;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_MODULE;

public class NotifyLockStatusHandle implements ICallFunction{
	
	@Override
	public int call(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**********************************************************************************************************
	 * @name NotifyPowerStatusHandle 处理模块主动上报继电器状的处理流程
	 * @param 	strMsg: 响应字符串 
	 * 					格式：  cookie, NOTIFYPWRSTA,<username>,< moduleid >，<returncode>, <status>
	 * 								< moduleid >：模块ID
									<status>：0BIT:继电器，1BIT:小夜灯
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @date 2015/04/10
	 * **********************************************************************************************************/
	public int resp(Runnable thread_base, String strMsg)
	{
		String strRet[] 		= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strNewCookie		= strRet[0].trim();
		String strUserName		= strRet[2].trim();
		String strDevId			= strRet[3].trim();
		int iStatus				= Integer.valueOf(strRet[4].trim());
		int iCharge				= Integer.valueOf(strRet[5].trim());
		int iUserType			= Integer.valueOf(strRet[6].trim());
		String strUserCode		= strRet[7].trim();
		
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
			//更新数据库
			boolean bRet = dbMgr.UpdateModuleInfo_Status_Charge(strDevId, iStatus, iCharge);
			if(!bRet)
			{
				LogWriter.WriteErrorLog(LogWriter.SELF, String.format("(%s)db operation failed. [ModuleInfo]", strDevId));
				return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;
			}
			
			Vector<USER_MODULE> info = dbMgr.QueryUserModuleByDevId(strDevId);
			
			if (info != null) {
				String strResult = strRet[4].trim() + "," + strRet[5].trim() + "," + strRet[6].trim() + "," + strRet[7].trim();
				for (int i = 0; i < info.size(); i++) {
					String username = info.get(i).getUserName();
					NotifyToAPP(username,strDevId, ServerCommDefine.NOTIFY_LOCK_STATUS, 
							ServerRetCodeMgr.SUCCESS_CODE, strResult);
				}
			}
			
			//通知模块通知已收到
			String moduleCommand = String.format("%s,%s,%s,%s,%d#", strNewCookie, ServerCommDefine.NOTIFY_LOCK_STATUS, strUserName, strDevId, 0);
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
}
