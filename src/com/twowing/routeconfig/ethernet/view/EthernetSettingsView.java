package com.twowing.routeconfig.ethernet.view;

import java.util.ArrayList;
import java.util.List;

import snmp.routeritv.commservice.IAidlRouterCommService;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.callback.RouterCommCallback;
import com.twowing.routeconfig.ethernet.adapter.EthernetAdapter;
import com.twowing.routeconfig.ethernet.entity.EthernetListBean;
import com.twowing.routeconfig.manager.EthernetManager;

public class EthernetSettingsView extends RelativeLayout {
	private static final String TAG = "EthernetView";
	private ListView mEthernetListView;
	private EthernetAdapter mAdapter;

	private EthernetManager mEthernetManager;

	public EthernetSettingsView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public EthernetSettingsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EthernetSettingsView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mEthernetListView = (ListView) findViewById(R.id.ethernet_listView);
		if (mEthernetListView == null) {
			throw new InflateException("Miss a child?");
		}
		initData();
	}

	public void initEthernetManager(IAidlRouterCommService routerService) {
		Log.e(TAG, "setRouterServiceListener  :: routerService="
				+ routerService);
		mEthernetManager = new EthernetManager(getContext(), routerService);
	}

	private void initData() {
		mEthernetListView.setDivider(null);
		mAdapter = new EthernetAdapter(getContext());
		mAdapter.addAll(getDatas());
		mEthernetListView.setAdapter(mAdapter);
		mEthernetListView.setOnItemClickListener(mOnItemClickListener);
	}

	private List<EthernetListBean> getDatas() {
		List<EthernetListBean> list = new ArrayList<EthernetListBean>();
		EthernetListBean bean;
		// 0.
		bean = new EthernetListBean();
		bean.titleId = R.string.pppoe_title;
		bean.iconId = R.drawable.base_radio_normal;
		bean.position = list.size();
		bean.viewType = EthernetAdapter.VIEWTYPE_NOMAL;
		list.add(bean);
		// 1.
		bean = new EthernetListBean();
		bean.titleId = R.string.dynamic_ip_title;
		bean.iconId = R.drawable.base_radio_normal;
		bean.position = list.size();
		bean.viewType = EthernetAdapter.VIEWTYPE_NOMAL;
		list.add(bean);
		// 2.
		bean = new EthernetListBean();
		bean.titleId = R.string.static_ip_title;
		bean.iconId = R.drawable.base_radio_normal;
		bean.position = list.size();
		bean.viewType = EthernetAdapter.VIEWTYPE_NOMAL;
		list.add(bean);
		return list;
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, "mOnItemClickListener ::position:" + position);
			EthernetListBean bean = mAdapter.getItem(position);
			if (bean != null && mEthernetManager != null) {
				if (bean.titleId == R.string.pppoe_title) {
					mEthernetManager.showEthernetPPPoEDialog();
				} else if (bean.titleId == R.string.dynamic_ip_title) {
					mEthernetManager.showEthernetDynamicDialog();
				} else if (bean.titleId == R.string.static_ip_title) {
					mEthernetManager.showEthernetStaticIpView();
				}
			}
		}
	};
	
	public  void connectionServiceFail(){
		if(mEthernetManager!=null){
			mEthernetManager.connectionRouterServiceFail();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mEthernetManager != null) {
			mEthernetManager.onDestory();
		}
	}
}