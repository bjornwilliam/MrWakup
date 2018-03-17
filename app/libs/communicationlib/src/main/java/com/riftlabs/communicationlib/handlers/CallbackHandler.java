package com.riftlabs.communicationlib.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;

import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.api.datatypes.KickBrightness;
import com.riftlabs.communicationlib.api.datatypes.KickColor;
import com.riftlabs.communicationlib.api.datatypes.KickWhiteBalance;
import com.riftlabs.communicationlib.data.Kick;
import com.riftlabs.communicationlib.data.KickAction;
import com.riftlabs.communicationlib.data.UdpPacket;
import com.riftlabs.communicationlib.utils.ConnectionUtils;
import com.riftlabs.communicationlib.utils.Log;

public class CallbackHandler {
	private static final String TAG = CallbackHandler.class.getName();
	List<Kick> connectedKicks;
	private KickCallbacks kickChangedCallback;

	public CallbackHandler() {
		connectedKicks = new ArrayList<Kick>();
	}

	public void removeConnectedKicks() {
		connectedKicks.clear();
	}

	public synchronized Kick kickAdded(UdpPacket udpPacket) {
		Kick kick = parseStatus(udpPacket, KickAction.CONNECTED);
		boolean newKick = isNewKick(kick);
		Log.d(TAG, "newKick=" + newKick);
		if (newKick) {
			kick.setIndex(connectedKicks.size());

			// if its a new kick for the 1st time
			connectedKicks.add(kick);
			kickChangedCallback.onKickAdded(kick);
		} else {
			boolean registeredKick = isRegisteredKick(kick);
			if (registeredKick) {
				// if its a previously registered kick coming from disconnect and reconnect
				kick.setKickAction(KickAction.CONNECTED);
				updateKick(kick);
				kickChangedCallback.onKickReAdded(kick);
			}
		}

		return kick;
	}

	public synchronized Kick kickConnected(Kick kick) {
		boolean newKick = isNewKick(kick);
		Log.w(TAG, "kickConnected: isNewKick -> " + newKick);

		if (newKick) {
			int index = connectedKicks.size();
			kick.setIndex(index);
			Log.w(TAG, "kickConnected: kick.setIndex -> " + index);

			// if its a new kick for the 1st time
			connectedKicks.add(kick);
			kickChangedCallback.onKickAdded(kick);
		} else {
			boolean registeredKick = isRegisteredKick(kick);

			if (registeredKick) {
				// if its a previously registered kick coming from disconnect and reconnect
				kick.setKickAction(KickAction.CONNECTED);
				updateKick(kick);
				kickChangedCallback.onKickReAdded(kick);
			}
		}

		return kick;
	}

	public void kickDisconnected(Kick kick) {
		if (kick != null)
		{
			kick.setKickAction(KickAction.DISCONNECTED);
			if (!isNewKick(kick)) {
				// sending the disconnected only for the registered devices. just a safety check. will not need most of the time
				updateKick(kick);
				kickChangedCallback.onKickDisconnected(kick);
				return;
			}
		}
		Log.e(TAG, "disconnected kick is not a registered kick");
	}

	public void kickChanged(Kick kick) {
		connectedKicks.set(kick.getIndex(), kick);
		kickChangedCallback.onKickBatteryLevelChanged(kick);
		kickChangedCallback.onKickTemperatureChanged(kick);
		kickChangedCallback.onKickWhiteBalanceChanged(kick);
	}

	public void kickDisconnected(UdpPacket udpPacket) {
		Kick disconnectedKick = getConnectedKickByAddress(udpPacket.getAddress());
		if (disconnectedKick != null)
		{
			disconnectedKick.setKickAction(KickAction.DISCONNECTED);
			if (!isNewKick(disconnectedKick)) {
				// sending the disconnected only for the registered devices. just a safety check. will not need most of the time
				updateKick(disconnectedKick);
				kickChangedCallback.onKickDisconnected(disconnectedKick);
				return;
			}
		}
		Log.e(TAG, "disconnected kick is not a registered kick");
	}

	public void kickBrightnessChanged(UdpPacket udpPacket) {
		Kick brightnessChangedKick = getConnectedKickByAddress(udpPacket.getAddress());
		if (brightnessChangedKick != null) {
			brightnessChangedKick.setKickAction(KickAction.BRIGHTNESSCHANGED);

			KickBrightness kickBrightness = new KickBrightness();
			byte[] statusData = udpPacket.getData();
			kickBrightness.setBrightness(ConnectionUtils.toUnsigned(statusData[0]));
			brightnessChangedKick.setKickBrightness(kickBrightness);

			// controlling the onoff status when hardware buttons for brightness is pressing
			if (kickBrightness.getBrightness() > 0) {
				brightnessChangedKick.setOn(true);
			} else {
				brightnessChangedKick.setOn(false);
			}

			updateKick(brightnessChangedKick);
			kickChangedCallback.onKickBrightnessChanged(brightnessChangedKick);
		}
	}

