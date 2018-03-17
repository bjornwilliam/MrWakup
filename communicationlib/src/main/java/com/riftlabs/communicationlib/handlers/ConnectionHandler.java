package com.riftlabs.communicationlib.handlers;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.data.KickConstants;
import com.riftlabs.communicationlib.networkreceivers.WifiReceiver;
import com.riftlabs.communicationlib.utils.ConnectionUtils;
import com.riftlabs.communicationlib.utils.Log;

public class ConnectionHandler {

	public static final String ACTION_START_WIFI_SCAN = "com.riftlabs.kickapp.actions.START_WIFI_SCAN";
	private static final int WIFI_SCAN_INTERVAL = 30000;
	private static final int WIFI_SCAN_DELAY = 5000;
	protected static final String TAG = ConnectionHandler.class.getName();
	private static WifiManager manager;
	private static WifiReceiver receiverWifi;
	private static KickCallbacks callback;
	private static Context context;
	private static Timer wifiScanTimer;
	private static boolean isConnected = false; 

	private BroadcastReceiver mWifiScaningReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			startWifiScanning();
		}
	};

	public ConnectionHandler(Context ctxt) {
		// test PR
		context = ctxt;
		manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		receiverWifi = new WifiReceiver(context, manager);
		registerBroadcast(context);
	}

	private void registerBroadcast(Context context) {
		IntentFilter filter = new IntentFilter(ConnectionHandler.ACTION_START_WIFI_SCAN);
		LocalBroadcastManager.getInstance(context).registerReceiver(mWifiScaningReceiver, filter);
	}

	private void unRegisterBroadcast(Context context) {
		if (mWifiScaningReceiver != null) {
			Log.v(TAG, "WifiScaningReceiver unregistering and setting null");
			LocalBroadcastManager.getInstance(context).unregisterReceiver(mWifiScaningReceiver);
			mWifiScaningReceiver = null;
		}

		if (receiverWifi != null) {
			Log.v(TAG, "receiverWifi unregistering and setting null");
			context.unregisterReceiver(receiverWifi);
			receiverWifi = null;
		}
	}

	private class WifiConnectTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			manager.startScan();
			Log.d("Connection Handler", "Phone Wifi enabled=" + manager.isWifiEnabled());
			Log.v(TAG, "--------stop scanner----------");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	/**
	 * Starts wifi scanning based on the interval if rift net is not connected
	 */
	public void startWifiScanning() {
		// If already scanning, stop
		if (wifiScanTimer != null)
			wifiScanTimer.cancel();

		context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifiScanTimer = new Timer();
		wifiScanTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				isConnected = isConnectedToRiftNet();
				Log.v(TAG, "isConnectedToRiftNet=" + isConnected);
				if (!isConnected) {
					Log.v(TAG, "Starting wifi scaning");
					// Start wifi scanning
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							new WifiConnectTask().execute();
						}
					});
					// Cancel the timer until the scan is done
					//if (wifiScanTimer != null) {
					//	wifiScanTimer.cancel();
					//}
				}
			}
		}, WIFI_SCAN_DELAY, WIFI_SCAN_INTERVAL);
	}

	private boolean isConnectedToRiftNet() {
		try {
			WifiInfo wifiInfo = this.manager.getConnectionInfo();
			if (wifiInfo != null && wifiInfo.getSSID() != null) {
				Log.v(TAG, "--------start scanner----------");
				Log.v(TAG, "wifiInfo.getSSID()=" + wifiInfo.getSSID());
				return wifiInfo.getSSID().contains(KickConstants.NETWORK_SSID);
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in isConnectedToRiftNet.", e, context);
		}
		return false;
	}

	public boolean IsConnectedToRiftNet() {
		return isConnected;
	}

	public void stopWifiScanning() {
		Log.v(TAG, "calling stopWifiScanning");
		unRegisterBroadcast(context);
		if (wifiScanTimer != null) {
			Log.v(TAG, "cancelling wifiScanTimer");
			wifiScanTimer.cancel();
			wifiScanTimer = null;
		}
	}

	public void setCallback(KickCallbacks callback) {
		this.callback = callback;
	}
}