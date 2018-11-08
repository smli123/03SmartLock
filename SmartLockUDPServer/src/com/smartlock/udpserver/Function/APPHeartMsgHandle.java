package com.smartlock.udpserver.Function;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.udpserver.ConnectInfo;
import com.smartlock.udpserver.ServerWorkThread;
import com.smartlock.udpserver.commdef.ICallFunction;
import com.smartlock.udpserver.commdef.ServerCommDefine;
import com.smartlock.udpserver.commdef.ServerParamConfiger;
import com.smartlock.udpserver.commdef.ServerRetCodeMgr;
import com.smartlock.udpserver.db.ServerDBMgr;

public class APPHeartMsgHandle  implements ICallFunction {
	/**********************************************************************************************************
	 * @name  处理模块的心跳
	 * @param 	strMsg: 响应字符串 
	 * 					格式：  cookie, HEART,<username>,< status >
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @date 2015/04/10
	 * **********************************************************************************************************/

	@Override
	public int call(Runnable thread_base, String strMsg) {
		
		
		String strRet[] 		= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie		= strRet[0].trim();
		String strCmd			= strRet[1].trim();
		String strUserName		= strRet[2].trim();
		String strStatis		= strRet[3].trim();		// 保留位
		
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		
		LogWriter.WriteDebugLog(LogWriter.SELF, String.format("(%s:%d)\t Resv APPHEART. APP(%s)", thread.getPacket().getAddress().toString(), 
				thread.getPacket().getPort(), strUserName));
		
//		ServerWorkThread.RefreshUserIP(strUserName, thread.getSrcIP(), thread.getSrcPort());
		
		/* 为了定位模块的Socket接受不到消息的bug，临时增加代码调试使用，正式发布必须删除 */
		String strHeartRsp0 = String.format("%s,%s,%s,%d#", strCookie, ServerCommDefine.APP_HEART_MSG_HEADER, strUserName, 0);
		try {
			Response(thread.getSrcIP(), thread.getSrcPort(), strHeartRsp0);
			
			LogWriter.WriteDebugLog(LogWriter.SEND, String.format("(%s:%d)\t [%s] Succeed to Send APPHEART",
					thread.getPacket().getAddress().toString(), 
					thread.getPacket().getPort(),
					strHeartRsp0));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			LogWriter.WriteErrorLog(LogWriter.SEND, String.format("(%s:%d)\t [%s] Fail to Send APPHEART",
					thread.getPacket().getAddress().toString(), 
					thread.getPacket().getPort(),
					strHeartRsp0));
			return ServerRetCodeMgr.ERROR_COMMON;
		}
		
		return ServerRetCodeMgr.SUCCESS_CODE;
	}
	
	public int resp(Runnable thread_base, String strMsg)
	{
		return 0;
	}

}
