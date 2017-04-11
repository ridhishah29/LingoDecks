package group22.myapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ContentProvider extends ContentProvider {
    public static final String LOG_TAG = "MovieContentProvider";
    /* public static final int MOVIE = 100;
    public static final int MOVIE_WITH_ID = 101; */
    private static final UriMatcher myUriMatcher = buildUriMatcher();
    public static LingodecksDBHelper myDBHelper;


}
