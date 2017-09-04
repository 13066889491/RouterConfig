package com.twowing.routeconfig;

import java.lang.ref.WeakReference;

import snmp.routeritv.commservice.IAidlRouterCommService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.twowing.routeconfig.callback.RouterCommCallback;
import com.twowing.routeconfig.ethernet.view.EthernetConnectInfo;
import com.twowing.routeconfig.ethernet.view.EthernetSettingsView;
import com.twowing.routeconfig.utils.SharePrefsUtils;
import com.twowing.routeconfig.utils.ToastUtils;
import com.twowing.routeconfig.wifi.WifiListInfoView;
import com.twowing.routeconfig.wireless.WirelessSettingsView;

public class RouteConfigActivity extends Activity {
	private static final String TAG = "RouteConfigActivity";

	private static final int MSG_SHOW_EHERNET_SETTINGS_VIEW = 0x1001;
	private static final int MSG_SET_RADIOBUTTON_BACKGROUND = 0x1002;
	private static final int MSG_SELECT_RADIOBUTTON_BACKGROUND = 0x1003;

	private static final int ETHERNET_RADIOBUTTON = 0;
	private static final int WIRELESS_RADIOBUTTON = 1;
	private static final int CLIENT_LIST_RADIOBUTTON = 2;

	private static final int SHOW_ETHERNET_SETTINGS_VIEW = 0;
	private static final int SHOW_WIRELESS_SETTINGS_VIEW = 1;
	private static final int SHOW_CLIENT_LIST_VIEW = 2;
	private static final String SERVICE_IROUTERADMIN = "snmp.routeritv.commservice.ROUTER_COMM_SERVICE";

	private Handler mHandler = new MyHandler(this);

	private EthernetSettingsView mEthernetView;
	private WirelessSettingsView mWirelessView;
	private WifiListInfoView mWifiListView;

	private RadioGroup mRountSettings;
	private RadioButton mEthernetSettings;
	private RadioButton mWirelessSettings;
	private RadioButton mClientSettings;

	private IAidlRouterCommService mRouterCommService;
	private RouterCommServiceConnection mRouterConnection;
	private RouterCommCallback mRouterCommCallback;

	private long mExitTime = 0;
	private boolean isServiceBind;

	private static class MyHandler extends Handler {
		private WeakReference<RouteConfigActivity> mMain;

		public MyHandler(RouteConfigActivity view) {
			mMain = new WeakReference<RouteConfigActivity>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			RouteConfigActivity view = mMain.get();
			if (view == null) {
				return;
			}
			switch (msg.what) {
			case MSG_SHOW_EHERNET_SETTINGS_VIEW:
				view.showEthernetSettingsView((Integer) msg.obj);
				break;
			case MSG_SET_RADIOBUTTON_BACKGROUND:
				view.showRadioButtonBackground(msg.arg1, (Boolean) msg.obj);
				break;
			case MSG_SELECT_RADIOBUTTON_BACKGROUND:
				view.selectRadioButtonBackground((Integer) msg.obj);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		bindServer();
		initView();
		setListener();
	}

	private void bindServer() {
		Intent intent = new Intent(SERVICE_IROUTERADMIN);
		isServiceBind = bindService(intent, mRouterConnection,
				Context.BIND_AUTO_CREATE);
		Log.d(TAG, "start bindService");
	}

	private void initData() {
		mRouterCommCallback = new RouterCommCallback(RouteConfigActivity.this);
		mRouterConnection = new RouterCommServiceConnection();
		SharePrefsUtils.setRouterNetWorkMode(this, null);
		Log.e(TAG, "initData :: getRouterNetWorkMode=" + SharePrefsUtils.getRouterNetWorkMode(this));
	}

	private void initView() {
		mRountSettings = (RadioGroup) findViewById(R.id.rount_settings);
		mEthernetView = (EthernetSettingsView) findViewById(R.id.ethernet_view);
		mEthernetInfo = (EthernetConnectInfo) findViewById(R.id.ethernet_connect_info);
		mWirelessView = (WirelessSettingsView) findViewById(R.id.wireless_view);
		mWifiListView = (WifiListInfoView) findViewById(R.id.wifi_list_info_view);
		mEthernetSettings = (RadioButton) findViewById(R.id.ethernet_settings);
		mWirelessSettings = (RadioButton) findViewById(R.id.wireless_settings);
		mClientSettings = (RadioButton) findViewById(R.id.client_list);
	}

	private void setListener() {
		mRountSettings.setOnCheckedChangeListener(mCheckedChangeListener);
		mEthernetSettings.setOnFocusChangeListener(mOnFocusChangeListener);
		mWirelessSettings.setOnFocusChangeListener(mOnFocusChangeListener);
		mClientSettings.setOnFocusChangeListener(mOnFocusChangeListener);
		mEthernetSettings.setChecked(true);
		mEthernetSettings.setClickable(true);
		mEthernetSettings.setSelected(true);
	}

	@SuppressLint("NewApi")
	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int radioButtonId = group.getCheckedRadioButtonId();
			Log.e(TAG, "mCheckedChangeListener radioButtonId=" + radioButtonId
					+ ", checkedId=" + checkedId);
			if (checkedId == R.id.ethernet_settings) {
				sendShowEthernetSettingsViewMessage(
						MSG_SHOW_EHERNET_SETTINGS_VIEW, ETHERNET_RADIOBUTTON);
				sendSelectBackgroundMessage(MSG_SELECT_RADIOBUTTON_BACKGROUND,
						ETHERNET_RADIOBUTTON);
			} else if (checkedId == R.id.wireless_settings) {
				sendShowEthernetSettingsViewMessage(
						MSG_SHOW_EHERNET_SETTINGS_VIEW, WIRELESS_RADIOBUTTON);
				sendSelectBackgroundMessage(MSG_SELECT_RADIOBUTTON_BACKGROUND,
						WIRELESS_RADIOBUTTON);
			} else if (checkedId == R.id.client_list) {
				sendShowEthernetSettingsViewMessage(
						MSG_SHOW_EHERNET_SETTINGS_VIEW, CLIENT_LIST_RADIOBUTTON);
				sendSelectBackgroundMessage(MSG_SELECT_RADIOBUTTON_BACKGROUND,
						CLIENT_LIST_RADIOBUTTON);
			}
		}
	};

