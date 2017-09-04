
package com.twowing.routeconfig.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 保存数据库中的常量
 * 
 * @author Administrator
 */
public class RouterProvider {

    // 这里只有一个authority
    public static final String AUTHORITY = "com.twowing.routeconfig";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.twowing.routeconfig";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.twowing.routeconfig";

    /**
     * 保存Router表中用到的常量
     * 
     * @author Administrator
     */
    public static final class Router implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/routers");
        public static final String TABLE_NAME = "RouterTableName";
        
        public static final String DEFAULT_SORT_ORDER = RouterProvider.Router._ID
                + "  desc";
        public static final String ROUTER_NETWORK_MODE = "network_mode";
        public static final String TNTERNET_TYPE = "internet_type";
        public static final String TNTERNET_ERROR_CODE = "internet_error_code";
        public static final String TNTERNET_ERROR_MSG = "internet_error_msg";
    }
    
    /**
     * 保存RouterWan表中用到的常量
     * 
     * @author Administrator
     */
    public static final class RouterWan implements BaseColumns {
    	 public static final Uri CONTENT_URI = Uri.parse("content://"
                 + AUTHORITY + "/routerwans");
    	 
        public static final String TABLE_NAME = "InternetTableName";
        
        public static final String DEFAULT_SORT_ORDER = RouterProvider.RouterWan._ID
                + "  desc";
        public static final String ROUTER_IP_ADDRESS = "router_ip_address";
        public static final String ROUTER_SUBNET_MASK = "router_subnet_mask";//子网掩码：
        public static final String ROUTER_DEFAULT_GATEWAY = "router_default_gateway";//默认网关：
        public static final String ROUTER_PPPOE_PRIMARY_DNS = "router_pppoe_primary_dns";
        public static final String ROUTER_DHCP_PRIMARY_DNS = "router_dhcp_primary_dns";
        public static final String ROUTER_SECOND_DNS = "router_second_dns";
    }

    /**
     * 保存RouterWireless表中用到的常量
     * 
     * @author Administrator
     */
    public static final class RouterWireless implements BaseColumns {
   	 public static final Uri CONTENT_URI = Uri.parse("content://"
             + AUTHORITY + "/routersirelesses");
        public static final String TABLE_NAME = "WirelessTableName";
        
        public static final String DEFAULT_SORT_ORDER = RouterProvider.RouterWireless._ID
                + "  desc";
        public static final String ROUTER_WIRELESS_SWITCH = "router_wireless_switch";
        public static final String ROUTER_WIRELESS_USERNAME = "router_wireless_username";
        public static final String ROUTER_WIRELESS_PASSWORD = "router_wireless_password";
    }

}
