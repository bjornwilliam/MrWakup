package wakeup.devicemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class DeviceManager {

	ArrayList<KickDevice> deviceList;
	KickDevice activeKick;
	
	public DeviceManager() {
		deviceList = new ArrayList<KickDevice>();
	}

	public void setActiveKick(KickDevice activeKick) {
		this.activeKick = activeKick;
	}

	KickUpdateEventListener kickUpdateEventListener;
	
	public void setKickAddNotifier(KickUpdateEventListener kickAddNotifier) {
		this.kickUpdateEventListener = kickAddNotifier;
	}
	
	// adds a new device to the devices list
	public void addKick(KickDevice newDevice) {
		deviceList.add(newDevice);
		// kickUpdateEventListener.onKickUpdate(newDevice);
	}
	
	public void disconnectKick(KickDevice newDevice) {
		// keeping the disconnected kick in the list. because we need to
		// reservce the number for that kick if it reconnected
	}

	// removes the device from the kick devices list
	public void removeKickDeviceFromDeviceList(int deviceNumber) {
		deviceList.remove(deviceNumber - 1);
	}

	// returns the size of the device list
	public int noOfDevicesConnected() {
		return deviceList.size();
	}

	// Gives a number to the new device that is going to be added to the device
	// list
	public synchronized int assignNumberToDevice() {
		return deviceList.size() + 1;
	}
	
	public KickDevice getDevice(byte[] address){
		for(KickDevice kick : deviceList){
			if(Arrays.equals(address, kick.getAddress())){
				return kick;
			}
		}
		return null;
	}

	public Iterator<KickDevice> getKickDevicesIterator(){
		return this.deviceList.iterator();
	}

	public ArrayList<KickDevice> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(ArrayList<KickDevice> deviceList) {
		this.deviceList = deviceList;
	}

	public KickDevice getActiveKick() {
		return activeKick;
	}
}
