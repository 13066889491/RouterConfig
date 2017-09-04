
package com.twowing.routeconfig.wan.db;

import com.twowing.routeconfig.db.RouterProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 提供ContentProvider对外的各种常量，当外部数据需要访问的时候，就可以参考这些常量操作数据。
 * 
 * @author HB
 */
public class SmartWakeContentData {
	// 这里只有一个authority
	public static final String AUTHORITY = "com.twowing.routeconfig";
	
	// 创建 数据库的时候，都必须加上版本信息；并且必须大于4
	public static final int DATABASE_VERSION = 4;
	
    public static final String DATABASE_NAME = "routerconfig.db";
    
    // 数据集的MIME类型字符串则应该以vnd.android.cursor.dir/开头
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.twowing.routeconfig";
    // 单一数据的MIME类型字符串应该以vnd.android.cursor.item/开头
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.twowing.routeconfig";
    
    public static final class UserTableData implements BaseColumns {
    	
    	// Uri，外部程序需要访问就是通过这个Uri访问的，这个Uri必须的唯一的。
    	public static final Uri CONTENT_URI = Uri.parse("content://"
    			+ AUTHORITY + "/routerconfig");
    	public static final String TABLE_NAME = "routerconfig";
    	
        /* 自定义匹配码 */
        public static final int TEACHERS = 1;
        /* 自定义匹配码 */
        public static final int TEACHER = 2;

        public static final String SEX = "SEX";
        
        
        public static final String DEFAULT_SORT_ORDER = RouterProvider.Router._ID
                + "  desc";
        public static final String ROUTER_NETWORK_MODE = "network_mode";
        public static final String TNTERNET_TYPE = "internet_type";
        public static final String TNTERNET_ERROR_CODE = "internet_error_code";
        public static final String TNTERNET_ERROR_MSG = "internet_error_msg";
    }
}
