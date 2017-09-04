package com.twowing.routeconfig.wifi.entity;

public class WifiListInfoBean {
	public String number;
	public String deviceName;
	public String macAddress;
	public String ipAddress;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public WifiListInfoBean(String number, String deviceName,
			String macAddress, String ipAddress) {
		super();
		this.number = number;
		this.deviceName = deviceName;
		this.macAddress = macAddress;
		this.ipAddress = ipAddress;
	}

	public WifiListInfoBean() {
	}
	@Override
	public String toString() {
		return "WifiListInfoBean [number=" + number + ", deviceName="
				+ deviceName + ", macAddress=" + macAddress + ", ipAddress="
				+ ipAddress + "]";
	}

}
