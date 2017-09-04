package com.twowing.routeconfig.constant;


public class RouterConstant {
	    public static final String ROUTER_INTERNET_TYPE = "PPPoE";
        public static final String ROUTER_NETWORK_MODE = "201";
        public static final String ROUTER_INTERNET_ERROR = "0";
        public static final String ROUTER_WIRELESS_RADIO = "0";
        public static final String ROUTER_IP_ADDRESS = "192.168.1.1";
        public static final String ROUTER_SUBNET_MASK = "255.255.255.255";//子网掩码：
        public static final String ROUTER_DEFAULT_GATEWAY = "255.255.255.255";//默认网关：
        public static final String ROUTER_PPPOE_PRIMARY_DNS = "255.255.255.255";
        public static final String ROUTER_DHCP_PRIMARY_DNS = "255.255.255.255";
        public static final String ROUTER_SECOND_DNS = "255.255.255.255";
        public static final String ROUTER_WIRELESS_SWITCH = "1";
        public static final String ROUTER_WIRELESS_USERNAME = "1";
        public static final String ROUTER_WIRELESS_PASSWORD = "12345678";
        
        public static final int MSG_GET_ROUTER_INTERNET_TYPE = 0x0001;
        public static final int MSG_GET_ROUTER_IP_ADDRESS = 0x0002;
        public static final int MSG_GET_ROUTER_SUBNET_MASK = 0x0003;
        public static final int MSG_GET_ROUTER_DEFAULT_GATEWAY = 0x0004;
        public static final int MSG_GET_ROUTER_PPPOE_PRIMARY_DNS = 0x0005;
        public static final int MSG_GET_DHCP_PRIMARY_DNS = 0x0006;
        public static final int MSG_GET_ROUTER_SECOND_DNS = 0x0007;

        public static final int MSG_SET_ROUTER_IP_ADDRESS = 0x0008;
        public static final int MSG_SET_ROUTER_SUBNET_MASK = 0x0009;
        public static final int MSG_SET_ROUTER_DEFAULT_GATEWAY = 0x00010;
        public static final int MSG_SET_ROUTER_PPPOE_PRIMARY_DNS = 0x00011;
        public static final int MSG_SET_DHCP_PRIMARY_DNS = 0x00012;
        public static final int MSG_SET_ROUTER_SECOND_DNS = 0x00013;

        
        public static final int MSG_GET_ROUTER_NETWORK_STATUS = 0x00014;
        public static final int MSG_SET_ROUTER_NETWORK_STATUS = 0x00015;
    	
        public static final int MSG_GET_ROUTER_INTERNET_ERROR = 0x00016;
        public static final int MSG_SET_ROUTER_INTERNET_ERROR = 0x00017;
        
        public static final int MSG_SET_ROUTER_INTERNET_TYPE = 0x00018;
        
        public static final int MSG_GET_ROUTER_INTERNET_CHANGE = 0x00021;
        public static final int MSG_GET_ROUTER_NETWORK_CHANGE  = 0x00022;
        
        
        public static final int MSG_GET_WIRELESS_SWITCH = 0x00023;
        public static final int MSG_SET_WIRELESS_SWITCH  = 0x00024;
        
        public static final int MSG_GET_WIRELESS_USERNAME = 0x00025;
        public static final int MSG_SET_WIRELESS_USERNAME  = 0x00026;
        
        public static final int MSG_GET_WIRELESS_PASSWORD = 0x00027;
        public static final int MSG_SET_WIRELESS_PASSWORD  = 0x00028;
        
        
    	public static final String ROUTER_PPPOE_CHANGE_ACTION = "com.twowing.routeconfig.ROUTER_PPPOE_CHAGE_ACTION";
    	public static final String ROUTER_PPPOE_CHANGE_ACTION_KEY = "com.twowing.routeconfig.ROUTER_PPPOE_CHAGE_ACTION_KEY";

    	public static final String ROUTER_DHCP_CHANGE_ACTION = "com.twowing.routeconfig.ROUTER_DHCP_CHANGE_ACTION";
    	public static final String ROUTER_DHCP_CHANGE_ACTION_KEY = "com.twowing.routeconfig.ROUTER_DHCP_CHANGE_ACTION_KEY";
    	
    	public static final String ROUTER_STATIC_IP_CHANGE_ACTION = "com.twowing.routeconfig.ROUTER_STATIC_IP_CHANGE_ACTION";
    	public static final String ROUTER_STATIC_IP_CHANGE_ACTION_KEY = "com.twowing.routeconfig.ROUTER_STATIC_IP_CHANGE_ACTION_KEY";

    	public static final String ROUTER_WIRELESS_STATUS_CHANGE_ACTION = "com.twowing.routeconfig.ROUTER_WIRELESS_STATUS_CHANGE_ACTION";
    	public static final String ROUTER_WIRELESS_STATUS_CHANGE_ACTION_KEY = "com.twowing.routeconfig.ROUTER_WIRELESS_STATUS_CHANGE_ACTION_KEY";

    	public static final String ROUTER_WIFIAP_CHANGE_ACTION = "com.twowing.routeconfig.ROUTER_WIFIAP_CHANGE_ACTION";
    	public static final String ROUTER_WIFIAP_CHANGE_ACTION_KEY = "com.twowing.routeconfig.ROUTER_WIFIAP_CHANGE_ACTION_LEY";
    	
    	public static final String ROUTER_WIRELESS_SWITCH_CHANGE_ACTION = "com.twowing.routeconfig.ROUTER_WIRELESS_SWITCH_CHANGE_ACTION";
    	public static final String ROUTER_WIRELESS_SWITCH_CHANGE_ACTION_KEY = "com.twowing.routeconfig.ROUTER_WIRELESS_SWITCH_CHANGE_ACTION_KEY";
    	
    	public static final String ROUTER_WAN_IP_ADDRESS = "router_wan_ip_address";
		public static final String ROUTER_WAN_SUBNET_MASK = "router_wan_sunnet_mask";
		public static final String ROUTER_WAN_DEFAULT_GATEWAY = "router_wan_default_gateway";
		public static final String ROUTER_WAN_PRIMARY_DNS = "router_wan_primary_dns";
		public static final String ROUTER_WAN_PPPOE_SECOND_DNS = "router_wan_pppoe_ip_second_dns";
		public static final String ROUTER_WAN_DHCP_SECOND_DNS = "router_wan_dhcp_second_dns";
		
		// 获取wan口的网络信息
		public static final int action_GET_WAN_PORT_CONNECT_STATUS = 112;
}