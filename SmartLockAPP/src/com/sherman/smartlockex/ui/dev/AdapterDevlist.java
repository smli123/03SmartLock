package com.sherman.smartlockex.ui.dev;

import java.util.ArrayList;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.dataprovider.SmartLockExLockHelper;
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

public class AdapterDevlist extends BaseAdapter {

	private ArrayList<SmartLockDefine> mDevlist = null;
	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private Handler mHandler = null;

	public AdapterDevlist(Context context,
			ArrayList<SmartLockDefine> devList, Handler handler) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mDevlist = devList;
		mHandler = handler;
	}

	@Override
	public int getCount() {
		return mDevlist.size();
	}

	@Override
	public Object getItem(int pos) {
		return mDevlist.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	private class ViewHolder {
		public RelativeLayout rl_lock_item;
		public ImageView iv_lock_icon;
		public TextView tv_lock_name;
		public ImageView iv_lock_online;
		public ImageView iv_lock_status;
		public ImageView iv_lock_charge;
		public TextView tv_lock_charge;
		public ImageView iv_lock_open;
		public ImageView iv_lock_close;
		public ImageView iv_lock_authorize;

		private void setImageRes(ImageView view, int resId) {
			view.setImageResource(resId);
		}

		@SuppressLint("ResourceAsColor") 
		public void ViewData(SmartLockDefine device, int position) {
			if (device != null) {
				tv_lock_name.setSingleLine(true);

				if (!TextUtils.isEmpty(device.mLockID)) {
					tv_lock_name.setText(device.mName + " [" + device.mLockID + "]");

					if (device.mOnline == true) {
						rl_lock_item.setBackgroundColor(Color.TRANSPARENT);
						iv_lock_online.setImageResource(R.drawable.smp_online);
						iv_lock_status.setImageResource(device.mStatus == 0 ? R.drawable.smp_lock_close : R.drawable.smp_lock_open);
						iv_lock_charge.setImageResource(R.drawable.smp_lock_charge);
						iv_lock_authorize.setImageResource(R.drawable.smp_lock_authorize);
					} else {
						rl_lock_item.setBackgroundColor(Color.LTGRAY);
						iv_lock_online.setImageResource(R.drawable.smp_offline);
						iv_lock_status.setImageResource(device.mStatus == 0 ? R.drawable.smp_lock_close_offline : R.drawable.smp_lock_open_offline);					
						iv_lock_charge.setImageResource(R.drawable.smp_lock_charge_offline);
						iv_lock_authorize.setImageResource(R.drawable.smp_lock_authorize_offline);
					}
			
					if (device.mRelation == 0) {
						iv_lock_authorize.setVisibility(View.VISIBLE);
						rl_lock_item.setOnLongClickListener(deletePlug);
					} else {
						iv_lock_authorize.setVisibility(View.GONE);
						rl_lock_item.setOnLongClickListener(null);
					}
						
					rl_lock_item.setOnClickListener(selectPlugClick);		
					iv_lock_icon.setImageResource(R.drawable.smp_lock_big);
					tv_lock_charge.setText(String.valueOf(device.mCharge));
					
					rl_lock_item.setContentDescription(String
							.valueOf(device.mLockID));
				}
			}
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ((mDevlist == null) || (mDevlist.size() == 0)) {
			return convertView;
		}
		if ((position < 0) || (position > mDevlist.size())) {
			return convertView;
		}
		if (mInflater == null) {
			return convertView;
		}
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.view_lock_item, null);
			
			holder.rl_lock_item = (RelativeLayout) convertView
					.findViewById(R.id.rl_lock_item);
			holder.iv_lock_icon = (ImageView) convertView
					.findViewById(R.id.iv_lock_icon);
			holder.tv_lock_name = (TextView) convertView
					.findViewById(R.id.tv_lock_name);
			holder.iv_lock_online = (ImageView) convertView
					.findViewById(R.id.iv_lock_online);
			holder.iv_lock_status = (ImageView) convertView
					.findViewById(R.id.iv_lock_status);
			holder.iv_lock_charge = (ImageView) convertView
					.findViewById(R.id.iv_lock_charge);
			holder.tv_lock_charge = (TextView) convertView
					.findViewById(R.id.tv_lock_charge);
			holder.iv_lock_open = (ImageView) convertView
					.findViewById(R.id.iv_lock_open);
			holder.iv_lock_close = (ImageView) convertView
					.findViewById(R.id.iv_lock_close);
			holder.iv_lock_authorize = (ImageView) convertView
					.findViewById(R.id.iv_lock_authorize);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (holder != null && mDevlist != null && position < mDevlist.size()) {
			SmartLockDefine FavoriteItem = mDevlist.get(position);
			convertView.setBackgroundColor(Color.TRANSPARENT);
			holder.ViewData(FavoriteItem, position);

			if (FavoriteItem.mType.equals("") == true) {
				// do nothing...
			}
		}
		return convertView;
	}

	View.OnClickListener selectPlugClick = new View.OnClickListener() {

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

	View.OnLongClickListener deletePlug = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(final View v) {
			new ActionSheetDialog(mContext)
					.builder()
					.setCancelable(true)
					.setCanceledOnTouchOutside(true)
					.addSheetItem(
							v.getContext().getString(R.string.smartlock_modify_name),
							SheetItemColor.Blue,
							new OnSheetItemClickListener() {
								@Override
								public void onClick(int which) {
									Message msg = new Message();
									msg.what = 0; 		// modify name
									msg.obj = v.getContentDescription()
											.toString();
									mHandler.sendMessage(msg);
								}
							})
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
