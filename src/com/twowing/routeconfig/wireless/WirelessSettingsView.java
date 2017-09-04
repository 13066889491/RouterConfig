package com.twowing.routeconfig.wireless;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import snmp.routeritv.commservice.IAidlRouterCommService;
import snmp.routeritv.commservice.util.Constant;
import snmp.routeritv.commservice.util.IAidlConstant;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.constant.RouterConstant;
import com.twowing.routeconfig.db.RouterConfigDBUtil;
import com.twowing.routeconfig.db.RouterProvider;
import com.twowing.routeconfig.dialogview.SettingLoadingDialog;
import com.twowing.routeconfig.ethernet.entity.ExtConstant;
import com.twowing.routeconfig.utils.CountDownTimerUtil;
import com.twowing.routeconfig.utils.SharePrefsUtils;
import com.twowing.routeconfig.utils.ToastUtils;

public class WirelessSettingsView extends RelativeLayout {
	private static final String TAG = "WirelessSettingsView";
	private View mWirelessSwitchView;
	private EthernetPPPoEView mEthernetWirelessView;
	private TextView mWirelessTitle;
	private CheckBox mWirelessCheckbox;
	public static Map<String, String> valMap = new HashMap<String, String>();
	private Handler mHandler = new MyHandler(this);
	private static final int MSG_SHOW_LOADING_DIALOG = 0x1001;
	private static final int MSG_SHOW_WIRELESS_VIEW = 0x1002;
	private SettingLoadingDialog mLoadingDialog;
	private IAidlRouterCommService mRouterCommService;
	private RouterConfigDBUtil mRouterConfigDBUtil;

	private static class MyHandler extends Handler {
		private WeakReference<WirelessSettingsView> mMain;

		public MyHandler(WirelessSettingsView view) {
			mMain = new WeakReference<WirelessSettingsView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			WirelessSettingsView view = mMain.get();
			if (view == null) {
				return;
			}
			switch (msg.what) {
			case MSG_SHOW_LOADING_DIALOG:
				view.showLoadingDialog();
				break;
			case MSG_SHOW_WIRELESS_VIEW:
				view.showWirelessSettingView((Boolean) msg.obj);
				break;
			default:
				break;
			}
		}
	}

	public WirelessSettingsView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public WirelessSettingsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WirelessSettingsView(Context context) {
		super(context);

	}

	private Context mContext;

	@SuppressLint("CutPasteId")
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mWirelessSwitchView = findViewById(R.id.wireless_switch_view);
		mWirelessTitle = (TextView) findViewById(R.id.ethernet_title);
		mEthernetWirelessView = (EthernetPPPoEView) findViewById(R.id.wireless_setting_view);
		mWirelessCheckbox = (CheckBox) findViewById(R.id.ethernet_checkbox);

