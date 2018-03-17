package com.riftlabs.communicationlib;

import android.content.Context;

import com.riftlabs.communicationlib.handlers.BluetoothCommunicationHandler;
import com.riftlabs.communicationlib.handlers.CommunicationHandler;

public class KickCommunicationFactory {
	private static KickCommunicationAPI kickCommunicationAPI;

	public static KickCommunicationAPI getKickCommunicationAPI(Context context) {
		if (kickCommunicationAPI == null) {
			 kickCommunicationAPI = new BluetoothCommunicationHandler(context);
//			kickCommunicationAPI = new CommunicationHandlerMock(context);
		}
		return kickCommunicationAPI;
	}

	public static void resetKickCommunicationAPIData() {
		kickCommunicationAPI = null;
	}
}
