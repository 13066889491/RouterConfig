package com.twowing.routeconfig.dialogview;

import java.util.HashMap;
import java.util.Map;

import snmp.routeritv.commservice.IAidlRouterCommService;
import snmp.routeritv.commservice.util.Constant;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.view.IpEditView;

public class EthernetStaticIpDialog extends Dialog implements
		android.view.View.OnClickListener {
	private static final String TAG = "EthernetStaticIpView";
	private Button mConfirmBtn;
	private IpEditView mIpaddrEd;
	private IpEditView mSubnetmaskEd;
	private IpEditView mGatewayEd;
	private IpEditView mDns1Ed;
	private IpEditView mDns2Ed;
	private Button mCancelBtn;
	private IAidlRouterCommService mRouterCommService;
	public static Map<String, String> valMap = new HashMap<String, String>();

	public EthernetStaticIpDialog(Context context, int stely,
			IAidlRouterCommService routerService) {
		super(context, stely);
		mRouterCommService = routerService;
	}

	public EthernetStaticIpDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ethernet_static_ip);
		initView();
		setListener();
	}

	private void setListener() {
		mCancelBtn.setOnClickListener((android.view.View.OnClickListener) this);
		mConfirmBtn
				.setOnClickListener((android.view.View.OnClickListener) this);
	}

	private void initView() {
		mCancelBtn = (Button) findViewById(R.id.btn_staticip_cancel);
		mConfirmBtn = (Button) findViewById(R.id.btn_ip_confirm);
		mIpaddrEd = (IpEditView) findViewById(R.id.iptxt_ipaddress);
		mSubnetmaskEd = (IpEditView) findViewById(R.id.iptxt_subnetmask);
		mGatewayEd = (IpEditView) findViewById(R.id.iptxt_gateway);
		mDns1Ed = (IpEditView) findViewById(R.id.iptxt_dns1);
		mDns2Ed = (IpEditView) findViewById(R.id.iptxt_dns2);
	}

	@Override
	public void onClick(View v) {

		if (mRouterCommService == null) {
			Log.e(TAG, "onClick :: mRouterCommService=" + mRouterCommService);
			Toast.makeText(getContext(), "服务器异常，请检查设备。", 0).show();
			dismiss();
			return;
		}
		switch (v.getId()) {
		case R.id.btn_ip_confirm:
			valMap.clear();
			float ipaddrEdSize = mIpaddrEd.getTextSize();
			float subnetmaskEdSize = mIpaddrEd.getTextSize();
			float gatewayEdSize = mIpaddrEd.getTextSize();
			float dns1EdSize = mIpaddrEd.getTextSize();
			float dns2EdSize = mIpaddrEd.getTextSize();
			Log.e(TAG, "onClick :: ipaddrEdSize=" + ipaddrEdSize
					+ ", subnetmaskEdSize=" + subnetmaskEdSize);
			Log.e(TAG, "onClick :: gatewayEdSize=" + gatewayEdSize
					+ ", dns1EdSize=" + dns1EdSize + ", dns2EdSize="
					+ dns2EdSize);

			new Thread(new Runnable() {
				@Override
				public void run() {
					if (mRouterCommService != null) {
						int wanIndex = 1; // wanIndex 只第几个wan口 ， 默认设置1,
											// 1指internet上网方式
						Map<Object, Object> map = new HashMap<Object, Object>();
						map.put(Constant.key.wan_PRIMARYDNS,
								mDns1Ed.getFormattedIp());
						map.put(Constant.key.wan_SECONDARYDNS,
								mDns2Ed.getFormattedIp());
						map.put(Constant.key.wan_PORTBIND, 1);
						map.put(Constant.key.wan_IPADDRESS,
								mIpaddrEd.getFormattedIp());
						map.put(Constant.key.wan_SUBNETMASK,
								mSubnetmaskEd.getFormattedIp());
						map.put(Constant.key.wan_GATEWAY,
								mGatewayEd.getFormattedIp());
						try {
							mRouterCommService
									.setWanStaticIpInfo(wanIndex, map);
						} catch (RemoteException e) {
							e.printStackTrace();
							Log.d(TAG,
									"send wan static internet info to service");
						}
					}
				}
			}).start();
			if(mStaticStateListener!=null){
				mStaticStateListener.showStaticStatus();
			}
			break;
		default:
			break;
		}
		dismiss();
	}
	
	public StaticStateListener mStaticStateListener;

	public interface StaticStateListener {
		void showStaticStatus();
	}


	public void connectStaticState(StaticStateListener listener) {
		if (listener != null) {
			mStaticStateListener = listener;
		}
	}
}