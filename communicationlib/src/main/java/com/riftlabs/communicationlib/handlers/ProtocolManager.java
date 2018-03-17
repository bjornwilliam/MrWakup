package com.riftlabs.communicationlib.handlers;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

import com.riftlabs.communicationlib.api.datatypes.KickBasePresetEffect;
import com.riftlabs.communicationlib.api.datatypes.KickBrightness;
import com.riftlabs.communicationlib.api.datatypes.KickColor;
import com.riftlabs.communicationlib.api.datatypes.KickEffectID;
import com.riftlabs.communicationlib.api.datatypes.KickExplosionEffect;
import com.riftlabs.communicationlib.api.datatypes.KickId;
import com.riftlabs.communicationlib.api.datatypes.KickLightningStormEffect;
import com.riftlabs.communicationlib.api.datatypes.KickRainbowEffect;
import com.riftlabs.communicationlib.api.datatypes.KickSineEffect;
import com.riftlabs.communicationlib.api.datatypes.KickStrobeEffect;
import com.riftlabs.communicationlib.api.datatypes.KickWhiteBalance;
import com.riftlabs.communicationlib.concurrency.SendSocketDetails;
import com.riftlabs.communicationlib.data.MyKickDevices;
import com.riftlabs.communicationlib.data.UdpPacket;
import com.riftlabs.communicationlib.utils.ConnectionUtils;
import com.riftlabs.communicationlib.utils.Log;

public class ProtocolManager {

	private static final String TAG = ProtocolManager.class.getName();
	// the commands coming from slave to master
	private static final char[] SLAVE_MARKER = { 'R', '$' };
	private static final int SLAVE_RESPONSE_DISCONNECTION = 0x88;
	private static final int SLAVE_RESPONSE_QUERY = 0x83;
	private static final int SLAVE_RESPONSE_HELLO = 0x01;
	private static final int SLAVE_RESPONSE_BRIGHTNESS_CHANGED = 0x06;
	private static final int SLAVE_RESPONSE_WHITEBALANCE_CHANGED = 0x05;
	private static final int SLAVE_RESPONSE_TEMPERATURE_CHANGED = 0x89;
	private static final int SLAVE_RESPONSE_BATTERYLEVEL_CHANGED = 0x90;

	// the commands to send from master to slave
	private static final char[] MASTER_MARKER = { 'R', 'L' };
	private static final int MASTER_RESPONSE_QUERY = 0x83;
	private static final int MASTER_RESPONSE_BRIGHTNESS_CHANGED = 0x06;
	private static final int MASTER_RESPONSE_COLOR_CHANGED = 0x01;
	private static final int MASTER_RESPONSE_MASTER_DISCONNECT = 0x88;
	private static final int MASTER_RESPONSE_PRESET_EFFECT_START = 0x10;
	private static final int MASTER_RESPONSE_PRESET_EFFECT_STOP = 0x11;
	private static final int MASTER_RESPONSE_PRESET_RAINBOW = 0x02;
	private static final int MASTER_RESPONSE_PRESET_LIGHTNINGSTORM = 0x04;
	private static final int MASTER_RESPONSE_PRESET_SINE = 0x05;
	private static final int MASTER_RESPONSE_PRESET_EXPLOSION = 0x03;
	private static final int MASTER_RESPONSE_PRESET_STROBE = 0x01;
	private static final int MASTER_RESPONSE_WHITEBALANCE_CHANGED = 0x05;

	private HashMap<String, SendSocketDetails> sendSocketDetails = new HashMap<String, SendSocketDetails>();

	boolean queryStatusSwitch;
	private CallbackHandler callbackHandler;

	private void onKickAddedCallBack(UdpPacket udpPacket) {
		this.callbackHandler.kickAdded(udpPacket);
	}

	private void onKickDisconnected(UdpPacket udpPacket) {
		this.callbackHandler.kickDisconnected(udpPacket);
	}

	private void onKickBrigtnessChanged(UdpPacket udpPacket) {
		this.callbackHandler.kickBrightnessChanged(udpPacket);
	}

