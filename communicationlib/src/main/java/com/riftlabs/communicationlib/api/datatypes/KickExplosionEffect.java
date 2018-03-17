package com.riftlabs.communicationlib.api.datatypes;

public class KickExplosionEffect extends KickBasePresetEffect {
	public KickExplosionEffect() {
		super("ExplosionEffect");
	}

	private byte[] cycleLength;
	private byte[] duration;
	private byte lightness;
	private byte[] hue;
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
