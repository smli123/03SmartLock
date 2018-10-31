package com.sherman.smartlockex.ui.message;

import java.util.ArrayList;

import com.sherman.smartlockex.R;
import com.sherman.smartlockex.ui.common.MessageDeviceDefine;
import com.sherman.smartlockex.ui.common.PubFunc;

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
		public TextView tv_message_no;
		public TextView tv_message_time;
		public TextView tv_message_devicename;
		public ImageView iv_message_type;
		public TextView tv_message_data;
		public TextView tv_message_usertype;
		public TextView tv_message_detail;

		private void setImageRes(ImageView view, int resId) {
			view.setImageResource(resId);
		}

		public void ViewData(MessageDeviceDefine device, int position) {
			if (device != null) {
				tv_message_data.setSingleLine(true);

				if (!TextUtils.isEmpty(device.mMessageID)) {
					tv_message_no.setText(device.mMessageID);
					tv_message_time.setText(device.mMessageTime);
					tv_message_devicename.setText(device.mDeviceName);

					iv_message_type.setImageResource(R.drawable.smp_message_type);
					String str_data = PubFunc.getStringMessageData(device.mMessageData);
					tv_message_data.setText(str_data);
					str_data = PubFunc.getStringUserType(device.mUserType);
					tv_message_usertype.setText(str_data);

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

			holder.tv_message_no = (TextView) convertView
					.findViewById(R.id.tv_message_no);
			holder.tv_message_time = (TextView) convertView
					.findViewById(R.id.tv_message_time);
			holder.tv_message_devicename = (TextView) convertView
					.findViewById(R.id.tv_message_devicename);
			holder.iv_message_type = (ImageView) convertView
					.findViewById(R.id.iv_message_type);
			holder.tv_message_data = (TextView) convertView
					.findViewById(R.id.tv_message_data);
			holder.tv_message_usertype = (TextView) convertView
					.findViewById(R.id.tv_message_usertype);
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

			if (FavoriteItem.mMessageType == 1) {
				// do nothing...
			}
		}
		return convertView;
	}
	
}
