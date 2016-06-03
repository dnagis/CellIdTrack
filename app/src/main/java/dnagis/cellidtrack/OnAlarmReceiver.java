package dnagis.cellidtrack;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
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


        TelephonyManager telph= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); //il faut lui passer le context
        int mode_avion = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);

        int cellid = -1; //par défault
        int lac = -1;
        int mnc = -1;
        int mcc = -1;
        String radio = "unknown";

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION); //depuis API23 les permissions au runtime... sigh...
        if (permissionCheck == PackageManager.PERMISSION_DENIED)
            cellid = -18;



        if ((mode_avion != 1) && (permissionCheck == PackageManager.PERMISSION_GRANTED)) {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.telephony.CellInfo
            List<CellInfo> cellinfo = telph.getAllCellInfo();
           //dans le métro quand cartes sims activées mais aucun signal ça plante... check (cellinfo != null)-pas suffisant finalement size marche bien
            if (cellinfo.size() > 0) {
                CellInfo cell0 = cellinfo.get(0);

                if (cell0 instanceof CellInfoGsm) {
                    cellid = ((CellInfoGsm) cell0).getCellIdentity().getCid();
                    lac = ((CellInfoGsm) cell0).getCellIdentity().getLac();
                    mnc = ((CellInfoGsm) cell0).getCellIdentity().getMnc();
                    mcc = ((CellInfoGsm) cell0).getCellIdentity().getMcc();
                    radio = "GSM";
                } else if (cell0 instanceof CellInfoCdma) { //2g ??
                    cellid = ((CellInfoCdma) cell0).getCellIdentity().getBasestationId();
                    radio = "CDMA";
                } else if (cell0 instanceof CellInfoLte) { //4G??
                    cellid = ((CellInfoLte) cell0).getCellIdentity().getCi();
                    mnc = ((CellInfoLte) cell0).getCellIdentity().getMnc();
                    mcc = ((CellInfoLte) cell0).getCellIdentity().getMcc();
                    lac = ((CellInfoLte) cell0).getCellIdentity().getTac();
                    radio = "LTE";
                } else if (cell0 instanceof CellInfoWcdma) { //3G? UMTS?
                    cellid = ((CellInfoWcdma) cell0).getCellIdentity().getCid();
                    lac = ((CellInfoWcdma) cell0).getCellIdentity().getLac();
                    mnc = ((CellInfoWcdma) cell0).getCellIdentity().getMnc();
                    mcc = ((CellInfoWcdma) cell0).getCellIdentity().getMcc();
                    radio = "UMTS";
                }

            } else {
                cellid = 0; //code pour se rappeler que cellid est vide (genre métro)
            }
        }

        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.FRANCE);
        String date_txt = format.format(date);
        Log.d("Vincent", "Tu es dans OnAlarmReceiver à="+date_txt+"  avec cellid="+cellid);

        values.put("TIME", timestamp);
        values.put("CELLID", cellid);
        values.put("LAC", lac);
        values.put("MNC", mnc);
        values.put("MCC", mcc);
        values.put("RADIO", radio);
        bdd.insert("cellid", null, values);



    }
}
