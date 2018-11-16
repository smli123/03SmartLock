package com.sherman.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.activation.CommandMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewTest {
	public static DatagramSocket dataSocket = null;

	private static boolean bModuleLogin = false;
	private static int lockstatus = 0;
	private static int lockcharge = 100;
	
	private static int alarm_no = 0;
	
	public static void main(String[] args) throws Exception{
		dataSocket = new DatagramSocket(PubDefine.LOCAL_PORT);
		dataSocket.setSoTimeout(5000);
		
		Thread app_server = new NewTest().new ServerMainThread();
		Thread heart_server = new NewTest().new HeartBeatThread();
		Thread notify_server = new NewTest().new NotifyThread();
		
		
		app_server.join();
		heart_server.join();
		notify_server.join();
		
		return;
	}

    public class SendMsgUDP extends Thread
    {
        private String command;
        
        public SendMsgUDP(String command)
        {
            this.command = command;
        }
        public void run()
        {
			try {
				System.out.println("Send Data:" + command);
				
				InetAddress remoteInetAddress = InetAddress.getByName(PubDefine.SERVER_IP);
			    byte[] bytesToSend = command.getBytes();
		
//			    DatagramSocket dataSocket = new DatagramSocket();
//			    dataSocket.setSoTimeout(5000);
			
			    DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
			        bytesToSend.length, remoteInetAddress, PubDefine.SERVER_PORT);
			
			    if (dataSocket.isClosed() == true) {
			    	dataSocket = new DatagramSocket(PubDefine.LOCAL_PORT);
					dataSocket.setSoTimeout(5000);
			    }
			    dataSocket.send(sendPacket);
	    		
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("\tCMD Fail:" + command);
			}
        }
    }

    //=================================================================================
    public class HeartBeatThread extends Thread {
    	
    	public HeartBeatThread()
    	{
    		this.start();
    	}

    	@Override
    	public void run()
    	{
    		try {
	    		while(bModuleLogin == false) {
	    			login_server();
	    			Thread.sleep(3* 1000);
	    		}
	    		
	    		while(true) {
	    			hear_beat();
	    		}
    		} catch (Exception e) {
        		e.printStackTrace();
        	}
    	}
    }

    public void login_server() {
    	try {
    		// SmartLock LOGIN
    		String cmd_string = PubFunc.genNewCookie() + ",LOGIN," + PubDefine.USER_NAME + "," + PubDefine.DEFAULT_MODULEID + ",Door," + PubDefine.DEFAULT_MAC + ",V1R1C00,1," + 
    							PubFunc.genRandom(2) + "," + PubFunc.genRandom(100) + ",0,0,0#";
    		
    		new NewTest().new SendMsgUDP(cmd_string).run();
    		PubFunc.DebugLog("login");
    		System.out.println(String.format("Login Server OK."));
    		Thread.sleep(1000);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	public void hear_beat() {
		try {
			String cmd_string = PubFunc.genNewCookie() + ",HEART," + PubDefine.USER_NAME + "," + PubDefine.DEFAULT_MODULEID + "#";
			
			new NewTest().new SendMsgUDP(cmd_string).run();
			System.out.println(String.format("Hear Beat OK."));
			Thread.sleep(50 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 //=================================================================================
    public class NotifyThread extends Thread {
    	
    	public NotifyThread()
    	{
    		this.start();
    	}

    	@Override
    	public void run()
    	{
    		while(true) {
    			try {
    				notify_status();
					Thread.sleep(30 * 1000);
	    			notify_alarm();
    			} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    private void notify_status() {
    	try {
    		lockstatus = PubFunc.genRandom(2);
    		lockcharge = PubFunc.genRandom(100);
    		int usertype = PubFunc.genRandom(6);
    		String memo = "simulator Sataus";
    		
			String cmd_string = PubFunc.genNewCookie() + ",NOTIFY_STASTUS," + PubDefine.USER_NAME + "," + PubDefine.DEFAULT_MODULEID + "," + lockstatus + "," + lockcharge + "," + usertype + "," + memo + "#";
			
			new NewTest().new SendMsgUDP(cmd_string).run();
			System.out.println(String.format("nofity status OK."));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void notify_alarm() {
    	try {
    		String strTime = PubFunc.genCurTime();
    		int alarmType = 0;
    		int alarmData = PubFunc.genRandom(4);
    		int usertype = PubFunc.genRandom(6);
    		String memo = "simulatorAlarm_" + alarm_no;
    		
			String cmd_string = PubFunc.genNewCookie() + ",NOTIFY_ALARM," + PubDefine.USER_NAME + "," + PubDefine.DEFAULT_MODULEID + "," + alarm_no + "," + strTime + "," + alarmType + "," + alarmData + "," + usertype + "," + memo + "#";
			
			new NewTest().new SendMsgUDP(cmd_string).run();
			System.out.println(String.format("nofity alarm OK."));
//			Thread.sleep(10 * 1000);
			
			alarm_no++;
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
//-----------------------------------------------------------------------------------
public class ServerMainThread extends Thread {
//	public DatagramSocket dataSocket = null;
	private String ipAddress = "";
	
//	private ExecutorService executor = Executors.newCachedThreadPool();
	
	public ServerMainThread()
	{
		ipAddress = "AA:AA:AA:AA:AA";
		
		this.start();
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try {
//				dataSocket = new DatagramSocket(PubDefine.RECV_SERVER_PORT);
				/*STEP2 监听*/
				while(true)
				{
					if (dataSocket.isClosed() == true) {
				    	dataSocket = new DatagramSocket(PubDefine.LOCAL_PORT);
						dataSocket.setSoTimeout(5000);
				    }
					
					byte[] receiveByte = new byte[1024];
					DatagramPacket dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
					dataSocket.receive(dataPacket);
					
					String sendStr = "";
					String receiveStr = new String(dataPacket.getData());

					System.out.println("Recv Data:" + receiveStr);
					
					receiveStr = receiveStr.substring(0, receiveStr.indexOf("#"));
					String[] recStrs = receiveStr.split(",");
					if (recStrs.length >= 2) {
						if (recStrs[1].equals("LOGIN") == true) {
							int retcode = Integer.valueOf(recStrs[4]);
							if (retcode == 0) {
								PubDefine.USER_NAME = recStrs[2];
								bModuleLogin = true;
							}
						} else if (recStrs[1].equals("LOCK_OPEN") == true) {
							lockstatus = Integer.valueOf(recStrs[4]);
							sendStr = PubFunc.genNewCookie() + ",LOCK_OPEN," + PubDefine.USER_NAME + "," + PubDefine.DEFAULT_MODULEID + ",0," + lockstatus + "#";
							response(dataSocket, sendStr, dataPacket.getAddress(), dataPacket.getPort());
						} else if (recStrs[1].equals("NOTIFY_STASTUS") == true) {
							System.out.println("NOTIFY_STASTUS");
						} else if (recStrs[1].equals("NOTIFY_ALARM") == true) {
							System.out.println("NOTIFY_ALARM");
						} else if (recStrs[1].equals("HEART") == true) {
							System.out.println("HEART");
						} else {
							System.out.println(String.format("Unknown Command:%s", receiveStr));
						}
						
					}
					
//					if (recStrs.length >= 2) {
//						if (recStrs[1].equals("COMMAND") == true) {
//							sendStr = "COMMAND,OK";
//							String command = recStrs[4];
//							
//							response(dataSocket, sendStr, dataPacket.getAddress(), dataPacket.getPort());
//						} else {
//							// Lishimin Define Command Response
//							sendStr = getCmdResonse(recStrs[1]);
//							if (sendStr == null) {
//								System.out.println(String.format("Unknown Command:%s", receiveStr));
//							} else {
//								// response Server result
//								response(dataSocket, sendStr, dataPacket.getAddress(), dataPacket.getPort());
//							}
//						}
//					}
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			
			if (null != dataSocket)
			{
				dataSocket.close();
			}
		}
	}
	
	private void response(DatagramSocket dataSocket, String info, InetAddress remoteAddress, int remotePort) {
		if (info.isEmpty() == false) {
			DatagramPacket res_dataPacket = new DatagramPacket(info.getBytes(), info.getBytes().length, remoteAddress, remotePort);    
			try {
				dataSocket.send(res_dataPacket);
				System.out.println(String.format("Send[%s:%d]:%s", remoteAddress.getHostAddress(), remotePort, info));
				System.out.println(String.format("Send Server OK"));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Response Fail.");
			}
		}
	}
}
    
}