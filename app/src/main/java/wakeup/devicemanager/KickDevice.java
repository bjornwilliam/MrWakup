package wakeup.devicemanager;

import com.riftlabs.communicationlib.api.datatypes.KickBasePresetEffect;
import com.riftlabs.communicationlib.api.datatypes.KickSignalStrengthLevels;
import com.riftlabs.communicationlib.data.KickAction;
import com.riftlabs.communicationlib.utils.Log;

public class KickDevice {

	private TemperatureLevel temperatureLevel;
	private BatteryLevel batteryLevel;
	private int eVVal;
	private int color;
	private int brightness;
	private int whiteBalance;
	private float[] cIEXYZvals;
	private byte[] address;
	private int deviceNumber;
	private KickAction kickAction;
	private boolean isOnCannedEffect = false;
	private String Effect = "None";
	private boolean isKickDeviceOn = true;
	private KickSignalStrengthLevels strengthLevel;
	private KickBasePresetEffect playingEffect;
	private boolean isKicksLinked;
	private int activeFilter;
	private String name;

	public boolean isKickDeviceOn() {
		return isKickDeviceOn;
	}

	public void setOnCannedEffect(boolean isOnCannedEffect) { this.isOnCannedEffect = isOnCannedEffect; }
	
	public void setKickDeviceOn(boolean isKickDeviceOn) {
		this.isKickDeviceOn = isKickDeviceOn;
	}

	public TemperatureLevel getTemperatureLevel() {
		return temperatureLevel;
	}

	public void setTemperatureLevel(TemperatureLevel temperatureLevel) { this.temperatureLevel = temperatureLevel; }

	public BatteryLevel getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(BatteryLevel batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public int geteVVal() {
		return eVVal;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void seteVVal(int eVVal) {
		this.eVVal = eVVal;
	}

	public float[] getcIEXYZvals() {
		return cIEXYZvals;
	}

	public void setcIEXYZvals(float[] cIEXYZvals) {
		this.cIEXYZvals = cIEXYZvals;
	}

	public byte[] getAddress() {
		return address;
	}

	public void setAddress(byte[] address) {
		this.address = address;
	}

	public int getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(int deviceNumber) {
		this.deviceNumber = deviceNumber;
		Log.e("KickDevice", "KickDevice @" + this + " setDeviceNumber(" + deviceNumber + ")");
	}

	public KickAction getKickAction() {
		return kickAction;
	}

	public int getActiveFilter() { return activeFilter; }

	public String getName() { return name; }

	public void setKickAction(KickAction kickAction) {
		this.kickAction = kickAction;
	}

	public KickSignalStrengthLevels getStrengthLevel() {
		return strengthLevel;
	}
	
	public void setEffect(String effect) {
		Effect = effect;
	}
 
	public String getEffect() {
		return Effect;
	}
	
	public boolean getOnCannedEffect(){
		return isOnCannedEffect;
	}
	
	public void setStrengthLevel(KickSignalStrengthLevels strengthLevel) { this.strengthLevel = strengthLevel; }

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getWhiteBalance() {
		return whiteBalance;
	}

	public void setWhiteBalance(int whiteBalance) {
		this.whiteBalance = whiteBalance;
	}

	public KickBasePresetEffect getPlayingEffect() {
		return playingEffect;
	}

	public void setPlayingEffect(KickBasePresetEffect playingEffect) { this.playingEffect = playingEffect; }

	public boolean isKicksLinked() {
		return isKicksLinked;
	}

	public void setKicksLinked(boolean isKicksLinked) {
		this.isKicksLinked = isKicksLinked;
	}

	public void setActiveFilter(int activeFilter) { this.activeFilter = activeFilter; }

	public void setName(String name) { this.name = name; }
}
