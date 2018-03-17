/*
 * 
 */
package com.riftlabs.communicationlib;

import com.riftlabs.communicationlib.api.datatypes.KickBasePresetEffect;
import com.riftlabs.communicationlib.api.datatypes.KickBrightness;
import com.riftlabs.communicationlib.api.datatypes.KickColor;
import com.riftlabs.communicationlib.api.datatypes.KickId;
import com.riftlabs.communicationlib.api.datatypes.KickWhiteBalance;
import com.riftlabs.communicationlib.data.Kick;

/**
 * The Interface KickCommunicationAPI. Provides the communication signatures to
 * communicate with any kick lighting device
 */
public interface KickCommunicationAPI {
	String getAPIVersion();

	Kick[] getKickFromId(KickId kickId);

	void startConnectionListeners(KickCallbacks callback);

	void stopConnectionListeners(KickCallbacks callback);

	void setDeviceBrightness(KickId kickId, KickBrightness kickBrightness);

	void setDeviceColor(KickId kickId, KickColor kickColor);

	void setMasterDisconnect();

	void startPresetEffect(KickId kickId, KickBasePresetEffect presetEffect);

	void stopPresetEffect(KickId kickId, KickBasePresetEffect presetEffect);
	
	void setDeviceWhiteBalance(KickId kickId, KickWhiteBalance kickWhiteBalance);

	void setDeviceToOn(KickId kickId);

	void setDeviceToOff(KickId kickId);

	void setActiveFilter(KickId kickId, int filterIndex);
}
