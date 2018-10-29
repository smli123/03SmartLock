package com.smartlock.udpserver.Function;

import java.sql.Timestamp;
import java.util.TimerTask;
import java.util.Vector;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ConnectInfo;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerParamConfiger;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.MODULE_DATA;
import com.smartlock.udpserver.db.ServerDBMgr;
import com.smartlock.udpserver.db.USER_MODULE;

public class ModuleHeartBeatTask_bak  extends TimerTask implements ICallFunction{
	String m_strModuleID = null;
	public ModuleHeartBeatTask_bak(String strModuleID)
	{
		m_strModuleID = strModuleID;
	}
	@Override
	public void run() {
		LogWriter.WriteDebugLog(LogWriter.SELF, String.format("[%s]Curr Timer:%s", m_strModuleID, this.toString()));
		
		ConnectInfo info = ServerWorkThread.getModuleConnectInfo(m_strModuleID);
		if (null == info)
		{
			//没有收到心跳包
			LogWriter.WriteHeartLog(LogWriter.SELF,
					String.format("[%s]Module not login. HearBeatTask is cancel.", m_strModuleID));
			// 40352 必须修改的地方
			this.cancel();
			return;
		}
		
		/* 判断当前的TASK是否为游离TASK */
//		if (info.getBeatTask() != this)	// 必须把前面的注释去掉
		{
			LogWriter.WriteDebugLog(LogWriter.SELF, 
					String.format("[BUG]This BeatTask is OffControl(%s),id=%s", this.toString(),m_strModuleID));
		}
		
		ServerDBMgr dbMgr = new ServerDBMgr();
		// Update ConnectInfo's LastTime
		ConnectInfo c_info = ServerWorkThread.getModuleConnectInfo(m_strModuleID);
		java.util.Date dt = new java.util.Date();
		Timestamp tsNow = new Timestamp(dt.getTime());
		Timestamp tsLastTime = c_info.getTsLastTime();
		long onlineTime = (tsNow.getTime() - tsLastTime.getTime())/1000;

		LogWriter.WriteTraceLog(LogWriter.SELF, String.format("[LastTime][%s]\tHearBeatTask:%d, now:%d diff:%d", m_strModuleID, tsLastTime.getTime(), tsNow.getTime(), onlineTime));
		
		if (onlineTime > 20) {	// 超过50秒，表示离线
			try
			{
				//通知APP模块已离线
				Vector<USER_MODULE> user_info = dbMgr.QueryUserModuleByDevId(m_strModuleID);
				if (user_info == null) {
					LogWriter.WriteErrorLog(LogWriter.SELF, String.format("\t Failed to QueryUserModuleByDevId. (DevID:%s)", 
							m_strModuleID));
				} else {
					for (int i = 0; i < user_info.size(); i++) {
						USER_MODULE user = user_info.get(i);
						NotifyToAPP(user.getUserName(), m_strModuleID, 
								ServerCommDefine.APP_NOTIFY_ONLINE_MSG_HEADER, 
								ServerRetCodeMgr.SUCCESS_CODE,
								String.valueOf(ServerCommDefine.MODULE_OFF_LINE)) ;		
					}
				}

				/* step2 清理模块存储的信息 */
				ServerWorkThread.UnRegisterModuleIP(m_strModuleID);
				
				/* step3 停止心跳检测定时器*/
				LogWriter.WriteDebugLog(LogWriter.SELF, String.format("Stop Heart Timer:%s, timer info:%s", m_strModuleID, info.getHeartTimer().toString()));
				info.getHeartTimer().cancel();
				
				// 更新模块上线日志信息 lishimin -- MODULE_DATA
				if (ServerParamConfiger.getRecordModuleData() == true) {
					dbMgr.BeginTansacion();
					MODULE_DATA data = dbMgr.QueryModuleDataByModuleId(m_strModuleID);
					Timestamp dtLogoutTime = dbMgr.getCurrentTime();
					data.setLogoutTime(dtLogoutTime);
					long ionlineTime = (dtLogoutTime.getTime() - data.getLoginTime().getTime())/1000;
					data.setOnlineTime(ionlineTime);
					
					boolean bRet = dbMgr.UpdateModuleData(data);
					if(!bRet)
					{
						LogWriter.WriteErrorLog(LogWriter.SELF, String.format("(%s)\t Failed to UpdateModuleData:[%s - %s]", 
								m_strModuleID,data.getLoginTime(),data.getLogoutTime()));
						dbMgr.Rollback();
						dbMgr.EndTansacion();
					} else {
						LogWriter.WriteErrorLog(LogWriter.SELF, String.format("(%s)\t Succeed to UpdateModuleData:[%s - %s]", 
								m_strModuleID,data.getLoginTime(),data.getLogoutTime()));
					}
					dbMgr.Commit();
					dbMgr.EndTansacion();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				dbMgr.Destroy();
			}
		}
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
