package com.twowing.routeconfig.db;

import java.lang.ref.WeakReference;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.twowing.routeconfig.constant.RouterConstant;
import com.twowing.routeconfig.wan.db.SmartWakeContentData;

public class RouterConfigDBUtil {
	private static final String TAG = "RouterConfigDBUtil";

	private Context mContext;
	private RouterInternetListener mInternetListener;

	public interface RouterInternetListener {
		void getRouterInternetType(String internetType);

		void getRouterNetWorkStatus(String netWorkStatus);

		void getRouterInternetErrorMsg(String errorMsg);
	}

	public void setRouterInternetListener(RouterInternetListener l) {
		mInternetListener = l;
	}
	
	private RouterWirelessListener mWirelessListener;

	public interface RouterWirelessListener {
		void getWirelessSwitch(boolean internetType);

		void getWirelessUserName(String userName);

		void getWirelessPassWord(String passWord);
	}

	public void setRouterWirelessListener(RouterWirelessListener l) {
		mWirelessListener = l;
	}
	public RouterConfigDBUtil(Context ct) {
		this.mContext = ct;
	}

	private static HandlerThread mHandlerThread = new HandlerThread(
			"fm.database");
	static {
		mHandlerThread.start();
	}
	private Handler mHandler = new MyHandler(mHandlerThread.getLooper(),
			RouterConfigDBUtil.this);

	private static class MyHandler extends Handler {
		private WeakReference<RouterConfigDBUtil> mMain;

		public MyHandler(Looper looper, RouterConfigDBUtil main) {
			super(looper);
			mMain = new WeakReference<RouterConfigDBUtil>(main);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			RouterConfigDBUtil main = mMain.get();
			if (main == null) {
				return;
			}
			switch (msg.what) {
			case RouterConstant.MSG_GET_ROUTER_INTERNET_CHANGE:
				main.handleInternetChanged();
				break;
			case RouterConstant.MSG_GET_ROUTER_NETWORK_CHANGE:
				main.handleNetWorkChanged();
				break;
			case RouterConstant.MSG_GET_ROUTER_INTERNET_ERROR:
				main.handleInternetErrorMsg();
				break;
			case RouterConstant.MSG_SET_ROUTER_INTERNET_TYPE:
				main.setInternetType((String) msg.obj);
				break;
			case RouterConstant.MSG_GET_ROUTER_INTERNET_TYPE:
				main.getInternetType();
				break;
			case RouterConstant.MSG_GET_ROUTER_IP_ADDRESS:
				main.getRouterIpAddress();
				break;
			case RouterConstant.MSG_GET_ROUTER_SUBNET_MASK:
				main.getRouterSubnetMask();
				break;
			case RouterConstant.MSG_GET_ROUTER_DEFAULT_GATEWAY:
				main.getRouterDefaultGateway();
				break;
			case RouterConstant.MSG_GET_ROUTER_PPPOE_PRIMARY_DNS:
				main.getRouterPPPoEPrimary();
				break;
			case RouterConstant.MSG_GET_DHCP_PRIMARY_DNS:
				main.getRouterDHCPPrimary();
				break;
			case RouterConstant.MSG_GET_ROUTER_SECOND_DNS:
				main.getRouterSecondDns();
				break;
			case RouterConstant.MSG_GET_ROUTER_NETWORK_STATUS:
				main.getRouterNetWorkMode();
				break;

			case RouterConstant.MSG_SET_ROUTER_IP_ADDRESS:
				main.setRouterIpAddress((String) msg.obj);
				break;
			case RouterConstant.MSG_SET_ROUTER_SUBNET_MASK:
				main.setRouterSubnetMask((String) msg.obj);
				break;
			case RouterConstant.MSG_SET_ROUTER_DEFAULT_GATEWAY:
				main.setRouterDefaultGateway((String) msg.obj);
				break;
			case RouterConstant.MSG_SET_ROUTER_PPPOE_PRIMARY_DNS:
				main.setRouterPPPoEPrimary((String) msg.obj);
				break;
			case RouterConstant.MSG_SET_DHCP_PRIMARY_DNS:
				main.setRouterDHCPPrimary((String) msg.obj);
				break;
			case RouterConstant.MSG_SET_ROUTER_SECOND_DNS:
				main.setRouterSecondDns((String) msg.obj);
				break;
			case RouterConstant.MSG_SET_ROUTER_NETWORK_STATUS:
				main.setRouterNetWorkMode((String) msg.obj);
				break;
			case RouterConstant.MSG_SET_ROUTER_INTERNET_ERROR:
				main.setRouterInternetError((String) msg.obj);
				break;
			case RouterConstant.MSG_GET_WIRELESS_SWITCH:
				main.getWirelessSwitch();
				break;
			case RouterConstant.MSG_SET_WIRELESS_SWITCH:
				main.setWirelessSwitch((String) msg.obj);
				break;
			case RouterConstant.MSG_GET_WIRELESS_USERNAME:
				main.getWirelessUserName();
				break;
			case RouterConstant.MSG_SET_WIRELESS_USERNAME:
				main.setWirelessUserName((String) msg.obj);
				break;
			case RouterConstant.MSG_GET_WIRELESS_PASSWORD:
				main.getWirelessPassWord();
				break;
			case RouterConstant.MSG_SET_WIRELESS_PASSWORD:
				main.setWirelessPassWord((String) msg.obj);
				break;
			default:
				break;
			}
		}
	}

