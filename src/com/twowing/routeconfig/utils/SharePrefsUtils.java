package com.twowing.routeconfig.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class SharePrefsUtils {

	// //////////////////////////////////////////////////////////////////
	// // 保存路由无线开关 ////
	// //////////////////////////////////////////////////////////////////
	private static final String ROUTE_WIFI_AP_SWITCH = "route_wifi_ap";

	private static final String ROUTE_WIFI_AP_SWITCH_KEY = "route_wifi_ap_key";

	/**
	 * Store route wifi_ap flag,
	 * 
	 * @param context
	 *            Context
	 */
	public static final void saveRouteSwitchFlag(Context context, boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(
				ROUTE_WIFI_AP_SWITCH, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(ROUTE_WIFI_AP_SWITCH_KEY, flag);
		editor.commit();
	}

	/**
	 * Get route wifi_ap flag
	 * 
	 * @param context
	 * @return
	 */
	public static final boolean getRouteSwitchFlag(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				ROUTE_WIFI_AP_SWITCH, Context.MODE_PRIVATE);
		boolean sourceFlag = sp.getBoolean(ROUTE_WIFI_AP_SWITCH_KEY, false);
		return sourceFlag;
	}

	private static final String ROUTE_CONNECT_MODE = "route_internet";

	private static final String ROUTE_CONNECT_MODE_KEY = "route_internet_key";

	/**
	 * Store route Internet  mode
	 * 
	 * @param context
	 *            Context
	 */
	public static final void setRouterInternetMode(Context context, String flag) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_CONNECT_MODE,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(ROUTE_CONNECT_MODE_KEY, flag);
		editor.commit();
	}

	/**
	 * Get route Internet  mode
	 * 
	 * @param context
	 * @return
	 */
	
	public static final String getRouterInternetMode(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_CONNECT_MODE,
				Context.MODE_PRIVATE);
		return sp.getString(ROUTE_CONNECT_MODE_KEY, "");
	}
	
	
	private static final String ROUTE_NETWORK_MODE = "route_network";

	private static final String ROUTE_NETWORK_MODE_KEY = "route_network_key";

	/**
	 * Store route Internet  mode
	 * 
	 * @param context
	 *            Context
	 */
	public static final void setRouterNetWorkMode(Context context, String flag) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_NETWORK_MODE,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(ROUTE_NETWORK_MODE_KEY, flag);
		editor.commit();
	}

	/**
	 * Get route Internet  mode
	 * 
	 * @param context
	 * @return
	 */
	
	public static final String getRouterNetWorkMode(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_NETWORK_MODE,
				Context.MODE_PRIVATE);
		return sp.getString(ROUTE_NETWORK_MODE_KEY, "");
	}
	
	
	
	private static final String ROUTE_SET_WIRELESS_RADIO = "route_wireless_radio";

	private static final String ROUTE_SET_WIRELESS_RADIO_KEY = "route_wireless_radio_key";

	/**
	 * Store route Internet  mode
	 * 
	 * @param context
	 *            Context
	 */
	public static final void setWirelessRadioKey(Context context, boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_SET_WIRELESS_RADIO,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(ROUTE_SET_WIRELESS_RADIO_KEY, flag);
		editor.commit();
	}

	/**
	 * Get route Internet  mode
	 * 
	 * @param context
	 * @return
	 */
	
	public static final boolean getWirelessRadioKey(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_SET_WIRELESS_RADIO,
				Context.MODE_PRIVATE);
		return sp.getBoolean(ROUTE_SET_WIRELESS_RADIO_KEY, true);
	}
	
	
	private static final String ROUTE_WIRELESS_USERNAME = "route_wireless_username";

	private static final String ROUTE_WIRELESS_USERNAME_KEY = "route_wireless_username_key";

	/**
	 * Store route Internet  mode
	 * 
	 * @param context
	 *            Context
	 */
	public static final void setWirelessUserName(Context context, String flag) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_WIRELESS_USERNAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(ROUTE_WIRELESS_USERNAME_KEY, flag);
		editor.commit();
	}

	/**
	 * Get route Internet  mode
	 * 
	 * @param context
	 * @return
	 */
	
	public static final String getWirelessUserName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_WIRELESS_USERNAME,
				Context.MODE_PRIVATE);
		return sp.getString(ROUTE_WIRELESS_USERNAME_KEY, "");
	}
	
	
	
	private static final String ROUTE_WIRELESS_PASSWORD = "route_wireless_password";

	private static final String ROUTE_WIRELESS_PASSWORD_KEY = "route_wireless_password_key";

	/**
	 * Store route Internet  mode
	 * 
	 * @param context
	 *            Context
	 */
	public static final void setWirelessPassWord(Context context, String flag) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_WIRELESS_PASSWORD,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(ROUTE_WIRELESS_PASSWORD_KEY, flag);
		editor.commit();
	}

	/**
	 * Get route Internet  mode
	 * 
	 * @param context
	 * @return
	 */
	
	public static final String getWirelessPassWord(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_WIRELESS_PASSWORD,
				Context.MODE_PRIVATE);
		return sp.getString(ROUTE_WIRELESS_PASSWORD_KEY, "");
	}
	
	
	private static final String ROUTE_PPPOE_PASSWORD = "route_pppoe_password";

	private static final String ROUTE_PPPOE_PASSWORD_KEY = "route_pppoe_password_key";

	/**
	 * Store route Internet  mode
	 * 
	 * @param context
	 *            Context
	 */
	public static final void setPppoePassWord(Context context, String flag) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_PPPOE_PASSWORD,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(ROUTE_PPPOE_PASSWORD_KEY, flag);
		editor.commit();
	}

	/**
	 * Get route Internet  mode
	 * 
	 * @param context
	 * @return
	 */
	
	public static final String getPppoePassWord(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_PPPOE_PASSWORD,
				Context.MODE_PRIVATE);
		return sp.getString(ROUTE_PPPOE_PASSWORD_KEY, "");
	}
	
	
	
	private static final String ROUTE_PPPOE_USERNAME = "route_pppoe_username";

	private static final String ROUTE_PPPOE_USERNAME_KEY = "route_pppoe_username_key";

	/**
	 * Store route Internet  mode
	 * 
	 * @param context
	 *            Context
	 */
	public static final void setPppoeUserName(Context context, String flag) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_PPPOE_USERNAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(ROUTE_PPPOE_USERNAME_KEY, flag);
		editor.commit();
	}

	/**
	 * Get route Internet  mode
	 * 
	 * @param context
	 * @return
	 */
	
	public static final String getPppoeUserName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ROUTE_PPPOE_USERNAME,
				Context.MODE_PRIVATE);
		return sp.getString(ROUTE_PPPOE_USERNAME_KEY, "");
	}
	
	
}
