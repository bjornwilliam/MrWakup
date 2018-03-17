package com.riftlabs.communicationlib.api.datatypes;

import java.io.Serializable;

/**
 * The Class KickDeviceInfo. Holds the kickdevice object and data related to
 * that
 */
public class KickDeviceInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private KickId kickId;
	private char[] description;
	private int descriptionLength;

	public KickId getKickId() {
		return kickId;
	}

	public void setKickId(KickId kickId) {
		this.kickId = kickId;
	}

	public char[] getDescription() {
		return description;
	}

	public void setDescription(char[] description) {
		this.description = description;
	}

	public int getDescriptionLength() {
		return descriptionLength;
	}

	public void setDescriptionLength(int descriptionLength) {
		this.descriptionLength = descriptionLength;
	}

}
