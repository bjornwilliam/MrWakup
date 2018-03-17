package com.riftlabs.communicationlib.networkreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;

import com.riftlabs.communicationlib.api.datatypes.KickSignalStrengthLevels;
import com.riftlabs.communicationlib.data.KickConstants;
import com.riftlabs.communicationlib.utils.Log;

public class SignalStrengthReceiver extends BroadcastReceiver {

	public static final String ACTION_KICK_NEW_RSSI = "com.riftlabs.communicationlib.KICK_NEW_RSSI";
	public static final String EXTRA_NEW_KICK_RSSI = "com.riftlabs.communicationlib.extras.EXTRA_KICK_NEW_RSSI";

	private static final String TAG = SignalStrengthReceiver.class.getName();
	private WifiManager mWifiManager;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.v(TAG, "onReceive SignalStrengthReceiver");

		if (mWifiManager == null) {
			mWifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
		}

		// Check if the current connected connection is Riftnet
		if (KickConstants.NETWORK_SSID.equals(mWifiManager.getConnectionInfo()
				.getSSID())) {

			// Calculate the signal strength
			int newRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
			Log.v(TAG, "RSSI Value" + newRssi);

			int signalLevel = WifiManager.calculateSignalLevel(newRssi,
					KickSignalStrengthLevels.values().length);

			Log.v(TAG, "Calculated Signal Level" + signalLevel);

			//Broadcast the new RSSI value
			Intent rssiChangedIntent = new Intent(ACTION_KICK_NEW_RSSI);
			rssiChangedIntent
					.putExtra(EXTRA_NEW_KICK_RSSI, signalLevel);

			LocalBroadcastManager.getInstance(context).sendBroadcast(
					rssiChangedIntent);
		}
	}
}