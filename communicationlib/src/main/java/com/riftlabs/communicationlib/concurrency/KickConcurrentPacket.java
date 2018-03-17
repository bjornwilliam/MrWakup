package com.riftlabs.communicationlib.concurrency;

import com.riftlabs.communicationlib.api.datatypes.KickDatagramPacket;

/**
 * The Interface KickConcurrentPacket.
 */
public interface KickConcurrentPacket {
	void setPacket(KickDatagramPacket datagramPacket);
	KickDatagramPacket getPacket();
}
