package com.riftlabs.communicationlib.concurrency;

import java.net.InetAddress;

public class SendSocketDetails {
	private InetAddress address;
	private int port;

	public SendSocketDetails(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
