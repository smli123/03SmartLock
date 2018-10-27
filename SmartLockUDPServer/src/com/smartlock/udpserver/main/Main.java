package com.smartlock.udpserver.main;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ServerMainThread;
import com.smartlock.udpserver.commdef.ServerParamConfiger;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.debugudpserver.DebugUDPServerThread;

public class Main {
	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		try {	
			//STEP1 初始化日志服务 
			LogWriter.Init(ServerParamConfiger.PRODUCT_NAME);
			LogWriter.WriteTraceLog(LogWriter.SELF,"==========SmartLockServer Begin============");
			
			//STEP2 读取配置参数
			ServerParamConfiger.Do();
			
			//STEP3 初始化数据库
			ServerDBMgr.Init();

			// 更新 MODULE_DATA的LOGOUT_TIME时间
			ServerDBMgr_InitModuleData();
			
			//STEP4 创建APP服务器线程
			Thread app_server = new ServerMainThread();
			LogWriter.WriteTraceLog(LogWriter.SELF,"SmartLockServer App Server started successfully.");
			
			//STEP4 创建远程调试服务器(UDP)
			Thread debug_udp_server = new DebugUDPServerThread();
			LogWriter.WriteTraceLog(LogWriter.SELF,"Remote Debug UDP Server started successfully.");
			
			//STEP5 等待各线程结束
			app_server.join();
			debug_udp_server.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF,e);
		} 
		LogWriter.WriteTraceLog(LogWriter.SELF,"==========SmartLockServer Terminate============");
	}
	
	private static void ServerDBMgr_InitModuleData() {
		if (ServerParamConfiger.getRecordModuleData() == true) {
			ServerDBMgr dbMgr = new ServerDBMgr();
			dbMgr.BeginTansacion();
			if (dbMgr.InitModuleData() == false) {
				dbMgr.Rollback();
			} else {
				dbMgr.Commit();	
			}
			dbMgr.EndTansacion();	
		}
	}
}
