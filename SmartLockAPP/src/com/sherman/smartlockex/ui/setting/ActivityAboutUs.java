package com.sherman.smartlockex.ui.setting;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ActivityAboutUs extends TitledActivity
		implements
			OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_setting_about, false);
		SmartLockApplication.resetTask();
		SmartLockApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartlock_goback,
				R.drawable.title_btn_selector, this);
		
	}
	
	private void initView() {
		setTitle(SmartLockApplication.getInstance().getString(R.string.app_about));
		
		TextView verText = (TextView) findViewById(R.id.tv_smartplug_version);
		verText.setText(PubFunc.getAppVersion());
		RelativeLayout rl_company_web = (RelativeLayout) findViewById(R.id.rl_company_web);
		rl_company_web.setOnClickListener(this);
		RelativeLayout rl_service_phone = (RelativeLayout) findViewById(R.id.rl_service_phone);
		rl_service_phone.setOnClickListener(this);

		TextView tv_app_version = (TextView) findViewById(R.id.tv_app_version);
		tv_app_version.setText(getVersion(this));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		initView();
	}
	
	public static String getVersion(Context context)// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return context
					.getString(R.string.smartlock_service_version_unknown);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.titlebar_leftbutton :
				finish();
				break;
			case R.id.rl_company_web : // web address
				String url = SmartLockApplication.getContext().getString(
						R.string.smartlock_company_web_address);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				break;
			case R.id.rl_service_phone : // 直接播出电话
				Uri uri = Uri.parse("tel:"
						+ SmartLockApplication.getContext().getString(
								R.string.smartlock_service_phone_number));
				Intent call = new Intent(Intent.ACTION_CALL, uri);
				startActivity(call);
				break;
		}
	}
}
