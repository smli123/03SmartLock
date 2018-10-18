package com.sherman.smartlockex.ui.smartlockex;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.PubDefine;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.SmartLockFragmentPagerAdapter;
import com.sherman.smartlockex.ui.dev.AddSocketActivity2;
import com.sherman.smartlockex.ui.dev.DeviceFragment;
import com.sherman.smartlockex.ui.login.LoginActivity;
import com.sherman.smartlockex.ui.message.MessageFragment;
import com.sherman.smartlockex.ui.setting.SettingFragment;
import com.sherman.smartlockex.ui.util.MyAlertDialog;

public class SmartLockActivity extends FragmentActivity
		implements
			View.OnClickListener,
			TextToSpeech.OnInitListener {
	private static final String TAG = SmartLockActivity.class.getName();
	
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private TextView tv_tab_dev, tv_tab_message, tv_tab_setting;
	private ImageView iv_tab_dev, iv_tab_message, iv_tab_setting;
	private LinearLayout ll_tab_dev, ll_tab_message, ll_tab_setting;
	
	protected boolean mBack2Exit = false;
	private TextView tvTitle;
//	private Button mBtnAddPlug = null;

	private Button btnLoginout;

	private int cur_index = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	private int position_two;
	private int position_three;
	
	private Resources resources;
	private Context mContext;
//	private SmartPlugHelper mPlugProvider = null;

	private SharedPreferences mSharedPreferences;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SmartLockApplication.getInstance().addActivity(this);
		SmartLockApplication.resetTask();

		mSharedPreferences = getSharedPreferences("PARAMETERCONFIG"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		loadData();

		mContext = this;
//		mPlugProvider = new SmartPlugHelper(mContext);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_viewpager);
		resources = getResources();
		InitWidth();
		InitTextView();
		InitViewPager();

		// IntentFilter filter = new IntentFilter();
		// filter.addAction("");
		// mContext.registerReceiver(mReceiver, filter);

//		IntentFilter filter = new IntentFilter();
//		filter.addAction(PubDefine.LOGOUT_BROADCAST);
//		filter.addAction(PubDefine.CONFIG_MODIFY_SPEEK_TIMER);
//		mContext.registerReceiver(mLogoutReveiver, filter);
	}


	private void loadData() {
	}


	@Override
	public void onInit(int status) {
		// if (status == TextToSpeech.SUCCESS) {
		// int result = tSpeech.setLanguage(Locale.CHINA);
		// if (result == TextToSpeech.LANG_MISSING_DATA
		// || result == TextToSpeech.LANG_NOT_SUPPORTED) {
		// Toast.makeText(getApplicationContext(),
		// "Language is not available.", Toast.LENGTH_LONG).show();
		// }
		// } else {
		// Toast.makeText(getApplicationContext(), "init failed",
		// Toast.LENGTH_LONG).show();
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		// tSpeech = new TextToSpeech(getApplicationContext(), this);
	}

	private void StopSpeech() {
		// if (tSpeech != null) {
		// tSpeech.stop();
		// }
	}

	private void InitTextView() {
		iv_tab_dev = (ImageView) findViewById(R.id.iv_tab_dev);
		iv_tab_message = (ImageView) findViewById(R.id.iv_tab_message);
		iv_tab_setting = (ImageView) findViewById(R.id.iv_tab_setting);
		
		tv_tab_dev = (TextView) findViewById(R.id.tv_tab_dev);
		tv_tab_message = (TextView) findViewById(R.id.tv_tab_message);
		tv_tab_setting = (TextView) findViewById(R.id.tv_tab_setting);

		ll_tab_dev = (LinearLayout) findViewById(R.id.ll_tab_dev);
		ll_tab_message = (LinearLayout) findViewById(R.id.ll_tab_message);
		ll_tab_setting = (LinearLayout) findViewById(R.id.ll_tab_setting);

		tvTitle = (TextView) findViewById(R.id.titlebar_caption);
		btnLoginout = (Button) findViewById(R.id.titlebar_leftbutton);

		/*
		 * mImgAddPlug = (ImageView) findViewById(R.id.titlebar_rightimage);
		 * mImgAddPlug.setImageResource(R.drawable.smp_scan_plug);
		 * mImgAddPlug.setVisibility(View.VISIBLE);
		 * mImgAddPlug.setOnClickListener(this);
		 */

//		mBtnAddPlug = (Button) findViewById(R.id.titlebar_rightbutton);
//		mBtnAddPlug.setText("增加");
//		mBtnAddPlug.setBackgroundResource(R.drawable.title_btn_selector);
//		mBtnAddPlug.setVisibility(View.VISIBLE);
//		mBtnAddPlug.setOnClickListener(this);

		// mImgFreshPlug = (ImageView) findViewById(R.id.titlebar_leftimage);
		// mImgFreshPlug.setImageResource(R.drawable.plug_fresh_pressed);
		// mImgFreshPlug.setVisibility(View.GONE);

		tvTitle.setVisibility(View.VISIBLE);
		btnLoginout.setVisibility(View.INVISIBLE);
//		btnLoginout.setText(getString(R.string.smartplug_goback));

		iv_tab_dev.setOnClickListener(new TabOnClickListener(0));
		iv_tab_message.setOnClickListener(new TabOnClickListener(1));
		iv_tab_setting.setOnClickListener(new TabOnClickListener(2));
		
		ll_tab_dev.setOnClickListener(new TabOnClickListener(0));
		ll_tab_message.setOnClickListener(new TabOnClickListener(1));
		ll_tab_setting.setOnClickListener(new TabOnClickListener(2));

		btnLoginout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SmartLockApplication.setLogined(false);

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(SmartLockApplication.getInstance(),
						LoginActivity.class);
				SmartLockApplication.getInstance().startActivity(intent);
			}
		});

		tvTitle.setText(getString(R.string.smartlock_title_dev));

	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vg_tab_pager);
		if (null != fragmentsList) {
			fragmentsList.clear();
			fragmentsList = null;
		}
		fragmentsList = new ArrayList<Fragment>();

		Fragment deviceFragment = DeviceFragment.newInstance();
		Fragment messageFragment = MessageFragment.newInstance();
		Fragment settingFragment = SettingFragment.newInstance();
		((SettingFragment) settingFragment).setHandler(mLogouthandler);
		
		fragmentsList.add(deviceFragment);
		fragmentsList.add(messageFragment);
		fragmentsList.add(settingFragment);

		mPager.setAdapter(new SmartLockFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentsList));

		mPager.setCurrentItem(0);
		
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
//		tv_tab_dev.setTextColor(resources.getColor(R.color.blue));
//		iv_tab_dev.setImageResource(R.drawable.smp_tab_devlist_pressed);
	}

	private void InitWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
