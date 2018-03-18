package wakeup.mrwakeup;

import android.content.BroadcastReceiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by willivr on 3/18/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
/*        AlarmFragment inst = AlarmFragment.instance();


        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();*/

        Intent turnOnLightIntent = new Intent();
        turnOnLightIntent.setAction("LIGHT_ON");

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.sendBroadcast(turnOnLightIntent);

        Intent playSongIntent = new Intent();
        playSongIntent.setAction("PLAY_SOUND");

        manager.sendBroadcast(playSongIntent);



        setResultCode(Activity.RESULT_OK);
    }

}
