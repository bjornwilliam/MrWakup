package wakeup.devicestatusmanager;

import android.graphics.Color;

import com.riftlabs.communicationlib.api.datatypes.KickBrightness;
import com.riftlabs.communicationlib.api.datatypes.KickColor;
import com.riftlabs.communicationlib.api.datatypes.KickWhiteBalance;
import com.riftlabs.communicationlib.data.Kick;
import com.riftlabs.communicationlib.utils.Log;


import wakeup.devicemanager.DeviceManager;
import wakeup.devicemanager.BatteryLevel;
import wakeup.devicemanager.KickDevice;
import wakeup.devicemanager.TemperatureLevel;


public class DeviceStatusManager {

	private DeviceManager deviceManager;
	private StatusReceiver statusReceiver;

	public DeviceStatusManager() {
		this.statusReceiver = new StatusReceiver(this);
	}

	public KickDevice createKick(Kick status) {
		KickDevice newDevice = new KickDevice();
		if (deviceManager.getDevice(status.getAddress()) == null) {
			newDevice.setKickAction(status.getKickAction());
			newDevice.setTemperatureLevel(getTemperatureLevel(status.getTemperature()));
			newDevice.setBatteryLevel(getBatteryLevel(status.getBatteryLevel()));
			newDevice.setDeviceNumber(deviceManager.assignNumberToDevice());
			Log.d("KickList", "createKick() Device Number -> " + newDevice.getDeviceNumber());
			newDevice.setAddress(status.getAddress());
			newDevice.setName(status.getDevice().getName());

			if (deviceManager != null)
				deviceManager.addKick(newDevice);

			return newDevice;
		} else
			return null;
	}

	
	public KickDevice updateKick(Kick kick) {
		KickDevice kickDevice = new KickDevice();
		kickDevice.setTemperatureLevel(getTemperatureLevel(kick.getTemperature()));
		kickDevice.setBatteryLevel(getBatteryLevel(kick.getBatteryLevel()));
		kickDevice.setKickAction(kick.getKickAction());
		kickDevice.setAddress(kick.getAddress());
		kickDevice.setName(kick.getDevice().getName());
		kickDevice.setBrightness(getBrightnessValue(kick.getKickBrightness()));
		kickDevice.setWhiteBalance(kick.getKickWhiteBalance().getColorTemperature());
		kickDevice.setColor(getColorValue(kick.getKickColor()));
		kickDevice.setKickDeviceOn(kick.isOn());

		KickDevice oldKickDevice = deviceManager.getDevice(kickDevice.getAddress());
		kickDevice.setDeviceNumber(oldKickDevice.getDeviceNumber());
		Log.d("KickList", "updateKick() Device Number -> " + kickDevice.getDeviceNumber());
		kickDevice.setOnCannedEffect(oldKickDevice.getOnCannedEffect());
		kickDevice.setPlayingEffect(oldKickDevice.getPlayingEffect());
		kickDevice.setKicksLinked(oldKickDevice.isKicksLinked());
		kickDevice.setEffect(oldKickDevice.getEffect());
		kickDevice.setActiveFilter(oldKickDevice.getActiveFilter());
		deviceManager.getDeviceList().set(deviceManager.getDeviceList().indexOf(oldKickDevice), kickDevice);
		return kickDevice;
	}
	
	public KickDevice updateLinkedBrightness(KickDevice kickDevice, KickBrightness kickBrightness) {
		kickDevice.setBrightness(getBrightnessValue(kickBrightness));
		return kickDevice;
	}
	
	public KickDevice updateLinkedWhiteBalance(KickDevice kickDevice, KickWhiteBalance kickWhiteBalance) {
		kickDevice.setWhiteBalance(kickWhiteBalance.getColorTemperature());
		return kickDevice;
	}

	private int getColorValue(KickColor kickColor) {
		int color = Color.rgb(kickColor.getRed(), kickColor.getGreen(), kickColor.getBlue());
		return color;
	}

	private int getBrightnessValue(KickBrightness kickBrightness) {
		int brightness = (int) (((float) kickBrightness.getBrightness() / 255f) * 100f);
		return brightness;
	}

	public BatteryLevel getBatteryLevel(int batteryLevel) {
		BatteryLevel bLevel = BatteryLevel.LOW;
		if (batteryLevel < 51){
			bLevel = BatteryLevel.LOW;
		} else {
			bLevel = BatteryLevel.CHARGING;
		}
		return bLevel;
	}

	public TemperatureLevel getTemperatureLevel(int temperatureLevel) {
		TemperatureLevel tLevel;

		if (temperatureLevel < 80){
			tLevel = TemperatureLevel.LOW;
		} else {
			tLevel = TemperatureLevel.HIGH;
		}

		return tLevel;
	}

	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	public StatusReceiver getStatusReceiver() {
		return statusReceiver;
	}

	public boolean isTempUpdate(Kick kick) {
		KickDevice kickDevice = deviceManager.getDevice(kick.getAddress());
		if(kickDevice != null){
			TemperatureLevel old = kickDevice.getTemperatureLevel();
			TemperatureLevel newT = getTemperatureLevel(kick.getTemperature());
			if (old != newT) {
				return true;
			}
		}
		return false;
	}

	public boolean isBatteryUpdate(Kick kick) {
		KickDevice kickDevice = deviceManager.getDevice(kick.getAddress());
		BatteryLevel old = kickDevice.getBatteryLevel();
		BatteryLevel newT = getBatteryLevel(kick.getBatteryLevel());
		if (old != newT)
			return true;
		else
			return false;
	}
}
