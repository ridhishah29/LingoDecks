package group22.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class LingodecksDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME="lingodecks.db";

    public SQLiteDatabase myDB;

    public LingodecksDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        myDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        myDB = db;
        String GermanQuery = "CREATE TABLE IF NOT EXISTS " +
                Contract.Lingodecks_Tables.TABLE_GERMAN +
                " (" + Contract.Lingodecks_Tables._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Lingodecks_Tables.COLUMN_GER_ENG +
                " TEXT NOT NULL," +
                Contract.Lingodecks_Tables.COLUMN_GER +
                " TEXT NOT NULL," +
                Contract.Lingodecks_Tables.COLUMN_GER_PIC +
                "TEXT NOT NULL );";

        String SpanishQuery = "CREATE TABLE IF NOT EXISTS " +
                Contract.Lingodecks_Tables.TABLE_SPANISH +
                " (" + Contract.Lingodecks_Tables._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Lingodecks_Tables.COLUMN_ESP_ENG +
                " TEXT NOT NULL," +
                Contract.Lingodecks_Tables.COLUMN_ESP +
                " TEXT NOT NULL," +
                Contract.Lingodecks_Tables.COLUMN_ESP_PIC +
                "TEXT NOT NULL );";

        String ScoreQuery = "CREATE TABLE IF NOT EXISTS " +
                Contract.Lingodecks_Tables.TABLE_SCORE +
                " (" + Contract.Lingodecks_Tables._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Lingodecks_Tables.COLUMN_SCORES +
                " INTEGER NOT NULL," +
                Contract.Lingodecks_Tables.COLUMN_DATE +
                " DATE NOT NULL );";

        db.execSQL(GermanQuery);
        db.execSQL(SpanishQuery);
        db.execSQL(ScoreQuery);
        Log.i("SQLiteSimpleDemoTEST", "LingodecksDBHelper onCreate()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Lingodecks_Tables.TABLE_GERMAN);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Lingodecks_Tables.TABLE_SPANISH);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Lingodecks_Tables.TABLE_SCORE);
        onCreate(db);
    }

    public void clearGermanTable(String table_name){
        myDB.execSQL("DELETE FROM " + Contract.Lingodecks_Tables.TABLE_GERMAN);
    }

    public void clearSpanishTable(String table_name){
        myDB.execSQL("DELETE FROM " + Contract.Lingodecks_Tables.TABLE_SPANISH);
    }

    public void clearScoreTable(String table_name){
        myDB.execSQL("DELETE FROM " + Contract.Lingodecks_Tables.TABLE_SCORE);
    }

}
