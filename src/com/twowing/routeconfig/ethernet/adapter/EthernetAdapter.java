package com.twowing.routeconfig.ethernet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.ethernet.entity.EthernetListBean;

public class EthernetAdapter extends ArrayAdapter<EthernetListBean> {
	private static final String TAG = EthernetAdapter.class.getSimpleName();
	public static final int VIEWTYPE_NOMAL = 1;
	public static final int VIEWTYPE_CHECKBOX = 2;
	private LayoutInflater mInflater;
	private Context mContext;

	public EthernetAdapter(Context context) {
		super(context, 0);
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		EthernetListBean info = getItem(position);
		return info.viewType;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EthernetListBean info = getItem(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			switch (info.viewType) {
			case VIEWTYPE_NOMAL:
				convertView = mInflater.inflate(R.layout.ethernet_item_view,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.ethernet_title);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.ethernet_img);
				viewHolder.imageView.setVisibility(View.VISIBLE);
				convertView.setTag(viewHolder);
				break;
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(info.titleId);
		return convertView;
	}

	private static class ViewHolder {
		TextView title;
		ImageView imageView;
		CheckBox checkBox;
	}
}
