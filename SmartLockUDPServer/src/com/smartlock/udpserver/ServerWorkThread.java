package com.smartlock.udpserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.Function.APPAddModuleMsgHandle;
import com.smartlock.udpserver.Function.AppLogOutMsgHandle;
import com.smartlock.udpserver.Function.AppLoginMsgHandle;
import com.smartlock.udpserver.Function.AppPassThroughMsgHandle;
import com.smartlock.udpserver.Function.APPDeleteModuleMsgHandle;
import com.smartlock.udpserver.Function.APPFindPwdHandle;
import com.smartlock.udpserver.Function.APPModEmailMsgHandle;
import com.smartlock.udpserver.Function.APPModPwdMsgHandle;
import com.smartlock.udpserver.Function.APPModifyPlugNameHandle;
import com.smartlock.udpserver.Function.ModuleBack2APCtrlMsgHandle;
import com.smartlock.udpserver.Function.NofityBellMsgHandle;
import com.smartlock.udpserver.Function.ModuleHeartBeatTask;
import com.smartlock.udpserver.Function.ModuleHeartMsgHandle;
import com.smartlock.udpserver.Function.ModuleLoginHandle;
import com.smartlock.udpserver.Function.ModuleLockCtrlMsgHandle;
import com.smartlock.udpserver.Function.NotifyLockStatusHandle;
import com.smartlock.udpserver.Function.APPQueryAllModuleInfoHandle;
import com.smartlock.udpserver.Function.TransmitHearBeatMsgHandle;
import com.smartlock.udpserver.Function.TransmitTransMsgHandle;
import com.smartlock.udpserver.Function.APPUserRegisterMsgHandle;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerParamConfiger;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;

public class ServerWorkThread  implements Runnable{
	private static Map<String,ConnectInfo> m_AppIPMap 		= new HashMap<String,ConnectInfo>();
	private static Map<String,ConnectInfo> m_ModuleIPMap 	= new HashMap<String,ConnectInfo>();
	private static Map<String,ConnectInfo> m_ModuleTransmitMap 	= new HashMap<String,ConnectInfo>();
	private static Map<Object,Object> m_SendFuncMap 		= new HashMap<Object,Object>();
	private static DatagramPacket m_packet = null;
	private static ServerWorkThread m_thread = null;
	
	private static Map<String,Integer> m_ModuleFileNOMap 	= new HashMap<String,Integer>();
	
	public ServerWorkThread(DatagramPacket packet)
	{
		m_packet = packet;
		m_thread = this;
	}
	public static Map<String,ConnectInfo> getModuleIPMap()
	{
		return m_ModuleIPMap;
	}
	public static DatagramPacket getPacket()
	{
		return m_packet;
	}
	
	public static String getSrcIP()
	{
		return m_packet.getAddress().getHostAddress();
	}
	
	public static int getSrcPort()
	{
		return m_packet.getPort();
	}
	
	public static int GetModuleFileNO(String strDevId)
	{
		if (!m_ModuleFileNOMap.containsKey(strDevId))
		{
			SetModuleFileNO(strDevId, 0);
		}
		return m_ModuleFileNOMap.get(strDevId);
	}

	public static void SetModuleFileNO(String strDevId, int fileno)
	{
		m_ModuleFileNOMap.put(strDevId, fileno);
	}
	
