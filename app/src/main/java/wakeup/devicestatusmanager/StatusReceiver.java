package wakeup.devicestatusmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.riftlabs.communicationlib.data.Kick;
import com.riftlabs.communicationlib.utils.Log;

public class StatusReceiver extends BroadcastReceiver {

	private DeviceStatusManager mDeviceStatusManager;

	public StatusReceiver(DeviceStatusManager statusManager) {
		mDeviceStatusManager = statusManager;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Kick kickStatus = (Kick)intent.getSerializableExtra(Kick.class.getName());
		Log.i(StatusReceiver.class.getName(), "ConnectionStatus : " + kickStatus.getKickAction());
		Log.i(StatusReceiver.class.getName(), "Temperature : " + String.valueOf(kickStatus.getTemperature()));
		Log.i(StatusReceiver.class.getName(), "Battery Level : " + String.valueOf(kickStatus.getBatteryLevel()));
		//Log.i(StatusReceiver.class.getName(), "EV: " + String.valueOf(kickStatus.getEV()));
		//Log.i(StatusReceiver.class.getName(), "CIEXYZ : " + String.valueOf(kickStatus.getCIEXYZ()));
		Log.i(StatusReceiver.class.getName(), "ConnectionStatus : " + kickStatus.getKickAction());
		mDeviceStatusManager.updateKick(kickStatus);
	}
}
