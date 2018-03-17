package com.riftlabs.communicationlib.api.datatypes;

public class KickEffectID {
	// UID 1 byte when starting. UID 2 bytes when stopping
	private byte[] effectUID;

	public byte[] getEffectUID() {
		return effectUID;
	}

	public void setEffectUID(byte[] effectUID) {
		this.effectUID = effectUID;
	}

}