	public static void Init()
	{
		/* APP透传通道命令 */
		m_SendFuncMap.put(ServerCommDefine.APP_PASSTHROUGH_MSG_HEADER, new AppPassThroughMsgHandle());

		/* APP命令 */
		m_SendFuncMap.put(ServerCommDefine.APP_REGUSER_MSG_HEADER, new APPUserRegisterMsgHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_RSTPWD_MSG_HEADER, new APPFindPwdHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_MOD_EMAIL_MSG_HEADER, new APPModEmailMsgHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_MOD_PWD_MSG_HEADER, new APPModPwdMsgHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_LOGIN_MSG_HEADER, new AppLoginMsgHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_LOGOUT_MSG_HEADER, new AppLogOutMsgHandle());
		
		m_SendFuncMap.put(ServerCommDefine.APP_QRY_PLUG_MSG_HEADER, new APPQueryAllModuleInfoHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_ADD_PLUG_MSG_HEADER, new APPAddModuleMsgHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_DEL_PLUG_MSG_HEADER, new APPDeleteModuleMsgHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_MOD_PLUG_MSG_HEADER, new APPModifyPlugNameHandle());
		
		/* APP和模块命令*/
		m_SendFuncMap.put(ServerCommDefine.APP_LOCK_CTRL_MSG_HEADER, new ModuleLockCtrlMsgHandle());
		m_SendFuncMap.put(ServerCommDefine.APP_BACK2AP_CTRL_MSG_HEADER, new ModuleBack2APCtrlMsgHandle());
		
		/* 模块命令 */
		m_SendFuncMap.put(ServerCommDefine.MODULE_LOGIN_MSG_HEADER, new ModuleLoginHandle());
		m_SendFuncMap.put(ServerCommDefine.LOCK_CTRL_MSG_HEADER, new ModuleLockCtrlMsgHandle());
		m_SendFuncMap.put(ServerCommDefine.NOTIFY_BELL_MSG_HEADER, new NofityBellMsgHandle());

		m_SendFuncMap.put(ServerCommDefine.MOD_PLUG_MSG_HEADER, new APPModifyPlugNameHandle());
		m_SendFuncMap.put(ServerCommDefine.BACK2AP_CTRL_MSG_HEADER, new ModuleBack2APCtrlMsgHandle());
		
		/* 模板上报消息 */
		m_SendFuncMap.put(ServerCommDefine.NOTIFY_LOCK_STATUS, new NotifyLockStatusHandle());
	}
	
	private static boolean IsNeedJudgeLogin(String strCmd)
	{
		if (strCmd.equalsIgnoreCase(ServerCommDefine.APP_LOGIN_MSG_HEADER) ||
			strCmd.equalsIgnoreCase(ServerCommDefine.APP_REGUSER_MSG_HEADER) ||
			strCmd.equalsIgnoreCase(ServerCommDefine.APP_RSTPWD_MSG_HEADER) ||
			strCmd.equalsIgnoreCase(ServerCommDefine.APP_PASSTHROUGH_MSG_HEADER))
		{
			return false;
		}
		
		return true;
	}
	
	public void StartHeartTimer(String strModuleID)
	{
		ConnectInfo info = ServerWorkThread.getModuleConnectInfo(strModuleID);
		LogWriter.WriteDebugLog(LogWriter.SELF, String.format("Start Heart Timer:%s, timer info:%s", strModuleID, info.getHeartTimer().toString()));
		info.getHeartTimer().schedule(info.getBeatTask(), 
				ServerParamConfiger.getiTimeOutHeartBeat(),  
				ServerParamConfiger.getiTimeOutHeartBeat());
	}
	
	public void StopHeartTimer(String strModuleID)
	{
		ConnectInfo info = ServerWorkThread.getModuleConnectInfo(strModuleID);
		LogWriter.WriteDebugLog(LogWriter.SELF, String.format("Stop Heart Timer:%s, timer info:%s", strModuleID, info.getHeartTimer().toString()));
		//info.getBeatTask().cancel();
		info.getHeartTimer().cancel();
	}
	
	/**
	 * * APP COOKIE
	 * */
	public static String GetCurAppIP(String strUserName)
	{
		if (!m_AppIPMap.containsKey(strUserName))
		{
			LogWriter.WriteDebugLog(LogWriter.SELF, String.format("User(%s) not login. NO APP IP",  strUserName));
			return null;
		}
		return m_AppIPMap.get(strUserName).getSrcIP();
	}
	
