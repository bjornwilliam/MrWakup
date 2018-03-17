package com.riftlabs.communicationlib.api.datatypes;

/**
 * The Class KickWhiteBalance. Holds the white balance/color temperature in the kick device
 */
public class KickWhiteBalance {
	private int colorTemperature;

	public int getColorTemperature() {
		return colorTemperature;
	}

	public void setColorTemperature(int colorTemperature) {
		this.colorTemperature = colorTemperature;
	}
}