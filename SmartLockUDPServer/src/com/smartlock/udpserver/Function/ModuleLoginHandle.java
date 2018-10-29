package com.smartlock.udpserver.Function;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ConnectInfo;
import com.smartlock.udpserver.PubDefine;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.MODULE_INFO;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_MODULE;


public class ModuleLoginHandle implements ICallFunction {
	/**********************************************************************************************************
	 * @name LoginHandle 处理模块登录
	 * @param 	strMsg: 命令字符串 
	 * 					格式：cookie, LOGIN,<username>,<moduleid>,<modluename>,<mac>,<version>,<moduletype>,<protocol type>,
	 *                                    <pwr_status>,<flash_mode>,<red>,<green>,<blue>,
	 *                                    <Timer_cnt>,< timer_id 1>,< timer_type1 >,< period 1>,< on time1 >,< offtime1 >,<enable>,<…>#
	 *                  20150101000101,LOGIN,20160723001310#                  
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @date 2015/04/13
	 * **********************************************************************************************************/
	public int resp(Runnable thread_base, String strMsg)
	{
		String strRet[] 		= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie		= strRet[0].trim();
		String strMsgHeader 	= strRet[1].trim();
		String strAppUserName 	= strRet[2].trim();
		String strDevId			= strRet[3].trim();
		String strModuleName	= strRet[4].trim();
		String strMac			= strRet[5].trim();
		String strModuleVer		= strRet[6].trim();
		String strModuleType	= strRet[7].trim();
		int iStatus    			= Integer.valueOf(strRet[8].trim());
		int iCharge    			= Integer.valueOf(strRet[9].trim());
		String strReserve1 		= strRet[11].trim();
		String strReserve2 		= strRet[12].trim();
		String strReserve3 		= strRet[13].trim();
		
		/* 获取DB管理器 */
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		ServerDBMgr dbMgr = new ServerDBMgr();
		
		try
		{
			/* 模块已登录*/
			if(ServerWorkThread.IsModuleLogin(strDevId))
			{
				/* 模块已登录的情况下，重复登录，取消原来的心跳定时器处理；其 它流程按新登录处理 */
				thread.StopHeartTimer(strDevId);
			} 
		
			dbMgr.BeginTansacion();

			boolean bRet = false;
//			// 处理ModuleID变化，但是Mac地址不变的情况
//			Vector<MODULE_INFO> deleteinfo = dbMgr.QueryModuleInfoFromMac(strMac); 
//			int i = 0;
//			int total = deleteinfo.size();
//			for (i = 0; i < total; i++) {
//				LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t [%d/%d]Find it Mac:[%s] %s - %s", 
//						i, total, strMac, strDevId, deleteinfo.get(i).getModuleId()));
//				
//				USER_MODULE user = dbMgr.QueryUserModuleByDevId(deleteinfo.get(i).getModuleId());
//				if (user != null) {
//					if (user.getUserName().equals(strAppUserName) == true) {
//					
//						if (deleteinfo.get(i).getModuleId().equals(strDevId) == false) {
//							LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t ModuleID Diff:%s - %s", 
//									strDevId, deleteinfo.get(i).getModuleId()));
//							
//							
//							// 删除 此模块的module_info, timer_info, module_user信息；
//							bRet = dbMgr.DeleteUserModule(strAppUserName, deleteinfo.get(i).getModuleId());
//							if (bRet == false) {
//								LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Failed to Delete UserModule:%s %s", 
//										strAppUserName, deleteinfo.get(i).getModuleId()));
//							}
//							bRet = dbMgr.DeleteModuleInfo(deleteinfo.get(i).getModuleId());
//							if (bRet == false) {
//								LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Failed to Delete ModuleID:%s", 
//										deleteinfo.get(i).getModuleId()));
//							}
//						}
//					}
//				}
//			}
			
			Vector<MODULE_INFO> module_infos = dbMgr.QueryModuleInfoFromMac(strMac);
			if (module_infos == null) {
				// read database failed
				LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Failed to QueryModuleInfoFromMac. (Mac:%s)", 
						strMac));
			} else {
				if (module_infos.size() == 0 ) {
					// not find module,
					LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Find ==0  Module have the Mac. (Mac: %s)", 
							strMac));
				} else if (module_infos.size() == 1 ) {
					// find one module, OK
					MODULE_INFO info = module_infos.get(0);
					info.setModuleId(strDevId);
//					info.setModuleName(strModuleName);
					info.setModuleType(strModuleType);
					info.setModuleVer(strModuleVer);
					info.setStatus(iStatus);
					info.setCharge(iCharge);
					info.setCookie(strCookie);
					dbMgr.UpdateModuleInfo(info);	
					
					// 配置网路后，第一次登陆
					if (strAppUserName.equalsIgnoreCase(PubDefine.DEFAULT_USERNAME) == true) {
						// 把USER_MODULE 中user和Mac的对应改为user和strDevId的对应关系
						Vector<USER_MODULE> infos = dbMgr.QueryUserModuleByDevId(strMac);
						if (infos == null) {
							LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Failed to QueryUserModuleByDevId. (DevID or Mac:%s)", 
									strMac));
						} else {
							if (infos.size() == 0) {
								LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Find 0 UserModule in QueryUserModuleByDevId. (DevID or Mac:%s)", 
										strMac));
							} else {
								LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Find %d UserModule in QueryUserModuleByDevId. (DevID or Mac:%s)", 
										infos.size(), strMac));
								for (int i = 0; i < infos.size(); i++) {
									USER_MODULE user = infos.get(i);
									user.setModuleId(strDevId);
									if (!dbMgr.UpdateUserModule(user)) {
										LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Fail to UpdateUserModule. (user:%s devid:%s Relation:%d)", 
												user.getUserName(), user.getModuleId(), user.getCtrlMode()));
									}
								}
							}
						}
					} else {	// 配置网络后，重复登陆
						
					}
				} else {
					// find more module 
					LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Find >=2  Module have same Mac. (Mac: %s)", 
							strMac));
				}
			}
			
