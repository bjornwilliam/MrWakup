package com.riftlabs.communicationlib.handlers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;

import android.content.Context;

import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.KickCommunicationAPI;
import com.riftlabs.communicationlib.api.datatypes.KickBasePresetEffect;
import com.riftlabs.communicationlib.api.datatypes.KickBrightness;
import com.riftlabs.communicationlib.api.datatypes.KickColor;
import com.riftlabs.communicationlib.api.datatypes.KickDatagramPacket;
import com.riftlabs.communicationlib.api.datatypes.KickId;
import com.riftlabs.communicationlib.api.datatypes.KickWhiteBalance;
import com.riftlabs.communicationlib.concurrency.PacketSender;
import com.riftlabs.communicationlib.concurrency.SendSocketDetails;
import com.riftlabs.communicationlib.connectors.DataCommunicator;
import com.riftlabs.communicationlib.data.Kick;
import com.riftlabs.communicationlib.data.KickConstants;
import com.riftlabs.communicationlib.utils.ConnectionUtils;
import com.riftlabs.communicationlib.utils.Log;

public class CommunicationHandler implements KickCommunicationAPI {

	private static final String TAG = CommunicationHandler.class.getName();
	private ProtocolManager protocolManager;
	private CallbackHandler callbackHandler;
	private DataCommunicator mDataCommunicator;
	private ConnectionHandler connectionHandler;
	private KickCallbacks callback;
	private Context context;

	public CommunicationHandler(Context context) {
		try {
			// setting the android context for future use
			this.context = context;

			// initializing the main handler classes
			callbackHandler = new CallbackHandler();
			protocolManager = new ProtocolManager();
			connectionHandler = new ConnectionHandler(context);

			// clearing the data and adding default details
			protocolManager.clearSendSocketDetails();
			InetAddress address = InetAddress.getByName(KickConstants.NETWORK_IP);
			SendSocketDetails value = new SendSocketDetails(address, KickConstants.NETWORK_PORT);
			protocolManager.addSendSocketDetails(ConnectionUtils.byteArrayToString(KickConstants.ADDRESS_ALL), value);
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in CommunicationHandler.", e, context);
		}
	}

	@Override
	public void startConnectionListeners(KickCallbacks callback) {
		try {
			if (mDataCommunicator != null && mDataCommunicator.isConnected())
				return;
			
			// setting the callback interface for future use. if its set then it will call onKickError when error occurs
			this.callback = callback;
			callbackHandler.setKickChangedCallback(callback);
			protocolManager.setCallbackHandler(callbackHandler);

			// starting the send and receive threads
			mDataCommunicator = new DataCommunicator(protocolManager, connectionHandler, context);
			mDataCommunicator.setConnected(true);
			mDataCommunicator.connect(callback);

			// starting the wifi connection handler class to start and keep the connection alive
			connectionHandler.setCallback(callback);
			connectionHandler.startWifiScanning();
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in StartConnectionListeners.", e, context);
		}
	}

	@Override
	public void stopConnectionListeners(KickCallbacks callback) {
	}
	@Override
	public void SendWakeUpEffect(KickId kickId, int minutes) {

	}
	@Override
	public void setDeviceBrightness(KickId kickId, KickBrightness kickBrightness) {
		try {
			Log.d(TAG, "Calling KickCommunicationAPI.setDeviceBrightness()");
			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}
			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found. Please set the device address to change the brightness");
			} else {
				// updating the objects with latest brightness values. so we can reuse that in on and off commands.
				//boolean changingBrighnessForTurnOnKick = true;
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId())) {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						kick.setKickBrightness(kickBrightness);
						
						KickId linkKickId = new KickId();
						linkKickId.setId(kick.getAddress());
						
						final DatagramPacket datagramPacket = protocolManager.generateResponseToBrightnessChangedRequest(linkKickId, kickBrightness);
						registerPacketToSend(datagramPacket, false);						
						//changingBrighnessForTurnOnKick = kick.isOn();
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						if (Arrays.equals(kick.getAddress(), kickId.getId())) {
							kick.setKickBrightness(kickBrightness);
						
							final DatagramPacket datagramPacket = protocolManager.generateResponseToBrightnessChangedRequest(kickId, kickBrightness);
							registerPacketToSend(datagramPacket, false);
							//changingBrighnessForTurnOnKick = kick.isOn();
							break;
						}
					}
				}
