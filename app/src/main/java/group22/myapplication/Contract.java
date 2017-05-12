package group22.myapplication;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    //Uri for ContentProvider
    public static final String CONTENT_AUTHORITY = "group22.myapplication";
    public static final Uri BASE_CONTENT_URI1 = Uri.parse("content://" + CONTENT_AUTHORITY + "/german");
    public static final Uri BASE_CONTENT_URI2 = Uri.parse("content://" + CONTENT_AUTHORITY + "/spanish");
    public static final Uri BASE_CONTENT_URI3 = Uri.parse("content://" + CONTENT_AUTHORITY + "/score");

    //don't seem to need this. it messes up the query cursor.
    public static final String PATH_LINGODECKS = "lingodecks";

    /* Inner class that defines the table contents */
    public static class Lingodecks_Tables implements BaseColumns {
        //Uri
        public static final Uri CONTENT_URI1 = BASE_CONTENT_URI1.buildUpon().build();
        public static final Uri CONTENT_URI2 = BASE_CONTENT_URI2.buildUpon().build();
        public static final Uri CONTENT_URI3 = BASE_CONTENT_URI3.buildUpon().build();
        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LINGODECKS;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LINGODECKS;

        //table for German Words
        public static final String TABLE_GERMAN = "German";
        public static final String COLUMN_GER_ENG = "english";
        public static final String COLUMN_GER = "german";
        public static final String COLUMN_GER_PIC = "picture";

        //table for Spanish Words
        public static final String TABLE_SPANISH = "Spanish";
        public static final String COLUMN_ESP_ENG = "english";
        public static final String COLUMN_ESP = "spanish";
        public static final String COLUMN_ESP_PIC = "picture";

        //table for Player Scores
        public static final String TABLE_SCORE = "Scoreboard";
        public static final String COLUMN_SCORES = "score";
        public static final String COLUMN_DATE = "date";

        public static Uri buildGermanUriWithID(long ID) {
            return ContentUris.withAppendedId(CONTENT_URI1, ID);
        }

        public static Uri buildSpanishUriWithID(long ID) {
            return ContentUris.withAppendedId(CONTENT_URI2, ID);
        }

        public static Uri buildScoreUriWithID(long ID) {
            return ContentUris.withAppendedId(CONTENT_URI3, ID);
        }
    }
}
