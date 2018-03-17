package com.riftlabs.communicationlib.api.datatypes;

import java.io.Serializable;

/**
 * The Class KickId. Holds the kick id/address information
 */
public class KickId implements Serializable {

	private static final long serialVersionUID = 1L;
	private byte[] id;
	private int idLength;

	public byte[] getId() {
		return id;
	}

	public void setId(byte[] id) {
		this.id = id;
	}

	public int getIdLength() {
		return idLength;
	}

	public void setIdLength(int idLength) {
		this.idLength = idLength;
	}
}