	public static int GetCurAppPort(String strUserName)
	{
		if (!m_AppIPMap.containsKey(strUserName))
		{
			LogWriter.WriteDebugLog(LogWriter.SELF, String.format("User(%s) not login. NO APP Port",  strUserName));
			return 0;
		}
		return m_AppIPMap.get(strUserName).getSrcPort();
	}
	
	public static void RegisterUserIP(String strUserName, String strIP, int strPort)
	{
		ConnectInfo info = new ConnectInfo(strIP, strPort);
		m_AppIPMap.put(strUserName, info);
	}
	
	public static void RefreshUserIP(String strUserName, String strIP, int strPort)
	{
		ConnectInfo info = m_AppIPMap.get(strUserName);
		if (null != info)
		{
			info.setSrcIP(strIP);
			info.setSrcPort(strPort);
			m_AppIPMap.put(strUserName, info);
		} else {
			RegisterUserIP(strUserName, strIP, strPort);
		}
	}
	
	public static void RefreshAppCookie(String strUserName, String strCookie)
	{
		ConnectInfo info = m_AppIPMap.get(strUserName);
		if (null != info)
		{
			info.setCookie(strCookie);
			m_AppIPMap.put(strUserName, info);	
		}
	}
	
	public static void UnRegisterUserIP(String strUserName)
	{
		m_AppIPMap.remove(strUserName);
	}
	
	public static boolean IsUserLogin(String strUserName)
	{
		// 临时：测试账户，不用检车用户登录
		if(strUserName.contains(PubDefine.TEST_USERNAME) == true) {
			return true;
		} else {
			return m_AppIPMap.containsKey(strUserName);
		}
	}
	
	public static int CheckCookie(String strLocal, String strRemote)
	{
//		//目前不校验Cookie
//		if(!strLocal.equalsIgnoreCase(strRemote))
//		{  
//			LogWriter.WriteDebugLog(LogWriter.SELF, 
//					String.format("local cookie is %s, remote cookie:%s", strLocal,strRemote));
//			return ServerRetCodeMgr.ERROR_CODE_COOKIE_INCORRECT;
//		}
		
		return ServerRetCodeMgr.SUCCESS_CODE;
	}
	
	/**
	 * * 
	 * */
	public static ConnectInfo getModuleConnectInfo(String strModuleID)
	{
		return m_ModuleIPMap.get(strModuleID);
	}
	
	public static String getModuleIp(String strModuleID)
	{
		ConnectInfo info = m_ModuleIPMap.get(strModuleID);
		if (null != info)
		{
			return m_ModuleIPMap.get(strModuleID).getSrcIP();
		}
		LogWriter.WriteErrorLog(LogWriter.SELF, String.format("Module(%s) not login. NO MODULE IP", strModuleID));
		
		return null;
	}

	public static int getModulePort(String strModuleID)
	{
		ConnectInfo info = m_ModuleIPMap.get(strModuleID);
		if (null != info)
		{
			return m_ModuleIPMap.get(strModuleID).getSrcPort();
		}
		LogWriter.WriteErrorLog(LogWriter.SELF, String.format("Module(%s) not login. NO MODULE Port", strModuleID));
		
		return 0;
	}
	public static String getAllOnlineModule()
	{
		String strInfo = "========all online module info========\n";
		for (Entry<String, ConnectInfo> entry : m_ModuleIPMap.entrySet()) 
		{  
			strInfo += "Key = " + entry.getKey() + ", Value(IP) = " + entry.getValue().getSrcIP() + ", Value(Port) = " + entry.getValue().getSrcPort();  
			strInfo += "\n";
		}  
		
		return strInfo;
	}
	
