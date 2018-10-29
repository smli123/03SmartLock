package com.smartlock.platform.commdefine;

public abstract class ServerPortDefine {
	public final static int SERVER_BASE_PORT_TCP					= 6010;
	//智能门锁 TCP
	public final static int SMART_LOCK_APP_SERVER_PORT				= SERVER_BASE_PORT_TCP + 1;		//6011
	public final static int SMART_LOCK_MODULE_SERVER_PORT			= SERVER_BASE_PORT_TCP + 2;		//6012
	public final static int SMART_LOCK_DEBUG_SERVER_PORT			= SERVER_BASE_PORT_TCP + 3;		//6013
	
	
	
	public final static int SERVER_BASE_PORT_UDP					= 6000;
	//智能门锁 UDP
	public final static int SMART_LOCK_UDP_SERVER_PORT				= SERVER_BASE_PORT_UDP;			//6000
	public final static int SMART_LOCK_APP_UDP_PORT					= SERVER_BASE_PORT_UDP + 2;		//6002
	public final static int SMART_LOCK_MODULE_UDP_PORT				= SERVER_BASE_PORT_UDP + 3;		//6003
	public final static int SMART_LOCK_DEBUG_UDP_SERVER_PORT		= SERVER_BASE_PORT_UDP + 4;		//6004
}
