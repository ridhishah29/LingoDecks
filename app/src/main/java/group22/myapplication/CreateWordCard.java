package group22.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
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

public class CreateWordCard extends Activity {

    TextView textView;
    EditText editText;
    public String translatedWord, languageSet;

    public static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        languageSet = langPref.getString("language", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card);

        //saves edittext on screen rotation
        editText = (EditText) findViewById(R.id.wordcard_txt);
        if (savedInstanceState != null) {
            String setText = savedInstanceState.getString("setText");
            editText.setText(setText);
        }

        Button btnSubmit = (Button) findViewById(R.id.submitword_btn);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editText = (EditText) findViewById(R.id.wordcard_txt);
                String user_input = editText.getText().toString();

                if (TextUtils.isEmpty(user_input)) {
                    textView = (TextView) findViewById(R.id.testview);
                    textView.setText("Please enter a word.");
                } else {
                    translateParams params = new translateParams(user_input, languageSet);
                    FetchTranslation myTask = new FetchTranslation();
                    myTask.execute(params);

                    insertDB();
                }
            }
        });
    }

    //to get values inside asynctask
    private static class translateParams {
        String userWord;
        String languageSet;

        translateParams(String userWord, String languageSet) {
            this.userWord = userWord;
            this.languageSet = languageSet;
        }
    }

    class FetchTranslation extends AsyncTask<translateParams, String, String> {
        String apiKey = "trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873";
        String result = "";
        String translationResult;

        @Override
        protected String doInBackground(translateParams... params) {

            final String jsonResult;
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
            if (networkInfo != null && networkInfo.isConnected()) {
                result = GET(uriBuilder.toString());

                jsonResult = getTranslationFromJson(result);
                //removes json format
                translationResult = jsonResult.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
                textView = (TextView) findViewById(R.id.testview);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String compareTxt = userWord;
                        if (translationResult.equals(compareTxt)) {
                            textView.setText("Unable to translate word. Please try another word.");
                            Log.v("Translation", "Not Found");
                        } else {
                            //to get translated word from asynctask to insertdb
                            SharedPreferences.Editor editor = getSharedPreferences("TRANSLATION", MODE_PRIVATE).edit();
                            editor.remove("translated_word");
                            editor.apply();
                            editor.putString("translated_word", translationResult);
                            editor.apply();
                            Log.v("AsyncTranslate", translationResult);
                        }
                    }
                });
            } else {
                Log.v("NETWORK", "No network connection");
            }
            return null;
        }
    }

    // Take the raw JSON data to get the data we need
    private String getTranslationFromJson(String jsonStr) {
        String resultStr;

        try {
            // JSON objects that need to be extracted
            final String TEXT = "text";

            JSONObject textJSON = new JSONObject(jsonStr);
            JSONArray resultArray = textJSON.getJSONArray(TEXT);
            resultStr = resultArray.toString();

        } catch (JSONException e) {
            return null;
        }
        return resultStr;
    }

    private String GET(String url) {

        InputStream is;
        String result = "";
        URL request = null;

        try {
            request = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) request.openConnection();
            conn.connect();

            is = conn.getInputStream();
            if (is != null) {
                result = convertInputStreamToString(is);
            } else {
                result = "Did not work!";
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            conn.disconnect();
        }
        return result;
    }

    private String convertInputStreamToString(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        is.close();
        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        editText = (EditText) findViewById(R.id.wordcard_txt);
        String text = editText.getText().toString();
        outState.putString("setText", text);
    }

    private void insertDB() {
        ContentValues values;
        editText = (EditText) findViewById(R.id.wordcard_txt);
        String user_input = editText.getText().toString();

        textView = (TextView) findViewById(R.id.testview);

        String[] word = new String[1];
        word[0] = user_input;

        //gets translated word from asynctask
        sp = getSharedPreferences("TRANSLATION", MODE_PRIVATE);
        translatedWord = sp.getString("translated_word", translatedWord);
        Log.v("translation22", translatedWord);

        if (languageSet == "en-de") {
            //check if exists in db
            Cursor c = getContentResolver().query(Contract.BASE_CONTENT_URI1, null, Contract.Lingodecks_Tables.COLUMN_GER_ENG + " = " + DatabaseUtils.sqlEscapeString(user_input), null, null);
            if (c.getCount() == 0) {
                values = new ContentValues();
                values.put(Contract.Lingodecks_Tables.COLUMN_GER_ENG, user_input);
                values.put(Contract.Lingodecks_Tables.COLUMN_GER, translatedWord);
                Log.v("translation3", translatedWord);

                getContentResolver().insert(Contract.Lingodecks_Tables.CONTENT_URI1, values);

                textView.setText("");

                Context context = getApplicationContext();
                CharSequence text = "Word " + translatedWord + " has been created.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                textView.setText("This card has already been created.");
            }

        } else if (languageSet == "en-es") {
            //check if exists in db
            Cursor c = getContentResolver().query(Contract.BASE_CONTENT_URI2, null, Contract.Lingodecks_Tables.COLUMN_GER_ENG + " = " + DatabaseUtils.sqlEscapeString(user_input), null, null);
            if (c.getCount() == 0) {
                values = new ContentValues();
                values.put(Contract.Lingodecks_Tables.COLUMN_ESP_ENG, user_input);
                values.put(Contract.Lingodecks_Tables.COLUMN_ESP, translatedWord);
                getContentResolver().insert(Contract.Lingodecks_Tables.CONTENT_URI2, values);

                textView.setText("");

                Context context = getApplicationContext();
                CharSequence text = "Word " + translatedWord + " has been created.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                textView.setText("This card has already been created.");
            }
        }
    }

}
