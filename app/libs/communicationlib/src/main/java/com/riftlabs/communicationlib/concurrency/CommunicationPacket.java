package com.riftlabs.communicationlib.concurrency;

import com.riftlabs.communicationlib.api.datatypes.KickDatagramPacket;
import com.riftlabs.communicationlib.utils.Log;

/**
 * The Class CommunicationPacket is to wrap the KickDatagramPacket
 */
public class CommunicationPacket implements KickConcurrentPacket {

	private static final String TAG = CommunicationPacket.class.getName();
	private KickDatagramPacket mDatagramPacket;
	private boolean locked = false;

	@Override
	public synchronized void setPacket(KickDatagramPacket datagramPacket) {
		if (datagramPacket != null) {
			while (locked == true) {
				try {
					wait();
				} catch (InterruptedException e) {
					Log.v(TAG, "Interrupted exception", e);
				}
			}
		}
		mDatagramPacket = datagramPacket;
		locked = true;
	}

	@Override
	public synchronized KickDatagramPacket getPacket() {
		return mDatagramPacket;
	}

	public synchronized void releaseLock() {
		locked = false;
		notifyAll();
	}
}