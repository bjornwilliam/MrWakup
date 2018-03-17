package com.riftlabs.communicationlib.utils;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.R;
import com.riftlabs.communicationlib.data.KickConstants;

public class ConnectionUtils {

	private static final String TAG = ConnectionUtils.class.getName();

	private static DatagramSocket dataSocket;
	
	public static DatagramSocket getDatagramSocket() {
		if(dataSocket==null){
			try {
				dataSocket = new DatagramSocket(KickConstants.NETWORK_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
				Log.e(TAG, "Failed to connect to socket");
			}
		}
		return dataSocket;
	}

	public static String byteArrayToString(byte[] address) {
		String addressStr = null;
		if (address != null && address.length > 0) {
			StringBuffer addressBuffer = new StringBuffer();
			for (byte id : address) {
				addressBuffer.append(id);
				addressBuffer.append(".");
			}
			addressStr = addressBuffer.deleteCharAt(addressBuffer.length()-1).toString();
		}

		return addressStr;
	}
	
	public static boolean checkKickRestrictions(String myKickDevices, String addressStr) {
		boolean processWithKick = true;
		if(myKickDevices!=null && !myKickDevices.isEmpty()){
			List<String> myKicks = Arrays.asList(myKickDevices.split("@"));
			if(!myKicks.contains(addressStr)){
				processWithKick = false;
			}
		}
		return processWithKick;
	}
	
	// convert 0,127,128,255 to 0,127,-128,-1
	public static byte toSigned(int b) {
		return (byte) b;
	}

	// will covert 0,127,128,-1 to 0,127,128,255
	public static int toUnsigned(byte b) {
		return b & 0xFF;
	}

	// convert 2 byte value to int. hb=46 lb=-32 means 12000
	public static int twoByteToInt(byte hb, byte lb) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[] { hb, lb });
		return bb.getShort();
	}

	// convert int to 2 byte value. 12000 means hb=46 lb=-32
	public static byte[] intToTwoBytes(int intValue) {
		return new byte[] { (byte) ((intValue >> 8) & 0xFF), (byte) (intValue & 0xFF) };
	}

	public static void callBackError(KickCallbacks callback, String error, Exception e, Context context) {
		Log.e(TAG, "callBackError ", e);
		if (callback != null && context != null) {
			callback.onKickError(context.getResources().getString(R.string.general_error));
		}
	}
}
