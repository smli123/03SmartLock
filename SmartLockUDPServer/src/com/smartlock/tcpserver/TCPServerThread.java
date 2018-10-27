package com.smartlock.tcpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.smartlock.platform.LogTool.LogWriter;
import com.smartlock.platform.commdefine.ServerPortDefine;

public class TCPServerThread extends Thread {
	/*�����̳߳�*/
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	public TCPServerThread()
	{
		TCPWorkThread.Init();
		this.start();
	}
	@Override
	public void run()
	{
		try {
			/*STEP1 ��ѯģ��IP��ַ*/
			@SuppressWarnings("resource")
			ServerSocket app_server = new ServerSocket(ServerPortDefine.SERVER_BASE_PORT_TCP);

			/*STEP2 ����*/
			while(true)
			{
				Socket app_client = app_server.accept();
				executor.execute(new TCPWorkThread(app_client));
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e);
		}
		LogWriter.WriteDebugLog(LogWriter.SRV_SELF_LOG,"App Server quit.");
	}
}
