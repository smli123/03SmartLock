package com.smartlock.udpserver.db;
/**
 * mysql> describe module_info;
+--------------+---------------------+------+-----+---------+-------+
| Field        | Type                | Null | Key | Default | Extra |
+--------------+---------------------+------+-----+---------+-------+
| module_id    | varchar(32)         | NO   | PRI | NULL    |       |
| module_name  | varchar(32)         | NO   |     | NULL    |       |
| module_mac   | varchar(32)         | NO   |     | NULL    |       |
| module_version | varchar(32)         | NO   |     | NULL    |       |
| module_type | varchar(32)         | NO   |     | NULL    |       |
| module_status          | tinyint(1) unsigned | NO   |     | NULL    |       |
| module_charge        | tinyint(1) unsigned | NO   |     | NULL    |       |
+--------------+---------------------+------+-----+---------+-------+
7 rows in set (0.00 sec)
 * */

public class MODULE_INFO {
	public final static String 	TABLE_NAME 				= "module_info";
	public final static String 	MODULE_ID				= "module_id";
	public final static String 	MODULE_NAME				= "module_name";
	public final static String  MODULE_MAC				= "module_mac";
	public final static String  MODULE_VER				= "module_version";
	public final static String  MODULE_TYPE				= "module_type";
	public final static String  MODULE_STATUS			= "module_status";
	public final static String  MODULE_CHARGE			= "module_charge";
	public final static String  COOKIE					= "cookie";
	
	public final static int DEVICE_OFF 		= 0;
	public final static int DEVICE_ON 		= 1;
	public final static int OFFLINE			= 0;
	public final static int ONLINE			= 1;
	
	private String m_strModuleId;
	private String m_strModuleName;
	private String m_strMac;
	private String m_strModuleVer;
	private String m_strModuleType;
	private int m_iStatus;
	private int m_iCharge;
	private String m_strCookie;
	
	public MODULE_INFO(String strModuleId,String strModuleName, String strMac, String strModuleVer, String strModuleType, 
			int iStatus, int iCharge, String strCookie)
	{
		this.setModuleId(strModuleId);
		this.setModuleName(strModuleName);
		this.setMac(strMac);
		this.setModuleVer(strModuleVer);
		this.setModuleType(strModuleType);
		this.setStatus(iStatus);
		this.setCookie(strCookie);
		this.setCharge(iCharge);
	}
	public MODULE_INFO(String strModuleId,String strModuleName, String strMac)
	{
		this.setModuleId(strModuleId);
		this.setModuleName(strModuleName);
		this.setMac(strMac);
		this.setModuleVer("unknow");
		this.setModuleType("0");
		this.setStatus(this.DEVICE_OFF);
		this.setCookie("0");
		this.setCharge(0);
	}
	public MODULE_INFO(String strModuleId,String strModuleName, String strMac, String strModuleVer, String strModuleType)
	{
		this.setModuleId(strModuleId);
		this.setModuleName(strModuleName);
		this.setMac(strMac);
		this.setModuleVer(strModuleVer);
		this.setModuleType(strModuleType);
		this.setStatus(this.DEVICE_OFF);
		this.setCookie("0");
		this.setCharge(0);
	}
	
	public boolean Equal(MODULE_INFO info)
	{
		if(info == null)
		{
			return false;
		}
		if((this.m_strModuleId.equalsIgnoreCase(info.getModuleId()) &&
			(this.m_strModuleName.equalsIgnoreCase(info.getModuleName()))))
		{
			return true;
		}
		return false;
	}

	public String getModuleId() {
		return m_strModuleId;
	}

	public void setModuleId(String strModuleId) {
		this.m_strModuleId = strModuleId;
	}

	/**
	 * @return the m_strModuleName
	 */
	public String getModuleName() {
		return m_strModuleName;
	}

	/**
	 * @param m_strModuleName the m_strModuleName to set
	 */
	public void setModuleName(String m_strModuleName) {
		this.m_strModuleName = m_strModuleName;
	}
	public int getStatus() {
		return m_iStatus;
	}
	public void setStatus(int iStatus) {
		this.m_iStatus = iStatus;
	}
	public int getCharge() {
		return m_iCharge;
	}
	public void setCharge(int iCharge) {
		this.m_iCharge = iCharge;
	}
	public String getCookie() {
		return m_strCookie;
	}
	public void setCookie(String strCookie) {
		this.m_strCookie = strCookie;
	}
	public String getMac() {
		return m_strMac;
	}
	public void setMac(String strMac) {
		this.m_strMac = strMac;
	}
	public String getModuleVer() {
		return m_strModuleVer;
	}
	public void setModuleVer(String strModuleVer) {
		this.m_strModuleVer = strModuleVer;
	}
	public String getModuleType() {
		return m_strModuleType;
	}
	public void setModuleType(String strModuleType) {
		this.m_strModuleType = strModuleType;
	}
}