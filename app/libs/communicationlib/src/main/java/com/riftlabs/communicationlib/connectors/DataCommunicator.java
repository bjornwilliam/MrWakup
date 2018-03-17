package com.riftlabs.communicationlib.connectors;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

import android.content.Context;

import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.api.datatypes.KickDatagramPacket;
import com.riftlabs.communicationlib.concurrency.CommunicationPacket;
import com.riftlabs.communicationlib.concurrency.PacketReceiver;
import com.riftlabs.communicationlib.concurrency.PacketSender;
import com.riftlabs.communicationlib.data.KickConstants;
import com.riftlabs.communicationlib.handlers.ConnectionHandler;
import com.riftlabs.communicationlib.handlers.ProtocolManager;
import com.riftlabs.communicationlib.utils.Log;

public class DataCommunicator {

	private static final String TAG = DataCommunicator.class.getName();

	private ProtocolManager mProtocolManager;
	private ConnectionHandler mConnectionHandler;
	private boolean connected = false;
	private int transmitInterval = 5;

	private CommunicationPacket mCommunicationPacket;
	private PacketReceiver mPacketReceiver;
	private PacketSender packetSender;
	private DatagramSocket mDatagramSocket;
	private Context context;

	public DataCommunicator(ProtocolManager protocolManager, ConnectionHandler connectionHandler, Context context) {
		this.mProtocolManager = protocolManager;
		this.mConnectionHandler = connectionHandler;
		this.context = context;
	}

	public void connect(KickCallbacks callback) {

		try {
			//Create the datagram socket
			mDatagramSocket = new DatagramSocket(null);
			mDatagramSocket.setReuseAddress(true);
			mDatagramSocket.setBroadcast(true);
			mDatagramSocket.bind(new InetSocketAddress(
					KickConstants.NETWORK_PORT));
		} catch (SocketException e) {
			Log.e(TAG, "Error creating socket", e);
		}
/*
		try {
			this.mDatagramSocket = new DatagramSocket(KickConstants.NETWORK_PORT);
			this.mDatagramSocket.setBroadcast(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		mCommunicationPacket = new CommunicationPacket();

		// Creating the receiver thread
		mPacketReceiver = new PacketReceiver(mDatagramSocket, mCommunicationPacket, mProtocolManager, mConnectionHandler, context);
		mPacketReceiver.setCallback(callback);

		// Creating the sender thread
		packetSender = new PacketSender(mDatagramSocket, mCommunicationPacket, mConnectionHandler, transmitInterval, context);
		packetSender.setCallback(callback);

		mPacketReceiver.start();
		Log.v(TAG, "Started receiver");
		packetSender.start();
		Log.v(TAG, "Started sender");
	}

	/**
	 * Register packet to be send from the sender thread
	 *
	 * @param datagramPacketContainer the datagram packet container
	 */
	public void registerPacket(KickDatagramPacket datagramPacketContainer) {
		if (mCommunicationPacket != null) {
			Log.v(TAG, "Registered Packet to send from outside");
			mCommunicationPacket.setPacket(datagramPacketContainer);
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public void stopAllListeners() {
		this.connected = false;
		if (mPacketReceiver != null) {
			mPacketReceiver.setConnected(false);
		}
		if (packetSender != null) {
			packetSender.setConnected(false);
		}
	}

	public PacketSender getPacketSender() {
		return packetSender;
	}
}
