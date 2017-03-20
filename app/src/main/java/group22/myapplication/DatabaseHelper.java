package group22.myapplication;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Michael on 20/03/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    //Creating a database with 2 tables
    public static final String DATABASE_NAME = "LingoDecks.db";

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

    //SQL Statement for creating German Table
    private static final String SQL_CREATE_TABLE_GERMAN = "CREATE TABLE" + TABLE_GERMAN + "("
            + COL_GER_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_GER_PIC +

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