//			//如果不存在该模块
//			if(null == dbMgr.QueryModuleInfo(strDevId))
//			{
//				//更新模块信息
//				MODULE_INFO info = new MODULE_INFO(strDevId,strModuleName,strMac,strModuleVer,strModuleType,
//						iStatus,
//						iCharge,
//						strCookie);
//				bRet = dbMgr.InsertModuleInfo(info);
//				if(!bRet)
//				{
//					LogWriter.WriteErrorLog(LogWriter.SELF, String.format("(%s)\t Failed to InsertModuleInfo:%s", 
//							strDevId,strMsgHeader));
//					dbMgr.Rollback();
//					dbMgr.EndTansacion();
//					return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;
//				}
//			}
//			
//			MODULE_INFO module_info = dbMgr.QueryModuleInfo(strDevId);
//
//			// Update USRE_MODULE module.
//			if (strAppUserName.equalsIgnoreCase("thingzdo") == false && 
//					strAppUserName.equalsIgnoreCase("usrname") == false &&
//					strAppUserName.equalsIgnoreCase("username") == false) {
//				USER_MODULE user_module = dbMgr.QueryUserModuleByDevId(strDevId);
//				if (user_module == null) {
//					LogWriter.WriteTraceLog(LogWriter.SELF, String.format("InsertUserModule[ModuleLoginMsg_Insert](user:%s, devid:%s)", strAppUserName,strDevId));
//					dbMgr.InsertUserModule(new USER_MODULE(strAppUserName,strDevId,USER_MODULE.PRIMARY));
//				} else {
//					LogWriter.WriteTraceLog(LogWriter.SELF, String.format("InsertUserModule[ModuleLoginMsg_Update](user:%s, devid:%s)", strAppUserName,strDevId));
//					dbMgr.UpdateUserModule(new USER_MODULE(strAppUserName,strDevId,USER_MODULE.PRIMARY));
//				}
//			}
			
			dbMgr.Commit();
			dbMgr.EndTansacion();
			
			//注册模块信息
			ServerWorkThread.RegisterModuleIP(strDevId, strCookie, thread.getSrcIP(), thread.getSrcPort());
			
			//开启心跳包定时器
			thread.StartHeartTimer(strDevId);
			
			/* 刷新心跳状态 */
			ServerWorkThread.RefreshModuleAliveFlag(strDevId, true);
				
			//通知模块登录成功
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
			ResponseToModule(strDevId, String.format("%s,%s,%s#", strCookie, ServerCommDefine.MODULE_LOGIN_MSG_HEADER, df.format(new Date())));
			
			// Update ConnectInfo's LastTime
			ConnectInfo c_info = ServerWorkThread.getModuleConnectInfo(strDevId);
			java.util.Date dt = new java.util.Date();
			Timestamp tsLastTime = new Timestamp(dt.getTime());
			c_info.setTsLastTime(tsLastTime);
			LogWriter.WriteTraceLog(LogWriter.SELF, String.format("[LastTime][%s]\tLoginTime:%d", strDevId, tsLastTime.getTime()));
			
			//通知APP模块已上线
			Vector<USER_MODULE> user_info = dbMgr.QueryUserModuleByDevId(strDevId);
			if (user_info == null) {
				LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Failed to QueryUserModuleByDevId. (DevID or Mac:%s)", 
						strMac));
			} else {
				for (int i = 0; i < user_info.size(); i++) {
					USER_MODULE user = user_info.get(i);
					NotifyToAPP(user.getUserName(), strDevId, 
							ServerCommDefine.APP_NOTIFY_ONLINE_MSG_HEADER, 
							ServerRetCodeMgr.SUCCESS_CODE,
							String.valueOf(ServerCommDefine.MODULE_ON_LINE)) ;		
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

	@Override
	public int call(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
}
