package com.riftlabs.communicationlib.concurrency;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.content.Context;

import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.api.datatypes.KickDatagramPacket;
import com.riftlabs.communicationlib.data.KickConstants;
import com.riftlabs.communicationlib.handlers.ConnectionHandler;
import com.riftlabs.communicationlib.handlers.ProtocolManager;
import com.riftlabs.communicationlib.utils.ConnectionUtils;
import com.riftlabs.communicationlib.utils.Log;

/**
 * Purpose of this class is to receive datagram packets and register packets to
 * be sent
 */
public class PacketReceiver extends Thread {

	private static final String TAG = PacketReceiver.class.getName();
	private DatagramSocket mDatagramSocket;
	private CommunicationPacket mCommunicationPacket;
	private ConnectionHandler mConnectionHandler;
	private ProtocolManager mProtocolManager;
	private boolean connected;
	private KickCallbacks callback;
	private Context context;

	public PacketReceiver(DatagramSocket mDatagramSocket, CommunicationPacket mCommunicationPacket, ProtocolManager mProtocolManager,
			ConnectionHandler connectionHandler, Context context) {
		super();
		this.mDatagramSocket = mDatagramSocket;
		this.mCommunicationPacket = mCommunicationPacket;
		this.mProtocolManager = mProtocolManager;
		this.mConnectionHandler = connectionHandler;
		this.context = context;
		this.connected = true;
	}

	@Override
	public void run() {

		while (connected) {

			if (mConnectionHandler.IsConnectedToRiftNet())
			{
				byte[] data = new byte[128];
				DatagramPacket dataPacket = new DatagramPacket(data, data.length);
				//if (mDatagramSocket.isConnected())
				{
					try {
		
						Log.v(TAG, "Listening on Socket");
						mDatagramSocket.receive(dataPacket);
		
						DatagramPacket datagramPacketReceived = mProtocolManager
								.generateResponseToSlaveResponse(dataPacket);
		
						if (datagramPacketReceived != null) {
							Log.v(TAG, "Setting Packet");
							//Register packet to be sent
							mCommunicationPacket.setPacket(new KickDatagramPacket(
									datagramPacketReceived, false));
						}
					} catch (IOException e) {
						ConnectionUtils.callBackError(callback, "Faild in PacketReceiver.", e, context);
						Log.e(TAG, "Error while receiving on socket", e);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							Log.e(TAG, "Error while sleeping inside exception", e);
						}
					}
				}
			}
			//else
			{
				try {
					Thread.sleep(200);
				}
				catch (Exception e) {
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
}
