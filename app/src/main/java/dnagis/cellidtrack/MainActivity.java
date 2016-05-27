package dnagis.cellidtrack;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//http://stackoverflow.com/questions/6407218/android-service-only-uses-getters
//http://stackoverflow.com/questions/5266878/android-regular-task-cronjob-equivalent
//http://stackoverflow.com/questions/11733736/alarmmanager-never-calling-onreceive-in-alarmreceiver-broadcastreceiver "parle de après reboot"

/*
* adb backup -f data.ab -noapk com.morphotox.cronjob
* dd if=data.ab bs=1 skip=24 | openssl zlib -d | tar -xvf -
* convertir epoch avec busybox date: date -d @1464159452 ##enlever les 3 lasts (ms)
*/
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        on_demarre();

        affiche_infos_db();

    }


    public void on_demarre(){
        AlarmManager mgr= (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        Intent i=new Intent(this, OnAlarmReceiver.class);
        PendingIntent pi= PendingIntent.getBroadcast(this, 0, i, 0);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 600000, pi);
        //Dans le log j'ai:04-25 10:41:30.130    1898-3699/? W/AlarmManager﹕ Suspiciously short interval 5000 millis; expanding to 60 seconds
        //toutes les 60s après j'ai: AlarmManager﹕ sending alarm {d33ff0f type 2 *walarm*:com.morphotox.cronjob/.OnAlarmReceiver}
    }

    public void affiche_infos_db() {
        TextView date_textvw = (TextView)findViewById(R.id.last_date);
        TextView id_textvw = (TextView)findViewById(R.id.id_max);
        TextView cellid_textvw = (TextView)findViewById(R.id.last_cellid);

        BaseDeDonnees maBDD;
        int ID_MAX;
        int CELLID;
        long EPOCH;




        maBDD = new BaseDeDonnees(this);
        SQLiteDatabase bdd=maBDD.getReadableDatabase();

        //http://stackoverflow.com/questions/10600670/sqlitedatabase-query-method
        //query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)

        Cursor cursor = bdd.query("cronjob", null, null, null, null, null, "ID DESC");

        if (cursor != null) {
            cursor.moveToFirst();

            ID_MAX = cursor.getInt(0);
            CELLID = cursor.getInt(2);
            EPOCH = cursor.getLong(1);

            id_textvw.setText(String.valueOf(ID_MAX));
            cellid_textvw.setText(String.valueOf(CELLID));

            Date date = new Date(EPOCH);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
            date_textvw.setText(format.format(date));
            //date_textview.setText(String.valueOf(columnIndex));
        }

        bdd.close();


    }
}