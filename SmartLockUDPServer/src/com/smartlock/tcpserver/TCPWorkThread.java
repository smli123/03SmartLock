package com.smartlock.tcpserver;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.platform.ObjMgrTool.ObjectMapManager;
import com.smartlock.platform.StringTool.StringConvertTool;
import com.smartlock.udpserver.FunctionModule.ModuleNotifyMsgHandle;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerParamConfiger;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.ServerDBMgr;

public class TCPWorkThread  implements Runnable{
	//
	private static ObjectMapManager m_SocketMap 	= new ObjectMapManager();
	private static ObjectMapManager m_ThreadMap 	= new ObjectMapManager();
	private Map<Object,Object> m_AppMsgMap 	= new HashMap<Object,Object>();
	private static Map<Object,Object> m_SendFuncMap 	= new HashMap<Object,Object>();
	
	private static String CMD_SPLIT_STRING		= "[,#]";
	private Socket m_Socket = null;
	private boolean m_bUserLogined 	= false;
	private String m_strUserName 			= null;
	private ServerDBMgr m_dbMgr 		= new ServerDBMgr();
	private String m_strCookie;
	
	public TCPWorkThread(Socket socket)
	{
		m_Socket = socket;
	}
	
	public Socket GetSocket()
	{
		return m_Socket;
	}
	
	public static void Init()
	{
		m_SendFuncMap.put(ServerCommDefine.BELL_ON_MSG_HEADER, new ModuleNotifyMsgHandle());
	}
	
	public void PushCmdString(String strMsgHeader, String strModuleId, String strMsg)
	{
		String strCmdIdx = strMsgHeader + strModuleId;
		m_AppMsgMap.put(strCmdIdx, strMsg);
		LogWriter.WriteDebugLog(LogWriter.SRV_SELF_LOG, String.format("PushString:%s", strMsg));
	}
	
	public String PopCmdString(String strMsgHeader, String strModuleId)
	{
		String strCmdIdx = strMsgHeader + strModuleId;
		String strValue = (String) m_AppMsgMap.get(strCmdIdx);
		m_AppMsgMap.remove(strCmdIdx);
		LogWriter.WriteDebugLog(LogWriter.SRV_SELF_LOG, String.format("PopString:%s", strValue));
		return strValue;
	}
	
	public static Socket getSocket(String strUserName)
	{
		return (Socket)m_SocketMap.GetValue(strUserName);
	}
	
	public static TCPWorkThread getThread(String strUserName)
	{
		return (TCPWorkThread)m_ThreadMap.GetValue(strUserName);
	}
	
	public static void RegisterSocket(String strUserName, Socket appSocket)
	{
		m_SocketMap.Register(strUserName, appSocket);
	}
	public static void RegisterThread(String strUserName, Runnable thread)
	{
		m_ThreadMap.Register(strUserName, thread);
	}
	public static void UnRegisterSocket(String strUserName, Socket appSocket)
	{
		m_SocketMap.UnRegister(strUserName, appSocket);
	}
	public static void UnRegisterThread(String strUserName, Runnable thread)
	{
		m_ThreadMap.UnRegister(strUserName, thread);
	}

	public ServerDBMgr GetDbMgr()
	{
		return m_dbMgr;
	}

	public void SetUserLoginFlag(boolean bFlag)
	{
		m_bUserLogined = bFlag;
	}
	
	public boolean GetUserLoginFlag()
	{
		return m_bUserLogined;
	}
	
	public void SetUserName(String strUserName)
	{
		m_strUserName = strUserName;
	}
	
	/**
	 * ResponseToAPP(String strCmd, String strUserName, String strModuleId, int error_code)
	 * @throws SQLException 
	 * */
	public void ResponseToAPP(String strCmd, String strUserName, String strModuleId, int ret_code, String strDesp) 
	{
		TCPWorkThread app_thread = TCPWorkThread.getThread(strUserName);
		if(null == app_thread)
		{
			LogWriter.WriteErrorLog(LogWriter.MOD_TO_SRV, String.format("user(%s) is offline.", strUserName));  
			return;
		}
		
		/* �����µ�COOKIE */
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//�������ڸ�ʽ
		String strNewCookie = df.format(new Date());
		
		//�洢��COOKIE
		app_thread.setCookie(strNewCookie);
		
		//��ȡSOCKET
		Socket appSocket = app_thread.GetSocket();

		String strRet = String.format("%s,%s,%s,%s,%08x#", strNewCookie, strCmd, strUserName, strModuleId, ret_code);
		try {
			appSocket.getOutputStream().write(strRet.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG, e,appSocket.getInetAddress().toString());
		}
		
		if(ret_code == ServerRetCodeMgr.SUCCESS_CODE)
		{
			LogWriter.WriteTraceLog(LogWriter.SRV_TO_APP, String.format("(%s)\t [%s] %s (%s)",
					appSocket.getInetAddress().toString(),
					strRet,
					ServerRetCodeMgr.getRetCodeDescription(ret_code),
					strUserName));
			return;
		}
		
		LogWriter.WriteErrorLog(LogWriter.SRV_TO_APP, String.format("(%s)\t [%s] %s (%s) description:%s",
				appSocket.getInetAddress().toString(),
				strRet,
				ServerRetCodeMgr.getRetCodeDescription(ret_code),
				strUserName,strDesp));
		return;
	}
	
