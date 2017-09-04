package com.twowing.routeconfig.ethernet.entity;

import com.twowing.routeconfig.ethernet.adapter.EthernetAdapter;

public class EthernetListBean {
	public int titleId;
	public int iconId;
	public boolean isSoftUpdate = false;
	public int moduleType = -1;
	public int position;
	public boolean visible = true;
	public int viewType = EthernetAdapter.VIEWTYPE_NOMAL;
}
