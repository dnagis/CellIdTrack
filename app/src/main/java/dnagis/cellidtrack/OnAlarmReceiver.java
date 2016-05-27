package dnagis.cellidtrack;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 27/05/16.
 */
public class OnAlarmReceiver extends BroadcastReceiver {

    private BaseDeDonnees maBDD;
    private SQLiteDatabase bdd;

    public void onReceive(Context context, Intent intent) {


        //System.out.println("Vincent Tu es dans OnAlarmReceiver");
        maBDD = new BaseDeDonnees(context);
        bdd = maBDD.getWritableDatabase();
        ContentValues values = new ContentValues();

        long timestamp = System.currentTimeMillis();
        int cellid=-1;

        TelephonyManager telph= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); //il faut lui passer le context
        int mode_avion = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);

        if (mode_avion != 1) {
            List<CellInfo> cellinfo = telph.getAllCellInfo();
            //dans le métro quand cartes sims activées mais aucun signal ça plante... je suspecte soit cellinfo null soit size=0
            if (cellinfo != null) {
                CellInfo cell0 = cellinfo.get(0);
                if (cell0 instanceof CellInfoGsm) {
                    cellid = ((CellInfoGsm) cell0).getCellIdentity().getCid();
                } else if (cell0 instanceof CellInfoCdma) {
                    cellid = ((CellInfoCdma) cell0).getCellIdentity().getBasestationId();
                } else if (cell0 instanceof CellInfoLte) {
                    cellid = ((CellInfoLte) cell0).getCellIdentity().getCi();
                } else if (cell0 instanceof CellInfoWcdma) {
                    cellid = ((CellInfoWcdma) cell0).getCellIdentity().getCid();
                }
            }
        }

        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.FRANCE);
        String date_txt = format.format(date);
        Log.d("Vincent", "Tu es dans OnAlarmReceiver à="+date_txt+"  avec cellid="+cellid);

        values.put("TIME", timestamp);
        values.put("CELLID", cellid);
        bdd.insert("cellid", null, values);



    }
}
