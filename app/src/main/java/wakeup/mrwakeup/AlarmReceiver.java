package wakeup.mrwakeup;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.TimeUnit;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by willivr on 3/18/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {


        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire(TimeUnit.MINUTES.toMillis(40));
        Intent turnOnLightIntent = new Intent();
        turnOnLightIntent.setAction("LIGHT_ON");

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.sendBroadcast(turnOnLightIntent);

        Intent playSongIntent = new Intent();
        playSongIntent.setAction("PLAY_SOUND");

        manager.sendBroadcast(playSongIntent);

        // Notification intent

        Intent showNotificationIntent = new Intent();
        showNotificationIntent.setAction("DISABLE_ALARM");

        NotificationUtils mNotificationUtils = new NotificationUtils(context);
        NotificationCompat.Builder nb = mNotificationUtils.getAndroidChannelNotification("Alarm active", "disable");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, showNotificationIntent, 0);
        nb.setContentIntent(pendingIntent);
        mNotificationUtils.getManager().notify(101, nb.build());


        setResultCode(Activity.RESULT_OK);
    }

}