	public void getRouterInternetType() {
		mHandler.sendEmptyMessage(RouterConstant.MSG_GET_ROUTER_INTERNET_CHANGE);
	}

	public void getRouterNetWorkStatus() {
		mHandler.sendEmptyMessage(RouterConstant.MSG_GET_ROUTER_NETWORK_CHANGE);
	}

	public void getRouterInternetErrorMsg() {
		mHandler.sendEmptyMessage(RouterConstant.MSG_GET_ROUTER_INTERNET_ERROR);
	}

	public void getRouterInfo(int Type) {
		mHandler.sendEmptyMessage(Type);
	}

	public void setRouterInfo(int Type, String data) {
		Message message = Message.obtain(mHandler, Type, data);
		mHandler.sendMessage(message);
	}

	private void handleInternetChanged() {
		synchronized (RouterConfigDBUtil.class) {
			Log.e(TAG, "handleInternetChanged :: mInternetListener="
					+ mInternetListener);
			if (mInternetListener != null) {
				mInternetListener.getRouterInternetType(getInternetType());
			}
		}
	}

	private void handleNetWorkChanged() {
		synchronized (RouterConfigDBUtil.class) {
			Log.e(TAG, "handleNetWorkChanged :: mInternetListener="
					+ mInternetListener);
			if (mInternetListener != null) {
				mInternetListener
						.getRouterNetWorkStatus(getRouterNetWorkMode());
			}
		}
	}

	private void handleInternetErrorMsg() {
		synchronized (RouterConfigDBUtil.class) {
			Log.e(TAG, "handleInternetErrorMsg :: mInternetListener="
					+ mInternetListener);
			if (mInternetListener != null) {
				mInternetListener
						.getRouterInternetErrorMsg(getRouterInternetError());
			}
		}
	}

