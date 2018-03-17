package com.riftlabs.communicationlib.networkreceivers;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.riftlabs.communicationlib.data.KickConstants;
import com.riftlabs.communicationlib.utils.Log;

public class WifiReceiver extends BroadcastReceiver {

	private static final String TAG = WifiReceiver.class.getName();

	private static WifiManager mManager;
	private static Context mContext;
	private WifiLock mWifiNetworkLock;
	private String mRiftConfigSSID;
	private boolean attemptedToEnableNetwork;

	public WifiReceiver() {
	}

	public WifiReceiver(Context context, WifiManager manager) {
		mContext = context;
		mManager = manager;
	}

	@Override
	public void onReceive(Context c, Intent intent) {

		try
		{
		if (mManager == null || mContext == null)
			return;
		
		// Construct another SSID with double quotes to compare with wifi
		// configs		
		mRiftConfigSSID = "\"" + KickConstants.NETWORK_SSID + "\"";
		String ssid = mManager.getConnectionInfo().getSSID();

		// Check if the current connected connection is Riftnet
		if (mRiftConfigSSID.equals(ssid)) {
			//mWifiNetworkLock = createLock(mManager);
			//mWifiNetworkLock.acquire();
			// Connected , nothing else to worry about
			Log.v(TAG, "Rift net connected");
			attemptedToEnableNetwork = false;
			return;
		}

		boolean riftNetInRange = isRiftNetInRange(mManager.getScanResults());
		Log.v(TAG, "attemptedToEnableNetwork=" + attemptedToEnableNetwork);
		Log.v(TAG, "riftNetInRange=" + riftNetInRange);
		if (riftNetInRange && !attemptedToEnableNetwork) {
			// If riftnet is not configured add as a preferred connection
			boolean riftNetConfigured = isRiftNetConfigured(mManager.getConfiguredNetworks());
			Log.v(TAG, "riftNetConfigured=" + riftNetConfigured);
			if (!riftNetConfigured) {
				// Add Riftnet Wifi as a preferred connection
				mManager.addNetwork(getWifiConfigurationForRiftNet());
				mManager.saveConfiguration();
			}

			mManager.disconnect();

			// It is going to take some time connect to the network
			boolean enableNetwork = mManager.enableNetwork(getRiftNetNetworkId(mManager.getConfiguredNetworks()), true);
			Log.v(TAG, "enableNetwork=" + enableNetwork);
			if (enableNetwork) {
				Log.v(TAG, "Rift net enabled");
				mWifiNetworkLock = createLock(mManager);
				mWifiNetworkLock.acquire();
				// As we have already attempted to enable the network do not
				// attempt to enable it again Until connected to riftnet
				attemptedToEnableNetwork = true;
			}
			mManager.reconnect();
		}
		}
		catch (Exception ex)
		{
			Log.v(TAG, ex.getMessage());
		}

		// Start wifi Scaning again to be sure that we are always connected to
		// riftnet
		//Intent wifiScanIntent = new Intent("com.riftlabs.kickapp.actions.START_WIFI_SCAN");
		//LocalBroadcastManager.getInstance(mContext).sendBroadcast(wifiScanIntent);
	}

	private boolean isRiftNetInRange(List<ScanResult> scanResults) {
		if (scanResults != null && !scanResults.isEmpty()) {
			for (ScanResult result : scanResults) {
				if (!TextUtils.isEmpty(result.SSID) && KickConstants.NETWORK_SSID.equals(result.SSID)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isRiftNetConfigured(List<WifiConfiguration> configuredNetworks) {
		for (WifiConfiguration networks : configuredNetworks) {
			if (!TextUtils.isEmpty(networks.SSID) && networks.SSID.equals(mRiftConfigSSID)) {
				return true;
			}
		}
		return false;
	}

	private int getRiftNetNetworkId(List<WifiConfiguration> configuredNetworks) {
		Log.v(TAG, "getRiftNetId");
		for (WifiConfiguration networks : configuredNetworks) {
			if (!TextUtils.isEmpty(networks.SSID) && networks.SSID.equals(mRiftConfigSSID)) {
				return networks.networkId;
			}
		}
		return -1;
	}

	private WifiConfiguration getWifiConfigurationForRiftNet() {
		Log.v(TAG, "getWifiConfigurationForRiftNet");
		WifiConfiguration configuration = new WifiConfiguration();
		configuration.SSID = "\"" + KickConstants.NETWORK_SSID + "\"";
		configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		return configuration;
	}

	// Create wifiLock so the connection is stable
	private WifiLock createLock(WifiManager manager) {
		WifiLock lock = manager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, KickConstants.WIFI_LOCK_NAME);
		return lock;
	}
}