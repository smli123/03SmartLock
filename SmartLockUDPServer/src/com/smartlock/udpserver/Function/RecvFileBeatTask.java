package com.smartlock.udpserver.Function;

import java.sql.Timestamp;
import java.util.TimerTask;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ConnectInfo;
import com.smartlock.udpserver.ModuleRecvFileMgr;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerParamConfiger;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.MODULE_DATA;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_MODULE;

public class RecvFileBeatTask  extends TimerTask implements ICallFunction{
	private ModuleRecvFileMgr m_RecvFileMgr = null;

	public RecvFileBeatTask(ModuleRecvFileMgr moduleRecvFileMgr) {
		// TODO Auto-generated constructor stub
		this.m_RecvFileMgr = moduleRecvFileMgr;
	}
	@Override
	public void run() {
		String strModuleID = m_RecvFileMgr.getModuleID();
		LogWriter.WriteDebugLog(LogWriter.SELF, String.format("[%s]RecvFileTask Timer:%s", strModuleID, this.toString()));
		
		m_RecvFileMgr.StopRecvFileStartTimer();
		ServerWorkThread.UnRegisterRecvFileMgr(strModuleID);
	}

	@Override
	public int call(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int resp(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
}
