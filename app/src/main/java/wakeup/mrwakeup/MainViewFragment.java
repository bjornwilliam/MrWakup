package wakeup.mrwakeup;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import wakeup.devicemanager.KickDevice;
import wakeup.devicemanager.DeviceManager;
import wakeup.devicestatusmanager.DeviceStatusManager;

import com.riftlabs.communicationlib.KickCommunicationAPI;
import com.riftlabs.communicationlib.KickCallbacks;
import com.riftlabs.communicationlib.api.datatypes.KickId;
import com.riftlabs.communicationlib.data.Kick;
import com.riftlabs.communicationlib.utils.Log;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainViewFragment extends Fragment implements IConnectedKickDeviceChangesListner {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainViewFragment newInstance(String param1, String param2) {
        MainViewFragment fragment = new MainViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private final KickCallbacks kickChangedCallback = new KickCallbacks() {
        @Override
        public void onKickAdded(Kick addedKick) {

        }

        @Override
        public void onKickTemperatureChanged(Kick temperatureChanged) {

        }

        @Override
        public void onKickReAdded(Kick reAddedKick) {

        }

        @Override
        public void onKickDisconnected(Kick disconnectedKick) {

        }

        @Override
        public void onKickBrightnessChanged(Kick brightnessChangedKick) {

        }

        @Override
        public void onKickWhiteBalanceChanged(Kick whiteBalanceChangedKick) {

        }

        @Override
        public void onKickBatteryLevelChanged(Kick batteryLevelChangedKick) {

        }

        @Override
        public void onKickError(String error) {

        }

        @Override
        public void setProgress(boolean show, String title, String message, int value, int max) {

        }

        @Override
        public void showWarning(String title, String message) {

        }
    };
    private ArrayList<KickDevice> connectedDeviceList;
    private DeviceManager mDeviceManager;
    private DeviceStatusManager mDeviceStatusManager;
    private boolean isKicksLinked = false;
    private KickDevice activeKickDevice;
    private KickCommunicationAPI kickCommunicationAPI = null;

    private static String TAG = MainViewFragment.class.getSimpleName();

    private void setConnecetDevicesStatus(KickDevice device) {
        Log.d(TAG, "setConnecetDevicesStatus");
        boolean isOn = device.isKickDeviceOn();
        for (KickDevice k : connectedDeviceList) {
            k.setKickDeviceOn(isOn);
        }
    }

    public void setDeviceManager(DeviceManager deviceManager) {
        this.mDeviceManager = deviceManager;
        mDeviceStatusManager = new DeviceStatusManager();
        mDeviceStatusManager.setDeviceManager(mDeviceManager);
    }

    public KickCallbacks getKickChangedCallback() {
        return kickChangedCallback;
    }

    @Override
    public void onKickDeviceStatusChanged(KickDevice kickDevice, ArrayList<KickDevice> deviceList) {
        if (isKicksLinked) {
            kickDevice.setKickDeviceOn(!kickDevice.isKickDeviceOn());
            activeKickDevice.setKickDeviceOn(kickDevice.isKickDeviceOn());
            setConnecetDevicesStatus(kickDevice);
            if (activeKickDevice.isKickDeviceOn()) {
                kickCommunicationAPI.setDeviceToOn(null);
            } else {
                kickCommunicationAPI.setDeviceToOff(null);
            }
        } else {
            for (KickDevice k : deviceList) {
                if (k.getDeviceNumber() == kickDevice.getDeviceNumber()) {
                    k.setKickDeviceOn(!k.isKickDeviceOn());
                    KickId kickId = new KickId();
                    kickId.setId(kickDevice.getAddress());
                    if (kickDevice.isKickDeviceOn()) {
                        kickCommunicationAPI.setDeviceToOn(kickId);
                    } else {
                        kickCommunicationAPI.setDeviceToOff(kickId);
                    }
                    break;

                }
            }
        }
    }
    @Override
    public void onActiveDeviceChanged(KickDevice kickDevice, ArrayList<KickDevice> deviceList) {
        //hideDevicesDropdown();
        //swapActiveKickDevice(kickDevice, deviceList);
    }

}
