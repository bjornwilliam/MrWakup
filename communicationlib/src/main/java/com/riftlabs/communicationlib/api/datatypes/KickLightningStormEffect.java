package com.riftlabs.communicationlib.api.datatypes;

public class KickLightningStormEffect extends KickBasePresetEffect {
	public KickLightningStormEffect() {
		super("LightningStormEffect");
	}

	private byte intensity;

	public byte getIntensity() {
		return intensity;
	}

	public void setIntensity(byte intensity) {
		this.intensity = intensity;
	}
}