		if (mWirelessSwitchView == null || mWirelessTitle == null
				|| mWirelessCheckbox == null || mEthernetWirelessView == null) {
			throw new InflateException("Miss a child?");
		}
		mContext = getContext();
		mRouterConfigDBUtil = new RouterConfigDBUtil(mContext);
		initView();
		setListener();
		initData();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		registerObserver();
		registerInternetReceiver();
	}

	private void registerObserver() {
		mWirelessObserver = new WirelessContentObserver();
		Log.e(TAG, "registerObserver :: mContext=" + mContext);
		if (mContext != null) {
			mContext.getContentResolver().registerContentObserver(
					RouterProvider.RouterWireless.CONTENT_URI, true,
					mWirelessObserver);
		}
	}

	private void initData() {
		mEthernetWirelessView.setUserNameTitle("无线网络名称：");
		mEthernetWirelessView.setPassWordTitle("无线网络密码：");
		mEthernetWirelessView.setHint(0);
		boolean isRouteSwitchFlag = SharePrefsUtils
				.getRouteSwitchFlag(getContext());
		Log.e(TAG, "initData :: isRouteSwitchFlag=" + isRouteSwitchFlag);
		mWirelessCheckbox.setChecked(isRouteSwitchFlag);
		sendVisibilityMessage(MSG_SHOW_WIRELESS_VIEW, isRouteSwitchFlag);
	}

	public void getRouterServiceListener(IAidlRouterCommService l) {
		this.mRouterCommService = l;
		Log.e(TAG, "setRouterServiceListener  :: mRouterCommService="
				+ mRouterCommService);
	}

	private void initView() {
		mWirelessTitle.setText(R.string.wireless_ecurity_ettings);
		mWirelessCheckbox.setVisibility(View.VISIBLE);
	}

	private void setListener() {
		mWirelessCheckbox.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mWirelessCheckbox.setOnFocusChangeListener(mOnFocusChangeListener);
		mEthernetWirelessView.setFocusChangeListener();
		mEthernetWirelessView.setClickListener(mOnClickListener);
	}

	private class WirelessContentObserver extends ContentObserver {
		public WirelessContentObserver() {
			super(null);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			String uriStr = uri.toString();
			Log.e(TAG, "ContentObserver :: uriStr=" + uriStr);
			if (RouterProvider.RouterWireless.CONTENT_URI.toString().equals(
					uriStr)) {

			}
		}
	}

	private void registerInternetReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(RouterConstant.ROUTER_WIRELESS_STATUS_CHANGE_ACTION);
		Log.e(TAG, "registerInternetReceiver :: mContext="+mContext);
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
			if (action == RouterConstant.ROUTER_WIRELESS_STATUS_CHANGE_ACTION) {
				finishTimerUtil();
				boolean isStatusSuccess = intent
						.getBooleanExtra(
								RouterConstant.ROUTER_WIRELESS_STATUS_CHANGE_ACTION_KEY,
								false);
				Log.e(TAG, "onReceive :: isStatusSuccess=" + isStatusSuccess);
				if (isStatusSuccess) {
					setLoadingTitle("无线网络设置成功!", true);
				} else {
					setLoadingTitle("无线网络设置失败,路由端响应超时!", true);
				}
			}
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final String userName = mEthernetWirelessView.getUserNameEditText();
			final String password = mEthernetWirelessView.getPassWordEditText();
			if (mRouterCommService == null) {
				Log.e(TAG, "onClick :: mRouterCommService="
						+ mRouterCommService);
				ToastUtils.showToastText(getContext(), "路由服务器异常，请检查!",
						Toast.LENGTH_SHORT);
				return;
			}
			if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
				if (v.getId() == R.id.wireless_settings_ok) {
					 sendServerData(userName, password);
					mHandler.sendEmptyMessage(MSG_SHOW_LOADING_DIALOG);
					upDataLoadingView();
				}
			} else {
				ToastUtils.showToastText(getContext(), "账户或密码不能空，请重新设置！",
						Toast.LENGTH_SHORT);
			}
		}
	};

	private void getWirelessSwitch(final boolean flag) {
		if (mRouterCommService == null) {
			Log.e(TAG, "getWirelessSwitch :: mRouterCommService="
					+ mRouterCommService);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mRouterCommService != null) {
					try {
						mRouterCommService.setWifiRadioOnOff(flag ? 0 : 1);
					} catch (RemoteException e) {
						e.printStackTrace();
						Log.d(TAG, "send wan dynamic internet info to service");
					}
				} 
			}
		}).start();
	}

	private void sendServerData(final String account, final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mRouterCommService != null) {
					try {
						mRouterCommService
								.setRouterAdminInfo(account, password);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void finishTimerUtil() {
		if (mCountDownTimerUtil != null) {
			mCountDownTimerUtil.cancel();
			mCountDownTimerUtil = null;
		}
	}

	private void upDataLoadingView() {
		finishTimerUtil();
		mCountDownTimerUtil = new CountDownTimerUtil(10000, 1000) {
			public void onTick(long millisUntilFinished) {
				Log.d(TAG, "CountDownTimerUtil :: onTick:: millisUntilFinished="+millisUntilFinished);
				setLoadingTitle("无线网络设置中，请稍等!", false);
			}

			public void onFinish() {
				setLoadingTitle("无线网络设置失败,路由端响应超时!", true);
				this.cancel();
			}
		}.start();
	}

	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			if (view.getId() == R.id.ethernet_checkbox) {
				if (mWirelessCheckbox != null) {
					mWirelessSwitchView
							.setBackgroundDrawable(hasFocus ? getContext()
									.getResources().getDrawable(
											R.drawable.base_item_focused)
									: null);
				}
			}
		}
	};

	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Log.e(TAG, "onCheckedChanged :: getId="
					+ (buttonView.getId() == R.id.ethernet_checkbox));
			getWirelessSwitch(isChecked);
			SharePrefsUtils.saveRouteSwitchFlag(getContext(), isChecked);
			sendVisibilityMessage(MSG_SHOW_WIRELESS_VIEW, isChecked);
		}
	};

	private WirelessContentObserver mWirelessObserver;
	private CountDownTimerUtil mCountDownTimerUtil;

	private void sendVisibilityMessage(int what, Object obj) {
		Message message = Message.obtain(mHandler, what, obj);
		mHandler.sendMessage(message);
	}

	private void showWirelessSettingView(boolean isVisibile) {
		if (mEthernetWirelessView != null) {
			mEthernetWirelessView.setVisibility(isVisibile ? View.VISIBLE
					: View.GONE);
		}
	}

	@SuppressWarnings("deprecation")
	public void setWirelessSettingsSwitch() {
		if (mWirelessSwitchView != null) {
			mWirelessSwitchView.setBackgroundDrawable(null);
		}
	}

	private void showLoadingDialog() {
		finishLoadingDialog();
		mLoadingDialog = new SettingLoadingDialog(getContext(),
				R.style.wdDialog);
		mLoadingDialog.show();
		mLoadingDialog.setLoadingInfo("正在设置无线网络...");
		mLoadingDialog.upDataCompletedLoading(false);
	}

	private void finishLoadingDialog() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}

	private void finishWirelessObserver() {
		if (mWirelessObserver != null && mContext != null) {
			mContext.getContentResolver().unregisterContentObserver(
					mWirelessObserver);
			mWirelessObserver = null;
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

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		finishLoadingDialog();
		finishWirelessObserver();
		unRegisterInternetReceiver();
	}

}