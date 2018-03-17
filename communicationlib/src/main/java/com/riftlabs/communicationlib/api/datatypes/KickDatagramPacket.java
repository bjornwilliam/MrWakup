package com.riftlabs.communicationlib.api.datatypes;

import java.net.DatagramPacket;

public class KickDatagramPacket {

	private DatagramPacket mDatagramPacket;
	private boolean finishing;

	public KickDatagramPacket(DatagramPacket DatagramPacket, boolean finishing) {
		this.mDatagramPacket = DatagramPacket;
		this.finishing = finishing;
	}

	public DatagramPacket getDatagramPacket() {
		return mDatagramPacket;
	}

	public void setDatagramPacket(DatagramPacket datagramPacket) {
		mDatagramPacket = datagramPacket;
	}

	public boolean isFinishing() {
		return finishing;
	}

	public void setFinishing(boolean finishing) {
		this.finishing = finishing;
	}
}
