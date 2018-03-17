package wakeup.mrwakeup;

import wakeup.devicemanager.KickDevice;

import java.util.ArrayList;

public interface IConnectedKickDeviceChangesListner {
	
	public void onActiveDeviceChanged(KickDevice kickDevice, ArrayList<KickDevice> deviceList);
	
	public void onKickDeviceStatusChanged(KickDevice kickDevice, ArrayList<KickDevice> deviceList);

}
