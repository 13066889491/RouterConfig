
package com.twowing.routeconfig.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * RouterContentProvider
 * 
 * @author Administrator
 */
public class RouterContentProvider extends ContentProvider {
	private static final String TAG = "RouterContentProvider";
    private static HashMap<String, String> sMap;
    
    private static final int ROUTER = 10;
    private static final int ROUTERS_ID = 11;

    private static final int ROUTEWAN = 12;
    private static final int ROUTEWANS_ID = 13;
    
    private static final int ROUTER_WIRELESS = 14;
    private static final int ROUTER_WIRELESSES_ID = 15;
    
    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 这里要增加另一张表的匹配项
        sUriMatcher.addURI(RouterProvider.AUTHORITY, "router", ROUTER);
        sUriMatcher.addURI(RouterProvider.AUTHORITY, "routers/#", ROUTERS_ID);
        
        sUriMatcher.addURI(RouterProvider.AUTHORITY, "routerwan", ROUTEWAN);
        sUriMatcher.addURI(RouterProvider.AUTHORITY, "routerwans/#", ROUTEWANS_ID);
        
        sUriMatcher.addURI(RouterProvider.AUTHORITY, "routerwireless", ROUTEWAN);
        sUriMatcher.addURI(RouterProvider.AUTHORITY, "routerwirelesses/#", ROUTEWANS_ID);

        // 保存所有表用到的字段
        sMap = new HashMap<String, String>();

        sMap.put(RouterProvider.Router._ID, RouterProvider.Router._ID);
        sMap.put(RouterProvider.Router.ROUTER_NETWORK_MODE, RouterProvider.Router.ROUTER_NETWORK_MODE);
        sMap.put(RouterProvider.Router.TNTERNET_TYPE, RouterProvider.Router.TNTERNET_TYPE);
        sMap.put(RouterProvider.Router.TNTERNET_ERROR_MSG, RouterProvider.Router.TNTERNET_TYPE);

        sMap.put(RouterProvider.RouterWan._ID, RouterProvider.RouterWan._ID);
        sMap.put(RouterProvider.RouterWan.ROUTER_IP_ADDRESS, RouterProvider.RouterWan.ROUTER_IP_ADDRESS);
        sMap.put(RouterProvider.RouterWan.ROUTER_SUBNET_MASK, RouterProvider.RouterWan.ROUTER_SUBNET_MASK);
        sMap.put(RouterProvider.RouterWan.ROUTER_DEFAULT_GATEWAY, RouterProvider.RouterWan.ROUTER_DEFAULT_GATEWAY);
        sMap.put(RouterProvider.RouterWan.ROUTER_PPPOE_PRIMARY_DNS, RouterProvider.RouterWan.ROUTER_PPPOE_PRIMARY_DNS);
        sMap.put(RouterProvider.RouterWan.ROUTER_DHCP_PRIMARY_DNS, RouterProvider.RouterWan.ROUTER_DHCP_PRIMARY_DNS);
        sMap.put(RouterProvider.RouterWan.ROUTER_SECOND_DNS, RouterProvider.RouterWan.ROUTER_SECOND_DNS);

