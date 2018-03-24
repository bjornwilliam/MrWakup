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
