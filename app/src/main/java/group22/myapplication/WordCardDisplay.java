package group22.myapplication;

import android.app.Activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WordCardDisplay extends Activity{

    public SQLiteDatabase myDB;
    private TextView textView9, textView10;
    private EditText EditTextView;
    private Button EditBtn, DeleteBtn, SubmitBtn;
    LingodecksDBHelper DBHelper;

    String translatedWord = "";
    String languageSet = "";
    public static final String LOG_TAG = "WordCardDisplay";
    Context toast_context = this;
    CharSequence deleted_text = "Card was successfully deleted";
    CharSequence edit_text = "Card was successfully edited";
    int duration = Toast.LENGTH_SHORT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        languageSet = langPref.getString("language", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card_display);

        DBHelper = new LingodecksDBHelper(this,LingodecksDBHelper.DB_NAME,null,LingodecksDBHelper.DB_VERSION);
        myDB = DBHelper.getWritableDatabase();

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
        final String DeleteQuery = "DELETE FROM " + Contract.Lingodecks_Tables.TABLE_GERMAN + " WHERE "
                + Contract.Lingodecks_Tables._ID + " = " + CardID;
        Log.i(LOG_TAG, DeleteQuery);
        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDB.execSQL(DeleteQuery);
                deleteToast.show();
                Intent intent = new Intent(WordCardDisplay.this, CardList.class);
                startActivity(intent);
            }
        });

        EditBtn = (Button)findViewById(R.id.editcard_btn) ;
        EditTextView = (EditText)findViewById(R.id.EditWordtv);
        SubmitBtn = (Button)findViewById(R.id.returnBtn);
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView9.setVisibility(View.GONE);
                EditTextView.setVisibility(View.VISIBLE);
                EditTextView.setText(Translation);
                SubmitBtn.setText("Submit");

            }
        });

        SubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Getting text value of button pressed
                SubmitBtn = (Button)findViewById(R.id.returnBtn);
                String button_clicked = SubmitBtn.getText().toString();

                if (button_clicked.equals("Submit")) {
                    String user_input = EditTextView.getText().toString();

                    if(TextUtils.isEmpty(user_input)) {
                        //default value
                        textView9.setText("Please enter a word");
                        Log.v("hi", user_input);
                    } else {
                        //Translates the word the user entered
                        translateParams params = new translateParams(user_input, languageSet);
                        FetchTranslation myTask = new FetchTranslation();
                        myTask.execute(params);

                        //Calls function to update the Database
                        updateDB();
                    }

                } else if (button_clicked.equals("Return to index")) {

                    Intent intent = new Intent(WordCardDisplay.this, CardList.class);
                    startActivity(intent);
                }
            }
        });


    }

    /*
    public void setType(View view) {


    }*/


    private void updateDB() {
        ContentValues values;
        String user_input = EditTextView.getText().toString();

        if (languageSet == "en-de") {
            Cursor c = getContentResolver().query(Contract.BASE_CONTENT_URI1, null, Contract.Lingodecks_Tables.COLUMN_GER_ENG + " = " + DatabaseUtils.sqlEscapeString(user_input), null, null);
            if (c.getCount() == 0) {
            //on the receiving side
            //get the intent that started this activity
            Intent intent = getIntent();

            //grab the data from CardList
            String message = intent.getStringExtra("Card");

            //split data into arrays
            String[] details = message.split(" - ");

            //declare variables for array
            final String CardID = details[0];

            //Convert from string to long
            long ID = Long.parseLong(CardID);

            //accessing Content_Uri
            Contract.Lingodecks_Tables ContentURI = new Contract.Lingodecks_Tables();
            Uri CONTENT_URI1 = ContentURI.CONTENT_URI1;

            //declaring a uri
            final Uri uri = ContentUris.withAppendedId(CONTENT_URI1, ID);

            //Defining content values to update database
            values = new ContentValues();
            values.put(Contract.Lingodecks_Tables.COLUMN_GER_ENG, user_input);
            values.put(Contract.Lingodecks_Tables.COLUMN_GER, translatedWord);

            //Content resolver to be passed to LDContentProvider
            getContentResolver().update(uri, values, CardID, null);
                Log.v("Exists", "No");
            } else {
                textView10.setText("Choose different word! Already Taken");
                Log.v("Exists", "Yes");
            }
        }else if (languageSet == "en-es") {
            Cursor c = getContentResolver().query(Contract.BASE_CONTENT_URI2, null, Contract.Lingodecks_Tables.COLUMN_ESP_ENG + " = " + DatabaseUtils.sqlEscapeString(user_input), null, null);
            if (c.getCount() == 0) {
            //on the receiving side
            //get the intent that started this activity
            Intent intent = getIntent();

            //grab the data from CardList
            String message = intent.getStringExtra("Card");

            //split data into arrays
            String[] details = message.split(" - ");

            //declare variables for array
            final String CardID = details[0];

            //Convert from string to long
            long ID = Long.parseLong(CardID);

            //accessing Content_Uri
            Contract.Lingodecks_Tables ContentURI = new Contract.Lingodecks_Tables();
            Uri CONTENT_URI2 = ContentURI.CONTENT_URI2;

            //declaring a uri
            final Uri uri = ContentUris.withAppendedId(CONTENT_URI2, ID);

            //Defining content values to update database
            values = new ContentValues();
            values.put(Contract.Lingodecks_Tables.COLUMN_ESP_ENG, user_input);
            values.put(Contract.Lingodecks_Tables.COLUMN_ESP, translatedWord);

            //Content resolver to be passed to LDContentProvider
            getContentResolver().update(uri, values, CardID, null);
                Log.v("Exists", "No");
            } else {
                textView10.setText("Choose different word! Already Taken");
                Log.v("Exists", "Yes");
            }
        }

        final Toast editToast = Toast.makeText(toast_context, edit_text, duration);
        editToast.show();
        Intent intent = new Intent(this, CardList.class);
        startActivity(intent);
    }

    private static class translateParams {
        String userWord;
        String languageSet;

        translateParams(String userWord, String languageSet) {
            this.userWord = userWord;
            this.languageSet = languageSet;
            Log.v("Params", userWord);
            Log.v("Params", languageSet);
        }
    }

    class FetchTranslation extends AsyncTask<translateParams, String, String> {
        String apiKey = "trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873";
        String result = "";

        @Override
        protected String doInBackground(translateParams... params) {
            final String translationResult;
            final String userWord = params[0].userWord;
            String languageDirection = params[0].languageSet;

            // Example API call
            // https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873&text=cat&lang=en-it

            //creating URI
            final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
            final String API_KEY = "key";
            final String LANGUAGE = "lang";
            final String TEXT = "text";

            Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, apiKey)
                    .appendQueryParameter(TEXT, userWord)
                    .appendQueryParameter(LANGUAGE, languageDirection)
                    .build();

            //check connectivity
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){
                result = GET(uriBuilder.toString());

                translationResult = getTranslationFromJson(result);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String compareTxt = "[\"" + userWord + "\"]";
                        if(translationResult.equals(compareTxt)) {
                            textView9.setText("Unable to translate word. Please try another word.");
                            Log.v("Translation", "Not Found");
                        }
                        else {
                            Log.v("Translation", translationResult);
                            translatedWord = translationResult;
                        }
                    }
                });
            }
            else{
                Log.v("NETWORK", "No network connection");
            }

            return null;
        }

    }
    // Take the raw JSON data to get the data we need?
    private String getTranslationFromJson(String jsonStr) {
        String resultStr;

        try{
            // JSON objects that need to be extracted
            final String TEXT = "text";

            JSONObject textJSON = new JSONObject(jsonStr);
            JSONArray resultArray = textJSON.getJSONArray(TEXT);
            resultStr = resultArray.toString();

        }catch(JSONException e){
            return null;
        }
        return resultStr;
    }

    private String GET(String url) {

        InputStream is;
        String result = "";
        URL request = null;

        try{
            request = new URL(url);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection) request.openConnection();
            conn.connect();

            is = conn.getInputStream();
            if(is != null){
                result = convertInputStreamToString(is);
            }else{
                result = "Did not work!";
            }

        }catch(IOException e){
            e.printStackTrace();

        }finally{
            conn.disconnect();
        }
        return result;
    }

    private String convertInputStreamToString(InputStream is) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }
        is.close();
        return result;
    }

}
