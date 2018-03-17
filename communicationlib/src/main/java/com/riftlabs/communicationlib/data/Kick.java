package com.riftlabs.communicationlib.data;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.io.Serializable;
import java.util.ArrayList;
import com.riftlabs.communicationlib.api.datatypes.KickBrightness;
import com.riftlabs.communicationlib.api.datatypes.KickColor;
import com.riftlabs.communicationlib.api.datatypes.KickWhiteBalance;

public class Kick implements Serializable {
	private static final long serialVersionUID = 1L;
	private BluetoothDevice device;
	private BluetoothGatt gatt;
	private BluetoothGattCharacteristic tx;
	private ArrayList<KickFilter> kickFilters;
	private int filterVersion;
	private int filterCount;
	private int filterIndex;

	private boolean supportsFilters;
	private boolean supportsKelvin;
	private int temperature;
	private int battery;
	private byte[] address;
	private KickAction kickAction;
	private KickBrightness kickBrightness;
	private KickWhiteBalance kickWhiteBalance;
	private KickColor kickColor;
	private boolean isOn = true;
	private int index;

	public Kick(KickAction kickAction, byte[] mAddress, boolean supportsFilters, boolean supportsKelvin) {
		this.kickAction = kickAction;
		this.address = mAddress;
		this.supportsFilters = supportsFilters;
		this.supportsKelvin = supportsKelvin;
	}

	public BluetoothDevice getDevice() { return device; }

	public BluetoothGatt getGatt() { return gatt; }

	public BluetoothGattCharacteristic getTx() { return tx; }

	public KickAction getKickAction() {
		return kickAction;
	}

	public boolean getSupportsFilters() { return supportsFilters; }

	public boolean getSupportsKelvin() { return supportsKelvin; }

	public int getTemperature() {
		return temperature;
	}

	public int getBatteryLevel() {
		return battery;
	}

	public byte[] getAddress() {
		return address;
	}

	public KickBrightness getKickBrightness() {
		return kickBrightness;
	}

	public KickWhiteBalance getKickWhiteBalance() {
		return kickWhiteBalance;
	}

	public KickColor getKickColor() {
		return kickColor;
	}

	public int getIndex() {
		return index;
	}

	public ArrayList<KickFilter> getKickFilters() { return kickFilters; }

	public int getFilterVersion() { return filterVersion; }

	public int getFilterCount() { return filterCount; }

	public int getFilterIndex() { return filterIndex; }

	public boolean isOn() {
		return isOn;
	}

	public boolean areFiltersInitialized() {
		return kickFilters != null
				&& kickFilters.size() == filterCount
				&& kickFilters.get(filterCount - 1).isInitialized();
	}

	public void setDevice(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattCharacteristic tx) {
		this.device = device;
		this.gatt = gatt;
		this.tx = tx;
	}

	public void setAddress(byte[] mAddress) {
		this.address = mAddress;
	}

	public void setTemperature(int temp) { this.temperature = temp; }

	public void setKickAction(KickAction kickAction) {
		this.kickAction = kickAction;
	}

	public void setKickBrightness(KickBrightness kickBrightness) { this.kickBrightness = kickBrightness; }

	public void setBattery(int battery) {
		this.battery = battery;
	}

	public void setKickWhiteBalance(KickWhiteBalance kickWhiteBalance) { this.kickWhiteBalance = kickWhiteBalance; }

	public void setOn(boolean isOn) { this.isOn = isOn; }

	public void setKickColor(KickColor kickColor) {
		this.kickColor = kickColor;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setKickFilters(ArrayList<KickFilter> kickFilters) { this.kickFilters = kickFilters; }

	public void setFilterVersion(int filterVersion) { this.filterVersion = filterVersion; }

	public void setFilterCount(int filterCount) { this.filterCount = filterCount; }

	public void setFilterIndex(int filterIndex) { this.filterIndex = filterIndex; }
}
