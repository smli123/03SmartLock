package com.smartlock.platform.commdefine;

public abstract class ServerPortDefine {
	public final static int SERVER_BASE_PORT_TCP					= 6010;
	//智能插座 TCP
	public final static int SMART_LOCK_APP_SERVER_PORT				= SERVER_BASE_PORT_TCP + 1;		//7011
	public final static int SMART_LOCK_MODULE_SERVER_PORT			= SERVER_BASE_PORT_TCP + 2;		//7012
	public final static int SMART_LOCK_DEBUG_SERVER_PORT			= SERVER_BASE_PORT_TCP + 3;		//7013
	
	
	
	public final static int SERVER_BASE_PORT_UDP					= 6000;
	//智能插座 UDP
	public final static int SMART_LOCK_UDP_SERVER_PORT				= SERVER_BASE_PORT_UDP;			//5000
	public final static int SMART_LOCK_APP_UDP_PORT					= SERVER_BASE_PORT_UDP + 2;		//5002
	public final static int SMART_LOCK_MODULE_UDP_PORT				= SERVER_BASE_PORT_UDP + 3;		//5003
	public final static int SMART_LOCK_DEBUG_UDP_SERVER_PORT		= SERVER_BASE_PORT_UDP + 4;		//5004
}
