package com.sherman.smartlockex.ui.setting;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.mail.MailSenderInfo;
import com.sherman.smartlockex.mail.SimpleMailSender;
import com.sherman.smartlockex.ui.common.PubFunc;
import com.sherman.smartlockex.ui.common.PubStatus;
import com.sherman.smartlockex.ui.common.TitledActivity;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class ActivityFeedback extends TitledActivity
    implements OnClickListener {
	
	private final String EMAIL_SMTP = "smtp.126.com";
	private final String EMAIL_HOST = "Thingzdo@126.com";
	private final String EMAIL_PWD  = "Thingzdo11";	
	
	private EditText mEdtContent = null;
	private Button   mBtnCommit = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_setting_feedback, false);
		SmartLockApplication.resetTask();
		SmartLockApplication.getInstance().addActivity(this);
		
		mEdtContent = (EditText) findViewById(R.id.edt_content);
		mBtnCommit  = (Button) findViewById(R.id.btn_feedback);
		mBtnCommit.setOnClickListener(this);
		
		setTitleLeftButton(R.string.smartlock_goback, R.drawable.title_btn_selector, this);
		setTitle(R.string.app_feedback);			
	}

	@Override
	public void onClick(View v) {
		if (R.id.titlebar_leftbutton == v.getId()) {
			finish();
		} else if (R.id.btn_feedback == v.getId()) {
			if (true == mEdtContent.getText().toString().isEmpty()) {
				PubFunc.thinzdoToast(ActivityFeedback.this, 
						ActivityFeedback.this.getString(R.string.app_feedback_content));
				return;
			}
			
			if (PubStatus.g_userEmail.isEmpty()) {
				PubFunc.thinzdoToast(ActivityFeedback.this, 
						ActivityFeedback.this.getString(R.string.app_feedback_not_found_email));
				return;
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("From:")
			  .append(PubStatus.g_userEmail)
			  .append("\n")
			  .append(mEdtContent.getText().toString());			
			
			new SendMail(sb.toString()).start();
			PubFunc.thinzdoToast(ActivityFeedback.this, 
					ActivityFeedback.this.getString(R.string.app_feedback_commit));
		}
	}
	
	private class SendMail extends Thread {
		private String mContent = "";
		public SendMail(String content) {
			mContent = content;
		}
		
		@Override
		public void run() {
			sendMail(mContent);
		}
	}
	
	private void sendMail(String content) {
	      MailSenderInfo mailInfo = new MailSenderInfo();   
	      mailInfo.setMailServerHost(EMAIL_SMTP);   
	      mailInfo.setMailServerPort("25");
	      mailInfo.setValidate(true);   
	      mailInfo.setUserName(EMAIL_HOST);   
	      mailInfo.setPassword(EMAIL_PWD);   
	      mailInfo.setFromAddress(EMAIL_HOST);   
	      mailInfo.setToAddress(EMAIL_HOST);   
	      mailInfo.setSubject("Smart Device Feedback");   
	      mailInfo.setContent(content);   

	      SimpleMailSender sms = new SimpleMailSender();  
	      sms.sendTextMail(mailInfo);
	      //sms.sendHtmlMail(mailInfo); 		
	}
    
       
}
