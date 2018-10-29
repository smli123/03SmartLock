package com.smartlock.udpserver.db;
/**TIMER_INFO(String strTimerId,String strModuleId,String strPeroid,byte TimeOn,byte TimeOff,byte bEnable)*/
public class MESSAGE_DEVICE {
	public final static String 	TABLE_NAME = "message_device";	//数据库表名
	public final static String 	MESSAGE_ID		= "message_id";	//消息ID
	public final static String	MODULE_ID		= "module_id";	//所属模块的ID
	public final static String	USER_NAME		= "user_name";	//所属模块的ID
	public final static String	MESSAGE_TYPE	= "message_type";	//类型    0
	public final static String	MESSAGE_DATA	= "message_data";	//类型    0：智能锁被撬   1：斜坡开锁报警
	public final static String 	USER_TYPE		= "user_type";		//用户操作类型 0：默认用户，1：指纹用户，2：密码用户，3：卡用户，4：钥匙用户，5，手机用户
	public final static String	USER_MEMO		= "user_memo";		//用户备注
	
	private int 	m_messageId;
	private String	m_strModuleId;
	private String	m_strUserName;
	private int		m_messageType;
	private int		m_messageData;
	private int		m_userType;
	private String  m_strUserMemo;
	
	public MESSAGE_DEVICE(int messageId, String strModuleId, String strUserName, int messageType, int messageData, int userType, String strUserMemo)
	{
		this.setMessageId(messageId);
		this.setModuleId(strModuleId);
		this.setUserName(strUserName);
		this.setMessageType(messageType);
		this.setMessageData(messageData);
		this.setUserType(userType);
		this.setUserMemo(strUserMemo);
	}
	public boolean equal(MESSAGE_DEVICE info)
	{
		if(null == info)
		{
			return false;
		}
		if(this.m_messageId == info.getMessageId() &&
			this.m_strModuleId.equalsIgnoreCase(info.getModuleId())&&
			(this.m_userType == info.getUserType())&&
			(this.m_messageData == info.getMessageData())&&
			this.m_userType == info.getUserType())
		{
			return true;
		}
		return false;
	}
	
	public int getMessageId() {
		return m_messageId;
	}
	public void setMessageId(int messageId) {
		this.m_messageId = messageId;
	}
	public String getModuleId() {
		return m_strModuleId;
	}

	public void setModuleId(String strModuleId) {
		this.m_strModuleId = strModuleId;
	}

	public String getUserName() {
		return m_strUserName;
	}

	public void setUserName(String strUserName) {
		this.m_strUserName = strUserName;
	}

	public int getMessageType() {
		return m_messageType;
	}
	public void setMessageType(int messageType) {
		this.m_messageType = messageType;
	}

	public int getMessageData() {
		return m_messageData;
	}
	public void setMessageData(int messageData) {
		this.m_messageData = messageData;
	}

	public int getUserType() {
		return m_userType;
	}
	public void setUserType(int userType) {
		this.m_userType = userType;
	}

	public String getUserMemo() {
		return m_strUserMemo;
	}
	public void setUserMemo(String userMemo) {
		this.m_strUserMemo = userMemo;
	}
}