	public static void RegisterModuleIP(String strModuleID, String strCookie, String strIP, int intPort)
	{
		if (m_ModuleIPMap.containsKey(strModuleID))
		{
			getModuleConnectInfo(strModuleID).getBeatTask().cancel();
			getModuleConnectInfo(strModuleID).getHeartTimer().cancel();
		}
		
		Timer heart_timer = new Timer();
		ModuleHeartBeatTask beatTask = new ModuleHeartBeatTask(strModuleID);
		m_ModuleIPMap.put(strModuleID, new ConnectInfo(strIP, intPort,strCookie, heart_timer, beatTask));
	}
	
	public static void RefreshModuleIP(String strModuleID, String strIP, int intPort)
	{
		ConnectInfo info = m_ModuleIPMap.get(strModuleID);
		if (null != info)
		{
			info.setSrcIP(strIP);
			info.setSrcPort(intPort);
			m_ModuleIPMap.put(strModuleID, info);
		}
	}
	
	public static void RefreshModuleCookie(String strModuleID, String strCookie)
	{
		ConnectInfo info = m_ModuleIPMap.get(strModuleID);
		if (null != info)
		{
			info.setCookie(strCookie);
			m_ModuleIPMap.put(strModuleID, info);	
		}
	}
	
	public static void RefreshModuleAliveFlag(String strModuleID, boolean bAlive)
	{
		String str;
		ConnectInfo info = m_ModuleIPMap.get(strModuleID);
		if (null != info)
		{
			info.setAlive(bAlive);
			m_ModuleIPMap.put(strModuleID, info);	
		}
		
		if (null == info)
		{
			str = String.format("Module=%s, aLive=%d; info is null.", strModuleID, bAlive?1:0);
		}
		else
		{
			str = String.format("Module=%s, aLive=%d, info is not null.", strModuleID, bAlive?1:0);
		}
//		Exception e = new Exception(str);
//		LogWriter.WriteExceptionLog(LogWriter.SELF, e, str);
	}
	
	public static void UnRegisterModuleIP(String strModuleID)
	{
		if (m_ModuleIPMap.containsKey(strModuleID))
		{
			getModuleConnectInfo(strModuleID).getHeartTimer().cancel();
		}
		
		m_ModuleIPMap.remove(strModuleID);
	}
	
	/* 转发器 组件
	 * Author: 
	 */
	public static boolean RegisterTransmitIP(String strModuleID, String strCookie, String strIP, int intPort)
	{
		ConnectInfo info = m_ModuleTransmitMap.get(strModuleID);
		if (null != info)
		{
			return false;
//			info.setSrcIP(strIP);
//			info.setSrcPort(intPort);
//			m_ModuleTransmitMap.put(strModuleID, info);
		} else {
			m_ModuleTransmitMap.put(strModuleID, new ConnectInfo(strIP, intPort,strCookie, null, null));
			return true;
		}
	}
	
	public static void UnRegisterTransmitIP(String strModuleID)
	{
		m_ModuleTransmitMap.remove(strModuleID);
	}
	
	public static ConnectInfo getTransmitConnectInfo(String strModuleID)
	{
		return m_ModuleTransmitMap.get(strModuleID);
	}

	public static int getNewTransmitID () {
		for (int i = 1000; i < 10000; i++) {
			String strId = String.valueOf(i);
			if (m_ModuleTransmitMap.get(strId) == null)
				return i;
		}
		return 0;
	}
	
	public static String getAllTransmitID () {
		String strOut = "";
		int i = 0;
		for (String key : m_ModuleTransmitMap.keySet()) {
			strOut += key;
			i++;
			if (i < m_ModuleTransmitMap.size())
				strOut += "@";
		}
		
		return strOut;
	}	
	
	public static String[] getAllTransmitIDs () {
		String[] strOut = new String[m_ModuleTransmitMap.size()];
		int i = 0;
		for (String key : m_ModuleTransmitMap.keySet()) {
			strOut[i++] = key;
		}
		
		return strOut;
	}
	
	
	public static boolean IsModuleLogin(String strModuleID)
	{
		return m_ModuleIPMap.containsKey(strModuleID);
	}
	
