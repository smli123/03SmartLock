package com.smartlock.udpserver.Function;

import java.sql.SQLException;
import java.util.Vector;

import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.MODULE_INFO;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_MODULE;

public class APPQueryAllModuleInfoHandle implements ICallFunction{
	private String GenModuleInfo(MODULE_INFO info, String strDevId, int iRelation)
	{
		if(null == info)
		{
			/* 查不到的模块回默认值 */
			String strModuleInfo = strDevId + ",";
			strModuleInfo += "smartlock_" + strDevId + ",";
			strModuleInfo += "00:00:00:00:00:00" + ",";	//MAC地址
			strModuleInfo += "201811028A1V1.4" + ",";	// Module Version
			strModuleInfo += "1" + ",";					// Module Type
			strModuleInfo += MODULE_INFO.OFFLINE + ",";
			strModuleInfo += 0 + ",";
			strModuleInfo += 0 + ",";	
			strModuleInfo += iRelation;
			
			return strModuleInfo;
		}
		
		//查询模块在线状态
		String strOnLine 		= String.valueOf(MODULE_INFO.OFFLINE);
		if (ServerWorkThread.IsModuleLogin(strDevId))
		{
			strOnLine 		= String.valueOf(MODULE_INFO.ONLINE);
		}
		
		String strStaus			= String.valueOf(info.getStatus());
		String strCharge		= String.valueOf(info.getCharge());
		
		//模块信息
		String strModuleInfo = strDevId + ",";
		strModuleInfo += info.getModuleName() + ",";
		strModuleInfo += info.getMac() + ",";
		strModuleInfo += info.getModuleVer() + ",";
		strModuleInfo += info.getModuleType() + ",";
		strModuleInfo += strOnLine + ",";
		strModuleInfo += strStaus + ",";
		strModuleInfo += strCharge + ",";
		strModuleInfo += iRelation;
		
		return strModuleInfo;
	}
	/**********************************************************************************************************
	 * @name QueryAllModuleInfoHandle 查询指定用户名下的所有模块信息
	 * @param 	strMsg: 命令字符串 格式：cookie,QRYPLUG,<username>
	 * @RET 		<new cookie>,APPQRYPLUG, <username>,<0xFFFF>,<code>,
	 *                                   <count>,<ID1>,<devname1>,<mac1>,<protocol type>,<online1>,<poweron1>,<ip1>,<red>,<green>,<blue>,
	 * 																<timer_count>,	<timer_id1>,<timer_enable>,<period1>,<powerontime1>,<powerofftime1>@...@
	 * 																							<timer_id_n>,<timer_enable>,<period_n>,<powerontime_n>,<powerofftime_n>#…#
	 * 											<ID_n>,<mac2>,< devname _n>,<protocol type>,<online_n>,<poweron_n>,<ip_n>,<red>,<green>,<blue>,
	 * 																<timer_count>,<timer_id1>,<timer_enable>,<period1>,<powerontime1>,<powerofftime1>@...@
	 * 																						  <timer_id_n>,<timer_enable>,<period_n>,<powerontime_n>,<powerofftime_n>
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @date 2015/04/12
	 * **********************************************************************************************************/
	public int call(Runnable thread_base, String strMsg) 
	{	
		//取值
		String strRet[] 	= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie	= strRet[0].trim();
		String strMsgHeader = strRet[1].trim();
		String strUserName 	= strRet[2].trim();
		
		/* 校验参数合法性 */
		int iRet = CheckAppCmdValid(strUserName, strCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			ResponseToAPP(strMsgHeader, strUserName, ServerCommDefine.DEFAULT_MODULE_ID, iRet);
			return iRet;
		}
		
		ServerDBMgr dbMgr = new ServerDBMgr();
		
		try
		{
			//查询用户名下所有模块
			Vector<USER_MODULE> vecUserModule = dbMgr.QueryUserModuleByUserName(strUserName);
			
			//组建返回字符串
			String strModuleInfoList = String.valueOf(vecUserModule.size());
			
			//组合模块信息列表
			for(USER_MODULE user_module:vecUserModule)
			{
				String strDevId = user_module.getModuleId() ;
				MODULE_INFO info = dbMgr.QueryModuleInfo(strDevId);
				
				//模块信息
				String strModuleInfo = GenModuleInfo(info, strDevId, user_module.getCtrlMode());
				
				//将该模块信息加入模块信息列表中
				strModuleInfoList += "," + strModuleInfo;
			}
			
			//返回信息给APP
			ResponseToAPP(strMsgHeader, strUserName, ServerRetCodeMgr.SUCCESS_CODE, strModuleInfoList);
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
