package com.smartlock.udpserver.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.smartlock.platform.DBTool.DBTool;
import com.smartlock.platform.DBTool.DataSourcePool;
import com.smartlock.platform.LogTool.LogWriter;

public class ServerDBMgr {
	private final static String DB_NAME 		= "smartlock";
	private final static String USER_NAME 		= "root"; 
	private final static String USER_PASS		= "2681b009";
	private DBTool m_dbTool = new DBTool();
	
	public static void Init()
	{
		DataSourcePool.Init(DB_NAME, USER_NAME, USER_PASS);
	}
	
	/**
	 * 创建module_info的触发器
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * */
	public void Destroy()
	{
		m_dbTool.Destroy();
	}
	
	private boolean CreateModuleInfoTriger()
	{
		/*删除module_info时，需要清理timer_info,user_module*/
		String strDropTrigger = "DROP TRIGGER IF EXISTS t_delete_module";
		try {
			m_dbTool.execute(strDropTrigger);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		String strCreateTrigger = "CREATE TRIGGER t_delete_module" +
					" ALTER DELETE ON " + MODULE_INFO.TABLE_NAME +
					" BEGIN" + 
					" delete from " + TIMER_INFO.TABLE_NAME + " where " + TIMER_INFO.MODULE_ID + "= old." + MODULE_INFO.MODULE_ID +
					" delete from " + USER_MODULE.TABLE_NAME + " where " + USER_MODULE.MODULE_ID  + "old." + MODULE_INFO.MODULE_ID;
		return true;
	}
	public boolean UpdateDB(int db_version)
	{		
		return true;
	}
	//开启事务机制
	public boolean BeginTansacion()
	{
		if(null != m_dbTool)
		{
			try {
				m_dbTool.setAutoCommit(false);
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
			}
		}
		return false;
	}
	//关闭事务机制
	public boolean EndTansacion() 
	{
		if(null != m_dbTool)
		{
			try {
				m_dbTool.setAutoCommit(true);
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
			}
		}
		return false;
	}
	//提交事务
	public boolean Commit()
	{
		if(null != m_dbTool)
		{
			try {
				m_dbTool.commit();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
			}
		}
		return false;
	}
	//取消事务
	public boolean Rollback() 
	{
		if(null != m_dbTool)
		{
			try {
				m_dbTool.rollback();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
			}
		}
		
		return false;
	}
	
	/****************************************************************************
	 * MODULE_DATA相关操作
	 * **************************************************************************/
	public MODULE_DATA QueryModuleDataByModuleId(String strModuleId)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(MODULE_DATA.MODULE_ID, strModuleId);
		selection.put(MODULE_DATA.LOGOUT_TIME, String.valueOf(new Timestamp(0)));
		ResultSet rs;
		try {
			rs = m_dbTool.query(MODULE_DATA.TABLE_NAME, selection);
			if(rs.next())
			{
				return new MODULE_DATA(rs.getString(MODULE_DATA.MODULE_ID),
						rs.getString(MODULE_DATA.MODULE_NAME),
						rs.getTimestamp(MODULE_DATA.LOGIN_TIME),
						rs.getTimestamp(MODULE_DATA.LOGOUT_TIME),
						rs.getInt(MODULE_DATA.ONLINE_TIME),
						rs.getString(MODULE_DATA.MEMO));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return null;
	}
	public boolean InsertModuleData(MODULE_DATA info)
	{
		Map<String,String> content = new HashMap<String,String>();
		content.put(MODULE_DATA.MODULE_ID, info.getModuleId());
		content.put(MODULE_DATA.MODULE_NAME, info.getModuleName());
		content.put(MODULE_DATA.LOGIN_TIME, String.valueOf(info.getLoginTime()));
		content.put(MODULE_DATA.LOGOUT_TIME, String.valueOf(info.getLogoutTime()));
		content.put(MODULE_DATA.ONLINE_TIME, String.valueOf(info.getOnlineTime()));
		content.put(MODULE_DATA.MEMO, info.getMemo());
		try {
			return m_dbTool.insert(MODULE_DATA.TABLE_NAME, content);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	
	public boolean UpdateModuleData(MODULE_DATA info)
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		
		selection.put(MODULE_DATA.MODULE_ID, info.getModuleId());
		selection.put(MODULE_DATA.LOGIN_TIME, String.valueOf(info.getLoginTime()));
		
		content.put(MODULE_DATA.LOGOUT_TIME, String.valueOf(info.getLogoutTime()));
		content.put(MODULE_DATA.ONLINE_TIME, String.valueOf(info.getOnlineTime()));
		content.put(MODULE_DATA.MEMO, info.getMemo());
		try {
			return m_dbTool.update(MODULE_DATA.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	
	public boolean InitModuleData()
	{
		Timestamp strCurTime = getCurrentTime();
		
		// 查找所有ModuleIds
		Vector<MODULE_INFO> moduleInfos = QueryModuleInfo();
		
		int total = moduleInfos.size();
		for (int i = 0; i < total; i++) {
			// 在ModuleId中查找只有LoginTime，没有LogoutTime的记录；
			String strModuleId = moduleInfos.get(i).getModuleId();
			MODULE_DATA data = QueryModuleDataByModuleId(strModuleId);
			if (data != null) {
				data.setLogoutTime(strCurTime);
				long ionlineTime = (strCurTime.getTime() - data.getLoginTime().getTime())/1000;
				data.setOnlineTime(ionlineTime);
				// 使用当前时间更新LogoutTime的记录；
				if (UpdateModuleData(data) == false)
					return false;
			}
		}
		return true;
	}
	
	public Timestamp getCurrentTime()
	{
		java.util.Date dt = new java.util.Date();
		Timestamp currentTime = new Timestamp(dt.getTime());
		
		return currentTime;
	}
	
	public boolean DeleteModuleData(String strModuleId)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(MODULE_DATA.MODULE_ID, strModuleId);
		try {
			return m_dbTool.delete(MODULE_DATA.TABLE_NAME, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}

	public boolean ClearModuleData() 
	{
		try {
			return m_dbTool.delete(MODULE_DATA.TABLE_NAME);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	
	/****************************************************************************
	 * USER_INFO相关操作
	 * **************************************************************************/
	/**
	 * @name IsUserNameExist 用户名是否存在
	 * @param strUserName
	 * @return boolean
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean IsUserNameExist(String strUserName)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_INFO.USER_NAME, strUserName);
		ResultSet rs;
		try {
			rs = m_dbTool.query(USER_INFO.TABLE_NAME, selection);
			return rs.next();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name IsEmailExist 邮箱是否存在
	 * @param strEmail
	 * @return boolean
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean IsEmailExist(String strEmail)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_INFO.EMAIL, strEmail);
		ResultSet rs;
		try {
			rs = m_dbTool.query(USER_INFO.TABLE_NAME, selection);
			return rs.next();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name QueryUserInfo 查询指定用户的用户信息
	 * @param strUserName
	 * @return USER_INFO 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public USER_INFO QueryUserInfoByUserName(String strUserName)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_INFO.USER_NAME, strUserName);
		ResultSet rs;
		try {
			rs = m_dbTool.query(USER_INFO.TABLE_NAME, selection);
			if(rs.next())
			{
				return new USER_INFO(rs.getString(USER_INFO.USER_NAME),
						rs.getString(USER_INFO.PASSWORD),
						rs.getString(USER_INFO.EMAIL),
						rs.getString(USER_INFO.COOKIE));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return null;
	}
	/**
	 * @name QueryUserInfo 查询指定邮箱的用户信息(因为EMAIL也是唯一的)
	 * @param strEmail 
	 * @return USER_INFO 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public USER_INFO QueryUserInfoByEmail(String strEmail)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_INFO.EMAIL, strEmail);
		ResultSet rs;
		try {
			rs = m_dbTool.query(USER_INFO.TABLE_NAME, selection);
			if(rs.next())
			{
				return new USER_INFO(rs.getString(USER_INFO.USER_NAME),
						rs.getString(USER_INFO.PASSWORD),
						rs.getString(USER_INFO.EMAIL),
						rs.getString(USER_INFO.COOKIE));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return null;
	}
	/**
	 * @name QueryUserInfo 查询所有用户信息
	 * @return Vector<USER_INFO> 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/04/28
	 */
	public Vector<USER_INFO> QueryUserInfo()
	{
		Vector<USER_INFO> vecUserInfo = new Vector<USER_INFO>();
		try {
			ResultSet rs = m_dbTool.query(USER_INFO.TABLE_NAME);
			while(rs.next())
			{
				vecUserInfo.add(new USER_INFO(rs.getString(USER_INFO.USER_NAME),
						rs.getString(USER_INFO.PASSWORD),
						rs.getString(USER_INFO.EMAIL),
						rs.getString(USER_INFO.COOKIE)));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return vecUserInfo;
	}
	/**
	 * @name InsertUserInfo 插入指定用户的用户信息
	 * @param user_info 待插入的用户信息
	 * @return boolean 
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean InsertUserInfo(USER_INFO user_info)
	{
		Map<String,String> content = new HashMap<String,String>();
		content.put(USER_INFO.USER_NAME, user_info.getUserName());
		content.put(USER_INFO.PASSWORD, user_info.getPassWord());
		content.put(USER_INFO.EMAIL, user_info.getEmail());
		content.put(USER_INFO.COOKIE, user_info.getCookie());
		try {
			return m_dbTool.insert(USER_INFO.TABLE_NAME, content);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name DeleteUserInfo 删除指定的用户信息
	 * @param strUserName 待删除的用户名称
	 * @return boolean 
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean DeleteUserInfo(String strUserName)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_INFO.USER_NAME, strUserName);
		try {
			return m_dbTool.delete(USER_INFO.TABLE_NAME, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name ClearUserInfo 删除所有用户信息
	 * @return boolean 
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean ClearUserInfo() 
	{
		try {
			return m_dbTool.delete(USER_INFO.TABLE_NAME);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name UpdateUserPWD 更新用户密码
	 * @param user_info 待更新的用户信息
	 * @return boolean 
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean UpdateUserPWD(USER_INFO user_info)
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(USER_INFO.PASSWORD, user_info.getPassWord());
		selection.put(USER_INFO.USER_NAME, user_info.getUserName());
		try {
			return m_dbTool.update(USER_INFO.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name UpdateUserCookie 更新用户密码
	 * @param user_info 待更新的用户信息
	 * @return boolean 
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean UpdateUserCookie(USER_INFO user_info)
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(USER_INFO.COOKIE, user_info.getCookie());
		selection.put(USER_INFO.USER_NAME, user_info.getUserName());
		try {
			return m_dbTool.update(USER_INFO.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name UpdateUserPWD 更新用户密码
	 * @param user_info 待更新的用户信息
	 * @return boolean 
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean UpdateUserEmail(USER_INFO user_info)
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(USER_INFO.EMAIL, user_info.getEmail());
		selection.put(USER_INFO.USER_NAME, user_info.getUserName());
		try {
			return m_dbTool.update(USER_INFO.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/****************************************************************************
	 * USER_MODULE相关操作
	 * **************************************************************************/
	/**
	 * @name QueryModuleList 查询指定用户拥有的模块列表
	 * @param strUserName
	 * @return USER_MODULE列表 
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public Vector<USER_MODULE> QueryUserModuleByUserName(String strUserName)
	{
		Vector<USER_MODULE> vecUserModuleList = new Vector<USER_MODULE>();
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_MODULE.USER_NAME, strUserName);
		ResultSet rs;
		try {
			rs = m_dbTool.query(USER_MODULE.TABLE_NAME, selection);
			while(rs.next())
			{
				vecUserModuleList.add(new USER_MODULE(rs.getString(USER_MODULE.USER_NAME),
						rs.getString(USER_MODULE.MODULE_ID),
						rs.getByte(USER_MODULE.CTRL_MODE)));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return vecUserModuleList;
	}
	/**
	 * @name QueryUserModuleByDevId 查询指定设备ID的用户信息
	 * @param strDevId	模块ID
	 * @return USER_MODULE
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public USER_MODULE QueryUserModuleByDevId(String strDevId)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_MODULE.MODULE_ID, strDevId);
		ResultSet rs;
		try {
			rs = m_dbTool.query(USER_MODULE.TABLE_NAME, selection);
			if(rs.next())
			{
				return new USER_MODULE(rs.getString(USER_MODULE.USER_NAME),
						rs.getString(USER_MODULE.MODULE_ID),
						rs.getByte(USER_MODULE.CTRL_MODE));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return null;
	}
	/**
	 * @name QueryModuleList 查询指定用户拥有的模块列表
	 * @param strUserName 	用户名
	 * 					strDevId			模块ID
	 * @return USER_MODULE
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public USER_MODULE QueryUserModule(String strUserName, String strDevId) 
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_MODULE.USER_NAME, strUserName);
		selection.put(USER_MODULE.MODULE_ID, strDevId);
		ResultSet rs;
		try {
			rs = m_dbTool.query(USER_MODULE.TABLE_NAME, selection);
			if(rs.next())
			{
				return new USER_MODULE(rs.getString(USER_MODULE.USER_NAME),
						rs.getString(USER_MODULE.MODULE_ID),
						rs.getByte(USER_MODULE.CTRL_MODE));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return null;
	}
	/**
	 * @name DeleteUserModule 删除指定用户拥有的模块列表
	 * @param strUserName 	用户名
	 * 					strDevId			模块ID
	 * @return boolean 是否成功
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean DeleteUserModule(String strUserName, String strDevId)
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(USER_MODULE.USER_NAME, strUserName);
		selection.put(USER_MODULE.MODULE_ID, strDevId);
		try {
			return m_dbTool.delete(USER_MODULE.TABLE_NAME, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name ClearUserModule 删除所有用户列表
	 * @return boolean 是否成功
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean ClearUserModule() 
	{
		try {
			return m_dbTool.delete(USER_MODULE.TABLE_NAME);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name QueryModuleList 查询指定用户拥有的模块列表
	 * @param strUserName
	 * @return USER_MODULE列表 
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean InsertUserModule(USER_MODULE user_module) 
	{
		Map<String,String> content = new HashMap<String,String>();
		content.put(USER_MODULE.USER_NAME, user_module.getUserName());
		content.put(USER_MODULE.MODULE_ID, user_module.getModuleId());
		content.put(USER_MODULE.CTRL_MODE, String.valueOf(user_module.getCtrlMode()));
		try {
			return m_dbTool.insert(USER_MODULE.TABLE_NAME, content);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return false;
	}
	
	public boolean UpdateUserModule(USER_MODULE user_module) 
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(USER_MODULE.USER_NAME, user_module.getUserName());
		content.put(USER_MODULE.MODULE_ID, user_module.getModuleId());
		content.put(USER_MODULE.CTRL_MODE, String.valueOf(user_module.getCtrlMode()));
		selection.put(USER_MODULE.MODULE_ID, user_module.getModuleId());
		try {
			return m_dbTool.update(USER_MODULE.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return false;
	}

	/****************************************************************************
	 * MODULE_INFO相关操作
	 * **************************************************************************/
	/**
	 * @name QueryModuleInfo 查询指定模块的信息
	 * @param strDevId 设备ID
	 * @return MODULE_INFO
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public MODULE_INFO QueryModuleInfo(String strDevId) 
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(MODULE_INFO.MODULE_ID, strDevId);
		ResultSet rs;
		try {
			rs = m_dbTool.query(MODULE_INFO.TABLE_NAME, selection);
			if(rs.next())
			{
				return new MODULE_INFO(rs.getString(MODULE_INFO.MODULE_ID),
						rs.getString(MODULE_INFO.MODULE_NAME),
						rs.getString(MODULE_INFO.MODULE_MAC),
						rs.getString(MODULE_INFO.MODULE_VER),
						rs.getString(MODULE_INFO.MODULE_TYPE),
						rs.getInt(MODULE_INFO.MODULE_STATUS),
						rs.getInt(MODULE_INFO.MODULE_CHARGE),
						rs.getString(MODULE_INFO.COOKIE));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return null;
	}
	/****************************************************************************
	 * MODULE_INFO相关操作
	 * **************************************************************************/
	/**
	 * @name QueryModuleInfo 查询指定模块的信息
	 * @param strDevId 设备ID
	 * @return MODULE_INFO
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public Vector<MODULE_INFO> QueryModuleInfoFromMac(String strMac) 
	{
		Vector<MODULE_INFO> vecModule = new Vector<MODULE_INFO>();
		
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(MODULE_INFO.MODULE_MAC, strMac);
		ResultSet rs;
		try {
			rs = m_dbTool.query(MODULE_INFO.TABLE_NAME, selection);
			while(rs.next())
			{
				vecModule.add(new MODULE_INFO(rs.getString(MODULE_INFO.MODULE_ID),
						rs.getString(MODULE_INFO.MODULE_NAME),
						rs.getString(MODULE_INFO.MODULE_MAC),
						rs.getString(MODULE_INFO.MODULE_VER),
						rs.getString(MODULE_INFO.MODULE_TYPE),
						rs.getInt(MODULE_INFO.MODULE_STATUS),
						rs.getInt(MODULE_INFO.MODULE_CHARGE),
						rs.getString(MODULE_INFO.COOKIE)));
			}
			return vecModule;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return null;
	}
	
	/**
	 * @name QueryModuleInfo 查询指定模块的信息
	 * @param strDevId 设备ID
	 * @return MODULE_INFO
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public Vector<MODULE_INFO> QueryModuleInfo()
	{
		Vector<MODULE_INFO> vecModule = new Vector<MODULE_INFO>();
		ResultSet rs;
		try {
			rs = m_dbTool.query(MODULE_INFO.TABLE_NAME);
			while(rs.next())
			{
				vecModule.add(new MODULE_INFO(rs.getString(MODULE_INFO.MODULE_ID),
						rs.getString(MODULE_INFO.MODULE_NAME),
						rs.getString(MODULE_INFO.MODULE_MAC),
						rs.getString(MODULE_INFO.MODULE_VER),
						rs.getString(MODULE_INFO.MODULE_TYPE),
						rs.getInt(MODULE_INFO.MODULE_STATUS),
						rs.getInt(MODULE_INFO.MODULE_CHARGE),
						rs.getString(MODULE_INFO.COOKIE)));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return vecModule;
	}
	/**
	 * @name UpdateModuleInfo_Status 更新插座通电状态
	 * @param strModuleId 用户模块ID，iPwrStatus 插座通电状，包括继电器，小夜灯
	 * @return boolean 
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean UpdateModuleInfo_Status(String strModuleId, int iStatus) 
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(MODULE_INFO.MODULE_STATUS, String.valueOf(iStatus));
		selection.put(MODULE_INFO.MODULE_ID, strModuleId);
		try {
			return m_dbTool.update(MODULE_INFO.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	
	public boolean UpdateModuleInfo_Status_Charge(String strModuleId, int iStatus, int iCharge) 
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(MODULE_INFO.MODULE_STATUS, String.valueOf(iStatus));
		content.put(MODULE_INFO.MODULE_CHARGE, String.valueOf(iCharge));
		selection.put(MODULE_INFO.MODULE_ID, strModuleId);
		try {
			return m_dbTool.update(MODULE_INFO.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}

	/**
	 * @name UpdateModuleInfo 更新模块所有状态信息(不包含模块名称)
	 * @param info 模块信息
	 * @return boolean 
	 * @throws SQLException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean UpdateModuleInfo(MODULE_INFO info) 
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(MODULE_INFO.MODULE_MAC, String.valueOf(info.getMac()));
		content.put(MODULE_INFO.MODULE_VER, String.valueOf(info.getModuleVer()));
		content.put(MODULE_INFO.MODULE_TYPE, String.valueOf(info.getModuleType()));
		content.put(MODULE_INFO.MODULE_STATUS, String.valueOf(info.getStatus()));
		content.put(MODULE_INFO.MODULE_NAME, String.valueOf(info.getModuleName()));
		content.put(MODULE_INFO.MODULE_CHARGE, String.valueOf(info.getCharge()));
		content.put(MODULE_INFO.COOKIE, info.getCookie());
		try {
			return m_dbTool.update(MODULE_INFO.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}

	/**
	 * @name InsertModuleInfo 填加指定模块的信息
	 * @param module_info 待填加的模块信息
	 * @return boolean 是否成功
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean InsertModuleInfo(MODULE_INFO module_info) 
	{
		Map<String,String> content = new HashMap<String,String>();
		content.put(MODULE_INFO.MODULE_ID, module_info.getModuleId());
		content.put(MODULE_INFO.MODULE_NAME, module_info.getModuleName());
		content.put(MODULE_INFO.MODULE_MAC, module_info.getMac());
		content.put(MODULE_INFO.MODULE_VER, module_info.getModuleVer());
		content.put(MODULE_INFO.MODULE_TYPE, module_info.getModuleType());
		content.put(MODULE_INFO.MODULE_STATUS, String.valueOf(module_info.getStatus()));
		content.put(MODULE_INFO.MODULE_CHARGE, String.valueOf(module_info.getCharge()));
		try {
			return m_dbTool.insert(MODULE_INFO.TABLE_NAME, content);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name QueryModuleInfo 查询指定模块的信息
	 * @param strDevId 设备ID
	 * @return MODULE_INFO
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean DeleteModuleInfo(String strModuleId) 
	{
		Map<String,String> selection = new HashMap<String,String>();
		selection.put(MODULE_INFO.MODULE_ID, strModuleId);
		try {
			return m_dbTool.delete(MODULE_INFO.TABLE_NAME, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		return false;
	}
	/**
	 * @name ClearModuleInfo 删除所有模块的信息
	 * @return MODULE_INFO
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/28
	 */
	public boolean ClearModuleInfo() 
	{
		try {
			return m_dbTool.delete(MODULE_INFO.TABLE_NAME);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return false;
	}
	/**
	 * @name UpdateModuleName 查询指定模块的信息
	 * @param strModuleId 设备ID
	 *                 strModuleName 模块名称
	 * @return MODULE_INFO
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/04/09
	 */
	public boolean UpdateModuleInfo_ModuleName(String strModuleId,String strModuleName) 
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(MODULE_INFO.MODULE_NAME, strModuleName);
		selection.put(MODULE_INFO.MODULE_ID, strModuleId);
		try {
			return m_dbTool.update(MODULE_INFO.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return false;
	}
	/**
	 * @name UpdateModuleInfo_Cookie 查询指定模块的cookie
	 * @param strModuleId 设备ID
	 *                 strModuleName 模块名称
	 * @return MODULE_INFO
	 * @throws SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/04/09
	 */
	public boolean UpdateModuleInfo_Cookie(String strModuleId,String strCookie) 
	{
		Map<String,String> content = new HashMap<String,String>();
		Map<String,String> selection = new HashMap<String,String>();
		content.put(MODULE_INFO.COOKIE, strCookie);
		selection.put(MODULE_INFO.MODULE_ID, strModuleId);
		try {
			return m_dbTool.update(MODULE_INFO.TABLE_NAME, content, selection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e,"");
		}
		
		return false;
	}
	
}