	public void Response(String strRet) throws IOException
	{
		byte[] info = strRet.getBytes();
		Response(info);
	}
	
	public void Response(byte[] info) throws IOException
	{
		DatagramPacket dataPacket = new DatagramPacket(info, info.length, m_packet.getAddress(), m_packet.getPort());    
		ServerMainThread.dataSocket.send(dataPacket);
	}
	
	/**
	 * ResponseToAPP(String strCmd, String strUserName, String strModuleId, int error_code)
	 * @throws SQLException 
	 * */
	public void ResponseToAPP(ServerWorkThread app_thread, String strCmd, String strUserName, String strModuleId, int ret_code, String strDesp) 
	{
		if(!ServerWorkThread.IsUserLogin(strUserName))
		{
			LogWriter.WriteErrorLog(LogWriter.SELF, String.format("user(%s) is offline.", strUserName));  
			return;
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");	
		String strNewCookie = df.format(new Date());
		
		ServerWorkThread.RefreshAppCookie(strUserName, strNewCookie);

		String strRet = String.format("%s,%s,%s,%s,%08x#", strNewCookie, strCmd, strUserName, strModuleId, ret_code);
		try {
			Response(strRet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e,m_packet.getAddress().toString() + ":" + m_packet.getPort());
		}
		
		if(ret_code == ServerRetCodeMgr.SUCCESS_CODE)
		{
			LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s:%d)\t [%s] %s (%s)",
					m_packet.getAddress().toString(),
					m_packet.getPort(),
					strRet,
					ServerRetCodeMgr.getRetCodeDescription(ret_code),
					strUserName));
			return;
		}
		
		LogWriter.WriteErrorLog(LogWriter.SELF, String.format("(%s:%d)\t [%s] %s (%s) description:%s",
				m_packet.getAddress().toString(),
				m_packet.getPort(),
				strRet,
				ServerRetCodeMgr.getRetCodeDescription(ret_code),
				strUserName,strDesp));
		return;
	}
	
	private static boolean isAppCommand(String strCmd)
	{
		if (strCmd.toUpperCase().startsWith("APP"))
		{
			return true;
		}
		return false;
	}
	
	public static int ServerMsgHandle(String strMsg, byte[] strMsgBin) throws IOException 
	{
		strMsg = strMsg.substring(0, strMsg.lastIndexOf("#") + 1);
		String[] strResult 	= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie	= strResult[0].trim();
		String strCmd 		= strResult[1].trim();
		String strUserName	= strResult[2].trim();
	
		ICallFunction func = (ICallFunction) m_SendFuncMap.get(strCmd);
		if(null == func)
		{
			LogWriter.WriteErrorLog(LogWriter.RECV, 
					String.format("(%s:%d)\t Invalid Msg Header:%s", 
							m_packet.getAddress().toString(),
							m_packet.getPort(),
							strMsg));
			return  ServerRetCodeMgr.ERROR_FAILD_FIND_CMD_FUNC;
		}
		

		/* APP命令判断 */
		if (isAppCommand(strCmd))
		{
			LogWriter.WriteDebugLog(LogWriter.RECV,
					String.format("(%s:%d)\t FromAPP: %s",
							m_packet.getAddress().toString(),
							m_packet.getPort(),
							strMsg));
			
			if (IsNeedJudgeLogin(strCmd))
			{
				/* 是否已经登录 */
				if(!ServerWorkThread.IsUserLogin(strUserName))
				{
					String strResp = String.format("%s,%s,%s,%s,2#",
							ServerCommDefine.DEFAULT_COOKIE, ServerCommDefine.APP_LOGOUT_MSG_HEADER, strUserName, "000000");
					
					LogWriter.WriteDebugLog(LogWriter.SEND,
							String.format("(%s:%d)\t SEND: %s",
									m_packet.getAddress().toString(),
									m_packet.getPort(),
									strResp));
					
					String strIP = getSrcIP();
					int strPort = getSrcPort();
					byte[] buffer = new byte[1024];
					DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(strIP), strPort);    
					dataPacket.setData(strResp.getBytes());  
					ServerMainThread.dataSocket.send(dataPacket);	
					
					return ServerRetCodeMgr.ERROR_CODE_APP_NOT_LOGIN;					
				}
			}
			
			/* 命令刷新 UserIP/Port */
			if (!(strCmd.equals(ServerCommDefine.APP_LOGIN_MSG_HEADER)  || 
				  strCmd.equals(ServerCommDefine.APP_RSTPWD_MSG_HEADER) ||
				  strCmd.equals(ServerCommDefine.APP_LOGOUT_MSG_HEADER) ||
				  strCmd.equals(ServerCommDefine.APP_REGUSER_MSG_HEADER)
				  )) {
				
				String old_strIP = ServerWorkThread.GetCurAppIP(strUserName);
				int old_Port = ServerWorkThread.GetCurAppPort(strUserName);
				String strIP = getSrcIP();
				int strPort = getSrcPort();
				/*  */
				if ((null == old_strIP) ||
					!(old_strIP.equals(strIP) && (old_Port == strPort))) {
					ServerWorkThread.RefreshUserIP(strUserName, strIP, strPort);
					
					LogWriter.WriteDebugLog(LogWriter.SELF, 
							String.format("old(%s:%d)->new(%s:%d)\t [%s] [%s] RefreshUserIP_Port",
									old_strIP == null ? "NULL" : old_strIP, 
									old_Port,
									strIP, 
									strPort,
									strUserName, strCmd));	
				}
			}
			if (strCmd.equals(ServerCommDefine.APP_PASSTHROUGH_MSG_HEADER))
			{
				return func.call(m_thread, strMsg, strMsgBin);
			} else {
				return func.call(m_thread, strMsg);
			}
		}
		
		LogWriter.WriteDebugLog(LogWriter.RECV,
				String.format("(%s:%d)\t FromModule: %s",
						m_packet.getAddress().toString(),
						m_packet.getPort(),
						strMsg));
		
		/* 模块命令 */
		return func.resp(m_thread, strMsg);
	}
	
	public void run() {
		try {
			/* JAVA环境编码 */
//			System.out.println("JVM file encoding: " + System.getProperty("file.encoding"));

//			String receiveStr = new String(m_packet.getData(), 0, m_packet.getLength(),"ISO-8859-1");
			
//			String receiveStr = new String(m_packet.getData(), 0, m_packet.getLength(),"UTF-8");
			String receiveStr = new String(m_packet.getData());
			
//			System.out.println(String.format("receiveIP: %s:%d", m_packet.getAddress().toString(), m_packet.getPort()));
//			System.out.println("receiveStr: " + receiveStr);
//			System.out.println("receiveStr(byte): " + Arrays.toString((m_packet.getData())));
			
//			if (receiveStr.getBytes()[0] == '\0') {
//				return;
//			}
			
			int iRet = ServerMsgHandle(receiveStr, m_packet.getData());
			LogWriter.WriteTraceLog(LogWriter.SELF, 
					String.format("(%s:%d)\t Completed to Excute Command(Ret:0x%08x).", m_packet.getAddress().toString(),m_packet.getPort(), iRet));	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e,m_packet.getAddress().toString() + ":" + m_packet.getPort());
		} 
	}
		
	public static void main(String[] args)
	{
		return;
	}

	public static String getAppCookie(String strUserName) {
		// 临时： 测试账户
		if(strUserName.equals(PubDefine.TEST_USERNAME) == true) {
			return "20171208101017";
		} else {
			return m_AppIPMap.get(strUserName).getCookie();
		}
	}
	
	public static String getModuleCookie(String strModuleID)
	{
		return m_ModuleIPMap.get(strModuleID).getCookie();
	}

}