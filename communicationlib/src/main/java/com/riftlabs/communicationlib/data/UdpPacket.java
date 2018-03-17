package com.riftlabs.communicationlib.data;

public class UdpPacket {

	private byte[] address;
	private byte command;
	private byte[] data;

	public UdpPacket(byte[] address, byte command, byte[] data) {
		this.address = address;
		this.command = command;
		this.data = data;
	}

	public byte[] getAddress() {
		return address;
	}

	public void setAddress(byte[] address) {
		this.address = address;
	}

	public byte getCommand() {
		return command;
	}

	public void setCommand(byte command) {
		this.command = command;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
