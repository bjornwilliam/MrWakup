package com.riftlabs.communicationlib.handlers;

import android.content.Context;
import android.os.AsyncTask;

import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.api.datatypes.KickBasePresetEffect;
import com.riftlabs.communicationlib.api.datatypes.KickBrightness;
import com.riftlabs.communicationlib.api.datatypes.KickColor;
import com.riftlabs.communicationlib.api.datatypes.KickId;
import com.riftlabs.communicationlib.api.datatypes.KickWhiteBalance;
import com.riftlabs.communicationlib.data.Kick;
import com.riftlabs.communicationlib.data.KickAction;
import com.riftlabs.communicationlib.utils.Log;

public class CommunicationHandlerMock extends CommunicationHandler {

	private static final String TAG = CommunicationHandlerMock.class.getName();
	private CallbackHandler callbackHandler;

	public CommunicationHandlerMock(Context context) {
		super(context);
		callbackHandler = new CallbackHandler();
	}

	private class MockCallBacks1 extends AsyncTask<Void, Void, Void> {
		private KickCallbacks callback;

		private MockCallBacks1(KickCallbacks callback) {
			this.callback = callback;
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {
				Kick addedKick1 = new Kick(KickAction.CONNECTED, new byte[] { 11, 11, 11 }, false, false); //0, 0, 0, new float[] { 0.0f });
				callback.onKickAdded(addedKick1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class MockCallBacks2 extends AsyncTask<Void, Void, Void> {
		private KickCallbacks callback;

		private MockCallBacks2(KickCallbacks callback) {
			this.callback = callback;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {
				Kick addedKick2 = new Kick(KickAction.CONNECTED, new byte[] { 22, 22, 22 }, false, false); //0, 0, 0, new float[] { 0.0f });
				callback.onKickAdded(addedKick2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class MockCallBacks3 extends AsyncTask<Void, Void, Void> {
		private KickCallbacks callback;

		private MockCallBacks3(KickCallbacks callback) {
			this.callback = callback;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {
				Kick addedKick3 = new Kick(KickAction.CONNECTED, new byte[] { 33, 33, 33 }, false, false); // 0, 0, 0, new float[] { 0.0f });
				callback.onKickAdded(addedKick3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setDeviceBrightness(KickId kickId, KickBrightness kickBrightness) {
		Log.d(TAG, "Calling mock setDeviceBrightness()");
	}

	@Override
	public String getAPIVersion() {
		Log.d(TAG, "Calling mock getAPIVersion()");
		return "0.0";
	}

    @Override
    public Kick[] getKickFromId(KickId kickId) {
        return new Kick[0];
    }

    @Override
	public void setDeviceColor(KickId kickId, KickColor kickColor) {
		Log.d(TAG, "Calling mock setDeviceColor()");
	}

	@Override
	public void setMasterDisconnect() {
		Log.d(TAG, "Calling mock setMasterDisconnect()");
	}

	@Override
	public void startPresetEffect(KickId kickId, KickBasePresetEffect presetEffect) {
		Log.d(TAG, "Calling mock startPresetEffect()");
	}

	@Override
	public void stopPresetEffect(KickId kickId, KickBasePresetEffect presetEffect) {
		Log.d(TAG, "Calling mock stopPresetEffect()");
	}

	@Override
	public void setDeviceWhiteBalance(KickId kickId, KickWhiteBalance kickWhiteBalance) {
		Log.d(TAG, "Calling mock setDeviceWhiteBalance()");
	}

	@Override
	public void setDeviceToOn(KickId kickId) {
		Log.d(TAG, "Calling mock setDeviceToOn()");
	}

	@Override
	public void setDeviceToOff(KickId kickId) {
		Log.d(TAG, "Calling mock setDeviceToOff()");
	}

	@Override
	public void startConnectionListeners(KickCallbacks callback) {
		callbackHandler.setKickChangedCallback(callback);

		MockCallBacks1 mockCallBacks1 = new MockCallBacks1(callback);
		mockCallBacks1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		MockCallBacks2 mockCallBacks2 = new MockCallBacks2(callback);
		mockCallBacks2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		MockCallBacks3 mockCallBacks3 = new MockCallBacks3(callback);
		mockCallBacks3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
