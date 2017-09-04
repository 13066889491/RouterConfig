package com.twowing.routeconfig.callback;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import snmp.routeritv.commservice.IAidlRouterCommCallback;
import snmp.routeritv.commservice.po.Replay;
import snmp.routeritv.commservice.util.Constant;
import snmp.routeritv.commservice.util.IAidlConstant;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.twowing.routeconfig.constant.RouterConstant;
import com.twowing.routeconfig.db.RouterConfigDBUtil;
import com.twowing.routeconfig.db.RouterConfigDBUtil.RouterInternetListener;
import com.twowing.routeconfig.utils.SharePrefsUtils;
import com.twowing.routeconfig.wan.db.SmartWakeContentData;

public class RouterCommCallback extends IAidlRouterCommCallback.Stub {
	private static final String TAG = "RouterCommCallback";
	private Context mContext;
	private Handler mHandler = new MyHandler(this);

	public static Map<String, Context> contextMap = new HashMap<String, Context>();
	public static Map<String, String> netStatusMap = new HashMap<String, String>();
	private RouterConfigDBUtil mRouterConfigDBUtil;
	private String mInternetErrorMsg = RouterConstant.ROUTER_INTERNET_ERROR;
	private String mNetWorkSMode = RouterConstant.ROUTER_NETWORK_MODE;
	private String mInternetType = RouterConstant.ROUTER_INTERNET_TYPE;
	private String mWirelessRadio = RouterConstant.ROUTER_WIRELESS_RADIO;
	private RouterInternetInfoListener mInternetListener;

	public interface RouterInternetInfoListener {
		void getRouterIpAddress(String ipAddress);

		void getRouterSubnetMask(String subnetMask);

		void getRouterDefaultGateway(String defaultGateway);

		void getRouterPrimaryDns(String primaryDns);

		void getRouterSecondDns(String secondDns);
	}

	public void setRouterInternetListener(RouterInternetInfoListener l) {
		mInternetListener = l;
	}

	public RouterCommCallback(Context context) {
		this.mContext = context;
		mRouterConfigDBUtil = new RouterConfigDBUtil(context);
	}

	private static class MyHandler extends Handler {
		private WeakReference<RouterCommCallback> mMain;

