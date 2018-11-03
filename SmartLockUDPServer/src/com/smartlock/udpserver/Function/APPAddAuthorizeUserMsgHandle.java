package com.smartlock.udpserver.Function;

import java.util.Vector;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.MODULE_INFO;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_INFO;
import com.smartlock.udpserver.db.USER_MODULE;

public class APPAddAuthorizeUserMsgHandle implements ICallFunction{
	/**********************************************************************************************************
	 * @name AddModuleHandle 填加新模块
	 * @param 	strMsg: 命令字符串 格式：<cookie>,ADDPLUG,< username>,<devname>,<module_id>,<module_mac>
	 *                                                 返回：<new_cookie>,ADDPLUG, <username>,<module_id>,<code>
	 * @return  boolean 是否填加成功
	 * @author zxluan
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public int call(Runnable thread_base, String strMsg)
	{
		String strRet[] = strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie	= strRet[0].trim();
		String strMsgHeader = strRet[1].trim();
		String strUserName 	= strRet[2].trim();
		String strDevId		= strRet[3].trim();
		String strAuthorizeUser 	= strRet[4].trim();
		int iRelation		= Integer.valueOf(strRet[5].trim());	// 0：Master， 1： Slave 预留接口，暂时不用
		
		iRelation = USER_MODULE.SLAVE;
		
		ServerWorkThread app_thread = (ServerWorkThread)thread_base;
		
		/* 校验参数合法性 */
		int iRet = CheckAppCmdValid(strUserName, strCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			ResponseToAPPWithDefaultCookie(strMsgHeader, strUserName, strDevId, iRet);
			return iRet;
		}

		ServerDBMgr dbMgr = new ServerDBMgr();
		
		USER_INFO user = dbMgr.QueryUserInfoByUserName(strAuthorizeUser);
		if (user == null) {
			ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_USER_NAME_UNREGISTERED);
			return iRet;
		}
		
		try
		{
			//开启事务机制
			dbMgr.BeginTansacion();
			
			// 增加USER_MODULE
			if(!dbMgr.InsertUserModule(new USER_MODULE(strAuthorizeUser,strDevId,iRelation)))
			{
				dbMgr.Rollback();
				dbMgr.EndTansacion();
				ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION);
				return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;		
			}
			//提交事务
			dbMgr.Commit();
			dbMgr.EndTansacion();
			
			LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s)\t App(%s) Succeed to add Share User(%s,%s). ", 
					app_thread.getSrcIP(),
					strUserName,strDevId,strAuthorizeUser));
			
			//通知 APP，加模块成功
			ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.SUCCESS_CODE);
			
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

	@Override
	public int resp(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
}