	private void onKickWhiteBalanceChanged(UdpPacket udpPacket) {
		this.callbackHandler.kickWhiteBalanceChanged(udpPacket);
	}

	private void onKickTempChanged(UdpPacket udpPacket) {
		this.callbackHandler.kickTemperatureChanged(udpPacket);
	}

	private void onKickBatteryChanged(UdpPacket udpPacket) {
		this.callbackHandler.kickBatteryLevelChanged(udpPacket);
	}

	public ProtocolManager() {
		queryStatusSwitch = false;
	}

	private DatagramPacket createUdpDatagram(byte[] data, UdpPacket udpPacket) {
		DatagramPacket dataPacket = new DatagramPacket(data, data.length);
		String addressStr = ConnectionUtils.byteArrayToString(udpPacket.getAddress());
		SendSocketDetails sendSocketDetail = sendSocketDetails.get(addressStr);
		dataPacket.setAddress(sendSocketDetail.getAddress());
		dataPacket.setPort(sendSocketDetail.getPort());
		return dataPacket;
	}

	public DatagramPacket generateResponseToSlaveResponse(DatagramPacket packet) {
		DatagramPacket masterResponse = null;
		Log.v(TAG, "Getting packet=" + packet);
		if(packet!=null){
			Log.v(TAG, "Getting packet with details=" + ConnectionUtils.byteArrayToString(packet.getData()));
		}
		
		if (isKickPacket(packet)) {
			final UdpPacket udpPacket = convertDatagramToUdpPacket(packet);
			String addressStr = ConnectionUtils.byteArrayToString(udpPacket.getAddress());
			boolean continueWithoutRestriction = ConnectionUtils.checkKickRestrictions(MyKickDevices.getMyDevicesList(), addressStr);
			if (continueWithoutRestriction) {
				byte[] responseByteStringComplete;
				switch (udpPacket.getCommand()) {
				case SLAVE_RESPONSE_HELLO:
					sendSocketDetails.put(addressStr, new SendSocketDetails(packet.getAddress(), packet.getPort()));
					Log.i(TAG, "Hello response from slave(" + addressStr + ")");
					responseByteStringComplete = generateQueryResponse(udpPacket.getAddress());
					queryStatusSwitch = true;
					masterResponse = createUdpDatagram(responseByteStringComplete, udpPacket);
					break;
				case (byte) SLAVE_RESPONSE_QUERY:
					Log.i(TAG, "Query status response from slave(" + addressStr + ")");
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							onKickAddedCallBack(udpPacket);
						}
					});
					queryStatusSwitch = false;
					break;
				case (byte) SLAVE_RESPONSE_DISCONNECTION:
					Log.i(TAG, "Disconnection response from slave(" + addressStr + ")");
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							onKickDisconnected(udpPacket);
						}
					});
					queryStatusSwitch = false;
					break;
				case (byte) SLAVE_RESPONSE_BRIGHTNESS_CHANGED:
					Log.i(TAG, "Brightness changed response from slave(" + addressStr + ")");
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							onKickBrigtnessChanged(udpPacket);
						}
					});
					queryStatusSwitch = false;
					break;
				case (byte) SLAVE_RESPONSE_WHITEBALANCE_CHANGED:
					Log.i(TAG, "Whitebalance changed response from slave(" + addressStr + ")");
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							onKickWhiteBalanceChanged(udpPacket);
						}
					});
					queryStatusSwitch = false;
					break;
				case (byte) SLAVE_RESPONSE_TEMPERATURE_CHANGED:
					Log.i(TAG, "Temperature changed response from slave(" + addressStr + ")");
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							onKickTempChanged(udpPacket);
						}
					});
					queryStatusSwitch = false;
					break;
				case (byte) SLAVE_RESPONSE_BATTERYLEVEL_CHANGED:
					Log.i(TAG, "Batterylevel changed response from slave(" + addressStr + ")");
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							onKickBatteryChanged(udpPacket);
						}
					});
					queryStatusSwitch = false;
					break;
				default:
					Log.i(TAG, "Unhandled response from slave(" + addressStr + ")");
					if (queryStatusSwitch) {
						responseByteStringComplete = generateQueryResponse(udpPacket.getAddress());
						masterResponse = createUdpDatagram(responseByteStringComplete, udpPacket);
					}
				}
			}
		}
		return masterResponse;
	}

	public DatagramPacket generateResponseToBrightnessChangedRequest(KickId kickId, KickBrightness kickBrightness) {
		byte[] responseData = new byte[] { (byte) kickBrightness.getBrightness() };
		DatagramPacket dataPacket = generateResponseToUserRequest(kickId, MASTER_RESPONSE_BRIGHTNESS_CHANGED, responseData);
		return dataPacket;
	}

	public DatagramPacket generateResponseToColorChangedRequest(KickId kickId, KickColor kickColor) {
		byte[] responseData = new byte[] { (byte) kickColor.getRed(), (byte) kickColor.getGreen(), (byte) kickColor.getBlue() };
		DatagramPacket dataPacket = generateResponseToUserRequest(kickId, MASTER_RESPONSE_COLOR_CHANGED, responseData);
		return dataPacket;
	}

	public DatagramPacket generateResponseToMasterDisconnectRequest(KickId kickId) {
		DatagramPacket dataPacket = generateResponseToUserRequest(kickId, MASTER_RESPONSE_MASTER_DISCONNECT, null);
		return dataPacket;
	}

	public DatagramPacket generateResponseToStartPresetEffect(KickId kickId, KickBasePresetEffect presetEffect) {
		byte[] responseData = null;
		if (presetEffect instanceof KickRainbowEffect) {
			responseData = getKickRainbowEffectData(presetEffect);
		} else if (presetEffect instanceof KickLightningStormEffect) {
			responseData = getKickLightningStormEffectData(presetEffect);
		} else if (presetEffect instanceof KickSineEffect) {
			responseData = getKickSineEffectData(presetEffect);
		} else if (presetEffect instanceof KickExplosionEffect) {
			responseData = getKickExplosionEffectData(presetEffect);
		} else if (presetEffect instanceof KickStrobeEffect) {
			responseData = getKickStrobeEffectData(presetEffect);
		}
		DatagramPacket dataPacket = generateResponseToUserRequest(kickId, MASTER_RESPONSE_PRESET_EFFECT_START, responseData);
		return dataPacket;
	}

	private byte[] getKickStrobeEffectData(KickBasePresetEffect presetEffect) {
		byte[] responseData;
		Log.d(TAG, "Starting KickStrobeEffect");
		KickStrobeEffect kickStrobeEffect = (KickStrobeEffect) presetEffect;
		KickEffectID kickEffectID = new KickEffectID();
		// have to pass this commands outside to make the kick library riftlab independent
		kickEffectID.setEffectUID(new byte[] { (byte) MASTER_RESPONSE_PRESET_STROBE });
		kickStrobeEffect.setKickEffectID(kickEffectID);

		byte uidByte = kickStrobeEffect.getKickEffectID().getEffectUID()[0];
		byte cycleLengthByte1 = kickStrobeEffect.getCycleLength()[0];
		byte cycleLengthByte2 = kickStrobeEffect.getCycleLength()[1];
		byte durationByte1 = kickStrobeEffect.getDuration()[0];
		byte durationByte2 = kickStrobeEffect.getDuration()[1];
		byte lightnessByte = kickStrobeEffect.getLightness();
		byte hueByte1 = kickStrobeEffect.getHue()[0];
		byte hueByte2 = kickStrobeEffect.getHue()[1];
		byte loopByte = kickStrobeEffect.getLoop();
		responseData = new byte[] { uidByte, cycleLengthByte1, cycleLengthByte2, durationByte1, durationByte2, lightnessByte, hueByte1,
				hueByte2, loopByte };
		Log.d(TAG, "preparing command with data[9] length=" + responseData.length);
		return responseData;
	}

	private byte[] getKickExplosionEffectData(KickBasePresetEffect presetEffect) {
		byte[] responseData;
		Log.d(TAG, "Starting KickFireEffect");
		KickExplosionEffect kickExplosionEffect = (KickExplosionEffect) presetEffect;
		KickEffectID kickEffectID = new KickEffectID();
		// have to pass this commands outside to make the kick library riftlab independent
		kickEffectID.setEffectUID(new byte[] { (byte) MASTER_RESPONSE_PRESET_EXPLOSION });
		kickExplosionEffect.setKickEffectID(kickEffectID);

		byte uidByte = kickExplosionEffect.getKickEffectID().getEffectUID()[0];
		byte cycleLengthByte1 = kickExplosionEffect.getCycleLength()[0];
		byte cycleLengthByte2 = kickExplosionEffect.getCycleLength()[1];
		byte durationByte1 = kickExplosionEffect.getDuration()[0];
		byte durationByte2 = kickExplosionEffect.getDuration()[1];
		byte lightnessByte = kickExplosionEffect.getLightness();
		byte hueByte1 = kickExplosionEffect.getHue()[0];
		byte hueByte2 = kickExplosionEffect.getHue()[1];
		byte loopByte = kickExplosionEffect.getLoop();
		
		responseData = new byte[] { uidByte, cycleLengthByte1, cycleLengthByte2, durationByte1, durationByte2, lightnessByte, hueByte1,
				hueByte2, loopByte };
		Log.d(TAG, "preparing command with data[9] length=" + responseData.length);
		return responseData;
	}

	private byte[] getKickSineEffectData(KickBasePresetEffect presetEffect) {
		byte[] responseData;
		Log.d(TAG, "Starting KickSineEffect");
		KickSineEffect kickSineEffect = (KickSineEffect) presetEffect;
		KickEffectID kickEffectID = new KickEffectID();
		// have to pass this commands outside to make the kick library riftlab independent
		kickEffectID.setEffectUID(new byte[] { (byte) MASTER_RESPONSE_PRESET_SINE });
		kickSineEffect.setKickEffectID(kickEffectID);

		byte uidByte = kickSineEffect.getKickEffectID().getEffectUID()[0];
		byte cycleLengthByte1 = kickSineEffect.getCycleLength()[0];
		byte cycleLengthByte2 = kickSineEffect.getCycleLength()[1];
		byte minAmplitudeByte = kickSineEffect.getMinAmplitude();
		byte maxAmplitudeByte = kickSineEffect.getMaxAmplitude();
		byte lightnessByte = kickSineEffect.getLightness();
		byte hueByte1 = kickSineEffect.getHue()[0];
		byte hueByte2 = kickSineEffect.getHue()[1];
		byte loopByte = kickSineEffect.getLoop();
		responseData = new byte[] { uidByte, cycleLengthByte1, cycleLengthByte2, minAmplitudeByte, maxAmplitudeByte, lightnessByte,
				hueByte1, hueByte2, loopByte };
		Log.d(TAG, "preparing command with data[9] length=" + responseData.length);
		return responseData;
	}

	private byte[] getKickLightningStormEffectData(KickBasePresetEffect presetEffect) {
		byte[] responseData;
		Log.d(TAG, "Starting KickLightningStormEffect");
		KickLightningStormEffect kickLightningStormEffect = (KickLightningStormEffect) presetEffect;
		KickEffectID kickEffectID = new KickEffectID();
		// have to pass this commands outside to make the kick library riftlab independent
		kickEffectID.setEffectUID(new byte[] { (byte) MASTER_RESPONSE_PRESET_LIGHTNINGSTORM });
		kickLightningStormEffect.setKickEffectID(kickEffectID);

		byte uidByte = kickLightningStormEffect.getKickEffectID().getEffectUID()[0];
		byte intensityByte = kickLightningStormEffect.getIntensity();
		responseData = new byte[] { uidByte, intensityByte };
		Log.d(TAG, "preparing command with data[2] length=" + responseData.length);
		return responseData;
	}

	private byte[] getKickRainbowEffectData(KickBasePresetEffect presetEffect) {
		byte[] responseData;
		Log.d(TAG, "Starting KickRainbowEffect");
		KickRainbowEffect kickRainbowEffect = (KickRainbowEffect) presetEffect;
		KickEffectID kickEffectID = new KickEffectID();
		// have to pass this commands outside to make the kick library riftlab independent
		kickEffectID.setEffectUID(new byte[] { (byte) MASTER_RESPONSE_PRESET_RAINBOW });
		kickRainbowEffect.setKickEffectID(kickEffectID);

		byte uidByte = kickRainbowEffect.getKickEffectID().getEffectUID()[0];
		byte lightnessStartByte = kickRainbowEffect.getLightnessStart();
		byte hueStartByte1 = kickRainbowEffect.getHueStart()[0];
		byte hueStartByte2 = kickRainbowEffect.getHueStart()[1];
		byte lightnessEndByte = kickRainbowEffect.getLightnessEnd();
		byte hueEndByte1 = kickRainbowEffect.getHueEnd()[0];
		byte hueEndByte2 = kickRainbowEffect.getHueEnd()[1];
		byte directionByte = kickRainbowEffect.getDirection();
		byte cycleLengthByte1 = kickRainbowEffect.getCycleLength()[0];
		byte cycleLengthByte2 = kickRainbowEffect.getCycleLength()[1];
		byte loopByte = kickRainbowEffect.getLoop();
		responseData = new byte[] { uidByte, lightnessStartByte, hueStartByte1, hueStartByte2, lightnessEndByte, hueEndByte1, hueEndByte2,
				directionByte, cycleLengthByte1, cycleLengthByte2, loopByte };
		Log.d(TAG, "preparing command with data[11] length=" + responseData.length);
		return responseData;
	}

	public DatagramPacket generateResponseToStopPresetEffect(KickId kickId, KickBasePresetEffect presetEffect) {
		byte[] responseData = null;
		if (presetEffect instanceof KickRainbowEffect) {
			responseData = new byte[] { Byte.valueOf((byte) 0x00), (byte) MASTER_RESPONSE_PRESET_RAINBOW };
		} else if (presetEffect instanceof KickLightningStormEffect) {
			responseData = new byte[] { (byte) MASTER_RESPONSE_PRESET_LIGHTNINGSTORM };
		} else if (presetEffect instanceof KickSineEffect) {
			responseData = new byte[] { (byte) MASTER_RESPONSE_PRESET_SINE };
		} else if (presetEffect instanceof KickExplosionEffect) {
			responseData = new byte[] { (byte) MASTER_RESPONSE_PRESET_EXPLOSION };
		} else if (presetEffect instanceof KickStrobeEffect) {
			responseData = new byte[] { (byte) MASTER_RESPONSE_PRESET_STROBE };
		}
		DatagramPacket dataPacket = generateResponseToUserRequest(kickId, MASTER_RESPONSE_PRESET_EFFECT_STOP, responseData);
		return dataPacket;
	}

	private DatagramPacket generateResponseToUserRequest(KickId kickId, int command, byte[] responseData) {
		Log.d(TAG, "Calling generateResponseToUserRequest()");
		Byte responseCommand = new Byte((byte) command);
		byte[] id = null;
		if (kickId != null) {
			id = kickId.getId();
		}
		UdpPacket response = new UdpPacket(id, responseCommand, responseData);
		byte[] data = arrangeByteDataForMasterResponse(response);
		DatagramPacket dataPacket = new DatagramPacket(data, data.length);

		String addressStr = ConnectionUtils.byteArrayToString(id);
		SendSocketDetails sendSocketDetail = sendSocketDetails.get(addressStr);
		dataPacket.setAddress(sendSocketDetail.getAddress());
		dataPacket.setPort(sendSocketDetail.getPort());
		return dataPacket;
	}

	private byte[] generateQueryResponse(byte[] address) {
		Log.d(TAG, "Calling generateQueryResponse()");
		byte[] responseByteStringComplete;
		Byte responseCommand = new Byte((byte) MASTER_RESPONSE_QUERY);
		UdpPacket response = new UdpPacket(address, responseCommand, null);
		responseByteStringComplete = arrangeByteDataForMasterResponse(response);
		return responseByteStringComplete;
	}

	public UdpPacket convertDatagramToUdpPacket(DatagramPacket datagramPacket) {
		ByteBuffer byteDataFromDatagramPacket = (ByteBuffer.wrap(datagramPacket.getData()));

		// extracting address, command and dataByte from Datagram
		byte[] addressByte = new byte[] { byteDataFromDatagramPacket.get(2), byteDataFromDatagramPacket.get(3),
				byteDataFromDatagramPacket.get(4) };
		byte commandByte = byteDataFromDatagramPacket.get(7);
		byte[] dataByte = Arrays.copyOfRange(datagramPacket.getData(), 8, datagramPacket.getData().length);

		// Creating udp packet with the extracted values
		UdpPacket packet = new UdpPacket(addressByte, commandByte, dataByte);
		return packet;
	}

	public byte[] arrangeByteDataForMasterResponse(UdpPacket udpPacket) {
		List<Byte> dynamicByteList = new ArrayList<Byte>();
		updateMasterMarker(dynamicByteList);
		updateAddress(udpPacket, dynamicByteList);
		updateLength(udpPacket, dynamicByteList);
		updateCommand(udpPacket, dynamicByteList);
		updateData(udpPacket, dynamicByteList);
		byte[] bytePacket = new byte[dynamicByteList.size()];
		for (int i = 0; i < dynamicByteList.size(); i++) {
			bytePacket[i] = dynamicByteList.get(i);
		}
		return bytePacket;
	}

	private void updateData(UdpPacket udpPacket, List<Byte> dynamicByteList) {
		// adding the data of n bytes
		if (udpPacket.getData() != null) {
			for (byte b : udpPacket.getData()) {
				dynamicByteList.add(Byte.valueOf(b));
			}
		}
	}

	private void updateCommand(UdpPacket udpPacket, List<Byte> dynamicByteArr) {
		// adding the command of 1 byte
		dynamicByteArr.add(Byte.valueOf(udpPacket.getCommand()));
	}

	private void updateLength(UdpPacket udpPacket, List<Byte> dynamicByteArr) {
		// adding the length of 2 bytes for command(1) and data length
		dynamicByteArr.add(Byte.valueOf((byte) 0x00));
		if (udpPacket.getData() != null) {
			dynamicByteArr.add(Byte.valueOf((byte) (1 + udpPacket.getData().length)));
		} else {
			dynamicByteArr.add(Byte.valueOf((byte) 1));
		}
	}

	private void updateAddress(UdpPacket udpPacket, List<Byte> dynamicByteArr) {
		// adding the slave UID of 4 bytes. filling the zeros to address if the
		// address is not having length 4
		if (udpPacket.getAddress() != null) {
			for (int i = udpPacket.getAddress().length; i < 4; i++) {
				dynamicByteArr.add(Byte.valueOf((byte) 0x00));
			}
			for (byte b : udpPacket.getAddress()) {
				dynamicByteArr.add(Byte.valueOf(b));
			}
		}
	}

	private void updateMasterMarker(List<Byte> dynamicByteArr) {
		// adding the marker of 2 bytes
		dynamicByteArr.add(Byte.valueOf((byte) MASTER_MARKER[0]));
		dynamicByteArr.add(Byte.valueOf((byte) MASTER_MARKER[1]));
	}

	private boolean isKickPacket(DatagramPacket packet) {
		// Check marker
		boolean isKickPacket = false;
		if (packet.getData()[0] == Byte.valueOf((byte) SLAVE_MARKER[0]) && packet.getData()[1] == Byte.valueOf((byte) SLAVE_MARKER[1])) {
			isKickPacket = true;
		}
		return isKickPacket;
	}

	public DatagramPacket generateResponseToWhiteBalanceChangedRequest(KickId kickId, KickWhiteBalance kickWhiteBalance) {
		byte[] responseData = ConnectionUtils.intToTwoBytes(kickWhiteBalance.getColorTemperature());
		DatagramPacket dataPacket = generateResponseToUserRequest(kickId, MASTER_RESPONSE_WHITEBALANCE_CHANGED, responseData);
		return dataPacket;
	}

	public void clearSendSocketDetails() {
		sendSocketDetails.clear();
	}

	public void addSendSocketDetails(String key, SendSocketDetails value) {
		sendSocketDetails.put(key, value);
	}

	public void setCallbackHandler(CallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
	}
}
