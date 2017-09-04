package com.twowing.routeconfig.ethernet.view;

import java.lang.ref.WeakReference;

import snmp.routeritv.commservice.IAidlRouterCommService;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.callback.RouterCommCallback;
import com.twowing.routeconfig.callback.RouterCommCallback.RouterInternetInfoListener;
import com.twowing.routeconfig.utils.SharePrefsUtils;

public class EthernetConnectInfo extends LinearLayout {
	private static final String TAG = "EthernetConnectInfo";
	private TextView mConnectTypeTv;
	private TextView mConnectIpTv;
	private TextView mSubnetMaskTv;
	private TextView mDefaultGatewayTv;
	private TextView mPrimaryDnsTv;
	private TextView mSecondDnsTv;
	private Context mContext;
	private String mIpAddress;
	private String mSubnetMask;
	private String mDefaultGateway;
	private String mPrimaryDns;
	private String mSecondDns;
	private IAidlRouterCommService mRouterCommService;
	private Handler mHandler = new MyHandler(this);
	private static final int MSG_UPDATA_INTERNET_INFO = 0x1000;
	private static final int MSG_GET_NETWORK_MODE = 0x1001;
	private static final int MSG_SET_DISCONNECT_INFO = 0x1002;

	private static final int MSG_DHCP_INTERNET_INFO = 0x1003;
	private static final int MSG_PPPOE_INTERNET_INFO = 0x1004;
	private static final int MSG_STATIC_INTERNET_INFO = 0x1005;
	private RouterCommCallback mRouterCommCallback;

	private static class MyHandler extends Handler {
		private WeakReference<EthernetConnectInfo> mMain;

		public MyHandler(EthernetConnectInfo view) {
			mMain = new WeakReference<EthernetConnectInfo>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			EthernetConnectInfo view = mMain.get();
			if (view == null) {
				return;
			}
			switch (msg.what) {
			case MSG_UPDATA_INTERNET_INFO:
				view.upDataRouterInfo();
				break;
			case MSG_GET_NETWORK_MODE:
				view.getNetWorkMode();
				break;
			case MSG_DHCP_INTERNET_INFO:
				view.getDhcpInternetInfo();
				break;
			case MSG_PPPOE_INTERNET_INFO:
				view.getPPPOEInternetInfo();
				break;
			case MSG_SET_DISCONNECT_INFO:
				view.setRouterDisConnectInfo();
				break;
			case MSG_STATIC_INTERNET_INFO:
				view.getStaticInternetInfo();
				break;
			default:
				break;
			}
		}
	}

	private void upDataRouterInfo() {
		if (mIpAddress != null || mSubnetMask != null
				|| mDefaultGateway != null || mPrimaryDns != null
				|| mSecondDns != null) {
			mConnectTypeTv.setText("Internet 连接方式："
					+ SharePrefsUtils.getRouterInternetMode(mContext));
			mConnectIpTv.setText("IP 地址： " + mIpAddress);
			mSubnetMaskTv.setText("子网掩码： " + mSubnetMask);
			mDefaultGatewayTv.setText("默认网关：  " + mDefaultGateway);
			mPrimaryDnsTv.setText("主 DNS： " + mPrimaryDns);
			mSecondDnsTv.setText("从 DNS：  " + mSecondDns);
		}else{
			mHandler.sendEmptyMessageDelayed(MSG_SET_DISCONNECT_INFO, 1000);
		}
	}

	private void setRouterDisConnectInfo() {
		mConnectTypeTv.setText("");
		mConnectIpTv.setText("");
		mSubnetMaskTv.setText("");
		mDefaultGatewayTv.setText("");
		mPrimaryDnsTv.setText("");
		mSecondDnsTv.setText("");
	}

	private void getDhcpInternetInfo() {
		mHandler.sendEmptyMessageDelayed(MSG_UPDATA_INTERNET_INFO, 4000);
		mHandler.sendEmptyMessageDelayed(MSG_DHCP_INTERNET_INFO, 5000);
	}

	private int mRouterPPPoECount = 0;
	private int mRouterStaticCount = 0;

	private void getPPPOEInternetInfo() {
		mRouterPPPoECount++;
		if (mRouterPPPoECount > 8) {
			return;
		}
		mHandler.sendEmptyMessageDelayed(MSG_UPDATA_INTERNET_INFO, 2000);
		mHandler.sendEmptyMessageDelayed(MSG_PPPOE_INTERNET_INFO, 5000);
	}

	private void getStaticInternetInfo() {
		mRouterStaticCount++;
		if (mRouterStaticCount > 8) {
			return;
		}
		mHandler.sendEmptyMessageDelayed(MSG_UPDATA_INTERNET_INFO, 4000);
		mHandler.sendEmptyMessageDelayed(MSG_STATIC_INTERNET_INFO, 5000);
	}

