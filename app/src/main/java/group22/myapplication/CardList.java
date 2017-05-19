package group22.myapplication;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.SharedPreferences;

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
import static android.R.attr.id;
import static android.os.Build.ID;


public class CardList extends Activity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{
    private LingodecksDBHelper DBHelper;
    private SQLiteDatabase db;
    TextView textView;
    String hasPicture = "No";
    private static final int GERMAN_LOADER = 1;
    private static final int SPANISH_LOADER = 2;
    ArrayList<String> languageArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_index);

        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        String lang = langPref.getString("language", "");
        Log.i("Language", lang);
        if(lang.equals("en-de")){
            getLoaderManager().initLoader(GERMAN_LOADER, null, this);
        }
        else if(lang.equals("en-es")){
            getLoaderManager().initLoader(SPANISH_LOADER, null, this);
        }
        //final List<String> datasource = new ArrayList(Arrays.asList(dataArray));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getApplicationContext(),R.layout.card_list,R.id.card_list_textview,languageArray);

        ListView lv = (ListView) findViewById(R.id.card_list_view);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = null;
                if (languageArray.get(position).split(" - ")[3].equals("Word")) {
                    intent = new Intent(CardList.this, WordCardDisplay.class);
                }else if (languageArray.get(position).split(" - ")[3].equals("Picture")) {
                    intent = new Intent(CardList.this, CardDisplay.class);
                }

                TextView tv = (TextView) view;
                intent.putExtra("Card",tv.getText().toString());
                //start new activity using the intent
                startActivity(intent);
            }

        });
        lv.setAdapter(adapter);

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
            else if(id == 2) {
                String[] columns = {
                        Contract.Lingodecks_Tables._ID,
                        Contract.Lingodecks_Tables.COLUMN_ESP,
                        Contract.Lingodecks_Tables.COLUMN_ESP_ENG,
                        Contract.Lingodecks_Tables.COLUMN_ESP_PIC
                };

                return new CursorLoader(this, Contract.Lingodecks_Tables.CONTENT_URI2, columns, null, null, null);
            }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int m = 0;
        String cursor0 = "";
        String cursor1 = "";
        String cursor2 = "";
        String cursor3 = "";
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext() && m < cursor.getCount()) {
                if(cursor.getString(0) != null){
                    cursor0 = cursor.getString(0);
                }
                if(cursor.getString(1) != null){
                    cursor1 = cursor.getString(1);
                }
                if(cursor.getString(2) != null){
                    cursor2 = cursor.getString(2);
                }
                m++;
                //checks to see if data in picture column is of type blob
                if (cursor.getType(3) == 4) {
                    hasPicture = "Yes";
                    cursor3 = "Picture";
                } else {
                    hasPicture = "No";
                    cursor3 = "Word";
                }

                languageArray.add((cursor0 + " - " + cursor2 + " - " + cursor1 + " - " + cursor3));

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.swapCursor(null);
        Log.v("reset", "reset");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, GameModeActivity.class);
        startActivity(intent);
    }
}

