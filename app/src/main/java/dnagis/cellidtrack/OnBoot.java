package dnagis.cellidtrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by root on 03/06/16.
 * *******Attention pas git add"ed" *********
 * http://stackoverflow.com/questions/2784441/trying-to-start-a-service-on-boot-on-android
 * Essaie de répondre au problème: si je cible OnAlarmReceiver vis à vis de BOOT_COMPLETED dans le manifest, marche une
 * fois, mais comme il n'y a pas d'alarm manager, après c'est finito!!
 * Donc je refais ce qu'il y a dans le onCreate() de MainActivity
 */
public class OnBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Vincent", "Tu es dans OnBoot...");
        AlarmManager mgr= (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent i=new Intent(context, OnAlarmReceiver.class);
        PendingIntent pi= PendingIntent.getBroadcast(context, 0, i, 0);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 600000, pi);

        // Start service
        //context.startService(i);

    }
}
