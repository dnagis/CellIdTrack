package dnagis.cellidtrack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 27/05/16.
 */
public class BaseDeDonnees extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cellid.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_BDD = "CREATE TABLE cellid (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIME INTEGER NOT NULL, CELLID INTEGER NOT NULL, MCC INTEGER NOT NULL, MNC INTEGER NOT NULL, LAC INTEGER NOT NULL, RADIO TEXT NOT NULL)";

    public BaseDeDonnees(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
