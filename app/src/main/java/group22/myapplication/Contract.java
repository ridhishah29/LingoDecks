package group22.myapplication;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    //Uri for ContentProvider
    private static final String CONTENT_AUTHORITY = "group22.myapplication";
    private static final Uri BASE_CONTENT_URI1 = Uri.parse("content://" + CONTENT_AUTHORITY + "/german");
    private static final Uri BASE_CONTENT_URI2 = Uri.parse("content://" + CONTENT_AUTHORITY + "/spanish");
    private static final Uri BASE_CONTENT_URI3 = Uri.parse("content://" + CONTENT_AUTHORITY + "/score");
    private static final String PATH_LINGODECKS = "lingodecks";

    /* Inner class that defines the table contents */
    public static class German_Table implements BaseColumns {
        //Uri
        public static final Uri CONTENT_URI1 = BASE_CONTENT_URI1.buildUpon().appendPath(PATH_LINGODECKS).build();
        public static final Uri CONTENT_URI2 = BASE_CONTENT_URI2.buildUpon().appendPath(PATH_LINGODECKS).build();
        public static final Uri CONTENT_URI3 = BASE_CONTENT_URI3.buildUpon().appendPath(PATH_LINGODECKS).build();
        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LINGODECKS;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LINGODECKS;

        //table for German Words
        public static final String TABLE_GERMAN = "German";
        public static final String COLUMN_GER_PIC = "Picture Ref";
        public static final String COLUMN_GER_ENG = "English Translation";
        public static final String COL_GER = "German Translation";

        //table for Spanish Words
        public static final String TABLE_SPANISH = "Spanish";
        public static final String COLUMN_ESP_PIC = "Picture Ref";
        public static final String COLUMN_ESP_ENG = "English Translation";
        public static final String COLUMN_ESP = "German Translation";

        //table for Player Scores
        public static final String TABLE_NAME = "Scoreboard";
        public static final String COLUMN_SCORES = "Score";
        public static final String COLUMN_DATE = "Date";

        public static Uri buildGermanUriWithID1(long ID) {
            return ContentUris.withAppendedId(CONTENT_URI1, ID);
        }

        public static Uri buildGermanUriWithID2(long ID) {
            return ContentUris.withAppendedId(CONTENT_URI2, ID);
        }

        public static Uri buildGermanUriWithID3(long ID) {
            return ContentUris.withAppendedId(CONTENT_URI3, ID);
        }
    }
}
