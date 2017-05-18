package group22.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class WordCardDisplay extends Activity{

    private TextView textView9, textView10;
    private EditText EditTextView;
    private Button EditBtn;
    private Button DeleteBtn;
    private static final int GERMAN_LOADER = 1;
    LingodecksDBHelper DBHelper;
    public SQLiteDatabase myDB;
    public static final String LOG_TAG = "WordCardDisplay";
    Context toast_context = this;
    CharSequence deleted_text = "Card was successfully deleted";
    int duration = Toast.LENGTH_SHORT;
    String lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card_display);

        DBHelper = new LingodecksDBHelper(this,LingodecksDBHelper.DB_NAME,null,LingodecksDBHelper.DB_VERSION);
        myDB = DBHelper.getWritableDatabase();
        final String DeleteQuery;

        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        String lang = langPref.getString("language", "");

        //on the receiving side
        //get the intent that started this activity
        Intent intent = getIntent();

        //grab the data
        String message = intent.getStringExtra("Card");

        //split data into arrays
        String[] details = message.split(" - ");

        //declare variables for array
        final String CardID = details[0];
        final String Translation = details[1];
        final String English = details[2];

        //display the data
        textView9 = (TextView)findViewById(R.id.textView9);
        textView9.setText(Translation);
        textView10 = (TextView)findViewById(R.id.textView10);
        textView10.setText(English);

        //delete the card
        DeleteBtn = (Button) findViewById(R.id.deletecard_button);
        final Toast deleteToast = Toast.makeText(toast_context, deleted_text, duration);

        if (lang.equals("en-de")) {
            DeleteQuery = "DELETE FROM " + Contract.Lingodecks_Tables.TABLE_GERMAN + " WHERE "
                    + Contract.Lingodecks_Tables._ID + " = " + CardID;
            Log.i(LOG_TAG, DeleteQuery);
        }
        else if(lang.equals("en-es")){
            DeleteQuery = "DELETE FROM " + Contract.Lingodecks_Tables.TABLE_SPANISH + " WHERE "
                    + Contract.Lingodecks_Tables._ID + " = " + CardID;
            Log.i(LOG_TAG, DeleteQuery);
        }
        else{
            DeleteQuery = "";
        }


        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDB.execSQL(DeleteQuery);
                deleteToast.show();
                Intent intent = new Intent(WordCardDisplay.this, CardList.class);
                startActivity(intent);
            }
        });

        //
        EditBtn = (Button)findViewById(R.id.editcard_btn) ;
        EditTextView = (EditText)findViewById(R.id.EditWordtv);
        final Button SubmitBtn = (Button)findViewById(R.id.returnBtn);
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView9.setVisibility(View.GONE);
                EditTextView.setVisibility(View.VISIBLE);
                EditTextView.setText(Translation);
                SubmitBtn.setText("Submit");
            }
        });
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

    public void setType(View view) {
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();

        if (button_clicked.equals("Submit")) {
            //EditCard();

        } else if (button_clicked.equals("Return to Index")) {

            Intent intent = new Intent(this, CardList.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        String translation = textView9.getText().toString();
        String english = textView10.getText().toString();
        outState.putString("TRANSLATION", translation);
        outState.putString("ENGLISH", english);
        super.onSaveInstanceState(outState);
        outState.putString("key", translation);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getString("TRANSLATION");
    }

}