	@SuppressLint("NewApi")
	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()) {
			case R.id.ethernet_settings:
				if (mEthernetSettings != null && mEthernetSettings.isChecked()) {
					return;
				}
				sendCheckedBackgroundMessage(MSG_SET_RADIOBUTTON_BACKGROUND,
						ETHERNET_RADIOBUTTON, hasFocus);
				break;
			case R.id.wireless_settings:
				if (mWirelessSettings != null && mWirelessSettings.isChecked()) {
					return;
				}
				sendCheckedBackgroundMessage(MSG_SET_RADIOBUTTON_BACKGROUND,
						WIRELESS_RADIOBUTTON, hasFocus);
				break;
			case R.id.client_list:
				if (mClientSettings != null && mClientSettings.isChecked()) {
					return;
				}
				sendCheckedBackgroundMessage(MSG_SET_RADIOBUTTON_BACKGROUND,
						CLIENT_LIST_RADIOBUTTON, hasFocus);
				break;
			default:
				break;
			}
		}
	};

	private EthernetConnectInfo mEthernetInfo;

	/****************************** send Message **************************/
	private void sendShowEthernetSettingsViewMessage(int what, int radioButtonid) {
		Message msg = Message.obtain(mHandler, what, radioButtonid);
		mHandler.sendMessage(msg);
	}

	private void sendCheckedBackgroundMessage(int what, int radioButtonid,
			boolean hasFocus) {
		Message msg = Message
				.obtain(mHandler, what, radioButtonid, 0, hasFocus);
		mHandler.sendMessage(msg);
	}

	private void sendSelectBackgroundMessage(int what, int radioButtonid) {
		Message msg = Message.obtain(mHandler, what, radioButtonid);
		mHandler.sendMessage(msg);
	}

	/****************************** END **************************/

	/****************************** show View **************************/
	private void showEthernetSettingsView(int type) {
		switch (type) {
		case SHOW_ETHERNET_SETTINGS_VIEW:
			mEthernetView.setVisibility(View.VISIBLE);
			mEthernetSettings.setChecked(true);
			mWirelessView.setVisibility(View.GONE);
			mWifiListView.setVisibility(View.GONE);
			break;
		case SHOW_WIRELESS_SETTINGS_VIEW:
			mWirelessView.setVisibility(View.VISIBLE);
			mEthernetView.setVisibility(View.GONE);
			mWifiListView.setVisibility(View.GONE);
			mWifiListView.getWifiListInfo(false);
			break;
		case SHOW_CLIENT_LIST_VIEW:
			mWifiListView.setVisibility(View.VISIBLE);
			mWifiListView.getWifiListInfo(true);
			mEthernetView.setVisibility(View.GONE);
			mWirelessView.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	private void selectRadioButtonBackground(int radioButtonId) {
		switch (radioButtonId) {
		case ETHERNET_RADIOBUTTON:
			mEthernetSettings.setBackground(getResources().getDrawable(
					R.drawable.base_bg_2));
			mWirelessSettings.setBackground(getResources().getDrawable(
					R.drawable.settings_sdcard_format_p));
			mClientSettings.setBackground(getResources().getDrawable(
					R.drawable.settings_sdcard_format_p));
			break;
		case WIRELESS_RADIOBUTTON:
			mWirelessSettings.setBackground(getResources().getDrawable(
					R.drawable.base_bg_2));

			mEthernetSettings.setBackground(getResources().getDrawable(
					R.drawable.settings_sdcard_format_p));
			mClientSettings.setBackground(getResources().getDrawable(
					R.drawable.settings_sdcard_format_p));
			break;
		case CLIENT_LIST_RADIOBUTTON:
			mClientSettings.setBackground(getResources().getDrawable(
					R.drawable.base_bg_2));

			mEthernetSettings.setBackground(getResources().getDrawable(
					R.drawable.settings_sdcard_format_p));
			mWirelessSettings.setBackground(getResources().getDrawable(
					R.drawable.settings_sdcard_format_p));
			break;

		default:
			break;
		}

	}

	@SuppressLint("NewApi")
	private void showRadioButtonBackground(int radioButtonid, boolean hasFocus) {
		if (mRountSettings != null) {
			mRountSettings.getChildAt(radioButtonid).setBackground(
					hasFocus ? getResources().getDrawable(
							R.drawable.settings_sdcard_format_n)
							: getResources().getDrawable(
									R.drawable.settings_sdcard_format_p));
		}
	}

	/****************************** END **************************/

	private class RouterCommServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			Log.d(TAG, "onServiceConnected");
			mRouterCommService = IAidlRouterCommService.Stub
					.asInterface(binder);
			Log.d(TAG, "onServiceConnected :: mRouterCommService="
					+ mRouterCommService);
			if (mRouterCommService != null && mRouterCommCallback != null) {
				try {
					mRouterCommService.setListener(mRouterCommCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			setRouterServiceListener();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.e(TAG, "onServiceDisconnected :: mEthernetView="+mEthernetView);
			if (mEthernetView != null) {
				mEthernetView.connectionServiceFail();
			}
			unRouterCommService();
		}
	}

	private void setRouterServiceListener() {
		if (mRouterCommService != null) {
			mEthernetView.initEthernetManager(mRouterCommService);
			mWirelessView.getRouterServiceListener(mRouterCommService);
			mWifiListView.getRouterServiceListener(mRouterCommService);
			mEthernetInfo.getRouterServiceListener(mRouterCommService);
			if(mRouterCommCallback!=null){
				mEthernetInfo.getRouterCommCallback(mRouterCommCallback);
			}
		} else {
			ToastUtils.showToastText(getApplicationContext(), "路由服务器异常，请检查!", Toast.LENGTH_SHORT);
		}
	}

	private void unRouterCommService() {
		Log.e(TAG, "unRouterCommService :: mRouterCommService="
				+ mRouterCommService);
		if (mRouterCommService != null) {
			try {
				mRouterCommService.setListener(null);
			} catch (RemoteException e) {
				Log.e(TAG, "onDestroy :: e=" + e);
				e.printStackTrace();
			}
			mRouterCommService = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy :: ");
		unBindRouteService();
		unRouterCommService();
		removeCallbacksMessages();
	}

	private void unBindRouteService() {
		Log.e(TAG, "onDestroy :: isServiceBind=" + isServiceBind
				+ ",mRouterConnection=" + mRouterConnection);
		if (isServiceBind) {
			if (mRouterConnection != null) {
				unbindService(mRouterConnection);
				isServiceBind = false;
			}
		}
	}

	private void removeCallbacksMessages() {
		Log.e(TAG, "onDestroy :: mHandler=" + mHandler);
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
			mHandler = null;
		}
	}

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			ToastUtils.showToastText(getApplicationContext(), "再按一次将退出程序！", Toast.LENGTH_SHORT);
			mExitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}
}