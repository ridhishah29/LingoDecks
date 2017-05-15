package group22.myapplication;

/**
 * Created by Michael on 11/04/2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class LDContentProvider extends ContentProvider {
    public static final String LOG_TAG = "LDContentProvider";
    public static final int GERMAN = 100;
    public static final int GERMAN_ID = 101;
    public static final int SPANISH = 200;
    public static final int SPANISH_ID = 201;
    public static final int SCORE = 300;
    public static final int SCORE_ID = 301;
    private static final UriMatcher myUriMatcher = buildUriMatcher();
    public static LingodecksDBHelper myDBHelper;

    private static final UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "german", GERMAN);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "german/#", GERMAN_ID);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "spanish", SPANISH);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "spanish/#", SPANISH_ID);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "score", SCORE);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "score/#", SCORE_ID);

        return matcher;

    }

    public LDContentProvider() {
    }


    @Override
    public boolean onCreate() {
        //Initializes the content provider on startup
        myDBHelper = new LingodecksDBHelper(getContext(),LingodecksDBHelper.DB_NAME,null,LingodecksDBHelper.DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Handles Query Request from clients
        int match_code = myUriMatcher.match(uri);
        Cursor myCursor;

        switch(match_code) {
            case GERMAN: {
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        Contract.Lingodecks_Tables.TABLE_GERMAN, //Table to Query
                        projection,//Columns
                        selection, // Columns for the "where" clause
                        selectionArgs, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        sortOrder // sort order
                );
                Log.i(LOG_TAG, "querying for GERMAN Table");
                Log.i(LOG_TAG, myCursor.getCount() + "");
                break;
            }
            case GERMAN_ID: {
                myCursor = myDBHelper.getReadableDatabase().query(
                        Contract.Lingodecks_Tables.TABLE_GERMAN,
                        projection,
                        Contract.Lingodecks_Tables._ID + " = '" + ContentUris.parseId(uri) +"'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case SPANISH: {
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        Contract.Lingodecks_Tables.TABLE_SPANISH, //Table to Query
                        projection,//Columns
                        selection, // Columns for the "where" clause
                        selectionArgs, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        sortOrder // sort order
                );
                Log.i(LOG_TAG, "querying for SPANISH Table");
                Log.i(LOG_TAG, myCursor.getCount() + "");
                break;
            }
            case SPANISH_ID: {
                myCursor = myDBHelper.getReadableDatabase().query(
                        Contract.Lingodecks_Tables.TABLE_SPANISH,
                        projection,
                        Contract.Lingodecks_Tables._ID + " = '" + ContentUris.parseId(uri) +"'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case SCORE: {
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        Contract.Lingodecks_Tables.TABLE_SCORE, //Table to Query
                        projection,//Columns
                        selection, // Columns for the "where" clause
                        selectionArgs, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        sortOrder // sort order
                );
                Log.i(LOG_TAG, "querying for SCORE Table");
                Log.i(LOG_TAG, myCursor.getCount() + "");
                break;
            }
            case SCORE_ID: {
                myCursor = myDBHelper.getReadableDatabase().query(
                        Contract.Lingodecks_Tables.TABLE_SCORE,
                        projection,
                        Contract.Lingodecks_Tables._ID + " = '" + ContentUris.parseId(uri) +"'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        myCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return myCursor;
    }

    @Override
    public String getType(Uri uri) {
        //Handles requests for the MIME type of the data at the given URI.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case GERMAN:
                return Contract.Lingodecks_Tables.CONTENT_TYPE_DIR;
            case GERMAN_ID:
                return Contract.Lingodecks_Tables.CONTENT_TYPE_ITEM;
            case SPANISH:
                return Contract.Lingodecks_Tables.CONTENT_TYPE_DIR;
            case SPANISH_ID:
                return Contract.Lingodecks_Tables.CONTENT_TYPE_ITEM;
            case SCORE:
                return Contract.Lingodecks_Tables.CONTENT_TYPE_DIR;
            case SCORE_ID:
                return Contract.Lingodecks_Tables.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //Handles requests to insert a new row.

        int match_code = myUriMatcher.match(uri);
        Uri retUri = null;

        switch(match_code){
            case GERMAN:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(Contract.Lingodecks_Tables.TABLE_GERMAN,null,values);
                if (_id > 0) {
                    retUri = Contract.Lingodecks_Tables.buildGermanUriWithID(_id);
                }
                else
                    throw new SQLException("failed to Insert, please try again!");
                break;
            }

            case SPANISH:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(Contract.Lingodecks_Tables.TABLE_SPANISH,null,values);
                if (_id > 0) {
                    retUri = Contract.Lingodecks_Tables.buildSpanishUriWithID(_id);
                }
                else
                    throw new SQLException("failed to Insert, please try again!");
                break;
            }

            case SCORE:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(Contract.Lingodecks_Tables.TABLE_SCORE,null,values);
                if (_id > 0) {
                    retUri = Contract.Lingodecks_Tables.buildScoreUriWithID(_id);
                }
                else
                    throw new SQLException("failed to Insert, please try again!");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        Log.i(LOG_TAG, "Insert Successful!");
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Handles requests to delete one or more rows.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case GERMAN:{
                myDBHelper.clearGermanTable(Contract.Lingodecks_Tables.TABLE_GERMAN);
                break;
            }
            case SPANISH:{
                myDBHelper.clearSpanishTable(Contract.Lingodecks_Tables.TABLE_SPANISH);
                break;
            }
            case SCORE:{
                myDBHelper.clearScoreTable(Contract.Lingodecks_Tables.TABLE_SCORE);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        return 0;
    }
}