//		offset = (int) ((screenW / 2.0 - bottomLineWidth) / 2);
//		PubFunc.log("MainActivity offset=" + offset);

		position_one = (int) (screenW / 3.0);
		position_two = position_one * 2;
		position_three = position_one * 3;
	}

	public class TabOnClickListener implements View.OnClickListener {
		private int index = 0;

		public TabOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int pos) {
			Animation animation = null;
			switch (pos) {
				case 0 :
					animation = new TranslateAnimation(position_one, 0, 0,
							0);
					tv_tab_dev.setTextColor(resources
							.getColor(R.color.blue));
					tv_tab_message.setTextColor(resources
							.getColor(R.color.gray));
					tv_tab_setting.setTextColor(resources
							.getColor(R.color.gray));
					
					iv_tab_dev
							.setImageResource(R.drawable.smp_tab_devlist_pressed);
					iv_tab_message
							.setImageResource(R.drawable.smp_tab_message_normal);
					iv_tab_setting
							.setImageResource(R.drawable.smp_tab_settings_normal);
					
					tvTitle.setText(getString(R.string.smartlock_title_dev));
					
//					mBtnAddPlug.setVisibility(View.VISIBLE);
					break;
				case 1 :
					animation = new TranslateAnimation(0, position_two, 0,
							0);
					
					tv_tab_dev.setTextColor(resources
							.getColor(R.color.gray));
					tv_tab_message.setTextColor(resources
							.getColor(R.color.blue));
					tv_tab_setting.setTextColor(resources
							.getColor(R.color.gray));
					
					iv_tab_dev
							.setImageResource(R.drawable.smp_tab_devlist_normal);
					iv_tab_message
							.setImageResource(R.drawable.smp_tab_message_pressed);
					iv_tab_setting
							.setImageResource(R.drawable.smp_tab_settings_normal);

					tvTitle.setText(getString(R.string.smartlock_title_message));
					
//					mBtnAddPlug.setVisibility(View.INVISIBLE);
					break;
				case 2 :
					animation = new TranslateAnimation(position_two, position_three, 0,
							0);
					
					tv_tab_dev.setTextColor(resources
							.getColor(R.color.gray));
					tv_tab_message.setTextColor(resources
							.getColor(R.color.gray));
					tv_tab_setting.setTextColor(resources
							.getColor(R.color.blue));
					
					iv_tab_dev
							.setImageResource(R.drawable.smp_tab_devlist_normal);
					iv_tab_message
							.setImageResource(R.drawable.smp_tab_message_normal);
					iv_tab_setting
							.setImageResource(R.drawable.smp_tab_settings_pressed);
		
					tvTitle.setText(getString(R.string.smartlock_title_settings));
					
//					mBtnAddPlug.setVisibility(View.INVISIBLE);
					break;
				default :
					break;
			}

			cur_index = pos;
			animation.setFillAfter(true);
			animation.setDuration(200);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			new MyAlertDialog(mContext)
					.builder()
					.setMsg(this.getString(R.string.smartlock_exit))
					.setPositiveButton(this.getString(R.string.smartlock_ok),
							okListener)
					.setNegativeButton(
							this.getString(R.string.smartlock_cancel),
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {

								}
							}).setCancelable(true).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	final OnClickListener okListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			finish();
			// PubDefine.disconnect();
			SmartLockApplication.getInstance().exit();
		}
	};

	protected void onDestroy() {
		fragmentsList.clear();
		// mContext.unregisterReceiver(mReceiver);
//		mContext.unregisterReceiver(mLogoutReveiver);

		super.onDestroy();
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		/*
		 * case R.id.titlebar_rightimage: //Intent intent = new
		 * Intent(this,AddSocketActivity2.class); //startActivity(intent);
		 * break;
		 */
			case R.id.titlebar_rightbutton :
				Intent intent = new Intent(this, AddSocketActivity2.class);
				startActivity(intent);
				break;
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
//		String plugId = intent.getStringExtra("new_plug");
//		Log.i(TAG, "new plug id is: " + plugId);

		super.onNewIntent(intent);

	}
	

	private Handler mLogouthandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					Intent mIntent = new Intent();
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mIntent.setClass(SmartLockApplication.getContext(),
							LoginActivity.class);
					mIntent.putExtra("FORCE_LOGOUT", 1);
					SmartLockApplication.getContext().startActivity(mIntent);
					finish();
					break;
				default :
					break;
			}
		};
	};
}
