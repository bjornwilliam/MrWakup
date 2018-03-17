package com.riftlabs.communicationlib.api.datatypes;

public class KickSineEffect extends KickBasePresetEffect {
	public KickSineEffect() {
		super("SineEffect");
	}

	private byte[] cycleLength;
	private byte minAmplitude;
	private byte maxAmplitude;
	private byte lightness;
	private byte[] hue;
	private byte loop;

	public byte[] getCycleLength() {
		return cycleLength;
	}

	public void setCycleLength(byte[] cycleLength) {
		this.cycleLength = cycleLength;
	}

	public byte getMinAmplitude() {
		return minAmplitude;
	}

	public void setMinAmplitude(byte minAmplitude) {
		this.minAmplitude = minAmplitude;
	}

	public byte getMaxAmplitude() {
		return maxAmplitude;
	}

	public void setMaxAmplitude(byte maxAmplitude) {
		this.maxAmplitude = maxAmplitude;
	}

	public byte getLightness() {
		return lightness;
	}

	public void setLightness(byte lightness) {
		this.lightness = lightness;
	}

	public byte[] getHue() {
		return hue;
	}

	public void setHue(byte[] hue) {
		this.hue = hue;
	}

	public byte getLoop() {
		return loop;
	}

	public void setLoop(byte loop) {
		this.loop = loop;
	}

}
