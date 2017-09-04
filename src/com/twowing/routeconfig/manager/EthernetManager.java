package com.twowing.routeconfig.manager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import snmp.routeritv.commservice.IAidlRouterCommService;
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
import android.util.Log;
import android.widget.Toast;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.constant.RouterConstant;
import com.twowing.routeconfig.db.RouterConfigDBUtil;
import com.twowing.routeconfig.db.RouterConfigDBUtil.RouterInternetListener;
import com.twowing.routeconfig.db.RouterProvider;
import com.twowing.routeconfig.dialogview.EthernetDynamicDialog;
import com.twowing.routeconfig.dialogview.EthernetDynamicDialog.DHCPStateListener;
import com.twowing.routeconfig.dialogview.EthernetPPPoEDialog;
import com.twowing.routeconfig.dialogview.EthernetPPPoEDialog.PPPoEStateListener;
import com.twowing.routeconfig.dialogview.EthernetStaticIpDialog;
import com.twowing.routeconfig.dialogview.EthernetStaticIpDialog.StaticStateListener;
import com.twowing.routeconfig.dialogview.SettingLoadingDialog;
import com.twowing.routeconfig.utils.CountDownTimerUtil;
import com.twowing.routeconfig.utils.ToastUtils;

public class EthernetManager {
	private static final String TAG = "EthernetManager";
	private IAidlRouterCommService mRouterCommService;
	private Context mContext;

	public static Map<String, String> setPPPOEinfoRespRetMap = new HashMap<String, String>();
	public static Map<String, String> internetStatusMap = new HashMap<String, String>();
	public static boolean isPppoeDailing = false;
	private Handler mHandler = new MyHandler(this);
	private static final int MSG_START_CONNECT_PPPOE = 0x1000;
	private static final int MSG_DISSMISS_DIALOG = 0x1001;
	private static final int MSG_UPDATING_LOADINGDIALOG_INFO = 0x1002;
	private static final int MSG_GET_ROUTER_INTERNET_INFO = 0x1003;
	private static final int MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO = 0x1004;
	private static final int MSG_GET_INTERNET_STATUS = 0x1005;

	private static final int MSG_START_CONNECT_DHCP = 0x1006;
	private static final int MSG_START_CONNECT_STATIC = 0x1007;
	private SettingLoadingDialog mLoadingDialog;
	private EthernetPPPoEDialog mEthernetPPPoEDialog;
	private EthernetDynamicDialog mEthernetDynamicDialog;
	private EthernetStaticIpDialog mEthernetStaticIpDialog;
	private EthernetContentObserver mEthernetObserver;
	private RouterConfigDBUtil mRouterConfigDBUtil;
	private CountDownTimerUtil mCountDownTimerUtil;
	private CountDownTimerUtil mDHCPTimerUtil;

	private static class MyHandler extends Handler {
		private WeakReference<EthernetManager> mMain;

		public MyHandler(EthernetManager view) {
			mMain = new WeakReference<EthernetManager>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			EthernetManager view = mMain.get();
			if (view == null) {
				return;
			}
			switch (msg.what) {
			case MSG_START_CONNECT_PPPOE:
				view.startConnectPPPoE();
				break;
			case MSG_DISSMISS_DIALOG:
				view.finishLoadingDialog();
				break;
			case MSG_UPDATING_LOADINGDIALOG_INFO:
				view.upDatingLoadingInfo((String) msg.obj);
				break;
			case MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO:
				view.upDataCompletedLoadingInfo((String) msg.obj);
				break;
			case MSG_GET_ROUTER_INTERNET_INFO:
				view.getRouterInternetInfo();
				break;
			case MSG_GET_INTERNET_STATUS:
				view.isInternetStatus();
				break;
			case MSG_START_CONNECT_DHCP:
				view.startCountDownTimer();
				break;
			case MSG_START_CONNECT_STATIC:
				view.startStaticTimer();
				break;
			default:
				break;
			}
		}
	}

