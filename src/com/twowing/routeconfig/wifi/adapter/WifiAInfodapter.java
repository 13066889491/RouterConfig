package com.twowing.routeconfig.wifi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.wifi.entity.WifiListInfoBean;

public class WifiAInfodapter extends ArrayAdapter<WifiListInfoBean> {

	private LayoutInflater mInflater;
	private Context mContext;

	public WifiAInfodapter(Context context) {
		super(context, 0);
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WifiListInfoBean info = getItem(position);
		convertView = mInflater.inflate(R.layout.wifi_list_item_view, parent,
				false);
		TextView listNumber = (TextView) convertView
				.findViewById(R.id.wifi_list_number);
		TextView deviceName = (TextView) convertView
				.findViewById(R.id.wifi_device_name);
		TextView macAddress = (TextView) convertView
				.findViewById(R.id.wifi_mac_address);
		TextView ipAddress = (TextView) convertView
				.findViewById(R.id.wifi_ip_address);
		listNumber.setText(info.getNumber());
		deviceName.setText(info.getDeviceName());
		macAddress.setText(info.getMacAddress());
		ipAddress.setText(info.getIpAddress());
		return convertView;
	}
}
