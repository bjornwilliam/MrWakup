package com.riftlabs.communicationlib.api.datatypes;

import java.io.Serializable;

/**
 * The Class KickStatus. Holds the kick device data information
 */
public class KickStatus implements Serializable {

	private static final long serialVersionUID = 1L;
	private int statusMask;
	private byte temperature;
	private byte batteryLevel;
	private int EV;
	private byte EV2;
	private short CCT;
	private long chroma;
	private KickColor color;
	private String version;
	private String summary;

	public int getStatusMask() {
		return statusMask;
	}

	public void setStatusMask(int statusMask) {
		this.statusMask = statusMask;
	}

	public byte getTemperature() {
		return temperature;
	}

	public void setTemperature(byte temperature) {
		this.temperature = temperature;
	}

	public byte getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(byte batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public int getEV() {
		return EV;
	}

	public void setEV(int eV) {
		EV = eV;
	}

	public byte getEV2() {
		return EV2;
	}

	public void setEV2(byte eV2) {
		EV2 = eV2;
	}

	public short getCCT() {
		return CCT;
	}

	public void setCCT(short cCT) {
		CCT = cCT;
	}

	public long getChroma() {
		return chroma;
	}

	public void setChroma(long chroma) {
		this.chroma = chroma;
	}

	public KickColor getColor() {
		return color;
	}

	public void setColor(KickColor color) {
		this.color = color;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