	/**
	 * 查询Router连接方式
	 */
	private String getInternetType() {
		String internetType = RouterConstant.ROUTER_INTERNET_TYPE;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.Router.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					internetType = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.Router.TNTERNET_TYPE));
				}
				cursor.close();
			}
			Log.e(TAG, "getInternetType :: internetType=" + internetType);
			return internetType;
		}
	}
	
	private synchronized void setInternetType(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.Router.TNTERNET_TYPE, data);
		try {
			mContext.getContentResolver().update(RouterProvider.Router.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterIpAddress:: Exception=" + e);
		}
	}

	/**
	 * 查询ip address
	 */
	private String getRouterIpAddress() {
		String ipAddress = RouterConstant.ROUTER_IP_ADDRESS;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWan.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ipAddress = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWan.ROUTER_IP_ADDRESS));
				}
				cursor.close();
			}
			Log.e(TAG, "getRouterIpAddress :: ipAddress=" + ipAddress);
			return ipAddress;
		}
	}

	private synchronized void setRouterIpAddress(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWan.ROUTER_IP_ADDRESS, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWan.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterIpAddress:: Exception=" + e);
		}
	}

	/**
	 * 查询subnet_mask
	 */
	private String getRouterSubnetMask() {
		String subnetMask = RouterConstant.ROUTER_SUBNET_MASK;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWan.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					subnetMask = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWan.ROUTER_SUBNET_MASK));
				}
				cursor.close();
			}
			Log.e(TAG, "getRouterSubnetMask :: subnetMask=" + subnetMask);
			return subnetMask;
		}
	}

	private synchronized void setRouterSubnetMask(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWan.ROUTER_SUBNET_MASK, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWan.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterIpAddress:: Exception=" + e);
		}
	}

	/**
	 * 查询default_gateway
	 */
	private String getRouterDefaultGateway() {
		String defaultGateway = RouterConstant.ROUTER_DEFAULT_GATEWAY;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWan.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					defaultGateway = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWan.ROUTER_DEFAULT_GATEWAY));
				}
				cursor.close();
			}
			Log.e(TAG, "getRouterDefaultGateway :: defaultGateway="
					+ defaultGateway);
			return defaultGateway;
		}
	}

	private synchronized void setRouterDefaultGateway(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWan.ROUTER_DEFAULT_GATEWAY, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWan.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterIpAddress:: Exception=" + e);
		}
	}

	/**
	 * 查询 pppoe default_gateway
	 */
	private String getRouterPPPoEPrimary() {
		String pppoePrimary = RouterConstant.ROUTER_PPPOE_PRIMARY_DNS;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWan.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					pppoePrimary = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWan.ROUTER_PPPOE_PRIMARY_DNS));
				}
				cursor.close();
			}
			Log.e(TAG, "getRouterPPPoEPrimary :: pppoePrimary=" + pppoePrimary);
			return pppoePrimary;
		}
	}

	private synchronized void setRouterPPPoEPrimary(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWan.ROUTER_PPPOE_PRIMARY_DNS, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWan.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterIpAddress:: Exception=" + e);
		}
	}

	/**
	 * 查询 dhcp default_gateway
	 */
	private String getRouterDHCPPrimary() {
		String dhcpPrimary = RouterConstant.ROUTER_DHCP_PRIMARY_DNS;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWan.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					dhcpPrimary = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWan.ROUTER_DHCP_PRIMARY_DNS));
				}
				cursor.close();
			}
			Log.e(TAG, "getRouterDHCPPrimary :: dhcpPrimary=" + dhcpPrimary);
			return dhcpPrimary;
		}
	}

	private synchronized void setRouterDHCPPrimary(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWan.ROUTER_DHCP_PRIMARY_DNS, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWan.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterIpAddress:: Exception=" + e);
		}
	}

	/**
	 * 查询Second_gateway
	 */
	private String getRouterSecondDns() {
		String secondPrimary = RouterConstant.ROUTER_SECOND_DNS;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWan.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					secondPrimary = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWan.ROUTER_SECOND_DNS));
				}
				cursor.close();
			}
			Log.e(TAG, "getRouterSecondDns :: secondPrimary=" + secondPrimary);
			return secondPrimary;
		}
	}

	private synchronized void setRouterSecondDns(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWan.ROUTER_SECOND_DNS, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWan.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterIpAddress:: Exception=" + e);
		}
	}

	/**
	 * 查询network Mode
	 */
	private String getRouterNetWorkMode() {
		String netWorkMode = RouterConstant.ROUTER_NETWORK_MODE;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.Router.CONTENT_URI, null, null, null, null);
			Log.e(TAG, "getRouterNetWorkMode :: cursor= "+cursor);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					netWorkMode = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.Router.ROUTER_NETWORK_MODE));
				}
				cursor.close();
			}
			Log.e(TAG, "getRouterNetWorkMode :: netWorkMode=" + netWorkMode);
			return netWorkMode;
		}
	}

	private synchronized void setRouterNetWorkMode(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.Router.ROUTER_NETWORK_MODE, data);
		try {
			mContext.getContentResolver().update(RouterProvider.Router.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterNetWorkMode:: Exception=" + e);
		}
	}

	/**
	 * 查询network error
	 */
	private String getRouterInternetError() {
		String internetError = RouterConstant.ROUTER_INTERNET_ERROR;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.Router.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					internetError = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.Router.TNTERNET_ERROR_MSG));
				}
				cursor.close();
			}
			Log.e(TAG, "getRouterNetWorkMode :: netWorkMode=" + internetError);
			return internetError;
		}
	}

	private synchronized void setRouterInternetError(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.Router.TNTERNET_ERROR_MSG, data);
		try {
			mContext.getContentResolver().update(RouterProvider.Router.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterNetWorkMode:: Exception=" + e);
		}
	}

	
	/**
	 * 查询Wireless Switch
	 */
	private String getWirelessSwitch() {
		String wirelessSwitch = RouterConstant.ROUTER_WIRELESS_SWITCH;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWireless.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					wirelessSwitch = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWireless.ROUTER_WIRELESS_SWITCH));
				}
				cursor.close();
			}
			Log.e(TAG, "getWirelessSwitch :: wirelessSwitch=" + wirelessSwitch);
			return wirelessSwitch;
		}
	}

	private synchronized void setWirelessSwitch(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_SWITCH, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWireless.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterNetWorkMode:: Exception=" + e);
		}
	}

	/**
	 * 查询Wireless UserName
	 */
	private String getWirelessUserName() {
		String wirelessSwitch = RouterConstant.ROUTER_WIRELESS_USERNAME;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWireless.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					wirelessSwitch = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWireless.ROUTER_WIRELESS_USERNAME));
				}
				cursor.close();
			}
			Log.e(TAG, "getWirelessSwitch :: wirelessSwitch=" + wirelessSwitch);
			return wirelessSwitch;
		}
	}

	private synchronized void setWirelessUserName(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_USERNAME, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWireless.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterNetWorkMode:: Exception=" + e);
		}
	}
	
	/**
	 * 查询Wireless UserName
	 */
	private String getWirelessPassWord() {
		String wirelessSwitch = RouterConstant.ROUTER_WIRELESS_PASSWORD;
		synchronized (RouterConfigDBUtil.class) {
			Cursor cursor = mContext.getContentResolver().query(
					RouterProvider.RouterWireless.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					wirelessSwitch = cursor
							.getString(cursor
									.getColumnIndex(RouterProvider.RouterWireless.ROUTER_WIRELESS_USERNAME));
				}
				cursor.close();
			}
			Log.e(TAG, "getWirelessSwitch :: wirelessSwitch=" + wirelessSwitch);
			return wirelessSwitch;
		}
	}

	private synchronized void setWirelessPassWord(String data) {
		ContentValues cv = new ContentValues();
		cv.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_PASSWORD, data);
		try {
			mContext.getContentResolver().update(RouterProvider.RouterWireless.CONTENT_URI, cv,
					null, null);
		} catch (Exception e) {
			Log.e(TAG, "setRouterNetWorkMode:: Exception=" + e);
		}
	}
	
}