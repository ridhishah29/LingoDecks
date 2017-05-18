package group22.myapplication;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.data;
import static android.os.Build.ID;


public class CardList extends Activity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{
    private LingodecksDBHelper DBHelper;
    private SQLiteDatabase db;
    TextView textView;
    String hasPicture;
    private static final int GERMAN_LOADER = 1;
    ArrayList<String> languageArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_index);

        getLoaderManager().initLoader(GERMAN_LOADER, null, this);
        //final List<String> datasource = new ArrayList(Arrays.asList(dataArray));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getApplicationContext(),R.layout.card_list,R.id.card_list_textview,languageArray);

        ListView lv = (ListView) findViewById(R.id.card_list_view);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(CardList.this, WordCardDisplay.class);

                TextView tv = (TextView) view;
                intent.putExtra("Card",tv.getText().toString());
                //start new activity using the intent
                startActivity(intent);
            }

        });
        lv.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == 1) {
            String[] columns = {
                    Contract.Lingodecks_Tables._ID,
                    Contract.Lingodecks_Tables.COLUMN_GER,
                    Contract.Lingodecks_Tables.COLUMN_GER_ENG,
                    Contract.Lingodecks_Tables.COLUMN_GER_PIC
            };

            return new CursorLoader(this, Contract.Lingodecks_Tables.CONTENT_URI1, columns, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int m = 0;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext() && m < cursor.getCount()) {
                languageArray.add(cursor.getString(0) + " - " + cursor.getString(2) + " - " + cursor.getString(1));
                m++;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.swapCursor(null);
        Log.v("reset", "reset");
    }
}

