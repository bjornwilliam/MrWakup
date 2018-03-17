package com.riftlabs.communicationlib.api.datatypes;

import java.io.Serializable;

/**
 * The Class KickBrightness. Holds the brightness information in the kick device
 */
public class KickBrightness implements Serializable {
	private static final long serialVersionUID = 1L;
	private int brightness;

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}
}