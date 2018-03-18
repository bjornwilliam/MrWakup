package wakeup.mrwakeup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.riftlabs.communicationlib.api.datatypes.KickBrightness;
import com.riftlabs.communicationlib.api.datatypes.KickId;
import com.riftlabs.communicationlib.api.datatypes.KickWhiteBalance;
import com.riftlabs.communicationlib.utils.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static AlarmFragment inst;

    private MediaPlayer mediaPlayer;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;





    public AlarmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static AlarmFragment instance() {
        return inst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(playSoundAlarmBroadcastReceiver,
                new IntentFilter("PLAY_SOUND"));

        alarmManager = (AlarmManager) this.getContext().getSystemService(ALARM_SERVICE);
        inst = this;
    }

    private final BroadcastReceiver playSoundAlarmBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Wait 5 minutes before playing song.
            Calendar delay = Calendar.getInstance();
            delay.set(Calendar.SECOND,10);

            final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.schedule(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer = MediaPlayer.create(getContext(), R.raw.divi);
                    mediaPlayer.start();
                }
            },300, TimeUnit.SECONDS);
                  //  delay.getTimeInMillis(),TimeUnit.MILLISECONDS);


        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        ToggleButton alarmToggle = (ToggleButton) view.findViewById(R.id.toggleButton);
        alarmToggle.setOnClickListener(this);
        alarmTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
        return view;
    }

    @Override
    public void onClick(View v) {
        if ((((ToggleButton) v).isChecked())) {
        //do what you want to do when button is clicked
        Log.d("MyActivity", "Alarm On");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

        Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, 0);

        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);


        Calendar calendertemp = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
            calendertemp.add(Calendar.SECOND, 1);
        //alarmManager.set(AlarmManager.RTC, calendertemp.getTimeInMillis(), pendingIntent);

    } else {
        alarmManager.cancel(pendingIntent);
        Log.d("MyActivity", "Alarm Off");
        }
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
}
