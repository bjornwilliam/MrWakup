package com.riftlabs.communicationlib.concurrency;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.content.Context;

import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.handlers.ConnectionHandler;
import com.riftlabs.communicationlib.utils.ConnectionUtils;
import com.riftlabs.communicationlib.utils.Log;

public class PacketSender extends Thread {

	private static final String TAG = PacketSender.class.getName();
	private DatagramSocket mDatagramSocket;
	private CommunicationPacket mCommunicationPacket;
	private ConnectionHandler mConnectionHandler;
	private boolean connected;
	private int transmitInterval;
	private KickCallbacks callback;
	private Context context;

	public PacketSender(DatagramSocket mDatagramSocket, CommunicationPacket mCommunicationPacket, ConnectionHandler connectionHandler, int transmitInterval,
			Context context) {
		super();
		this.mDatagramSocket = mDatagramSocket;
		this.mCommunicationPacket = mCommunicationPacket;
		this.mConnectionHandler = connectionHandler;
		this.transmitInterval = transmitInterval;
		this.context = context;
		this.connected = true;
	}

	@Override
	public void run() {
		while (connected) {
			if (!mConnectionHandler.IsConnectedToRiftNet() || mCommunicationPacket.getPacket() == null) {
				try {
					mCommunicationPacket.releaseLock();
					Thread.sleep(transmitInterval);
				} catch (InterruptedException e) {
					Log.e(TAG, "Error while sleeping", e);
				}
				continue;
			}

			if (mCommunicationPacket.getPacket() != null) {

				DatagramPacket sendPacket = mCommunicationPacket.getPacket()
						.getDatagramPacket();

				if (sendPacket != null) {

					try {

						Thread.sleep(transmitInterval);
						Log.d(TAG, "Sending packet=" + ConnectionUtils.byteArrayToString(sendPacket.getData()));
						mDatagramSocket.send(sendPacket);
						Log.d(TAG, "Packet sent");

						if (mCommunicationPacket.getPacket().isFinishing()) {
							Log.d(TAG, "Set connected to false");
							connected = false;
						}
					} catch (Exception e) {
						ConnectionUtils.callBackError(callback, "Failed in PacketSender.", e, context);
					} finally {
						mCommunicationPacket.setPacket(null);
						mCommunicationPacket.releaseLock();
					}
				}
			}
		}
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public void setCallback(KickCallbacks callback) {
		this.callback = callback;
	}

	public boolean isConnected() {
		return connected;
	}
}