	public void kickWhiteBalanceChanged(UdpPacket udpPacket) {
		Kick whiteBalanceChangedKick = getConnectedKickByAddress(udpPacket.getAddress());
		if (whiteBalanceChangedKick != null) {
			whiteBalanceChangedKick.setKickAction(KickAction.WHITEBALANCECHANGED);
			int newWhiteBalance = ConnectionUtils.twoByteToInt(udpPacket.getData()[0], udpPacket.getData()[1]);
			KickWhiteBalance kickWhiteBalance = new KickWhiteBalance();
			kickWhiteBalance.setColorTemperature(newWhiteBalance);
			whiteBalanceChangedKick.setKickWhiteBalance(kickWhiteBalance);
			kickChangedCallback.onKickWhiteBalanceChanged(whiteBalanceChangedKick);
		}
	}

	public void setProgress(boolean show, String title, String message, int value, int max) {
		kickChangedCallback.setProgress(show, title, message, value, max);
	}

	public void showWarning(String title, String message) {
		kickChangedCallback.showWarning(title, message);
	}

	private boolean isNewKick(Kick newKickStatus) {
		for (Kick kick : connectedKicks) {
			if (Arrays.equals(newKickStatus.getAddress(), kick.getAddress())) {
				return false;
			}
		}
		return true;
	}

	private boolean isRegisteredKick(Kick newKickStatus) {
		boolean registeredKick = false;
		for (Kick kick : connectedKicks) {
			if (Arrays.equals(newKickStatus.getAddress(), kick.getAddress())) {
				registeredKick = true;
				break;
			}
		}
		return registeredKick;
	}

	private void updateKick(Kick kick) {
		connectedKicks.set(kick.getIndex(), kick);
	}

	private Kick getConnectedKickByAddress(byte[] address) {
		Kick connectedKick = null;
		for (Kick kick : connectedKicks) {
			if (Arrays.equals(address, kick.getAddress())) {
				connectedKick = kick;
				break;
			}
		}
		
		if (connectedKick != null) {
			Log.d(TAG,
					"getConnectedKickByAddress="
							+ ConnectionUtils.byteArrayToString(connectedKick
									.getAddress()));
		}
		return connectedKick;
	}

	private Kick parseStatus(UdpPacket udpPacket, KickAction kickAction) {
		// TODO : check if statusData is having the necessary size
		byte[] statusData = udpPacket.getData();
		int temperature = statusData[0];
		int batteryLevel = statusData[1] & (0xff);
		int offset = 2;
		int EVval = 0;
		for (int i = 0; i < 4; i++) {
			EVval <<= 8;
			EVval |= statusData[i + offset] & (0xff);
		}
		float[] CIEXYZVals = new float[3];
		for (int i = 0; i < 3; i++) {
			int offset2 = 6 + i * 4;
			int val = 0;
			for (int j = 0; j < 4; j++) {
				val <<= 8;
				val |= statusData[j + offset2] & (0xff);
			}
			CIEXYZVals[i] = Float.intBitsToFloat(val);
		}
		Log.d(TAG, "Parsing Complete");
		byte[] address = udpPacket.getAddress();
		Kick status = new Kick(kickAction, address, false, false); // temperature, batteryLevel, EVval, CIEXYZVals);
		status.setAddress(udpPacket.getAddress());
		status.setKickBrightness(new KickBrightness());
		status.setKickWhiteBalance(new KickWhiteBalance());
		// color setting to the white. which is default color when u turn on the kick from brightness hardware buttons
		status.setKickColor(new KickColor(255, 255, 255));
		return status;

	}

	public void setKickChangedCallback(KickCallbacks callback) {
		this.kickChangedCallback = callback;
	}

	public void kickTemperatureChanged(UdpPacket udpPacket) {
		Kick temperatureChangedKick = getConnectedKickByAddress(udpPacket.getAddress());
		if (temperatureChangedKick != null) {
			temperatureChangedKick.setKickAction(KickAction.TEMPERATURECHANGED);
			byte newTemeparature = udpPacket.getData()[0];
			boolean isTempChanged = temperatureChangedKick.getTemperature() != newTemeparature;
			if (isTempChanged) {
				temperatureChangedKick.setTemperature(newTemeparature);
				updateKick(temperatureChangedKick);
				kickChangedCallback.onKickTemperatureChanged(temperatureChangedKick);
			}
		}
	}

	public void kickBatteryLevelChanged(UdpPacket udpPacket) {
		Kick batteryLevelChangedKick = getConnectedKickByAddress(udpPacket.getAddress());
		if (batteryLevelChangedKick != null) {
			batteryLevelChangedKick.setKickAction(KickAction.BATTERYLEVELCHANGED);
			byte[] statusData = udpPacket.getData();
			int newBatteryLevel = ConnectionUtils.toUnsigned(statusData[0]);
			batteryLevelChangedKick.setBattery(newBatteryLevel);
			updateKick(batteryLevelChangedKick);
			kickChangedCallback.onKickBatteryLevelChanged(batteryLevelChangedKick);
		}
	}

	public List<Kick> getConnectedKicks() {
		return connectedKicks;
	}
}
