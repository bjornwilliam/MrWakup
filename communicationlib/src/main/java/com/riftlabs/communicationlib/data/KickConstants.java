package com.riftlabs.communicationlib.data;

public class KickConstants {

	public static byte[] ADDRESS_ALL = new byte[] { (byte) 0xA9, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF }; // 0xFF, 0x00, 0x00, 0x00
	public static int NETWORK_PORT = 8080;
	public static String NETWORK_IP = "169.254.1.1"; // 169.254.255.255 "192.168.111.1";
	public static String NETWORK_SSID = "RiftNet";
	public static final String WIFI_LOCK_NAME = "RIFTNET_WIFILOCK";
}