	private void getNetWorkMode() {
		String netWorkMode = SharePrefsUtils.getRouterNetWorkMode(mContext);
		Log.e(TAG, "initData:: netWorkMode=" + netWorkMode);
		mHandler.sendEmptyMessageDelayed(MSG_GET_NETWORK_MODE, 5000);
		if (!TextUtils.isEmpty(netWorkMode)) {
			if (netWorkMode.equals("200")) {
			String	internetMode = SharePrefsUtils.getRouterInternetMode(mContext);
			Log.e(TAG, "getNetWorkMode :: mInternetMode="+mInternetMode);
			Log.e(TAG, "initData:: internetMode=" + internetMode);
				if(!internetMode.equals(mInternetMode)){
					mInternetMode=internetMode;
					showDisConnectInfo();
					finishInternetInfo();
				}else{
					if (!TextUtils.isEmpty(internetMode)) {
						if (internetMode.equalsIgnoreCase("DHCP")) {
							mHandler.sendEmptyMessageDelayed(
									MSG_DHCP_INTERNET_INFO, 5000);
						} else if (internetMode.equalsIgnoreCase("PPPOE")) {
							mHandler.sendEmptyMessageDelayed(
									MSG_PPPOE_INTERNET_INFO, 5000);
						} else if (internetMode.equalsIgnoreCase("STATIC")) {
							mHandler.sendEmptyMessageDelayed(
									MSG_STATIC_INTERNET_INFO, 5000);
						}
					}
				}
			} else {
				showDisConnectInfo();
				finishInternetInfo();
			}
		} else {
			mRouterPPPoECount = 0;
			mRouterStaticCount = 0;
		}
	}

	private void showDisConnectInfo() {
		mRouterPPPoECount = 0;
		mRouterStaticCount = 0;
		mHandler.removeMessages(MSG_DHCP_INTERNET_INFO);
		mHandler.removeMessages(MSG_STATIC_INTERNET_INFO);
		mHandler.removeMessages(MSG_PPPOE_INTERNET_INFO);
		mHandler.removeMessages(MSG_UPDATA_INTERNET_INFO);
		mHandler.sendEmptyMessageDelayed(MSG_SET_DISCONNECT_INFO, 1000);
	}

	private void finishInternetInfo() {
		mIpAddress=null;
		mSubnetMask=null;
		mDefaultGateway=null;
		mPrimaryDns=null;
		mSecondDns=null;
	}

	public void getRouterServiceListener(IAidlRouterCommService l) {
		this.mRouterCommService = l;
		Log.e(TAG, "setRouterServiceListener  :: mRouterCommService="
				+ mRouterCommService);
	}

	public void getRouterCommCallback(RouterCommCallback routerCommCallback) {
		this.mRouterCommCallback = routerCommCallback;
		Log.e(TAG, "getRouterCommCallback  :: routerCommCallback="
				+ routerCommCallback);
		mRouterCommCallback.setRouterInternetListener(mRouterInfoListener);
	}

	private RouterInternetInfoListener mRouterInfoListener = new RouterInternetInfoListener() {

		@Override
		public void getRouterIpAddress(String ipAddress) {
			if (TextUtils.isEmpty(ipAddress)) {
				ipAddress = "";
			}
			mIpAddress = ipAddress;
		}

		@Override
		public void getRouterSubnetMask(String subnetMask) {
			Log.e(TAG, "mRouterInfoListener :: subnetMask=" + subnetMask);
			if (TextUtils.isEmpty(subnetMask)) {
				subnetMask = "";
			}
			mSubnetMask = subnetMask;
		}

		@Override
		public void getRouterDefaultGateway(String defaultGateway) {
			Log.e(TAG, "mRouterInfoListener :: defaultGateway="
					+ defaultGateway);
			if (TextUtils.isEmpty(defaultGateway)) {
				defaultGateway = "";
			}
			mDefaultGateway = defaultGateway;
		}

		@Override
		public void getRouterPrimaryDns(String primaryDns) {
			Log.e(TAG, "mRouterInfoListener :: primaryDns=" + primaryDns);
			if (TextUtils.isEmpty(primaryDns)) {
				primaryDns = "";
			}
			mPrimaryDns = primaryDns;
		}

		@Override
		public void getRouterSecondDns(String secondDns) {
			Log.e(TAG, "mRouterInfoListener :: secondDns=" + secondDns);
			if (TextUtils.isEmpty(secondDns)) {
				secondDns = "";
			}
			mSecondDns = secondDns;
		}
	};
	private String mInternetMode ;

	public EthernetConnectInfo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EthernetConnectInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EthernetConnectInfo(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mConnectTypeTv = (TextView) findViewById(R.id.ethernet_connect_type);
		mConnectIpTv = (TextView) findViewById(R.id.ethernet_connect_ip);
		mSubnetMaskTv = (TextView) findViewById(R.id.ethernet_subnet_mask);
		mDefaultGatewayTv = (TextView) findViewById(R.id.ethernet_default_gateway);
		mPrimaryDnsTv = (TextView) findViewById(R.id.ethernet_primary_dns);
		mSecondDnsTv = (TextView) findViewById(R.id.ethernet_second_dns);
		if (mConnectTypeTv == null || mConnectIpTv == null
				|| mSubnetMaskTv == null || mDefaultGatewayTv == null
				|| mPrimaryDnsTv == null || mSecondDnsTv == null) {
			throw new InflateException("Miss a child?");
		}
		mContext = getContext();
		initData();
	}

	private void initData() {
		mHandler.sendEmptyMessageDelayed(MSG_GET_NETWORK_MODE, 5000);
	}
}