        sMap.put(RouterProvider.RouterWireless._ID, RouterProvider.RouterWireless._ID);
        sMap.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_SWITCH, RouterProvider.RouterWireless.ROUTER_WIRELESS_SWITCH);
        sMap.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_USERNAME, RouterProvider.RouterWireless.ROUTER_WIRELESS_USERNAME);
        sMap.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_PASSWORD, RouterProvider.RouterWireless.ROUTER_WIRELESS_PASSWORD);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Log.e(TAG, "query :: qb="+qb+", sortOrder="+sortOrder);
        String orderBy;
        Log.e(TAG, "query :: uri="+uri);
        Log.e(TAG, "query :: sUriMatcher.match(uri)="+sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) { // 这里要对不同表的匹配结果做不同处理
            case ROUTER:
            case ROUTERS_ID:
                qb.setTables(RouterProvider.Router.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = RouterProvider.Router.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case ROUTEWAN:
            case ROUTEWANS_ID:
                qb.setTables(RouterProvider.RouterWan.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = RouterProvider.RouterWan.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case ROUTER_WIRELESS:
            case ROUTER_WIRELESSES_ID:
                qb.setTables(RouterProvider.RouterWireless.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = RouterProvider.RouterWireless.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        switch (sUriMatcher.match(uri)) {
            case ROUTER:
                qb.setProjectionMap(sMap);
                break;
            case ROUTERS_ID:
                qb.setProjectionMap(sMap);
                qb.appendWhere(RouterProvider.Router._ID + "="
                        + uri.getPathSegments().get(1));
                break;
            case ROUTEWAN:
                qb.setProjectionMap(sMap);
                break;
            case ROUTEWANS_ID:
                qb.setProjectionMap(sMap);
                qb.appendWhere(RouterProvider.RouterWan._ID + "="
                        + uri.getPathSegments().get(1));
                break;
            case ROUTER_WIRELESS:
                qb.setProjectionMap(sMap);
                break;
            case ROUTER_WIRELESSES_ID:
                qb.setProjectionMap(sMap);
                qb.appendWhere(RouterProvider.RouterWireless._ID + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) { // 这里也要增加匹配项
            case ROUTER:
            case ROUTEWAN:
            case ROUTER_WIRELESS:
                return RouterProvider.CONTENT_TYPE;
            case ROUTERS_ID:
            case ROUTEWANS_ID:
            case ROUTER_WIRELESSES_ID:
                return RouterProvider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        String tableName = "";
        String nullColumn = "";
        switch (sUriMatcher.match(uri)) { // 这里要对不同表的匹配结果做不同处理
            case ROUTER:
                tableName = RouterProvider.Router.TABLE_NAME;
                nullColumn = RouterProvider.Router._ID;
                // Make sure that the fields are all set
                if (values.containsKey(RouterProvider.Router.ROUTER_NETWORK_MODE) == false) {
                    values.put(RouterProvider.Router.ROUTER_NETWORK_MODE, "");
                }
                if (values.containsKey(RouterProvider.Router.TNTERNET_TYPE) == false) {
                    values.put(RouterProvider.Router.TNTERNET_TYPE, "");
                }
                if (values.containsKey(RouterProvider.Router.TNTERNET_ERROR_MSG) == false) {
                    values.put(RouterProvider.Router.TNTERNET_ERROR_MSG, "");
                }
                break;
            case ROUTEWAN:
                tableName = RouterProvider.RouterWan.TABLE_NAME;
                nullColumn = RouterProvider.RouterWan._ID;
                // Make sure that the fields are all set
                if (values.containsKey(RouterProvider.RouterWan.ROUTER_IP_ADDRESS) == false) {
                    values.put(RouterProvider.RouterWan.ROUTER_IP_ADDRESS, "");
                }

                if (values.containsKey(RouterProvider.RouterWan.ROUTER_SUBNET_MASK) == false) {
                    values.put(RouterProvider.RouterWan.ROUTER_SUBNET_MASK, 0);
                }
                if (values.containsKey(RouterProvider.RouterWan.ROUTER_DEFAULT_GATEWAY) == false) {
                    values.put(RouterProvider.RouterWan.ROUTER_DEFAULT_GATEWAY, "");
                }

                if (values.containsKey(RouterProvider.RouterWan.ROUTER_PPPOE_PRIMARY_DNS) == false) {
                    values.put(RouterProvider.RouterWan.ROUTER_PPPOE_PRIMARY_DNS, "");
                }

                if (values.containsKey(RouterProvider.RouterWan.ROUTER_DHCP_PRIMARY_DNS) == false) {
                    values.put(RouterProvider.RouterWan.ROUTER_DHCP_PRIMARY_DNS, 0);
                }
                if (values.containsKey(RouterProvider.RouterWan.ROUTER_SECOND_DNS) == false) {
                    values.put(RouterProvider.RouterWan.ROUTER_SECOND_DNS, 0);
                }
                break;
            case ROUTER_WIRELESS:
                tableName = RouterProvider.RouterWireless.TABLE_NAME;
                nullColumn = RouterProvider.RouterWireless._ID;
                // Make sure that the fields are all set
                if (values.containsKey(RouterProvider.RouterWireless.ROUTER_WIRELESS_SWITCH) == false) {
                    values.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_SWITCH, "");
                }

                if (values.containsKey(RouterProvider.RouterWireless.ROUTER_WIRELESS_USERNAME) == false) {
                    values.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_USERNAME, 0);
                }
                if (values.containsKey(RouterProvider.RouterWireless.ROUTER_WIRELESS_PASSWORD) == false) {
                    values.put(RouterProvider.RouterWireless.ROUTER_WIRELESS_PASSWORD, "");
                }
                break;
            default:
                // Validate the requested uri
                throw new IllegalArgumentException("Unknown URI " + uri);

        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(tableName, nullColumn, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) { // 这里要对不同表的匹配结果做不同处理，注意下面用到的表名不要弄错了

            case ROUTER:
                count = db.delete(RouterProvider.Router.TABLE_NAME, where, whereArgs);
                break;

            case ROUTERS_ID:
                String router = uri.getPathSegments().get(1);
                count = db.delete(RouterProvider.Router.TABLE_NAME, RouterProvider.Router._ID
                        + "="
                        + router
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                : ""), whereArgs);
                break;
            case ROUTEWAN:
                count = db.delete(RouterProvider.RouterWan.TABLE_NAME, where, whereArgs);
                break;

            case ROUTEWANS_ID:
                String routerWan = uri.getPathSegments().get(1);
                count = db.delete(RouterProvider.RouterWan.TABLE_NAME, RouterProvider.RouterWan._ID
                        + "="
                        + routerWan
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                : ""), whereArgs);
                break;
            case ROUTER_WIRELESS:
                count = db.delete(RouterProvider.RouterWireless.TABLE_NAME, where, whereArgs);
                break;

            case ROUTER_WIRELESSES_ID:
                String routerWireless = uri.getPathSegments().get(1);
                count = db.delete(RouterProvider.RouterWireless.TABLE_NAME, RouterProvider.RouterWireless._ID
                        + "="
                        + routerWireless
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) { // 这里要对不同表的匹配结果做不同处理，注意下面用到的表名不要弄错了

            case ROUTER:
                count = db.update(RouterProvider.Router.TABLE_NAME, values, where,
                        whereArgs);
                break;

            case ROUTERS_ID:
                String router = uri.getPathSegments().get(1);
                count = db.update(
                        RouterProvider.Router.TABLE_NAME,
                        values,
                        RouterProvider.Router._ID
                                + "="
                                + router
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;
            case ROUTEWAN:
                count = db.update(RouterProvider.RouterWan.TABLE_NAME, values, where,
                        whereArgs);
                break;

            case ROUTEWANS_ID:
                String routerWan = uri.getPathSegments().get(1);
                count = db.update(
                        RouterProvider.RouterWan.TABLE_NAME,
                        values,
                        RouterProvider.RouterWan._ID
                                + "="
                                + routerWan
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;
            case ROUTER_WIRELESS:
                count = db.update(RouterProvider.RouterWireless.TABLE_NAME, values, where,
                        whereArgs);
                break;

            case ROUTER_WIRELESSES_ID:
                String routerWireless = uri.getPathSegments().get(1);
                count = db.update(
                        RouterProvider.RouterWireless.TABLE_NAME,
                        values,
                        RouterProvider.RouterWireless._ID
                                + "="
                                + routerWireless
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
