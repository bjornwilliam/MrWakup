package wakeup.mrwakeup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;


import com.riftlabs.communicationlib.KickCommunicationAPI;
import com.riftlabs.communicationlib.KickCommunicationFactory;
import com.riftlabs.communicationlib.utils.Log;

public class MainActivity extends AppCompatActivity {


    private BluetoothManager bluetoothManager;
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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
            //getAPI().startConnectionListeners(settingsFrag.getKickChangedCallback());
        }
    }


    private KickCommunicationAPI getAPI() {
        return KickCommunicationFactory.getKickCommunicationAPI(getApplicationContext());
    }

}
