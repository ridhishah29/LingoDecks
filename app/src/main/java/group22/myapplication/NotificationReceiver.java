package group22.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver{

    int MID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                                                        .setContentTitle("LingoDecks")
                                                        .setContentText("What new word are you going to learn today?")
                                                        .setSound(alarmSound)
                                                        .setAutoCancel(true).setWhen(when)
                                                        .setContentIntent(pendingIntent)
                                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        nm.notify(MID, notificationBuilder.build());
        //auto increment unique ID
        MID++;

    }
}
