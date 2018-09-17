package com.sherman.smartlockex.ui.util;

import java.util.TimerTask;

import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

public class AppTimeoutTask extends TimerTask {

	@Override
	public void run() {
		SmartLockApplication.setLogined(false);
		SmartLockApplication.getInstance().exit();
	}
}
