package com.riftlabs.communicationlib.api.datatypes;

public class KickRainbowEffect extends KickBasePresetEffect {
	public KickRainbowEffect() {
		super("RainbowEffect");
	}

	// one byte, 50-100
	private byte lightnessStart;

	// two bytes, 0-360
	private byte[] hueStart;

	// one byte, 50-100
	private byte lightnessEnd;

	// two bytes, 0-360
	private byte[] hueEnd;

	private byte direction;
	private byte[] cycleLength;

	// 0x00 is loop forever
	private byte loop;

	public byte getLightnessStart() {
		return lightnessStart;
	}

	public void setLightnessStart(byte lightnessStart) {
		this.lightnessStart = lightnessStart;
	}

	public byte[] getHueStart() {
		return hueStart;
	}

	public void setHueStart(byte[] hueStart) {
		this.hueStart = hueStart;
	}

	public byte getLightnessEnd() {
		return lightnessEnd;
	}

	public void setLightnessEnd(byte lightnessEnd) {
		this.lightnessEnd = lightnessEnd;
	}

	public byte[] getHueEnd() {
		return hueEnd;
	}

	public void setHueEnd(byte[] hueEnd) {
		this.hueEnd = hueEnd;
	}

	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	public byte[] getCycleLength() {
		return cycleLength;
	}

	public void setCycleLength(byte[] cycleLength) {
		this.cycleLength = cycleLength;
	}

	public byte getLoop() {
		return loop;
	}

	public void setLoop(byte loop) {
		this.loop = loop;
	}
}
