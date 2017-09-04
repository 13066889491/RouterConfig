
package com.twowing.routeconfig.wan.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 这个类给外部程序提供访问内部数据的一个接口
 * 
 * @author HB
 */
public class SmartWakeProvider extends ContentProvider {

    private SmartWakeDBOpenHelper dbOpenHelper = null;
    public static final UriMatcher uriMatcher;
    static {
        // 常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(SmartWakeContentData.AUTHORITY, "routerconfig",
                SmartWakeContentData.UserTableData.TEACHERS);
        uriMatcher.addURI(SmartWakeContentData.AUTHORITY, "routerconfigs/#",
                SmartWakeContentData.UserTableData.TEACHER);
    }

    // UriMatcher类用来匹配Uri，使用match()方法匹配路径时返回匹配码
    /**
     * 是一个回调函数，在ContentProvider创建的时候，就会运行,第二个参数为指定数据库名称，如果不指定，就会找不到数据库；
     * 如果数据库存在的情况下是不会再创建一个数据库的。（当然首次调用 在这里也不会生成数据库必须调用SQLiteDatabase的
     * getWritableDatabase,getReadableDatabase两个方法中的一个才会创建数据库）
     */
    @Override
    public boolean onCreate() {
        // 这里会调用 DBOpenHelper的构造函数创建一个数据库；
        dbOpenHelper = new SmartWakeDBOpenHelper(this.getContext());
        return true;
    }

    private SQLiteDatabase getSQLiteDatabase() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        return db;
    }

    /**
     * 当执行这个方法的时候，如果没有数据库，他会创建，同时也会创建表，但是如果没有表，下面在执行insert的时候就会出错
     * 这里的插入数据也完全可以用sql语句书写，然后调用 db.execSQL(sql)执行。
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = 0;
        switch (uriMatcher.match(uri)) {
            case SmartWakeContentData.UserTableData.TEACHERS:
                id = getSQLiteDatabase().insert(SmartWakeContentData.UserTableData.TABLE_NAME, null, values); // 返回的是记录的行号，主键为int，实际上就是主键值
                Uri noteUri = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            case SmartWakeContentData.UserTableData.TEACHER:
                id = getSQLiteDatabase().insert(SmartWakeContentData.UserTableData.TABLE_NAME, null, values);
                String path = uri.toString();
                Uri mUri = Uri.parse(path.substring(0, path.lastIndexOf("/"))
                        + id);
                getContext().getContentResolver().notifyChange(mUri, null);
                return mUri;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case SmartWakeContentData.UserTableData.TEACHERS:
                count = getSQLiteDatabase().delete(SmartWakeContentData.UserTableData.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case SmartWakeContentData.UserTableData.TEACHER:
                long personid = ContentUris.parseId(uri);
                String where = "_ID=" + personid; // 删除指定id的记录
                where += !TextUtils.isEmpty(selection) ? " and (" + selection
                        + ")" : "";
                count = getSQLiteDatabase().delete(SmartWakeContentData.UserTableData.TABLE_NAME, where,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getSQLiteDatabase().close();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case SmartWakeContentData.UserTableData.TEACHERS:
                count = getSQLiteDatabase().update(SmartWakeContentData.UserTableData.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case SmartWakeContentData.UserTableData.TEACHER:
                long personid = ContentUris.parseId(uri);
                String where = "_ID=" + personid;// 获取指定id的记录
                where += !TextUtils.isEmpty(selection) ? " and (" + selection
                        + ")" : "";// 把其它条件附加上
                count = getSQLiteDatabase().update(SmartWakeContentData.UserTableData.TABLE_NAME, values, where,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getSQLiteDatabase().close();
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SmartWakeContentData.UserTableData.TEACHERS:
                return SmartWakeContentData.CONTENT_TYPE;
            case SmartWakeContentData.UserTableData.TEACHER:
                return SmartWakeContentData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case SmartWakeContentData.UserTableData.TEACHERS:
                return db.query(SmartWakeContentData.UserTableData.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
            case SmartWakeContentData.UserTableData.TEACHER:
                long personid = ContentUris.parseId(uri);
                String where = "_ID=" + personid;// 获取指定id的记录
                where += !TextUtils.isEmpty(selection) ? " and (" + selection
                        + ")" : "";// 把其它条件附加上
                return db.query(SmartWakeContentData.UserTableData.TABLE_NAME, projection, where, selectionArgs,
                        null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}
