package com.sherman.smartlockex.ui.dev;

import java.util.ArrayList;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
import com.sherman.smartlockex.ui.common.AuthorizeUserDefine;
import com.sherman.smartlockex.ui.common.SmartLockDefine;
import com.sherman.smartlockex.ui.smartlockex.SmartLockApplication;
import com.sherman.smartlockex.ui.wheelutils.ActionSheetDialog;
import com.sherman.smartlockex.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.sherman.smartlockex.ui.wheelutils.ActionSheetDialog.SheetItemColor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdapterAuthorizeList extends BaseAdapter {

	private ArrayList<AuthorizeUserDefine> mItemList = null;
	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private Handler mHandler = null;

	public AdapterAuthorizeList(Context context,
			ArrayList<AuthorizeUserDefine> devList, Handler handler) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mItemList = devList;
		mHandler = handler;
	}

	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mItemList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	private class ViewHolder {
		public RelativeLayout rl_item;
		public TextView tv_no;
		public TextView tv_name;
		public ImageView iv_user;

		private void setImageRes(ImageView view, int resId) {
			view.setImageResource(resId);
		}

		@SuppressLint("ResourceAsColor") 
		public void ViewData(AuthorizeUserDefine item, int position) {
			if (item != null) {
				tv_no.setSingleLine(true);
				tv_name.setSingleLine(true);

				tv_no.setText(String.valueOf(item.mIndex));
				tv_name.setText(String.valueOf(item.mUserName));
				
				rl_item.setContentDescription(String
						.valueOf(item.mUserName));
				rl_item.setOnLongClickListener(deleteItemClick);
				
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ((mItemList == null) || (mItemList.size() == 0)) {
			return convertView;
		}
		if ((position < 0) || (position > mItemList.size())) {
			return convertView;
		}
		if (mInflater == null) {
			return convertView;
		}
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.view_authorize_item, null);
			
			holder.rl_item =(RelativeLayout) convertView
					.findViewById(R.id.rl_item); 
			holder.tv_no = (TextView) convertView
					.findViewById(R.id.tv_no);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			holder.iv_user = (ImageView) convertView
					.findViewById(R.id.iv_user);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (holder != null && mItemList != null && position < mItemList.size()) {
			AuthorizeUserDefine FavoriteItem = mItemList.get(position);
			convertView.setBackgroundColor(Color.TRANSPARENT);
			holder.ViewData(FavoriteItem, position);

		}
		return convertView;
	}

	View.OnClickListener selectItemClick = new View.OnClickListener() {

		@SuppressWarnings("null")
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			String lockId = v.getContentDescription().toString();
			intent.putExtra("LOCKID", lockId);

			SmartLockExLockHelper mPlugHelper = new SmartLockExLockHelper(
					SmartLockApplication.getContext());
			SmartLockDefine mPlug = mPlugHelper.get(lockId);
			if (null == mPlug) {
				return;
			}
			intent.putExtra("ONLINE", mPlug.mOnline);

			if ((mPlug.mType.equals("1") == true)) { 
				intent.setClass(mContext, LockDetailActivity.class);
			} else {
				return;
			}

			mContext.startActivity(intent);
		}
	};

	View.OnLongClickListener deleteItemClick = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(final View v) {
			new ActionSheetDialog(mContext)
					.builder()
					.setCancelable(true)
					.setCanceledOnTouchOutside(true)
					.addSheetItem(
							v.getContext().getString(
									R.string.register_info_delete),
							SheetItemColor.Blue,
							new OnSheetItemClickListener() {
								@Override
								public void onClick(int which) {
									Message msg = new Message();
									msg.what = 1; 		// delete
									msg.obj = v.getContentDescription()
											.toString();
									mHandler.sendMessage(msg);
								}
							}).show();
			return false;
		}
	};
}
