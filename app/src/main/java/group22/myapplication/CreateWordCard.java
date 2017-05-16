package group22.myapplication;

import android.app.Activity;
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

public class CreateWordCard extends Activity {

    TextView textView;
    EditText editText;
    String translatedWord = "";
    String languageSet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        languageSet = langPref.getString("language", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card);

        Button btnResume = (Button) findViewById(R.id.submitword_btn);
        btnResume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editText = (EditText) findViewById(R.id.wordcard_txt);
                String user_input = editText.getText().toString();

                if(TextUtils.isEmpty(user_input)) {
                    textView = (TextView) findViewById(R.id.testview);
                    textView.setText("Please enter a word");
                    Log.v("hi", user_input);
                } else {
                    translateParams params = new translateParams(user_input, languageSet);
                    FetchTranslation myTask = new FetchTranslation();
                    myTask.execute(params);

                   insertDB();
                }
            }
        });
//
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "I got 10000 in Quickdraw!");
//        sendIntent.setType("text/plain");
//        startActivity(sendIntent);
    }

    private void insertDB() {
        ContentValues values;
        editText = (EditText) findViewById(R.id.wordcard_txt);
        String user_input = editText.getText().toString();

        if(languageSet == "en-de") {

            values = new ContentValues();
            values.put(Contract.Lingodecks_Tables.COLUMN_GER_ENG, user_input);
            values.put(Contract.Lingodecks_Tables.COLUMN_GER, translatedWord);
            getContentResolver().insert(Contract.Lingodecks_Tables.CONTENT_URI1, values);

            Cursor c = getContentResolver().query(Contract.Lingodecks_Tables.CONTENT_URI1, null, Contract.Lingodecks_Tables.COLUMN_GER  + " = " + DatabaseUtils.sqlEscapeString("rainbowflies"), null, null);
            if(c.getCount() == 0) {
                Log.v("Exists", "False");
                Log.d("insertText", "5");

            }
            else {
                Log.v("Exists", "True");
                Log.d("insertText", "6");
            }

        }
//        else if(languageSet == "en-es") {
//            values = new ContentValues();
//            values.put("COLUMN_ESP_ENG", user_input);
//            values = new ContentValues();
//            values.put("COLUMN_ESP", translatedWord);
//            getContentResolver().insert(2, values);

//        }
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
                textView = (TextView) findViewById(R.id.testview);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String compareTxt = "[\"" + userWord + "\"]";
                        if(translationResult.equals(compareTxt)) {
                            textView.setText("Unable to translate word. Please try another word.");
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
