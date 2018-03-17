package com.riftlabs.communicationlib.api.datatypes;

import java.io.Serializable;

/**
 * The Class KickColorTemperature. Holds the color temperature information in
 * the kick device
 */
public class KickColorTemperature implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte temperature;

	public byte getTemperature() {
		return temperature;
	}

	public void setTemperature(byte temperature) {
		this.temperature = temperature;
	}
}