	/**********************************************************************************************************
	 * @name IsCheckCookie �Ƿ���ҪУ��COOKIE
	 * @param 	������
	 * @RET 		
	 * @return  boolean �Ƿ���Ч
	 * @author zxluan
	 * @date 2015/04/07
	 * **********************************************************************************************************/
	private boolean IsCheckCookie(String strCmd)
	{
		if(strCmd.equalsIgnoreCase(ServerCommDefine.APP_LOGIN_MSG_HEADER)
			|| strCmd.equalsIgnoreCase(ServerCommDefine.APP_REGUSER_MSG_HEADER))
		{
			return false;
		}
		return true;
	}
	
	/**********************************************************************************************************
	 * @name CheckCmdValid ���������Ч��
	 * @param 	
	 * @RET 		
	 * @return  boolean �Ƿ���Ч
	 * @author zxluan
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @date 2015/04/07
	 * **********************************************************************************************************/
	private boolean CheckAppCmdValid(String strMsg)
	{
		String strRet[] 			= strMsg.split(CMD_SPLIT_STRING);
		String strCookie			= strRet[0].trim();
		String strCmd				= strRet[1].trim();
		String strUserName	= strRet[2].trim();
		
		/* У���û��Ƿ��¼ */
		if( !this.GetUserLoginFlag()
		&& !strCmd.equalsIgnoreCase(ServerCommDefine.APP_LOGIN_MSG_HEADER) 
		&& !strCmd.equalsIgnoreCase(ServerCommDefine.APP_REGUSER_MSG_HEADER))
		{
			LogWriter.WriteErrorLog(LogWriter.APP_TO_SRV, String.format("(%s)\t app[%s] dis not login", 
					m_Socket.getInetAddress().toString(), strUserName));
			
			ResponseToAPP(strCmd, strUserName, ServerCommDefine.DEFAULT_MODULE_ID, 
					ServerRetCodeMgr.ERROR_CODE_APP_NOT_LOGIN,"");
			return false;
		}
		
		if(IsCheckCookie(strCmd))
		{
			//У��COOKIE
			if(!getCookie().equalsIgnoreCase(strCookie))
			{  
				String strCode = String.format("local cookie is %s, remote cookie:%s", getCookie(),strCookie);
				ResponseToAPP(strCmd, strUserName, ServerCommDefine.DEFAULT_MODULE_ID, 
						ServerRetCodeMgr.ERROR_CODE_COOKIE_INCORRECT, strCode);
				return false;
			}
		}

		/*
		//�Ϸ���У��
		if(iCmdLength != strRet.length)
		{
			ResponseToAPP(strCmd, strUserName, DEFAULT_MODULE_ID, ServerRetCodeMgr.ERROR_CODE_RECEIVED_INVALID_STRING);
			return false;		
		}*/
		return true;
	}
	private int AppServerMsgHandle(String strMsg) throws IOException 
	{
		String[] strResult = strMsg.split(CMD_SPLIT_STRING);
		String strCmd 				= strResult[1].trim();
		String strUserName 	= strResult[2].trim();
		
		LogWriter.WriteDebugLog(LogWriter.APP_TO_SRV,
				String.format("(%s)\t [%s] Received Request from App(%s)",
				m_Socket.getRemoteSocketAddress().toString(),strMsg,m_strUserName));
		
		/* ����Ϸ���У�� */
		if(!CheckAppCmdValid(strMsg))
		{
			return ServerRetCodeMgr.ERROR_COMMON;
		}
		
		/* ͨ�ô���ص� */
		ICallFunction func = (ICallFunction) m_SendFuncMap.get(strCmd);
		if(null == func)
		{
			LogWriter.WriteErrorLog(LogWriter.MOD_TO_SRV, 
					String.format("(%s)\t Invalid Msg Header:%s", 
					m_Socket.getInetAddress().toString(),strCmd));
			return  ServerRetCodeMgr.ERROR_COMMON;
		}
		
		return func.call(this, strMsg);
	}
	
	public void run() {
		LogWriter.WriteTraceLog(LogWriter.APP_TO_SRV,String.format("(%s)\t app client connectd.",m_Socket.getRemoteSocketAddress().toString()));
		try {
			while(true)
			{
				byte[] recvBuf = new byte[ServerParamConfiger.getiAppReceivedBufferSize()];
				if(-1 == m_Socket.getInputStream().read(recvBuf))
				{	   
					LogWriter.WriteTraceLog(LogWriter.SRV_SELF_LOG, 
							String.format("socket(%s) read  -1(user:%s).",m_Socket.getRemoteSocketAddress().toString(), m_strUserName));	
					break;
				}
				String strRecord = StringConvertTool.Byte2String(recvBuf);
				int iRet = AppServerMsgHandle(strRecord);
				if(ServerRetCodeMgr.APP_QUIT == iRet)
				{
					LogWriter.WriteTraceLog(LogWriter.SRV_SELF_LOG, 
							String.format("App Logout(%s).",m_strUserName));	
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG, e,m_Socket.getInetAddress().toString());
		} 
		finally
		{
			m_SocketMap.UnRegister(m_strUserName, m_Socket);
			m_ThreadMap.UnRegister(m_strUserName, this);
			SetUserLoginFlag(false);
			LogWriter.WriteTraceLog(LogWriter.SRV_SELF_LOG, 
					String.format("(%s)\t App Client  Quit(%s).",m_Socket.getRemoteSocketAddress().toString(),
					m_strUserName));			
		}
	}
		
	public static void main(String[] args)
	{
		return;
	}

	public String getCookie() {
		return m_strCookie;
	}

	public void setCookie(String strCookie) {
		m_strCookie = strCookie;
	}
}