		public MyHandler(RouterCommCallback view) {
			mMain = new WeakReference<RouterCommCallback>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			RouterCommCallback view = mMain.get();
			if (view == null) {
				return;
			}
			Replay replay = (Replay) msg.obj;
			int action = msg.what;
			Log.e(TAG, "getIaidlerr() ==" + replay.getIaidlerr()
					+ ",getSnmperr()=" + replay.getSnmperr());
			if (replay.getIaidlerr() == IAidlConstant.iaidlerr.IAIDLERR_SUCCESS) {
				JSONObject json = replay.getJson();
				Log.e(TAG, "handleMessage :: json=" + json);
				String text = "";
				if (json == null) {
					text = "get the result data failed: json = null";
					Log.d(TAG, text);
				} else {
					int snmperr = replay.getSnmperr();
					String snmperrmsg = replay.getSnmperrmsg();
					Log.e(TAG, "handleMessage :: action=" + action
							+ " ,Snmperr=" + snmperr);
					Log.e(TAG, "handleMessage :: snmperrmsg=" + snmperrmsg);
					switch (action) {
					case Constant.action.action_GET_INTERNET_STATEINFO:
						if ("404".equals(snmperrmsg)) {
							return;
						}
						Iterator it = json.keys();
						while (it.hasNext()) {
							String key = (String) it.next();
							Log.e(TAG, "handleMessage :: key=" + key);
							String netWorkMode = null;
							try {
								netWorkMode = json.getString(key);
								Log.e(TAG, "handleMessage :: getString(key)="
										+ netWorkMode);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							try {
								if (Constant.key.netinfo_MODE.equals(key)) {
									boolean isInternetType = view
											.isInternetType(netWorkMode);
									Log.e(TAG,
											"handleMessage :: isInternetType="
													+ isInternetType);
								} else if (Constant.key.netinfo_STATE
										.equals(key)) {
									boolean isNetWokStatue = view
											.isNetWokStatue(netWorkMode);
									Log.e(TAG,
											"handleMessage :: isNetWokStatue="
													+ isNetWokStatue);
								} else if (Constant.key.netinfo_LINKDOWN_CODE
										.equals(key)) {
									boolean isInternetErrorMsg = view
											.isInternetErrorMsg(netWorkMode);
									Log.e(TAG,
											"handleMessage :: isInternetErrorMsg="
													+ isInternetErrorMsg);
								} else if (Constant.key.netinfo_LINKDOWN_REASON
										.equals(key)) {
									netWorkMode += ","
											+ json.getString(Constant.key.netinfo_LINKDOWN_CODE);
									boolean isInternetErrorMsg = view
											.isInternetErrorMsg(netWorkMode);
									Log.e(TAG, "handleMessage :: netWorkMode="
											+ netWorkMode
											+ ", isInternetErrorMsg="
											+ isInternetErrorMsg);
								} 
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						break;
					case Constant.action.action_SET_ROUTER_ADMIN_INFO:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								view.sendWirelessChangeBroadcast(true);
							} else {
								view.sendWirelessChangeBroadcast(false);
							}
						}
						break;
					case Constant.action.action_GET_WIFI_CLIENTS:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								Iterator wifiClient = json.keys();
								while (wifiClient.hasNext()) {
									String key = (String) wifiClient.next();
									Log.e(TAG, "handleMessage :: key=" + key);
									String wifiClientName = null;
									try {
										wifiClientName = json.getString(key);
										Log.e(TAG,
												"handleMessage :: wifiClientName="
														+ wifiClientName);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									if (Constant.key.wlan_SSID2G_WIFICLIENT
											.equals(key)) {
										view.sendWifiApChangeBroadcast(wifiClientName);
									}
								}
							} else {
								view.sendWifiApChangeBroadcast(null);
							}
						}

						break;
					case Constant.action.action_SET_PPPOE_INFO:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								view.sendRouterPPPOEChangeBroadcast(true);
							} else {
								view.sendRouterPPPOEChangeBroadcast(false);
							}
						}
						break;
					case Constant.action.action_GET_STATIC_IP_INFO:
						break;
					case Constant.action.action_SET_STATIC_IP_INFO:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								view.sendStaticIpChangeBroadcast(true);
							} else {
								view.sendStaticIpChangeBroadcast(false);
							}
						}
						break;
					case Constant.action.action_SET_DYNAMIC_INTERNET:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								Log.e(TAG,
										"handleMessage :: sendDHCPChangeBroadcast=");
								view.sendDHCPChangeBroadcast(true);
							} else {
								view.sendDHCPChangeBroadcast(false);
							}
						}
						break;
					case Constant.action.action_GET_INTERNET_NETINFO:

						break;
					case Constant.action.action_SET_WIFI_AUTHMODE:
						break;
					case Constant.action.action_SET_WIFI_RADIOONOFF:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								Iterator radioIt = json.keys();
								while (radioIt.hasNext()) {
									String key = (String) radioIt.next();
									Log.e(TAG, "handleMessage :: key=" + key);
									String radio = null;
									try {
										radio = json.getString(key);
										Log.e(TAG, "handleMessage :: radio="
												+ radio);
										if (Constant.key.wlan_SSID2G_RADIOONOFF
												.equals(key)) {
											view.saveRouteWirelessSwitch(radio);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
//								view.saveRouteWirelessSwitch(true);
//								// view.sendWirelessSwitchBroadcast(true);
//							} else {
//								view.saveRouteWirelessSwitch(false);
//								// view.sendWirelessSwitchBroadcast(false);
							}
						}
						break;
					case RouterConstant.action_GET_WAN_PORT_CONNECT_STATUS:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								view.setRouterInfoListener(json);
							}
						}
						break;
					case Constant.action.action_GET_SSID2D4GENTRY:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								Iterator wirelessIt = json.keys();
								while (wirelessIt.hasNext()) {
									String key = (String) wirelessIt.next();
									Log.e(TAG, "handleMessage :: key=" + key);
									String wireless = null;
									try {
										wireless = json.getString(key);
										Log.e(TAG, "handleMessage :: wireless="
												+ wireless);
										if (Constant.key.wlan_SSID2G_RADIOONOFF
												.equals(key)) {
											view.saveRouteWirelessSwitch(wireless);
										}else if(Constant.key.wlan_SSID2G_2D4GNAME
												.equals(key)){
											view.saveRouteWirelessUserName(wireless);
										}else if(Constant.key.wlan_SSID2G_PSKPASSWD
												.equals(key)){
											view.saveRouteWirelessPassWord(wireless);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						}
						break;
					case Constant.action.action_GET_PPPOE_INFO:
						if (snmperr == 200) {
							if ("200".equals(snmperrmsg)) {
								Iterator pppoeIt = json.keys();
								while (pppoeIt.hasNext()) {
									String key = (String) pppoeIt.next();
									Log.e(TAG, "handleMessage :: key=" + key);
									String pppoe = null;
									try {
										pppoe = json.getString(key);
										Log.e(TAG, "handleMessage :: wireless="
												+ pppoe);
										if (Constant.key.wan_PPPOEUSER
												.equals(key)) {
											view.saveRoutePppoeUserName(pppoe);
										}else if(Constant.key.wan_PPPOEPASSWORD
												.equals(key)){
											view.saveRoutePppoePassWord(pppoe);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						}
						break;
					}
				}
			}
		}

	}

	private void setRouterInfoListener(JSONObject json) {
		Log.e(TAG, "handleMessage :: mInternetListener=" + mInternetListener);
		if (mInternetListener != null) {
			Iterator it = json.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Log.e(TAG, "handleMessage :: key=" + key);
				String routerInfo = "";
				try {
					routerInfo = json.getString(key);
					Log.e(TAG, "handleMessage :: getString(key)=" + routerInfo);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (RouterConstant.ROUTER_WAN_DEFAULT_GATEWAY.equals(key)) {
					mInternetListener.getRouterDefaultGateway(routerInfo);
				} else if (RouterConstant.ROUTER_WAN_IP_ADDRESS.equals(key)) {
					mInternetListener.getRouterIpAddress(routerInfo);
				} else if (RouterConstant.ROUTER_WAN_PRIMARY_DNS.equals(key)) {
					mInternetListener.getRouterPrimaryDns(routerInfo);
				} else if (RouterConstant.ROUTER_WAN_PPPOE_SECOND_DNS
						.equals(key)) {
					mInternetListener.getRouterSecondDns(routerInfo);
				} else if (RouterConstant.ROUTER_WAN_DHCP_SECOND_DNS
						.equals(key)) {
					mInternetListener.getRouterSecondDns(routerInfo);
				} else if (RouterConstant.ROUTER_WAN_SUBNET_MASK.equals(key)) {
					mInternetListener.getRouterSubnetMask(routerInfo);
				}
			}
		}
	}

	private void saveRouteWirelessSwitch(String flag) {
		if(!TextUtils.isEmpty(flag)){
			if("0".equals(flag)){
				SharePrefsUtils.saveRouteSwitchFlag(mContext, true);
			}else{
				SharePrefsUtils.saveRouteSwitchFlag(mContext, false);
			}
		}else{
			SharePrefsUtils.saveRouteSwitchFlag(mContext, false);
		}
	}
	private void saveRoutePppoeUserName(String flag) {
		if(!TextUtils.isEmpty(flag)){
			SharePrefsUtils.setPppoePassWord(mContext, flag);
		}
	}
	private void saveRoutePppoePassWord(String flag) {
		if(!TextUtils.isEmpty(flag)){
			SharePrefsUtils.setPppoePassWord(mContext, flag);
		}
	}
	
	private void saveRouteWirelessUserName(String flag) {
		if(!TextUtils.isEmpty(flag)){
				SharePrefsUtils.setWirelessUserName(mContext, flag);
		}
	}
	private void saveRouteWirelessPassWord(String flag) {
		if(!TextUtils.isEmpty(flag)){
				SharePrefsUtils.setWirelessUserName(mContext, flag);
		}
	}

	private boolean isInternetType(String mode) {
		String internetMode = SharePrefsUtils.getRouterInternetMode(mContext);
		Log.e(TAG, "isInternetType :: mode=" + mode + mInternetType
				+ ",internetMode=" + internetMode);
		if (!TextUtils.isEmpty(mode) && !mode.equals(internetMode)) {
			SharePrefsUtils.setRouterInternetMode(mContext, mode);
			return true;
		}
		return false;
	}

	private boolean isNetWokStatue(String mode) {
		String netWorkMode = SharePrefsUtils.getRouterNetWorkMode(mContext);
		Log.e(TAG, "isNetWokStatue :: mode=" + mode + ", netWorkMode="
				+ netWorkMode);
		if (!TextUtils.isEmpty(mode) && !mode.equals(netWorkMode)) {
			SharePrefsUtils.setRouterNetWorkMode(mContext, mode);
			return true;
		}
		return false;
	}

	private boolean isInternetErrorMsg(String mode) {
		Log.e(TAG, "isInternetErrorMsg :: mode=" + mode
				+ ", mInternetErrorMsg=" + mInternetErrorMsg);
		if (!TextUtils.isEmpty(mode) && !mode.equals(mInternetErrorMsg)) {
			mInternetErrorMsg = mode;
			return true;
		}
		return false;
	}


	private void setRouterInfo(int what, String data) {
		if (mRouterConfigDBUtil != null) {
			mRouterConfigDBUtil.setRouterInfo(what, data);
		}
	}

	/**
	 * action: 操作行为，即操作码
	 * msg：操作结果数据，返回的msg为一个gson字符串，该gson字符串对应的是Replay对象，Replay对象
	 * 主要有iaidlerr，snmperr，jsonobject三个属性 iaidlerr
	 * 接口调用状态，当其值为0时表示调用操作成功(SUCCESS) snmperr 服务进程和路由端通信状态，当其值为0时表示操作成功(SUCCESS)
	 * iaidlerr 和 snmperr定义在IAidlConstant类里 jsonobject
	 * 存储操作数据的json对象，其key定义在Constant类里
	 */
	@Override
	public void update(int action, String msg) throws RemoteException {
		Log.e(TAG, "update :: action=" + action + ", msg =" + msg);

		Gson gson = new Gson();
		Replay replay = gson.fromJson(msg, Replay.class);

		if (replay != null) {
			Message message = Message.obtain();
			message.what = action;
			message.obj = replay;
			mHandler.sendMessage(message);
		}
	}

	private void sendRouterPPPOEChangeBroadcast(boolean flag) {
		Log.e(TAG, "sendNetWorkChangeBroadcast :: mContext=" + mContext
				+ ", flag=" + flag);
		Intent intent = new Intent();
		intent.setAction(RouterConstant.ROUTER_PPPOE_CHANGE_ACTION);
		intent.putExtra(RouterConstant.ROUTER_PPPOE_CHANGE_ACTION_KEY, flag);
		if (mContext != null) {
			mContext.sendBroadcast(intent);
		}
	}

	private void sendDHCPChangeBroadcast(boolean flag) {
		Log.e(TAG, "sendNetWorkChangeBroadcast :: mContext=" + mContext
				+ ", flag=" + flag);
		Intent intent = new Intent();
		intent.setAction(RouterConstant.ROUTER_DHCP_CHANGE_ACTION);
		intent.putExtra(RouterConstant.ROUTER_DHCP_CHANGE_ACTION_KEY, flag);
		if (mContext != null) {
			mContext.sendBroadcast(intent);
		}
	}

	private void sendStaticIpChangeBroadcast(boolean flag) {
		Log.e(TAG, "sendNetWorkChangeBroadcast :: mContext=" + mContext
				+ ", flag=" + flag);
		Intent intent = new Intent();
		intent.setAction(RouterConstant.ROUTER_STATIC_IP_CHANGE_ACTION);
		intent.putExtra(RouterConstant.ROUTER_STATIC_IP_CHANGE_ACTION_KEY, flag);
		if (mContext != null) {
			mContext.sendBroadcast(intent);
		}
	}

	private void sendWirelessChangeBroadcast(boolean flag) {
		Log.e(TAG, "sendNetWorkChangeBroadcast :: mContext=" + mContext
				+ ", flag=" + flag);
		Intent intent = new Intent();
		intent.setAction(RouterConstant.ROUTER_WIRELESS_STATUS_CHANGE_ACTION);
		intent.putExtra(
				RouterConstant.ROUTER_WIRELESS_STATUS_CHANGE_ACTION_KEY, flag);
		if (mContext != null) {
			mContext.sendBroadcast(intent);
		}
	}

	private void sendWifiApChangeBroadcast(String flag) {
		Log.e(TAG, "sendNetWorkChangeBroadcast :: mContext=" + mContext
				+ ", flag=" + flag);
		Intent intent = new Intent();
		intent.setAction(RouterConstant.ROUTER_WIFIAP_CHANGE_ACTION);
		intent.putExtra(RouterConstant.ROUTER_WIFIAP_CHANGE_ACTION_KEY, flag);
		if (mContext != null) {
			mContext.sendBroadcast(intent);
		}
	}

	private void sendWirelessSwitchBroadcast(boolean flag) {
		Log.e(TAG, "sendNetWorkChangeBroadcast :: mContext=" + mContext
				+ ", flag=" + flag);
		Intent intent = new Intent();
		intent.setAction(RouterConstant.ROUTER_WIRELESS_SWITCH_CHANGE_ACTION);
		intent.putExtra(
				RouterConstant.ROUTER_WIRELESS_SWITCH_CHANGE_ACTION_KEY, flag);
		if (mContext != null) {
			mContext.sendBroadcast(intent);
		}
	}

	/**
	 * 获取内容提供者中的数据
	 * 
	 * @throws Throwable
	 */
	public int queryProviderDB(Context context) throws Throwable {
		int id = 0;
		Cursor cursor = context.getContentResolver().query(
				SmartWakeContentData.UserTableData.CONTENT_URI, null, null,
				null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				id = cursor
						.getInt(cursor
								.getColumnIndex(SmartWakeContentData.UserTableData.SEX));
			}
			cursor.close();
		}
		return id;
	}

	/**
	 * 添加内容提供者中的数据
	 * 
	 * @throws Throwable
	 */
	public void insertProviderDB(Context context, int data) throws Throwable {
		ContentResolver contentResolver = context.getContentResolver();
		ContentValues contentValues = new ContentValues();
		contentValues.put(SmartWakeContentData.UserTableData.SEX, data);
		contentResolver.insert(SmartWakeContentData.UserTableData.CONTENT_URI,
				contentValues);
	}

	/**
	 * 删除内容提供者中的数据
	 * 
	 * @throws Throwable
	 */
	public void deleteProviderDB(Context context) throws Throwable {
		ContentResolver contentResolver = context.getContentResolver();
		contentResolver.delete(SmartWakeContentData.UserTableData.CONTENT_URI,
				null, null);
	}

}
