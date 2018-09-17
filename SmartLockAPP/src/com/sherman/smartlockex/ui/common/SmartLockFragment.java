package com.sherman.smartlockex.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.sherman.smartlockex.internet.SendMsgProxy;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import com.sherman.smartlockex.R;

public abstract class SmartLockFragment extends Fragment{
    protected View mFragmentView;
    protected Context mContext = null;
    protected SmartLockProgressDlg mProgress = null;
    protected String mErrorMsg = "";

    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    } 
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//updateUI();	
	}
    
    protected void onCreate(Bundle savedInstanceState, int layoutResID, boolean backToExit) {
    	super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	return super.onCreateView(inflater, container, savedInstanceState);
    }
    
	protected Handler timeoutHandler = new Handler();
	
	protected Runnable timeoutProcess = new Runnable() {

		@Override
		public void run() {
			if (null != mHandler) {
				mHandler.sendEmptyMessage(0);
			}
			
			if (null != mProgress) {
				mProgress.dismiss();
			}	
			if (mErrorMsg.isEmpty()) {
	    		String error = SmartLockApplication.getContext().getString(R.string.common_timeout);
	    		PubFunc.thinzdoToast(SmartLockApplication.getContext(), error);
			} else {
				PubFunc.thinzdoToast(SmartLockApplication.getContext(), 
						mErrorMsg);				
			}
		}
		
	}; 
	
    protected void sendMsg(boolean containCookie, String msg, boolean needDelay) {
//    	SendMsgProxy.sendCtrlMsg(containCookie, msg,  timeoutHandler);
    	
		if (true == needDelay) {
			timeoutHandler.removeCallbacks(timeoutProcess);
			timeoutHandler.postDelayed(timeoutProcess, PubDefine.WAIT_SER_RESPONSE);
		}
    };
    
    private Handler mHandler = null;
    protected void registerTimeoutHandler(Handler handler) {
    	mHandler = handler;
    }
}
