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

public class APPQueryAuthorizeUserHandle implements ICallFunction{
	private String GenInfo(USER_MODULE info)
	{
		//信息
		String strModuleInfo = info.getModuleId() + ",";
		strModuleInfo += info.getUserName() + ",";
		strModuleInfo += "0";
		
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
		String strDevID 	= strRet[3].trim();
		
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
			Vector<USER_MODULE> vecUserModule = dbMgr.QueryUserModuleByDevId(strDevID, USER_MODULE.SLAVE);
			
			//组建返回字符串
			String strModuleInfoList = String.valueOf(vecUserModule.size());
			
			//组合模块信息列表
			for(USER_MODULE user_module:vecUserModule)
			{
				String strModuleInfo = GenInfo(user_module);
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
