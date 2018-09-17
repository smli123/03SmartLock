package com.smartlock.udpserver.Function;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ModuleRecvFileMgr;
import com.smartlock.udpserver.ModuleUpgradeOnLineMgr;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;

public class ModuleRecvFileEndMsgHandle implements ICallFunction{
	
	@Override
	public int call(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**********************************************************************************************************
	 * @name ModuleRecvFileEndMsgHandle 模块通知服务器其已收到END消息
	 * @param 模块回复：20150101000506,JPG_END,yungrade_service,10590495#
	 * @param 模块回复：0,JPG_OVER,test,11223344#
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @date 2015/04/10
	 * **********************************************************************************************************/
	public int resp(Runnable thread_base, String strMsg) 
	{
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		String strRet[] 			= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie			= strRet[0].trim();
		String strMsgHeader			= strRet[1].trim();
		String strUserName 			= strRet[2].trim();
		String strModuleId			= strRet[3].trim();
		
		ServerWorkThread.RefreshModuleIP(strModuleId, thread.getSrcIP(), thread.getSrcPort());
		
		ModuleRecvFileMgr mgr = thread.GetModuleRecvFileMgr(strModuleId);
		mgr.StopRecvFileStartTimer();

		/* 注销接收文件 */
		thread.UnRegisterRecvFileMgr(strModuleId);
		
		thread.SetModuleFileNO(strModuleId, thread.GetModuleFileNO(strModuleId) + 1);
		
		/* 校验参数合法性 */
		int iRet = ServerRetCodeMgr.SUCCESS_CODE;
			
		/* 透传给模块 */
		try {
			/* 待模块返回 */
			String strRsp = String.format("%s,%s,%s,%s", strCookie,ServerCommDefine.MODULE_RECV_FILE_OVER_MSG_HEADER,strUserName,strModuleId);
			iRet = NotifyToModule(strRsp);
			if (ServerRetCodeMgr.SUCCESS_CODE != iRet)
			{
				return iRet;
			}
			
			return ServerRetCodeMgr.SUCCESS_CODE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e);
		}

		return ServerRetCodeMgr.ERROR_COMMON;
	}
}
