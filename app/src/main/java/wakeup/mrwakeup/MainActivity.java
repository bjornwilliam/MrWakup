package wakeup.mrwakeup;

import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

/*import android.support.v7.app.FragmentManager;
import android.support.v7.app.FragmentTransaction;*/
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TimePicker;


import com.riftlabs.communicationlib.KickCommunicationAPI;
import com.riftlabs.communicationlib.KickCommunicationFactory;
import com.riftlabs.communicationlib.utils.Log;


import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import wakeup.devicemanager.DeviceManager;

public class MainActivity extends AppCompatActivity implements AlarmFragment.OnFragmentInteractionListener, LightFragment.OnFragmentInteractionListener{
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private String btSpeakerDeviceMac = "F4:4E:FD:5F:78:E7";
    private String btSpeakerDeviceName = "Audio Pro T3";
    public static LightFragment lightFragment;
    private BluetoothManager bluetoothManager;
    private DeviceManager mDeviceManager;

    private static final int REQUEST_ENABLE_BT = 8080;
    private static final String TAG = MainActivity.class.getName();

    private TextView mTextMessage;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());

        mTextMessage = (TextView) findViewById(R.id.message);
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mDeviceManager = new DeviceManager();
        lightFragment = new LightFragment();
        lightFragment.setDeviceManager(mDeviceManager);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
            else {
                // already have permission
                // set up bluetooth LE
                bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                    Log.e("BTLE", "Bluetooth not enabled -> starting BluetoothAdapter.ACTION_REQUEST_ENABLE activity...");
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                else {
                    Log.e("BTLE", "Bluetooth enabled! startConnectionListeners...");
                    getAPI().startConnectionListeners(lightFragment.getKickChangedCallback());
                }
            }
        }


        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(lightFragment,"lightfrag");
        fragmentTransaction.commit();

        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);

        //connectToBtSpeaker();
        // Start auto connect to Bt speaker
/*        final ScheduledExecutorService ses = Executors.newScheduledThreadPool(5);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    connectToBtSpeaker();
                }
                catch (Throwable ex) {
                    Log.d(TAG,ex.toString());
                }
            }

        },1,1, TimeUnit.SECONDS);*/

    }


    private KickCommunicationAPI getAPI() {
        return KickCommunicationFactory.getKickCommunicationAPI(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                    // set up bluetooth LE
                    bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                        Log.e("BTLE", "Bluetooth not enabled -> starting BluetoothAdapter.ACTION_REQUEST_ENABLE activity...");
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                    else {
                        Log.e("BTLE", "Bluetooth enabled! startConnectionListeners...");
                        getAPI().startConnectionListeners(lightFragment.getKickChangedCallback());
                    }

                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    public void nrOfConnectedLightsChanged(int nrOfConnectedLights) {
        AlarmFragment alarmFragment = (AlarmFragment) getSupportFragmentManager().findFragmentById(R.id.alarmFragment);
        if (alarmFragment != null) {
            alarmFragment.updateNrOfConnectedLights(nrOfConnectedLights);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    public  void onUserWantsToActivateAlarm(int minutes) {
        LightFragment lightFrag = (LightFragment) getSupportFragmentManager().findFragmentByTag("lightfrag");
        lightFrag.ActivateWakeupEffect(minutes);
    }



/*
    public void connectToA2dp() {
        BluetoothAdapter bleAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        bleAdapter.getProfileProxy (getApplicationContext(), listener, BluetoothProfile.A2DP);
    }

    // Bluetooth a2dp
    public void connectToBtSpeaker() {
        BluetoothAdapter bleAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        Set<BluetoothDevice> pairedDevices = bleAdapter.getBondedDevices();
        for (BluetoothDevice d : pairedDevices) {
            //if (d.getAddress().equals(btSpeakerDeviceMac)) {
            if (d.getName().equals(btSpeakerDeviceName)) {
                d.createInsecureRfcommSocketToServiceRecord(d.getUuids()[0].getUuid());

                UUID.fromString()
                d.connectGatt(MainActivity.this, true, new BluetoothGattCallback() {

                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        switch (newState) {
                            case BluetoothProfile.STATE_CONNECTED:
                                Log.i("GattCallback", "connected");
                                gatt.getServices();
                                break;
                            case BluetoothProfile.STATE_DISCONNECTED:
                                Log.i("GattCallback", "DisconnectedTurbo");
                                break;
                            default:
                                int what = 5;
                                break;
                        }
                    }
                });
            }
        }
    }*/

}
