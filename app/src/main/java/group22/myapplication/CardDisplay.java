//package group22.myapplication;
//
//
//import android.app.Activity;
//import android.content.CursorLoader;
//import android.content.Loader;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.SimpleCursorAdapter;
//import android.widget.TextView;
//
//import static group22.myapplication.Contract.Lingodecks_Tables.COLUMN_GER_PIC;
//
//public class CardDisplay extends Activity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{
//
//    TextView textView7, textView8;
//    ImageView imageView2;
//    SimpleCursorAdapter adapter;
//    private static final int GERMAN_LOADER = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.card_index);
//
//
//        getLoaderManager().initLoader(GERMAN_LOADER, null, this);
//
//        textView7 = (TextView) findViewById(R.id.textView7);
//        imageView2 = (ImageView) findViewById(R.id.imageView2);
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        if(id == 1) {
//            String[] columns = {
//                    Contract.Lingodecks_Tables._ID,
//                    Contract.Lingodecks_Tables.COLUMN_GER,
//                    Contract.Lingodecks_Tables.COLUMN_GER_ENG,
//                    COLUMN_GER_PIC
//            };
//            return new CursorLoader(this, Contract.Lingodecks_Tables.CONTENT_URI1, columns, null, null, null);
//        }
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        if (cursor != null && cursor.getCount() > 0) {
//            StringBuilder stringBuilderQueryResult = new StringBuilder("");
//            while (cursor.moveToNext()) {
//                stringBuilderQueryResult.append(cursor.getString(1));
//            }
//            textView7.setText(stringBuilderQueryResult.toString());
//        }
//        imageView2.setImageURI(Contract.Lingodecks_Tables.CONTENT_URI1);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        adapter.swapCursor(null);
//        Log.v("reset", "reset");
//    }
//}