	public EthernetManager(Context context,
			IAidlRouterCommService routerCommService) {
		mContext = context;
		mRouterCommService = routerCommService;
		Log.e(TAG, "EthernetManager :: mRouterCommService="
				+ mRouterCommService);
		mRouterConfigDBUtil = new RouterConfigDBUtil(context);
		mRouterConfigDBUtil.setRouterInternetListener(mRouterInternetListener);
		registerInternetReceiver();
		registerObserver();
		getWirelessInfo();
		getPPPOEInfo();
	}

	private void getPPPOEInfo() {
		if (mRouterCommService == null) {
			Log.e(TAG, "getPPPOEInfo :: mRouterCommService="
					+ mRouterCommService);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mRouterCommService != null) {
					try {
						mRouterCommService.getPppoeInfo(1);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} 
			}
		}).start();
	}

	private void getWirelessInfo() {
		if (mRouterCommService == null) {
			Log.e(TAG, "getWirelessInfo :: mRouterCommService="
					+ mRouterCommService);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mRouterCommService != null) {
					try {
						Log.d(TAG, "send wan dynamic internet info to service");
						mRouterCommService.getSsid2d4gEntry(1);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} 
			}
		}).start();
	}

	private void registerInternetReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(RouterConstant.ROUTER_DHCP_CHANGE_ACTION);
		filter.addAction(RouterConstant.ROUTER_PPPOE_CHANGE_ACTION);
		filter.addAction(RouterConstant.ROUTER_STATIC_IP_CHANGE_ACTION);
		if (mContext != null) {
			mContext.registerReceiver(mInternetReceiver, filter);
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
			if (action == RouterConstant.ROUTER_PPPOE_CHANGE_ACTION) {
				mHandler.removeMessages(MSG_START_CONNECT_PPPOE);
				boolean isSuccess = intent.getBooleanExtra(
						RouterConstant.ROUTER_PPPOE_CHANGE_ACTION_KEY, false);
				Log.e(TAG, "onReceive :: isSuccess=" + isSuccess);
				finishCountDownTimer();
				if (isSuccess) {
					sendUpDataDialogTitle(MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
					"拨号成功！");
					
//					sendUpDataDialogTitle(MSG_UPDATING_LOADINGDIALOG_INFO,
//							"账户密码设置成功，" + "\n正在检测网络是否可用。。。。");
//					getInternetState();
				} else {
					sendUpDataDialogTitle(
							MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
							"拨号失败!\n请确认以下项是否符合条件：" + "\n1. pppoe账户密码是否正确;"
									+ "\n2. wan口网线是否正确连接;"
									+ "\n3. internet是否正常。");
				}
			} else if (action == RouterConstant.ROUTER_DHCP_CHANGE_ACTION) {
				mHandler.removeMessages(MSG_START_CONNECT_DHCP);
				finishDHCPTimer();
				boolean isSuccess = intent.getBooleanExtra(
						RouterConstant.ROUTER_DHCP_CHANGE_ACTION_KEY, false);
				Log.e(TAG, "onReceive :: isSuccess=" + isSuccess);
				if (isSuccess) {
					sendUpDataDialogTitle(
							MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
							"动态网络设置成功！");
//					sendUpDataDialogTitle(MSG_UPDATING_LOADINGDIALOG_INFO,
//							"动态网设置信息已设置，" + "\n正在检测网络是否可用。。。。");
//					getInternetState();
				} else {
					sendUpDataDialogTitle(
							MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
							"动态网络设置失败!\n请确认以下项是否符合条件：" + "\n1. wan口网线是否正确连接;"
									+ "\n2. internet是否正常。");
				}
			} else if (action == RouterConstant.ROUTER_STATIC_IP_CHANGE_ACTION) {
				mHandler.removeMessages(MSG_START_CONNECT_STATIC);
				finishStaticTimer();
				boolean isSuccess = intent.getBooleanExtra(
						RouterConstant.ROUTER_STATIC_IP_CHANGE_ACTION_KEY, false);
				Log.e(TAG, "onReceive :: isSuccess=" + isSuccess);
				if (isSuccess) {
					sendUpDataDialogTitle(
							MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
							"静态IP网络设置成功！");
				} else {
					sendUpDataDialogTitle(
							MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
							"静态IP网络设置失败!\n请确认以下项是否符合条件：" + "\n1. wan口网线是否正确连接;"
									+ "\n2. internet是否正常。"+ "\n3. ip地址、DNS等是否正常。");
				}
			}
		}
	};

	private void getInternetState() {
		mInternetStatusCount = 0;
		mHandler.removeMessages(MSG_GET_INTERNET_STATUS);
		mHandler.sendEmptyMessageDelayed(MSG_GET_INTERNET_STATUS, 2000);
	}

	private int mInternetStatusCount = 0;

	private void isInternetStatus() {
		++mInternetStatusCount;
		Log.e(TAG, "isInternetStatus :: mInternetStatusCount="
				+ mInternetStatusCount);
		if (mInternetStatusCount < 1) {
			mHandler.removeMessages(MSG_GET_ROUTER_INTERNET_INFO);
			mHandler.removeMessages(MSG_GET_INTERNET_STATUS);
		} else if (mInternetStatusCount > 4) {
			mHandler.removeMessages(MSG_GET_INTERNET_STATUS);
			mHandler.removeMessages(MSG_GET_ROUTER_INTERNET_INFO);
			mInternetStatusCount = 0;
			return;
		}
		getInternetStatus();
		Log.e(TAG, "isInternetStatus :: mInternetStatusCount="
				+ mInternetStatusCount + " ,mRouterConfigDBUtil="
				+ mRouterConfigDBUtil);
		mHandler.sendEmptyMessageDelayed(MSG_GET_INTERNET_STATUS, 1100);
	}

	private String mInternetMode = "0";
	private String mNetWorkStatus = "200";
	private RouterInternetListener mRouterInternetListener = new RouterInternetListener() {

		@Override
		public void getRouterInternetType(String type) {
			Log.e(TAG, "getRouterInternetType :: type=" + type
					+ ",mInternetStatusCount=" + mInternetStatusCount);
			mInternetMode = type;
		}

		@Override
		public void getRouterNetWorkStatus(String status) {
			mNetWorkStatus = status;
			Log.e(TAG, "getRouterNetWorkStatus :: status=" + status
					+ ",mInternetMode=" + mInternetMode);
			if (mInternetStatusCount == 4) {
				if (mInternetMode.equalsIgnoreCase("PPPOE")) {
					if (status.equals("200")) {
						mHandler.removeMessages(MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO);
						sendUpDataDialogTitle(
								MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
								"拨号成功！！！");
					}
				} else if (mInternetMode.equalsIgnoreCase("DHCP")) {
					if (status.equals("200")) {
						mHandler.removeMessages(MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO);
						sendUpDataDialogTitle(
								MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
								"动态网络设置成功！！！");
					}
				}
			}
		}

		@Override
		public void getRouterInternetErrorMsg(String msg) {
			Log.e(TAG, "getRouterInternetErrorMsg :: msg=" + msg
					+ ",mNetWorkStatus=" + mNetWorkStatus+",mInternetStatusCount="+mInternetStatusCount);
			if (mInternetStatusCount == 4) {
				if (mInternetMode.equalsIgnoreCase("PPPOE")) {
					if (!mNetWorkStatus.equals("200")) {
						mHandler.removeMessages(MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO);
						sendUpDataDialogTitle(
								MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
								"拨号失败!\n请确认以下项是否符合条件：" + "\n1. pppoe账户密码是否正确;"
										+ "\n2. wan口网线是否正确连接;"
										+ "\n3. internet是否正常。");
					}
				} else if (mInternetMode.equalsIgnoreCase("DHCP")) {
					if (!mNetWorkStatus.equals("200")) {
						mHandler.removeMessages(MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO);
						sendUpDataDialogTitle(
								MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
								"动态网络设置失败!\n请确认以下项是否符合条件：" + "\n1. wan口网线是否正确连接;"
										+ "\n2. internet是否正常。");
					}
				}
			}
		}
	};

	
	private void registerObserver() {
		mEthernetObserver = new EthernetContentObserver();
		Log.e(TAG, "registerObserver :: mContext=" + mContext);
		if (mContext != null) {
			mContext.getContentResolver().registerContentObserver(
					RouterProvider.Router.CONTENT_URI, true, mEthernetObserver);
		}
	}

	public void upDataCompletedLoadingInfo(String st) {
		if (mLoadingDialog != null) {
			mLoadingDialog.setLoadingInfo(st);
			mLoadingDialog.upDataCompletedLoading(true);
		}
	}

	private void upDatingLoadingInfo(String data) {
		if (mLoadingDialog != null) {
			mLoadingDialog.setLoadingInfo(data);
		}
	}

	private void sendUpDataDialogTitle(int what, Object object) {
		Message mMessage = Message.obtain();
		mMessage.what = what;
		mMessage.obj = object;
		mHandler.sendMessage(mMessage);
	}

	public void showEthernetDynamicDialog() {
		Log.e(TAG, "showEthernetDynamicDialog ：： ");
		showInitRouterServiceError();
		finishEthernetConfigDialog();
		mEthernetDynamicDialog = new EthernetDynamicDialog(mContext,
				R.style.wdDialog, mRouterCommService);
		Log.e(TAG, "showEthernetDynamicDialog ：： mEthernetDynamicDialog="
				+ mEthernetDynamicDialog);
		mEthernetDynamicDialog.show();
		mEthernetDynamicDialog.connectDHCPState(mDHCPStateListener);
	}

	public void showEthernetStaticIpView() {
		showInitRouterServiceError();
		finishStaticIpDialog();
		mEthernetStaticIpDialog = new EthernetStaticIpDialog(mContext,
				R.style.wdDialog, mRouterCommService);
		mEthernetStaticIpDialog.show();
		mEthernetStaticIpDialog.connectStaticState(mStaticStateListener);
	}

	public void showEthernetPPPoEDialog() {
		showInitRouterServiceError();
		finishEthernetPPPoEDialog();
		mEthernetPPPoEDialog = new EthernetPPPoEDialog(mContext,
				R.style.wdDialog, mRouterCommService,
				EthernetPPPoEDialog.PPPOE_NETWORK);
		mEthernetPPPoEDialog.show();
		mEthernetPPPoEDialog.connectPPPoEState(mPPPoEStateListener);
	}

	private void showInitRouterServiceError() {
		if (mRouterCommService == null) {
			Log.e(TAG, "onClick :: mRouterCommService=" + mRouterCommService);
			ToastUtils.showToastText(mContext, "路由服务器异常，请检查!",
					Toast.LENGTH_SHORT);
			return;
		}
	}

	private DHCPStateListener mDHCPStateListener = new DHCPStateListener() {

		@Override
		public void showDHCPStatus() {
			showLoadingDialog("正在设置动态网络...", false);
			mHandler.sendEmptyMessageDelayed(MSG_START_CONNECT_DHCP, 500);
		}
	};

	private StaticStateListener mStaticStateListener =new StaticStateListener(){

		@Override
		public void showStaticStatus() {
			showLoadingDialog("正在设置静态网络,请等候...", false);
			mHandler.sendEmptyMessageDelayed(MSG_START_CONNECT_STATIC, 500);
		}
	};
	private PPPoEStateListener mPPPoEStateListener = new PPPoEStateListener() {

		@Override
		public void showPPPoEStatus() {
			showLoadingDialog("正在发送PPPOE账号信息到路由端...", false);
			mHandler.sendEmptyMessageDelayed(MSG_START_CONNECT_PPPOE, 500);
		}
	};
	private CountDownTimerUtil mStaticTimerUtil;

	private void showLoadingDialog(String text, boolean flag) {
		finishLoadingDialog();
		mLoadingDialog = new SettingLoadingDialog(mContext, R.style.wdDialog);
		mLoadingDialog.show();
		mLoadingDialog.setLoadingInfo(text);
		mLoadingDialog.upDataCompletedLoading(flag);
	}

	private void startConnectPPPoE() {
		finishCountDownTimer();
		mCountDownTimerUtil = new CountDownTimerUtil(10000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				sendUpDataDialogTitle(MSG_UPDATING_LOADINGDIALOG_INFO,
						"正在开始拨号，请稍等。。。");
			}

			@Override
			public void onFinish() {
				sendUpDataDialogTitle(MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
						"拨号失败,路由端响应超时!");
				cancel();
			}
		}.start();
	}

	private void startCountDownTimer() {
		finishDHCPTimer();
		mDHCPTimerUtil = new CountDownTimerUtil(10000, 1000) {
			public void onTick(long millisUntilFinished) {
				sendUpDataDialogTitle(MSG_UPDATING_LOADINGDIALOG_INFO,
						"动态网络设置中，请稍等!");
			}

			public void onFinish() {
				sendUpDataDialogTitle(MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
						"动态网络设置失败,路由端响应超时!");
				this.cancel();
			}
		}.start();
	}

	private  void startStaticTimer(){
		finishStaticTimer();
		mStaticTimerUtil = new CountDownTimerUtil(10000, 1000) {
			public void onTick(long millisUntilFinished) {
				sendUpDataDialogTitle(MSG_UPDATING_LOADINGDIALOG_INFO,
						"静态IP网络设置中，请稍等!");
			}
			public void onFinish() {
				sendUpDataDialogTitle(MSG_UPDATA_COMPLETED_LOADINGDIALOG_INFO,
						"静态IP网络设置失败,路由端响应超时!");
				this.cancel();
			}
		}.start();
	}
	private void finishDHCPTimer() {
		if (mDHCPTimerUtil != null) {
			mDHCPTimerUtil.cancel();
			mDHCPTimerUtil = null;
		}
	}

	private void finishCountDownTimer() {
		if (mCountDownTimerUtil != null) {
			mCountDownTimerUtil.cancel();
			mCountDownTimerUtil = null;
		}
	}
	private void finishStaticTimer() {
		if (mStaticTimerUtil != null) {
			mStaticTimerUtil.cancel();
			mStaticTimerUtil = null;
		}
	}

	private void finishLoadingDialog() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}

	private void finishEthernetConfigDialog() {
		if (mEthernetDynamicDialog != null) {
			mEthernetDynamicDialog.dismiss();
			mEthernetDynamicDialog = null;
		}
	}

	private void finishStaticIpDialog() {
		if (mEthernetStaticIpDialog != null) {
			mEthernetStaticIpDialog.dismiss();
			mEthernetStaticIpDialog = null;
		}
	}

	private void finishEthernetPPPoEDialog() {
		if (mEthernetPPPoEDialog != null) {
			mEthernetPPPoEDialog.dismiss();
			mEthernetPPPoEDialog = null;
		}
	}

	private class EthernetContentObserver extends ContentObserver {
		public EthernetContentObserver() {
			super(null);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			String uriStr = uri.toString();
			Log.e(TAG, "ContentObserver :: uriStr=" + uriStr);
			if (RouterProvider.Router.CONTENT_URI.toString().equals(uriStr)) {
				getInternetStatus();
			}
		}
	}
	
	private void getInternetStatus() {
		mHandler.removeMessages(MSG_GET_ROUTER_INTERNET_INFO);
		mHandler.sendEmptyMessageDelayed(MSG_GET_ROUTER_INTERNET_INFO, 1000);
	}

	private void getRouterInternetInfo() {
		if (mRouterConfigDBUtil != null) {
			mRouterConfigDBUtil.getRouterInternetType();
			mRouterConfigDBUtil.getRouterNetWorkStatus();
			mRouterConfigDBUtil.getRouterInternetErrorMsg();
		}
	}

	private void unRegisterObserver() {
		Log.e(TAG, "unRegisterObserver ::mEthernetObserver="
				+ mEthernetObserver);
		if (mEthernetObserver != null) {
			mContext.getContentResolver().unregisterContentObserver(
					mEthernetObserver);
		}
	}

	public void onDestory() {
		unRegisterObserver();
		finishEthernetConfigDialog();
		finishStaticIpDialog();
		finishLoadingDialog();
		finishStaticIpDialog();
		finishEthernetPPPoEDialog();
		unRegisterInternetReceiver();
		finishCountDownTimer();
		finishStaticTimer();
		finishDHCPTimer();
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	private void unRegisterInternetReceiver() {
		if (mContext != null && mInternetReceiver != null) {
			mContext.unregisterReceiver(mInternetReceiver);
		}
	}

	public void connectionRouterServiceFail() {
		Log.e(TAG, "connectionRouterServiceFail ::mRouterCommService="
				+ mRouterCommService);
		showLoadingDialog("路由服务器出现异常，请检查！", true);
	}
}
