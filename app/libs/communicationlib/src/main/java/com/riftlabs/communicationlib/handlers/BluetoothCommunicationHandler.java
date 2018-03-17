package com.riftlabs.communicationlib.handlers;

import android.content.Context;
import android.bluetooth.*;
import android.os.Handler;
import android.os.Looper;
import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.KickCommunicationAPI;
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
import com.riftlabs.communicationlib.data.Kick;
import com.riftlabs.communicationlib.data.KickAction;
import com.riftlabs.communicationlib.data.KickConstants;
import com.riftlabs.communicationlib.data.KickFilter;
import com.riftlabs.communicationlib.utils.ConnectionUtils;
import com.riftlabs.communicationlib.utils.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class BluetoothCommunicationHandler implements KickCommunicationAPI, BluetoothAdapter.LeScanCallback {
	private static final String TAG = BluetoothCommunicationHandler.class.getName();
	private CallbackHandler callbackHandler;
	private Context context;
	private BluetoothAdapter bluetoothAdapter;
	private ArrayList<String> devices = new ArrayList<>();
	private Handler handler;
	private Semaphore filterSyncLocker = new Semaphore(1);
	private int deviceConnectCount = 0;

	public static final UUID ServiceUUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
	public static final UUID txCharacteristicUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
	public static final UUID rxCharacteristicUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

	public BluetoothCommunicationHandler(Context context) {
		try {
			this.context = context;
			this.handler = new Handler(Looper.getMainLooper());
			callbackHandler = new CallbackHandler();
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in CommunicationHandler.", e, context);
		}
	}

	@Override
	public void startConnectionListeners(KickCallbacks callback) {
		if (bluetoothAdapter == null) {
			BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = bluetoothManager.getAdapter();
		}

		if (bluetoothAdapter == null) {
			Log.e("BTLE", "bluetoothAdapter is null -> exiting startConnectionListeners!");
			return;
		}

		try {
			Log.e("BTLE", "bluetoothAdapter.startLeScan...");
			callbackHandler.setKickChangedCallback(callback);
			deviceConnectCount = 0;
			bluetoothAdapter.startLeScan(this);

			/*
			final BluetoothCommunicationHandler btHandler = this;

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					bluetoothAdapter.startLeScan(/*new UUID[]{ServiceUUID}, btHandler);
				}
			};

			handler.post(runnable);*/
		} catch (Exception e) {
			callbackHandler.showWarning("Couldn't access Bluetooth", "This app uses bluetooth to connect to Luxli devices. Please restart app and enable bluetooth!");
		}
	}

	@Override
	public void stopConnectionListeners(KickCallbacks callback) {
		Log.w("BTLE", "Stopping LE scan...");
		bluetoothAdapter.stopLeScan(this);
		callbackHandler.setKickChangedCallback(callback);
		deviceConnectCount = 0;
	}

	@Override
	public void onLeScan (final BluetoothDevice device, int rssi, byte[] scanRecord) {
		if (devices.contains(device.getAddress()) || device.getName() == null)
			return;

		Log.e("BTLE", "Found bluetooth device '" + device.getName() + "@" + device.getAddress());
		deviceConnectCount++;

		if (deviceConnectCount == 50) {
			callbackHandler.showWarning("Connection issues?", "Your phones bluetooth might have become unstable. If you can't connect:\n\n"
					+ "1. Close this app and turn off bluetooth and start it again.\n\n"
					+ "2. If that doesn't help: Try restarting your phone.");
		}

		devices.add(device.getAddress());

		final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
			private Kick kick;
			private BluetoothGatt gatt;
			private BluetoothGattCharacteristic tx;
			private BluetoothGattCharacteristic rx;

			public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
				if (newState == BluetoothProfile.STATE_CONNECTED) {
					Log.e("BTLE", "Connected to GATT server.");
					this.gatt = gatt;
					Log.e("BTLE", "Starting service discovery...");
					gatt.discoverServices();
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					Log.e("BTLE", "Disconnected from GATT server (status " + status + ")");
					tx = null;
					rx = null;
					this.gatt = null;
					devices.remove(gatt.getDevice().getAddress());

					if (kick != null) {
						Runnable runnable = new Runnable() {
							@Override
							public void run() {
								callbackHandler.kickDisconnected(kick);
								Log.w("BTLE", "Removed kick @" + toHexString(kick.getAddress()));
								kick = null;
							}
						};
						handler.post(runnable);
					}
				}
			}

			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				if (status != BluetoothGatt.GATT_SUCCESS) {
					Log.e("BTLE", "Service discovery failed with status: " + status);
					return;
				}

				BluetoothGattService service = gatt.getService(ServiceUUID);

				if (service == null) {
					Log.e("BTLE", "Service [" + ServiceUUID + "] not found! :(");
					return;
				}

				Log.e("BTLE", "Service [" + ServiceUUID + "] found :)");

				tx = service.getCharacteristic(txCharacteristicUUID);

				if (tx == null) {
					Log.e("BTLE", "TX Characteristic not found! :(");
					return;
				}

				Log.e("BTLE", "TX Characteristic found :)");

				rx = service.getCharacteristic(rxCharacteristicUUID);

				if (rx == null) {
					Log.e("BTLE", "RX Characteristic not found! :(");
					return;
				}

				Log.e("BTLE", "RX Characteristic found :)");

				if (gatt.setCharacteristicNotification(rx, true))
					Log.e("BTLE", "RX Characteristic Notification enabled! :)");
				else
					Log.e("BTLE", "Unable to enable RX Characteristic Notification! :(");

				BluetoothGattDescriptor descriptor = rx.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
				Log.e("BTLE", "RX Characteristic descriptor -> " + descriptor);

				if (descriptor != null) {
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					gatt.writeDescriptor(descriptor);
				}
			}

			public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
				Log.i("BTLE", "Characteristic [" + characteristic.getUuid() + "] read triggered!");
			}

			public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
				Log.i("BTLE", "Characteristic [" + characteristic.getUuid() + "] write triggered! -> status " + status);
			}

			private int currentFilterCategory;
			private int currentFilterIndex;
			private byte currentFilterDataType;
			private byte currentFilterPacketNumber;

			public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
				byte[] data = characteristic.getValue();
				Log.w("BTLE", "RX -> " + toHexString(data));

				if (data[7] == (byte)0x99) {
					String[] addressParts = device.getAddress().split(":");
					byte[] addressBytes = new byte[addressParts.length];
					for (int i=0; i<addressParts.length; i++) {
						addressBytes[i] = (byte)Integer.parseInt(addressParts[i], 16);
					}

					boolean supportsFilters = ((data[18] & 0x1) == 0x1);
					boolean supportsKelvin = ((data[18] & 0x2) == 0x2);
					kick = new Kick(KickAction.CONNECTED, addressBytes, supportsFilters, supportsKelvin);
					kick.setKickBrightness(new KickBrightness());
					kick.setKickWhiteBalance(new KickWhiteBalance());
					kick.setKickColor(new KickColor(255, 255,255));
					kick.setDevice(device, this.gatt, this.tx);
					Log.w("BTLE", "Adding kick @" + toHexString(kick.getAddress()) + "...");

					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							callbackHandler.kickConnected(kick);
							Log.w("BTLE", "Added kick @" + toHexString(kick.getAddress()));
							BluetoothCommunicationHandler.this.sendData((byte)0x83, null, gatt, tx);
						}
					};

					handler.post(runnable);
				} else if (data[7] == (byte) 0x83) {
					Log.w("BTLE", "Getting temp & battery -> " + data[8] + " & " + (data[9] & 0xFF));
					final byte temp = data[8];
					final int battery = data[9] & 0xFF;

					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							kick.setTemperature(temp);
							kick.setKickAction(KickAction.TEMPERATURECHANGED);
							callbackHandler.kickChanged(kick);
							kick.setBattery(battery);
							kick.setKickAction(KickAction.BATTERYLEVELCHANGED);
							callbackHandler.kickChanged(kick);

							if (kick.getSupportsFilters()) {
								BluetoothCommunicationHandler.this.sendData((byte) 0x18, null, gatt, tx);
							} else if (kick.getSupportsKelvin()) {
								BluetoothCommunicationHandler.this.sendData((byte) 0x9D, null, gatt, tx);
							}
						}
					};

					handler.post(runnable);
				} else if (data[7] == (byte)0xA2) {
					final int filterIndex = ((data[10] & 0xFF) << 8) + (data[11] & 0xFF);
					int kelvin = ((data[12] & 0xFF) << 8) + (data[13] & 0xFF);
					final KickWhiteBalance whiteBalance = new KickWhiteBalance();
					whiteBalance.setColorTemperature(kelvin);
					Log.w("BTLE", "Getting kelvin -> " + kelvin);

					handler.post(new Runnable() {
						@Override
						public void run() {
							kick.setKickWhiteBalance(whiteBalance);
							kick.setKickAction(KickAction.WHITEBALANCECHANGED);
							callbackHandler.kickChanged(kick);
							kick.setFilterIndex(filterIndex);
							kick.setKickAction(KickAction.ACTIVEFILTERCHANGED);
							callbackHandler.kickChanged(kick);
							BluetoothCommunicationHandler.this.sendData((byte) 0x14, null, gatt, tx);
						}
					});
				} else if (data[7] == (byte)0x05) {
					int kelvin = ((data[8] & 0xFF) << 8) + (data[9] & 0xFF);
					final KickWhiteBalance whiteBalance = new KickWhiteBalance();
					whiteBalance.setColorTemperature(kelvin);
					Log.w("BTLE", "Getting kelvin -> " + kelvin);

					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							kick.setKickWhiteBalance(whiteBalance);
							kick.setKickAction(KickAction.WHITEBALANCECHANGED);
							callbackHandler.kickChanged(kick);

							if (kick.getSupportsFilters() && !kick.areFiltersInitialized()) {
								BluetoothCommunicationHandler.this.sendData((byte)0x14, null, gatt, tx);
							}
						}
					};

					handler.post(runnable);
				} else if (data[7] == (byte)0xA1) {
					int filterVersion = ((data[8] & 0xFF) << 8) + (data[9] & 0xFF);

					if (kick.getFilterVersion() == filterVersion)
						return;

					// Check if it's the default filters
					if (filterVersion == 8193) {
						Log.w(TAG, "Setting default filter data (v" + filterVersion + ").");
						kick.setFilterVersion(filterVersion);
						kick.setKickFilters(new ArrayList<KickFilter>());
						KickFilter[] defaultFilters = KickFilter.GetDefaultFilters();
						kick.setFilterCount(defaultFilters.length);

						for (int i = 0; i < defaultFilters.length; i++) {
							defaultFilters[i].setInitialized(true);
							kick.getKickFilters().add(defaultFilters[i]);
						}

						return;
					}

					try {
						BluetoothCommunicationHandler.this.filterSyncLocker.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					kick.setFilterVersion(filterVersion);
					kick.setKickFilters(null);
					String filterDataFile = "filter_data_v" + kick.getFilterVersion();

					try {
						if (context.getFileStreamPath(filterDataFile).exists()) {
							Log.w(TAG, "Loading filter data from internal storage...");
							FileInputStream fis = context.openFileInput(filterDataFile);
							ObjectInputStream is = new ObjectInputStream(fis);
							kick.setKickFilters((ArrayList<KickFilter>) is.readObject());
							kick.setFilterCount(kick.getKickFilters().size());
							BluetoothCommunicationHandler.this.filterSyncLocker.release();
							is.close();
							fis.close();
						}
					} catch (Exception ex) {
						Log.e(TAG, "Error reading filter data file!!!");
					}

					if (kick.getKickFilters() == null) {
						//TODO: Skipping cateogry-check (CMD 0x15) for now...
						BluetoothCommunicationHandler.this.sendData((byte) 0x16, null, gatt, tx);
					}
				} else if (data[7] == (byte)0xA4) {
					if (kick.areFiltersInitialized()) return; //QUIRK: jump out if filters are initialized

					final int filterCount = ((data[8] & 0xFF) << 8) + (data[9] & 0xFF);
					kick.setFilterCount(filterCount);
					currentFilterIndex = 0;
					currentFilterCategory = 0;
					currentFilterDataType = 0x3;
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							BluetoothCommunicationHandler.this.callbackHandler.setProgress(true, "Sync", "Syncing Cello filter data...", 0, filterCount);
						}
					};

					handler.post(runnable);

					BluetoothCommunicationHandler.this.sendData((byte)0x17, new byte[] { (byte)currentFilterCategory, 0x0, (byte)((currentFilterIndex >> 8) & 0xFF), (byte)(currentFilterIndex & 0xFF), currentFilterDataType, 0x0 }, gatt, tx);
				} else if (data[7] == (byte)0xA0) {
					if (kick.areFiltersInitialized()) return; //QUIRK: jump out if filters are initialized

					if (kick.getKickFilters() == null)
						kick.setKickFilters(new ArrayList<KickFilter>());

					KickFilter filter = null;
					for (int i = kick.getKickFilters().size() - 1; i >= 0; i--) {
						KickFilter f = kick.getKickFilters().get(i);
						if (f.getCategory() == currentFilterCategory && f.getIndex() == currentFilterIndex) {
							filter = f;
							break;
						}
					}

					if (filter == null) {
						filter = new KickFilter();
						filter.setCategory(currentFilterCategory);
						filter.setIndex(currentFilterIndex);
						kick.getKickFilters().add(filter);
					}

					switch (currentFilterDataType) {
						case 0x3:
							filter.setR(data[8]);
							filter.setG(data[9]);
							filter.setB(data[10]);
							currentFilterDataType = 0x1;
							BluetoothCommunicationHandler.this.sendData((byte)0x17, new byte[] { (byte)currentFilterCategory, 0x0, (byte)((currentFilterIndex >> 8) & 0xFF), (byte)(currentFilterIndex & 0xFF), currentFilterDataType, 0x0 }, gatt, tx);
							break;
						case 0x1:
							String name = filter.getName() == null ? "" : filter.getName();
							String namePart = "";
							boolean nullTerminated = false;

							for (int i = 8; i < data.length; i++) {
								if (data[i] == 0x0) {
									nullTerminated = true;
									break;
								}

								namePart += (char)data[i];
							}

							filter.setName(name + namePart);

							if (nullTerminated) {
								filter.setInitialized(true);
								Log.w("BTLE", "Filter " + filter.getName() + " sync done.");

								Runnable runnable = new Runnable() {
									@Override
									public void run() {
										BluetoothCommunicationHandler.this.callbackHandler.setProgress(true, "Sync", "Syncing Cello filter data...", currentFilterIndex + 1, kick.getFilterCount());
									}
								};

								handler.post(runnable);

								if (!kick.areFiltersInitialized()) {
									currentFilterIndex++;
									currentFilterPacketNumber = 0;
									currentFilterDataType = 0x3;
									BluetoothCommunicationHandler.this.sendData((byte)0x17, new byte[] { (byte)currentFilterCategory, 0x0, (byte)((currentFilterIndex >> 8) & 0xFF), (byte)(currentFilterIndex & 0xFF), currentFilterDataType, 0x0 }, gatt, tx);
								} else {
									Log.w("BTLE", "Filters sync done! (version " + kick.getFilterVersion() + ")");

									try {
										FileOutputStream fos = context.openFileOutput("filter_data_v" + kick.getFilterVersion(), Context.MODE_PRIVATE);
										ObjectOutputStream os = new ObjectOutputStream(fos);
										os.writeObject(kick.getKickFilters());
										os.close();
										fos.close();
										Log.w(TAG, "Filter data saved.");
									}
									catch(Exception ex) {
										// Faawk!! Show some dialog here
										Log.e(TAG, "Unable to save filter data!!!");
									}

									BluetoothCommunicationHandler.this.filterSyncLocker.release();
									runnable = new Runnable() {
										@Override
										public void run() {
											BluetoothCommunicationHandler.this.callbackHandler.setProgress(false, "", "", 0, 0);
										}
									};

									handler.post(runnable);
								}
							} else {
								currentFilterPacketNumber++;
								BluetoothCommunicationHandler.this.sendData((byte)0x17, new byte[] { (byte)currentFilterCategory, 0x0, (byte)((currentFilterIndex >> 8) & 0xFF), (byte)(currentFilterIndex & 0xFF), currentFilterDataType, currentFilterPacketNumber }, gatt, tx);
							}
							break;
						default:
							Log.e("BTLE", "Unknown/unsupported 0xA0 currentFilterDataType " + currentFilterDataType);
							break;
					}
				}
			}

			public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
				Log.e("BTLE", "Descriptor read [" + descriptor.getUuid() + "] triggered! -> status " + status);
			}

			public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
				Log.e("BTLE", "Descriptor write [" + descriptor.getUuid() + "] triggered! -> status " + status);
				BluetoothCommunicationHandler.this.sendData((byte)0x99, null, gatt, tx);
			}

			public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
				Log.e("BTLE", "Reliable write completed -> status " + status);
			}

			public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
				Log.e("BTLE", "Read remote Rssi -> status " + status);
			}
		};

		device.connectGatt(context, true, gattCallback);
	}

	private static String toHexString(byte[] bytes) {
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];

		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		return new String(hexChars);
	}

	private void sendData(byte command, byte[] data, BluetoothGatt gatt, BluetoothGattCharacteristic tx)
	{
		byte[] fullData = new byte[9 + (data != null ? data.length : 0)];
		fullData[0] = (byte)'R';
		fullData[1] = (byte)'L';
		fullData[2] = 0x00; // group
		fullData[3] = 0x00; // address 1
		fullData[4] = 0x00; // address 2
		fullData[5] = 0x00; // address 3
		fullData[6] = 0x00; // length 1
		fullData[7] = (byte) (0x01 + (data != null ? data.length : 0));
		fullData[8] = command;

		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				fullData[9 + i] = data[i];
			}
		}

		Log.w("BTLE", "TX -> " + toHexString(fullData));
		tx.setValue(fullData);
		tx.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
		boolean writeResult = gatt.writeCharacteristic(tx);

		if (!writeResult)
			Log.e("BTLE", "TX failed!");
	}

	@Override
	public void setDeviceBrightness(KickId kickId, KickBrightness kickBrightness) {
		try {
			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}

			Log.i(TAG, "setDeviceBrightness brightness " + kickBrightness.getBrightness() + " @" + toHexString(kickId.getId()));

			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found...");
			} else {
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId())) {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						kick.setKickBrightness(kickBrightness);
						sendData((byte)0x06, new byte[] {(byte)kickBrightness.getBrightness()}, kick.getGatt(), kick.getTx());
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						if (Arrays.equals(kick.getAddress(), kickId.getId())) {
							kick.setKickBrightness(kickBrightness);
							sendData((byte)0x06, new byte[] {(byte)kickBrightness.getBrightness()}, kick.getGatt(), kick.getTx());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in SetDeviceBrightness.", e, context);
		}
	}

	@Override
	public String getAPIVersion() {
		Log.d(TAG, "Calling KickCommunicationAPI.getAPIVersion()");
		return "1.0";
	}

	@Override
	public Kick[] getKickFromId(KickId kickId) {
		if (kickId.getId() == null || kickId.getId().length == 0) {
			return new Kick[0];
		} else {
			if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId())) {
				return (Kick[])callbackHandler.getConnectedKicks().toArray();
			} else {
				for (Kick kick : callbackHandler.getConnectedKicks()) {
					if (Arrays.equals(kick.getAddress(), kickId.getId())) {
						return new Kick[] { kick };
					}
				}
			}
		}

		return new Kick[0];
	}

	@Override
	public void setDeviceColor(KickId kickId, KickColor kickColor) {
		try {
			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}

			byte[] data = new byte[] { (byte)kickColor.getRed(), (byte)kickColor.getGreen(), (byte)kickColor.getBlue() };
			Log.i(TAG, "setDeviceColor RGB " + toHexString(data) + " @" + toHexString(kickId.getId()));

			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found...");
			} else {
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId())) {
					for (Kick kick : callbackHandler.getConnectedKicks())
					{	
						kick.setKickColor(kickColor);
						sendData((byte)0x01, data, kick.getGatt(), kick.getTx());
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						if (Arrays.equals(kick.getAddress(), kickId.getId())) {
							kick.setKickColor(kickColor);
							sendData((byte)0x01, data, kick.getGatt(), kick.getTx());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in SetDeviceColor.", e, context);
		}
	}

	@Override
	public void setMasterDisconnect() {
		try {
			for (Kick kick : callbackHandler.getConnectedKicks()) {
				Log.w("BTLE", "Disconnect GATT for kick device @" + kick.getDevice().getAddress());
				kick.getGatt().disconnect();
				Log.w("BTLE", "Close GATT for kick device @" + kick.getDevice().getAddress());
				kick.getGatt().close();
			}

			devices.clear();
			callbackHandler.removeConnectedKicks();
			deviceConnectCount = 0;
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in SetDeviceColor.", e, context);
		}
	}

	@Override
	public void startPresetEffect(KickId kickId, KickBasePresetEffect presetEffect) {
		try {
			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}

			byte[] data = null;
			if (presetEffect instanceof KickRainbowEffect) {
				data = getKickRainbowEffectData(presetEffect);
			} else if (presetEffect instanceof KickLightningStormEffect) {
				data = getKickLightningStormEffectData(presetEffect);
			} else if (presetEffect instanceof KickSineEffect) {
				data = getKickSineEffectData(presetEffect);
			} else if (presetEffect instanceof KickExplosionEffect) {
				data = getKickExplosionEffectData(presetEffect);
			} else if (presetEffect instanceof KickStrobeEffect) {
				data = getKickStrobeEffectData(presetEffect);
			}

			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found...");
			} else {
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId())) {
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						sendData((byte)0x10, data, kick.getGatt(), kick.getTx());
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						if (Arrays.equals(kick.getAddress(), kickId.getId())) {
							sendData((byte)0x10, data, kick.getGatt(), kick.getTx());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in SetDeviceColor.", e, context);
		}
	}

	private static final int MASTER_RESPONSE_PRESET_STROBE = 0x01;
	private static final int MASTER_RESPONSE_PRESET_RAINBOW = 0x02;
	private static final int MASTER_RESPONSE_PRESET_EXPLOSION = 0x03;
	private static final int MASTER_RESPONSE_PRESET_LIGHTNINGSTORM = 0x04;
	private static final int MASTER_RESPONSE_PRESET_SINE = 0x05;

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
		responseData = new byte[] { uidByte, cycleLengthByte1, cycleLengthByte2, durationByte1, durationByte2, lightnessByte, hueByte1, hueByte2, loopByte };
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

	@Override
	public void stopPresetEffect(KickId kickId, KickBasePresetEffect presetEffect) {
		try {
			byte[] data = null;
			if (presetEffect instanceof KickRainbowEffect) {
				data = new byte[] {MASTER_RESPONSE_PRESET_RAINBOW};
			} else if (presetEffect instanceof KickLightningStormEffect) {
				data = new byte[] {MASTER_RESPONSE_PRESET_LIGHTNINGSTORM};
			} else if (presetEffect instanceof KickSineEffect) {
				data = new byte[] {MASTER_RESPONSE_PRESET_SINE};
			} else if (presetEffect instanceof KickExplosionEffect) {
				data = new byte[] {MASTER_RESPONSE_PRESET_EXPLOSION};
			} else if (presetEffect instanceof KickStrobeEffect) {
				data = new byte[] {MASTER_RESPONSE_PRESET_STROBE};
			}

			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found...");
			} else {
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId())) {
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						sendData((byte)0x11, data, kick.getGatt(), kick.getTx());
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						if (Arrays.equals(kick.getAddress(), kickId.getId())) {
							sendData((byte)0x11, data, kick.getGatt(), kick.getTx());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in StopPresetEffect.", e, context);
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
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId()))
				{
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						kick.setKickWhiteBalance(kickWhiteBalance);
						sendData((byte)0x05, ConnectionUtils.intToTwoBytes(kickWhiteBalance.getColorTemperature()), kick.getGatt(), kick.getTx());
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						if (Arrays.equals(kick.getAddress(), kickId.getId()))
						{
							kick.setKickWhiteBalance(kickWhiteBalance);
							sendData((byte)0x05, ConnectionUtils.intToTwoBytes(kickWhiteBalance.getColorTemperature()), kick.getGatt(), kick.getTx());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in SetDeviceWhiteBalance.", e, context);
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
						sendData((byte)0x06, new byte[] {(byte)kick.getKickBrightness().getBrightness()}, kick.getGatt(), kick.getTx());
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks()) {
						if (Arrays.equals(kick.getAddress(), kickId.getId())) {
							kick.setOn(true);
							sendData((byte)0x06, new byte[] {(byte)kick.getKickBrightness().getBrightness()}, kick.getGatt(), kick.getTx());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in SetDeviceToOn.", e, context);
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
						sendData((byte)0x06, new byte[] {(byte)kickBrightness.getBrightness()}, kick.getGatt(), kick.getTx());
					}
					else if (Arrays.equals(kick.getAddress(), kickId.getId()))
					{
						kick.setOn(false);
						sendData((byte)0x06, new byte[] {(byte)kickBrightness.getBrightness()}, kick.getGatt(), kick.getTx());
						break;
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in SetDeviceToOff.", e, context);
		}
	}

	public void setActiveFilter(KickId kickId, int filterIndex) {
		try {
			Log.d(TAG, "Calling KickCommunicationAPI.setActiveFilter()");

			if (kickId == null) {
				kickId = new KickId();
				kickId.setId(KickConstants.ADDRESS_ALL);
			}

			if (kickId.getId() == null || kickId.getId().length == 0) {
				Log.e(TAG, "Address not found. Please set the device address to change the WhiteBalance");
			} else {
				if (Arrays.equals(KickConstants.ADDRESS_ALL, kickId.getId()))
				{
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						int kelvin = kick.getKickWhiteBalance().getColorTemperature();
						Log.w("WB", kick.getAddress() + " kelvin: " + kelvin);
						sendData((byte)0x19, new byte[] {0x0, 0x0, (byte)((filterIndex >> 8) & 0xFF), (byte)(filterIndex & 0xFF), (byte)((kelvin >> 8) & 0xFF), (byte)(kelvin & 0xFF)}, kick.getGatt(), kick.getTx());
					}
				} else {
					for (Kick kick : callbackHandler.getConnectedKicks())
					{
						if (Arrays.equals(kick.getAddress(), kickId.getId()))
						{
							int kelvin = kick.getKickWhiteBalance().getColorTemperature();
							sendData((byte)0x19, new byte[] {0x0, 0x0, (byte)((filterIndex >> 8) & 0xFF), (byte)(filterIndex & 0xFF), (byte)((kelvin >> 8) & 0xFF), (byte)(kelvin & 0xFF)}, kick.getGatt(), kick.getTx());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ConnectionUtils.callBackError(null, "Failed in SetDeviceWhiteBalance.", e, context);
		}
	}
}
