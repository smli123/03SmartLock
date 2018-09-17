package com.sherman.smartlockex.ui.common;

public class CmdDefine {
	public int    mCommand;       //������
	public String mCommand_Param; //�������
	public int    mCommand_Desc_Id; //��������
	
	public CmdDefine(int command, String param, int commandResId) {
		mCommand = command;
		mCommand_Param = param;
		mCommand_Desc_Id = commandResId;
	}
}
