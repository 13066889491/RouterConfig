package com.twowing.routeconfig.wifi;

import java.lang.ref.WeakReference;

import snmp.routeritv.commservice.IAidlRouterCommService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.constant.RouterConstant;
import com.twowing.routeconfig.dialogview.SettingLoadingDialog;
import com.twowing.routeconfig.utils.CountDownTimerUtil;
import com.twowing.routeconfig.wifi.adapter.WifiAInfodapter;
import com.twowing.routeconfig.wifi.entity.WifiListInfoBean;

public class WifiListInfoView extends RelativeLayout {
	private static final String TAG = "WifiListInfoView";
	private ListView mListView;
	private WifiAInfodapter mWifiAInfodapter;
	private Context mContext;
	private Handler mHandler = new MyHandler(this);
	private static final int MSG_SHOW_LOADING_DIALOG = 0x1001;
	private static final int MSG_GET_WIFI_LIST = 0x1002;
	private SettingLoadingDialog mLoadingDialog;
	private IAidlRouterCommService mRouterCommService;
	private CountDownTimerUtil mCountDownTimerUtil;

	private static class MyHandler extends Handler {
		private WeakReference<WifiListInfoView> mMain;

		public MyHandler(WifiListInfoView view) {
			mMain = new WeakReference<WifiListInfoView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			WifiListInfoView view = mMain.get();
			if (view == null) {
				return;
			}
			switch (msg.what) {
			case MSG_SHOW_LOADING_DIALOG:
				view.showLoadingDialog();
				break;
			case MSG_GET_WIFI_LIST:
				view.getWifiListCountDownTimer();
				break;
			default:
				break;
			}
		}
	}

	public WifiListInfoView(Context context) {
		super(context);
	}

	public WifiListInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WifiListInfoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mListView = (ListView) findViewById(R.id.list_view);
		if (mListView == null) {
			throw new InflateException("Miss a child?");
		}
		mContext = getContext();
		initView();
	}

	public void getRouterServiceListener(IAidlRouterCommService l) {
		this.mRouterCommService = l;
		Log.e(TAG, "setRouterServiceListener  :: mRouterCommService="
				+ mRouterCommService);
	}

	private void initView() {
		mListView.setFocusable(false);
		mListView.setVisibility(View.VISIBLE);
		mWifiAInfodapter = new WifiAInfodapter(getContext());
		// mWifiAInfodapter.addAll(getData());
		// mListView.setAdapter(mWifiAInfodapter);
	}

	private void showInitRouterServiceError() {
		Log.e(TAG, "showInitRouterServiceError :: mRouterCommService="
				+ mRouterCommService);
		if (mRouterCommService == null) {
			Toast.makeText(getContext().getApplicationContext(),
					"服务器初始化异常，请检查设备。", 0).show();
			return;
		}
	}

	public void getWifiListInfo(boolean flag) {
		showInitRouterServiceError();
		Log.e(TAG, "getWifiListInfo :: flag=" + flag);
		if (flag && mRouterCommService != null) {
			if(mWifiAInfodapter!=null){
				mWifiAInfodapter.clear();
			}
			mHandler.sendEmptyMessage(MSG_SHOW_LOADING_DIALOG);
			getWifiClients();
		} else {
			finishLoadingDialog();
			finishTimerCancel();
		}
	}

	private void finishTimerCancel() {
		if (mCountDownTimerUtil != null) {
			mCountDownTimerUtil.cancel();
			mCountDownTimerUtil = null;
		}
	}

	private void showLoadingDialog() {
		Log.e(TAG, "showLoadingDialog :......");
		finishLoadingDialog();
		mLoadingDialog = new SettingLoadingDialog(getContext(),
				R.style.wdDialog);
		mLoadingDialog.show();
		mLoadingDialog.setLoadingInfo("正在获取已连接无线网络列表,请稍等...");
		mLoadingDialog.upDataCompletedLoading(false);
	}

	private void finishLoadingDialog() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}

	private void setLoadingTitle(String st, boolean flag) {
		if (mLoadingDialog != null) {
			mLoadingDialog.setLoadingInfo(st);
		}
		setLoadingImgVisibility(flag);
	}

	private void setLoadingImgVisibility(boolean flag) {
		if (mLoadingDialog != null) {
			mLoadingDialog.upDataCompletedLoading(flag);
		}
	}

	private void getWifiClients() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mRouterCommService != null) {
					try {
						// int lineIndex = 1; // 传入获取哪个ssid 得wifi客户端参数
						mRouterCommService.getWifiClients(1);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getContext().getApplicationContext(),
							"服务器异常，请检查设备。", 0).show();
				}
			}
		}).start();
		mHandler.sendEmptyMessage(MSG_GET_WIFI_LIST);
	}

	private void getWifiListCountDownTimer() {
		mCountDownTimerUtil = new CountDownTimerUtil(10000, 1000) {
			public void onTick(long millisUntilFinished) {
				setLoadingTitle("无线网络列表获取中,请稍等...!", false);
			}

			public void onFinish() {
				setLoadingTitle("无线网络列表获取失败,路由端响应超时!", true);
				finishTimerCancel();
			}
		}.start();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		registerInternetReceiver();
	}

	private void registerInternetReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(RouterConstant.ROUTER_WIFIAP_CHANGE_ACTION);
		if (mContext != null) {
			mContext.registerReceiver(mInternetReceiver, filter);
		}
	}

	private void unRegisterInternetReceiver() {
		if (mContext != null && mInternetReceiver != null) {
			mContext.unregisterReceiver(mInternetReceiver);
		}
	}

	private BroadcastReceiver mInternetReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			String action = intent.getAction();
			Log.e(TAG, "mInternetReceiver :: action=" + action);
			if (TextUtils.isEmpty(action)) {
				return;
			}
			if (action == RouterConstant.ROUTER_WIFIAP_CHANGE_ACTION) {
				finishTimerCancel();
				String wifiClientName = intent
						.getStringExtra(RouterConstant.ROUTER_WIFIAP_CHANGE_ACTION_KEY);
				Log.e(TAG, "onReceive :: wifiClientName=" + wifiClientName);
				if (TextUtils.isEmpty(wifiClientName)) {
					setLoadingTitle("无线网络列表获取失败,路由端响应超时!", true);
				} else {
					// List<WifiListInfoBean> list = new
					// ArrayList<WifiListInfoBean>();
					String[] wifiClient = wifiClientName.split(";");
					Log.e(TAG, "onReceive :: wifiClient.length="
							+ wifiClient.length);
					for (int i = 0; i < wifiClient.length; i++) {
						String[] wifiname = wifiClient[i].split(",");
						Log.e(TAG, "onReceive :: wifiClient[=" + i
								+ ", wifiClient=" + wifiClient[i] + ",]");
						Log.e(TAG, "onReceive :: wifiname.length="
								+ wifiname.length + ",wifiname[=" + i
								+ ", wifiname=" + wifiname[i] + ",]");
						WifiListInfoBean mWifiListInfoBean = new WifiListInfoBean(
								String.valueOf(i), wifiname[0].toString(),
								wifiname[1].toString(), wifiname[2].toString());
						mWifiAInfodapter.add(mWifiListInfoBean);
					}
					mListView.setAdapter(mWifiAInfodapter);
					mWifiAInfodapter.notifyDataSetChanged();
					finishLoadingDialog();
				}
			}
		}
	};

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		finishLoadingDialog();
		finishTimerCancel();
		unRegisterInternetReceiver();
	}
}
