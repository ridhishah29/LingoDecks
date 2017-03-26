package group22.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Michael on 20/03/2017.
 */

//Creating a database with 2 tables
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    //Columns for German Table
    public static final String TABLE_GERMAN = "German";
    public static final String COL_GER_ID = "ID";
    public static final String COL_GER_PIC = "Picture";
    public static final String COL_ENG1 = "English Translation";
    public static final String COL_GER = "German Translation";

    //Columns for Spanish Table
    public static final String TABLE_SPANISH = "Spanish";
    public static final String COL_ESP_ID = "ID";
    public static final String COL_ESP_PIC = "Picture";
    public static final String COL_ENG2 = "English Translation";
    public static final String COL_ESP = "Spanish Translation";

    private static final String DATABASE_NAME = "LingoDecks.db";
    private static final int DATABASE_VERSION = 1;

    //SQL Statement for creating German Table
    private static final String SQL_CREATE_TABLE_GERMAN = "CREATE TABLE" + TABLE_GERMAN + "("
            + COL_GER_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " //Creating a primary key
            + COL_GER_PIC + "BLOB, "
            + COL_ENG1 + "TEXT NOT NULL, "
            + COL_GER + "TEXT NOT NULL, "
            +");";

    //SQL Statement for creating German Table
    private static final String SQL_CREATE_TABLE_SPANISH = "CREATE TABLE" + TABLE_SPANISH + "("
            + COL_ESP_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " //Creating a primary key
            + COL_ESP_PIC + "BLOB, "
            + COL_ENG2 + "TEXT NOT NULL, "
            + COL_ESP + "TEXT NOT NULL, "
            +");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_GERMAN);
        database.execSQL(SQL_CREATE_TABLE_SPANISH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,
                "Upgrading the database from version " + oldVersion + " to "+ newVersion);

        //Clear all data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GERMAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPANISH);

        //Recreate the tables
        onCreate(db);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
}
