package com.sherman.smartlockex.ui.message;

import java.util.ArrayList;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.MessageDeviceDefine;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageDevicelistAdapter extends BaseAdapter {

	private ArrayList<MessageDeviceDefine> mDevlist = null;
	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private Handler mHandler = null;

	public MessageDevicelistAdapter(Context context,
			ArrayList<MessageDeviceDefine> devList) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mDevlist = devList;
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
		public ImageView iv_message_type;
		public TextView tv_message_type;
		public TextView tv_message_detail;

		private void setImageRes(ImageView view, int resId) {
			view.setImageResource(resId);
		}

		public void ViewData(MessageDeviceDefine device, int position) {
			if (device != null) {
				tv_message_type.setSingleLine(true);

				if (!TextUtils.isEmpty(device.mMessageID)) {
					String str_type = String.valueOf(device.mOperType);
					tv_message_type.setText(str_type);

					iv_message_type.setImageResource(R.drawable.smp_message_type);
					tv_message_detail.setText(device.mDetail);

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
			convertView = mInflater.inflate(R.layout.view_messge_device_item, null);
			
			holder.iv_message_type = (ImageView) convertView
					.findViewById(R.id.iv_message_type);
			holder.tv_message_type = (TextView) convertView
					.findViewById(R.id.tv_message_type);
			holder.tv_message_detail = (TextView) convertView
					.findViewById(R.id.tv_message_detail);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (holder != null && mDevlist != null && position < mDevlist.size()) {
			MessageDeviceDefine FavoriteItem = mDevlist.get(position);
			convertView.setBackgroundColor(Color.TRANSPARENT);
			holder.ViewData(FavoriteItem, position);

			if (FavoriteItem.mOperType == 1) {
				// do nothing...
			}
		}
		return convertView;
	}
	
}
