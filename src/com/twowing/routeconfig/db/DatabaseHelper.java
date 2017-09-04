package com.twowing.routeconfig.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 直接操作数据库类
 * 
 * @author Administrator
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "routeconfig.db";
	private static final int DATABASE_VERSION = 5;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + RouterProvider.Router.TABLE_NAME + " ("
				+ RouterProvider.Router._ID + " INTEGER PRIMARY KEY,"
				+ RouterProvider.Router.ROUTER_NETWORK_MODE + " TEXT,"
				+ RouterProvider.Router.TNTERNET_TYPE + " TEXT,"
				+ RouterProvider.Router.TNTERNET_ERROR_MSG + " TEXT"
				+ ");");
		
		db.execSQL("CREATE TABLE " + RouterProvider.RouterWan.TABLE_NAME + " ("
				+ RouterProvider.RouterWan._ID + " INTEGER PRIMARY KEY,"
				+ RouterProvider.RouterWan.ROUTER_IP_ADDRESS + " TEXT,"
				+ RouterProvider.RouterWan.ROUTER_SUBNET_MASK + " TEXT,"
				+ RouterProvider.RouterWan.ROUTER_DEFAULT_GATEWAY + " TEXT," 
				+ RouterProvider.RouterWan.ROUTER_PPPOE_PRIMARY_DNS+ " TEXT," 
				+ RouterProvider.RouterWan.ROUTER_DHCP_PRIMARY_DNS + " TEXT," 
				+ RouterProvider.RouterWan.ROUTER_SECOND_DNS + " TEXT" + ");");
		
		db.execSQL("CREATE TABLE " + RouterProvider.RouterWireless.TABLE_NAME + " ("
				+ RouterProvider.RouterWireless._ID + " INTEGER PRIMARY KEY,"
				+ RouterProvider.RouterWireless.ROUTER_WIRELESS_SWITCH + " TEXT,"
				+ RouterProvider.RouterWireless.ROUTER_WIRELESS_USERNAME + " TEXT,"
				+ RouterProvider.RouterWireless.ROUTER_WIRELESS_PASSWORD + " TEXT" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + RouterProvider.Router.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + RouterProvider.RouterWan.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + RouterProvider.RouterWireless.TABLE_NAME);
		onCreate(db);
	}
}
