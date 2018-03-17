package com.riftlabs.communicationlib.api.datatypes;

import java.io.Serializable;

/**
 * The Class KickColor. Holds the color information in the kick device
 */
public class KickColor implements Serializable {
	private static final long serialVersionUID = 1L;
	private int red;
	private int green;
	private int blue;

	public KickColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

}
