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

public class APPModuleAddMsgHandle implements ICallFunction{
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
		String strDevName 	= strRet[3].trim();
		String strDevId		= strRet[4].trim();
		String strMac		= strRet[5].trim();
		int iRelation		= Integer.valueOf(strRet[6].trim());	// 0：Master， 1： Slave

		ServerWorkThread app_thread = (ServerWorkThread)thread_base;
		
		/* 校验参数合法性 */
		int iRet = CheckAppCmdValid(strUserName, strCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			ResponseToAPPWithDefaultCookie(strMsgHeader, strUserName, strDevId, iRet);
			return iRet;
		}

		ServerDBMgr dbMgr = new ServerDBMgr();
		
		try
		{
			//开启事务机制
			dbMgr.BeginTansacion();

			if (iRelation == USER_MODULE.PRIMARY) { 	// 主用户，即管理用户
				Vector<USER_MODULE> user_infos = dbMgr.QueryUserModuleByDevId(strDevId);
				if(null != user_infos) {
					//删除用户与模块关联关系 user_module
					for (int i = 0; i < user_infos.size(); i++) {
						if(!dbMgr.DeleteUserModule(user_infos.get(i).getUserName(), strDevId))
						{
							dbMgr.Rollback();
							//关闭事务机制
							dbMgr.EndTansacion();
							ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION);
							LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s:%d)\t App(%s) [Database] AddModule:Failed to del relation of APP and module(%s). ", 
									app_thread.getSrcIP(),app_thread.getSrcPort(),strUserName,strDevId));
							return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;
						}
					}
				}
				
				// 根据Mac地址添加默认模块
				//如果不存在该模块,则增加；
				if(null == dbMgr.QueryModuleInfo(strDevId))
				{
					if(!dbMgr.InsertModuleInfo(new MODULE_INFO(strDevId,strDevName,strMac)))
					{
						dbMgr.Rollback();
						dbMgr.EndTansacion();
						ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION);
						return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;		
					}
				}
				
				// 增加USER_MODULE
				if(!dbMgr.InsertUserModule(new USER_MODULE(strUserName,strDevId,iRelation)))
				{
					dbMgr.Rollback();
					dbMgr.EndTansacion();
					ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION);
					return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;		
				}
				//提交事务
				dbMgr.Commit();
				dbMgr.EndTansacion();
				
				LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s)\t App(%s) Succeed to add smart lock(%s,%s). ", 
						app_thread.getSrcIP(),
						strUserName,strDevName,strDevId));
				
				//通知 APP，加模块成功
				ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.SUCCESS_CODE);
				
			} else if (iRelation == USER_MODULE.SLAVE) { 	// 从用户，即分享用户
				if(null == dbMgr.QueryModuleInfo(strDevId)) {
					ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_MODULE_NOT_EXIST);
					return ServerRetCodeMgr.ERROR_CODE_MODULE_NOT_EXIST;	
				} else {
					// 增加USER_MODULE
					if(!dbMgr.InsertUserModule(new USER_MODULE(strUserName,strDevId,iRelation)))
					{
						dbMgr.Rollback();
						dbMgr.EndTansacion();
						ResponseToAPP(strMsgHeader, strUserName, strDevId, ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION);
						return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;		
					}

					//提交事务
					dbMgr.Commit();
					dbMgr.EndTansacion();
				}
				
			} else {
				LogWriter.WriteTraceLog(LogWriter.SELF, String.format("Relation of APP and module is Error(%d). UserName:%s, ModuleID:%s", 
						iRelation,strUserName,strDevId));
				return ServerRetCodeMgr.ERROR_COMMON;
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

	@Override
	public int resp(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
}
