package com.sherman.smartlockex.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sherman.smartlockex.R;

public class SmartLockProgressDlg extends Dialog{

	private static Context mContext = null;
	private static boolean mCloseVisible = false;
    private static SmartLockProgressDlg mCarGuardProgressDlg = null;  
    private OnCloseClick mClick = null;
    
    public interface OnCloseClick {
        void click();	
    }
    
    
	public SmartLockProgressDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SmartLockProgressDlg(Context context, int theme) {  
        super(context, theme);  
    } 	
	

	public static SmartLockProgressDlg createDialog(Context context, boolean closeVisible){
		mContext = context;
		mCloseVisible = closeVisible;
		LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.view_progressdlg, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);

        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.imgProgress);  

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                		context, R.anim.loading_animation);  
        // ʹ��ImageView��ʾ����  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
  
        mCarGuardProgressDlg = new SmartLockProgressDlg(context, R.style.loading_dialog);
  
        mCarGuardProgressDlg.setCancelable(false);
        mCarGuardProgressDlg.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));
        return mCarGuardProgressDlg;  
    }  
   

    /** 
     *  
     * [Summary] 
     *       setTitile 
     * @param strTitle 
     * @return 
     * 
     */  
    public SmartLockProgressDlg setTitile(String strTitle){  
        return mCarGuardProgressDlg;  
    }  
      
    /** 
     *  
     * [Summary] 
     *       setMessage 
     * @param strMessage 
     * @return 
     * 
     */  
    public SmartLockProgressDlg setMessage(String strMessage){  
        TextView tvMsg = (TextView)findViewById(R.id.tipTextView);  
          
        if (tvMsg != null){  
            tvMsg.setText(strMessage);  
        }  
          
        return mCarGuardProgressDlg;  
    }  	
    
    public void setClose(){  
    	ImageView closeImage = (ImageView)findViewById(R.id.imgProgressClose); 
    	closeImage.setVisibility(mCloseVisible ? View.VISIBLE : View.GONE);
    	closeImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss(); 
				if (null != mClick) {
					mClick.click();	
				}
			}
		});
        
    } 
    
    public void setCloseCallback(OnCloseClick click) {
    	mClick = click;	
    }

}
