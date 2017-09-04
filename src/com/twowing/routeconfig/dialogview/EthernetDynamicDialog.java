package com.twowing.routeconfig.dialogview;

import java.util.HashMap;
import java.util.Map;

import snmp.routeritv.commservice.IAidlRouterCommService;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.utils.CountDownTimerUtil;
import com.twowing.routeconfig.view.IpEditView;

public class EthernetDynamicDialog extends Dialog implements
		android.view.View.OnClickListener {
	private static final String TAG = "EthernetDynamicView";
	private Button mConfirmBtn;
	private IpEditView mIpaddrEd;
	private IpEditView mSubnetmaskEd;
	private IpEditView mGatewayEd;
	private IpEditView mDns1Ed;
	private IpEditView mDns2Ed;
	private Button mCancelBtn;
	private final String data[] = new String[] { "0", "0", "0", "0" };
	private IAidlRouterCommService mRouterCommService;
	public static Map<String, String> valMap = new HashMap<String, String>();
	private TextView mNetWorktitle;
	public DHCPStateListener mDHCPStateListener;

	public interface DHCPStateListener {
		void showDHCPStatus();
	}

	public void connectDHCPState(DHCPStateListener listener) {
		if (listener != null) {
			mDHCPStateListener = listener;
		}
	}

	public EthernetDynamicDialog(Context context, int stely,
			IAidlRouterCommService routerService) {
		super(context, stely);
		Log.e(TAG, "EthernetDynamicDialog :: context=" + context);
		mRouterCommService = routerService;
		Log.e(TAG, "EthernetDynamicDialog :: mRouterCommService="
				+ mRouterCommService);
	}

	public EthernetDynamicDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate :: savedInstanceState=" + savedInstanceState);
		setContentView(R.layout.ethernet_static_ip);
		initView();
		initData();
		setListener();
	}

	private void setListener() {
		mConfirmBtn
				.setOnClickListener((android.view.View.OnClickListener) this);
		mCancelBtn.setOnClickListener((android.view.View.OnClickListener) this);
	}

	private void initView() {
		mNetWorktitle = (TextView) findViewById(R.id.network_settings_title);
		mCancelBtn = (Button) findViewById(R.id.btn_staticip_cancel);
		mConfirmBtn = (Button) findViewById(R.id.btn_ip_confirm);
		mIpaddrEd = (IpEditView) findViewById(R.id.iptxt_ipaddress);
		mSubnetmaskEd = (IpEditView) findViewById(R.id.iptxt_subnetmask);
		mGatewayEd = (IpEditView) findViewById(R.id.iptxt_gateway);
		mDns1Ed = (IpEditView) findViewById(R.id.iptxt_dns1);
		mDns2Ed = (IpEditView) findViewById(R.id.iptxt_dns2);
	View mIpAddress =	findViewById(R.id.iptxt_ipaddress_view);
	View mSubnetMask =	findViewById(R.id.iptxt_subnetmask_view);
	View mGateway=	findViewById(R.id.iptxt_gateway_view);
	View mDns1View =	findViewById(R.id.iptxt_dns1_view);
	View mDns2View =	findViewById(R.id.iptxt_dns2_view);
	mIpAddress.setVisibility(View.GONE);
	mSubnetMask.setVisibility(View.GONE);
	mGateway.setVisibility(View.GONE);
	mDns1View.setVisibility(View.GONE);
	mDns2View.setVisibility(View.GONE);
	
	}

	private void initData() {
		mNetWorktitle.setText("动态IP上网");
		mIpaddrEd.setSuperEdittextValue(data);
		mSubnetmaskEd.setSuperEdittextValue(data);
		mGatewayEd.setSuperEdittextValue(data);
		mDns1Ed.setSuperEdittextValue(data);
		mDns2Ed.setSuperEdittextValue(data);
		setEditextEnabled(mIpaddrEd);
		setEditextEnabled(mSubnetmaskEd);
		setEditextEnabled(mGatewayEd);
		setEditextEnabled(mDns1Ed);
		setEditextEnabled(mDns2Ed);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	public void onClick(View v) {
		if (mRouterCommService == null) {
			Log.e(TAG, "onClick :: mRouterCommService=" + mRouterCommService);
			Toast.makeText(getContext(), "服务器异常，请检查设备!", 0).show();
			dismiss();
			return;
		}
		switch (v.getId()) {
		case R.id.btn_ip_confirm:
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (mRouterCommService != null) {
						int wanIndex = 1; // wanIndex 只第几个wan口 ， 默认设置1,
											// 1指internet上网方式
						Map map = new HashMap();
						try {
							Log.d(TAG,
									"send wan dynamic internet info to service");
							mRouterCommService.setWanDynamicInternet(wanIndex,
									map);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					} else {

					}
				}
			}).start();
			if (mDHCPStateListener != null) {
				mDHCPStateListener.showDHCPStatus();
			}
			break;
		default:
			break;
		}
		dismiss();
	}

	private void setEditextEnabled(IpEditView editView) {
		for (int i = 0; i < editView.getChildCount(); i++) {
			EditText edit = (EditText) editView.getChildAt(i);
			edit.setEnabled(false);
			edit.setFocusable(false);
			edit.setFocusableInTouchMode(false);
			edit.setCursorVisible(false);
		}
	}
}