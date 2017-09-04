package com.twowing.routeconfig.wan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SmartWakeDBOpenHelper extends SQLiteOpenHelper {
	// 在SQLiteOepnHelper的子类当中，必须有该构造函数，用来创建一个数据库；
	public SmartWakeDBOpenHelper(Context context) {
		super(context, SmartWakeContentData.DATABASE_NAME, null,
				SmartWakeContentData.DATABASE_VERSION);
	}

	/**
	 * 只有当数据库执行创建 的时候，才会执行这个方法。如果更改表名，也不会创建，只有当创建数据库的时候，才会创建改表名之后 的数据表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "
				+ SmartWakeContentData.UserTableData.TABLE_NAME + " ("
				+ SmartWakeContentData.UserTableData._ID
				+ " INTEGER PRIMARY KEY,"
				+ SmartWakeContentData.UserTableData.SEX + " TEXT" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "
				+ SmartWakeContentData.UserTableData.TABLE_NAME);
	}

}
