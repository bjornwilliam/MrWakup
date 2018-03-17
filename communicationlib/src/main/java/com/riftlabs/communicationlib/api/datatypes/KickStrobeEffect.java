package com.riftlabs.communicationlib.api.datatypes;

public class KickStrobeEffect extends KickBasePresetEffect {
	public KickStrobeEffect() {
		super("StrobeEffect");
	}

	// two bytes
	private byte[] cycleLength;

	// two bytes
	private byte[] duration;

	// one byte, 50-100
	private byte lightness;

	// two bytes, 0-360
	private byte[] hue;

	// 0x00 is loop forever
	private byte loop;

	public byte[] getCycleLength() {
		return cycleLength;
	}

	public void setCycleLength(byte[] cycleLength) {
		this.cycleLength = cycleLength;
	}

	public byte[] getDuration() {
		return duration;
	}

	public void setDuration(byte[] duration) {
		this.duration = duration;
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
