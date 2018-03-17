package com.riftlabs.communicationlib;

import com.riftlabs.communicationlib.data.Kick;

/**
 * The Interface KickCallbacks. Which provides all the callback methods
 * returning from KickCommunicationAPI
 */
public interface KickCallbacks {

	void onKickAdded(Kick addedKick);

	void onKickTemperatureChanged(Kick temperatureChanged);

	void onKickReAdded(Kick reAddedKick);

	void onKickDisconnected(Kick disconnectedKick);

	void onKickBrightnessChanged(Kick brightnessChangedKick);

	void onKickWhiteBalanceChanged(Kick whiteBalanceChangedKick);

	void onKickBatteryLevelChanged(Kick batteryLevelChangedKick);
	
	void onKickError(String error);

	void setProgress(boolean show, String title, String message, int value, int max);

    void showWarning(String title, String message);
}