/*
				if (changingBrighnessForTurnOnKick) {
					final DatagramPacket datagramPacket = protocolManager
							.generateResponseToBrightnessChangedRequest(kickId, kickBrightness);
					registerPacketToSend(datagramPacket, false);
				}
*/					
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in SetDeviceBrightness.", e, context);
		}
	}

	@Override
	public String getAPIVersion() {
		Log.d(TAG, "Calling KickCommunicationAPI.getAPIVersion()");
		return "1.0";
	}

    @Override
    public Kick[] getKickFromId(KickId kickId) {
        return new Kick[0];
    }

    @Override
	public void setDeviceColor(KickId kickId, KickColor kickColor) {
		try {
			Log.d(TAG, "Calling KickCommunicationAPI.setDeviceColor()");
			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}
			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found. Please set the device address to change the brightness");
			} else {
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId())) {
					for (Kick kick : callbackHandler.getConnectedKicks())
					{	
						kick.setKickColor(kickColor);
						
						KickId linkKickId = new KickId();
						linkKickId.setId(kick.getAddress());
						
						final DatagramPacket datagramPacket = protocolManager.generateResponseToColorChangedRequest(linkKickId, kickColor);
						registerPacketToSend(datagramPacket, false);
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						if (Arrays.equals(kick.getAddress(), kickId.getId())) {
							kick.setKickColor(kickColor);
							
							final DatagramPacket datagramPacket = protocolManager.generateResponseToColorChangedRequest(kickId, kickColor);
							registerPacketToSend(datagramPacket, false);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in SetDeviceColor.", e, context);
		}
	}

	@Override
	public void setMasterDisconnect() {
		try {
			if (mDataCommunicator == null)
				return;
			
			Log.d(TAG, "Calling KickCommunicationAPI.setDeviceToDisconnect()");
			setDeviceToOff(null);
			PacketSender packetSender = mDataCommunicator.getPacketSender();
			if (!callbackHandler.getConnectedKicks().isEmpty())
			{
				Iterator<Kick> kickIterator = callbackHandler.getConnectedKicks().iterator();
				while (kickIterator.hasNext())
				{
					Kick kick = kickIterator.next();
					KickId kickId = new KickId();
					kickId.setId(kick.getAddress());
					
					final DatagramPacket datagramPacket = protocolManager.generateResponseToMasterDisconnectRequest(kickId);
					registerPacketToSend(datagramPacket, !kickIterator.hasNext()); // Finish with last packet
				}
				//KickId kickId = null;
				//kickId = new KickId();
				//kickId.setId(KickConstants.ADDRESS_ALL);
				Log.d(TAG, "Going to disconnect all slaves");
			} else {
				packetSender.setConnected(false);
			}
			callbackHandler.removeConnectedKicks();

			Log.d(TAG, "checking last packet has been sent");
			while (packetSender.isConnected()) {
				// waiting till last packet has been sent
				Log.d(TAG, "Waitingggg");
				Thread.sleep(100);
			}
			Log.d(TAG, "going to call stopAllListeners");
			mDataCommunicator.stopAllListeners();
			Log.d(TAG, "going to call stopWifiScanning");
			connectionHandler.stopWifiScanning();
			
			mDataCommunicator = null;
			
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in SetMasterDisconnect.", e, context);
		}
	}

	@Override
	public void startPresetEffect(KickId kickId, KickBasePresetEffect presetEffect) {
		try {
			Log.d(TAG, "Calling stratPresetEffect");
			/*if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}*/
			if (presetEffect == null) {
				Log.e(TAG, "Preset effect not defined correctly. cannot start the effect");
			} else {
				Log.d(TAG, "start presetEffect="+presetEffect.getEffectName());
				if (kickId == null)
				{
					for (Kick kick : callbackHandler.getConnectedKicks())
					{	
						KickId linkKickId = new KickId();
						linkKickId.setId(kick.getAddress());
						
						final DatagramPacket datagramPacket = protocolManager.generateResponseToStartPresetEffect(linkKickId, presetEffect);
						registerPacketToSend(datagramPacket, false);
					}
				}
				else
				{
					final DatagramPacket datagramPacket = protocolManager.generateResponseToStartPresetEffect(kickId, presetEffect);
					registerPacketToSend(datagramPacket, false);
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in StratPresetEffect.", e, context);
		}
	}

	@Override
	public void stopPresetEffect(KickId kickId, KickBasePresetEffect presetEffect) {
		try {
			Log.d(TAG, "Calling stopPresetEffect ");
			/*if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}*/
			if (presetEffect == null) {
				Log.e(TAG, "Preset effect not defined correctly. cannot stop the effect");
			} else {
				Log.d(TAG, "stop presetEffect="+presetEffect.getEffectName());
				if (kickId == null)
				{
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						KickId linkKickId = new KickId();
						linkKickId.setId(kick.getAddress());
						
						final DatagramPacket datagramPacket = protocolManager.generateResponseToStopPresetEffect(linkKickId, presetEffect);
						registerPacketToSend(datagramPacket, false);
					}
				}
				else
				{
					final DatagramPacket datagramPacket = protocolManager.generateResponseToStopPresetEffect(kickId, presetEffect);
					registerPacketToSend(datagramPacket, false);
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in StopPresetEffect.", e, context);
		}
	}

	private void registerPacketToSend(DatagramPacket datagramPacket, boolean finishing) {
		if (mDataCommunicator != null) {
			Log.v(TAG, "Registering packet to Send");
			mDataCommunicator.setConnected(true);
			mDataCommunicator.registerPacket(new KickDatagramPacket(datagramPacket, finishing));
			Log.v(TAG, "Registered packet");
		}
	}

	@Override
	public void setDeviceWhiteBalance(KickId kickId, KickWhiteBalance kickWhiteBalance) {
		try {
			Log.d(TAG, "Calling KickCommunicationAPI.setDeviceWhiteBalance()");
			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}
			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found. Please set the device address to change the WhiteBalance");
			} else {
				KickColor kickColor = null;
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId()))
				{
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						kickColor = kick.getKickColor();
						kickColor.setRed(255);
						kickColor.setGreen(255);
						kickColor.setBlue(255);
						kick.setKickWhiteBalance(kickWhiteBalance);

						KickId linkKickId = new KickId();
						linkKickId.setId(kick.getAddress());

						final DatagramPacket datagramPacket = protocolManager.generateResponseToWhiteBalanceChangedRequest(linkKickId, kickWhiteBalance);
						registerPacketToSend(datagramPacket, false);
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						if (Arrays.equals(kick.getAddress(), kickId.getId()))
						{
							kickColor = kick.getKickColor();
							kickColor.setRed(255);
							kickColor.setGreen(255);
							kickColor.setBlue(255);
							kick.setKickWhiteBalance(kickWhiteBalance);

							final DatagramPacket datagramPacket = protocolManager.generateResponseToWhiteBalanceChangedRequest(kickId, kickWhiteBalance);
							registerPacketToSend(datagramPacket, false);
							break;
						}
					}
				}

				//final DatagramPacket datagramPacket = protocolManager.generateResponseToWhiteBalanceChangedRequest(kickId, kickWhiteBalance);
				//registerPacketToSend(datagramPacket, false);
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in SetDeviceWhiteBalance.", e, context);
		}
	}

	@Override
	public void setDeviceToOn(KickId kickId) {
		try {
			Log.d(TAG, "Calling KickCommunicationAPI.setDeviceToOn()");
			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}
			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found. Please set the device address to change the brightness");
			} else {
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId())) {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						kick.setOn(true);
						KickBrightness kickBrightness = kick.getKickBrightness();
						
						KickId linkKickId = new KickId();
						linkKickId.setId(kick.getAddress());

						final DatagramPacket datagramPacket = protocolManager.generateResponseToBrightnessChangedRequest(linkKickId, kickBrightness);
						registerPacketToSend(datagramPacket, false);
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						if (Arrays.equals(kick.getAddress(), kickId.getId())) {
							kick.setOn(true);
							KickBrightness kickBrightness = kick.getKickBrightness();
							
							final DatagramPacket datagramPacket = protocolManager.generateResponseToBrightnessChangedRequest(kickId, kickBrightness);
							registerPacketToSend(datagramPacket, false);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in SetDeviceToOn.", e, context);
		}
	}

	@Override
	public void setDeviceToOff(KickId kickId) {
		try {
			Log.d(TAG, "Calling KickCommunicationAPI.setDeviceToOff()");
			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}
			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found. Please set the device address to change the brightness");
			} else {
				for (Kick kick : callbackHandler.getConnectedKicks())
				{
					KickBrightness kickBrightness = new KickBrightness();
					kickBrightness.setBrightness(0);

					if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId()))
					{
						kick.setOn(false);

						KickId linkKickId = new KickId();
						linkKickId.setId(kick.getAddress());

						final DatagramPacket datagramPacket = protocolManager.generateResponseToBrightnessChangedRequest(linkKickId, kickBrightness);
						registerPacketToSend(datagramPacket, false);
					}
					else if (Arrays.equals(kick.getAddress(), kickId.getId()))
					{
						kick.setOn(false);

						final DatagramPacket datagramPacket = protocolManager.generateResponseToBrightnessChangedRequest(kickId, kickBrightness);
						registerPacketToSend(datagramPacket, false);
						break;
					}
				}
				
				/*KickBrightness kickBrightness = new KickBrightness();
				kickBrightness.setBrightness(0);
				final DatagramPacket datagramPacket = protocolManager.generateResponseToBrightnessChangedRequest(kickId, kickBrightness);
				registerPacketToSend(datagramPacket, false);*/
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(callback, "Failed in SetDeviceToOff.", e, context);
		}
	}

	@Override
	public void setActiveFilter(KickId kickId, int filterIndex) {
		// Do nothing!
	}
}
