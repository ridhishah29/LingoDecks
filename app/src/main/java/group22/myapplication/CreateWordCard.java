package group22.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card);

        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        final String languageSet = langPref.getString("language", "");
        new FetchTranslation().execute();


        Button btnResume = (Button) findViewById(R.id.submitword_btn);
        btnResume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editText = (EditText) findViewById(R.id.wordcard_txt);
                String user_input = editText.getText().toString();


//                if(TextUtils.isEmpty(user_input)) {
//                    textView = (TextView) findViewById(R.id.testview);
//                    textView.setText("Please enter a word");
//                } else {
//                    translateParams params = new translateParams(user_input, languageSet);
//                    FetchTranslation myTask = new FetchTranslation();
//                    myTask.execute(params);
//                }
            }
        });
    }

//     PUT THESE METHODS IN CREATE-A-CARD ACTIVITY

    class FetchTranslation extends AsyncTask<Void, String, String> {
        String apiKey = "trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873";
        String languageDirection = "en-de";
        String result = "";
        //Dummy data
        String text = "The cat sat on the mat";

        @Override
        protected String doInBackground(Void... voids) {
            String translationResult;

            // Example API call
            // https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873&text=cat&lang=en-it

            //creating URI
            final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
            final String API_KEY = "key";
            final String LANGUAGE = "lang";
            final String TEXT = "text";

            Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, apiKey)
                    .appendQueryParameter(TEXT, text)
                    .appendQueryParameter(LANGUAGE, languageDirection)
                    .build();

            //check connectivity
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){
                result = GET(uriBuilder.toString());

                translationResult = getTranslationFromJson(result);

                if(translationResult != null){
                    Log.v("Translation", translationResult);
                }
            }
            else{
                Log.v("NETWORK","No network connection");
            }

            return null;
        }

    }
    // Take the raw JSON data to get the data we need?
    private String getTranslationFromJson(String jsonStr) {
        String resultStr;

        try{
            // JSON objects that need to be extracted
            final String LANGUAGE = "lang";
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

    //--------------------

//    private static class translateParams {
//        String userWord;
//        String languageSet;
//
//        translateParams(String userWord, String languageSet) {
//            this.userWord = userWord;
//            this.languageSet = languageSet;
//        }
//    }
//
//    class FetchTranslation extends AsyncTask<translateParams, String, String> {
//        String apiKey = "trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873";
//        String result = "";
//        //Dummy data
//
//        @Override
//        protected String doInBackground(translateParams... params) {
//            final String translationResult;
//            final String userWord = params[0].userWord;
//            String languageDirection = params[0].languageSet;
//
//            // Example API call
//            // https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873&text=cat&lang=en-it
//
//            //creating URI
//            final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
//            final String API_KEY = "key";
//            final String LANGUAGE = "lang";
//            final String TEXT = "text";
//
//            Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
//                    .appendQueryParameter(API_KEY, apiKey)
//                    .appendQueryParameter(TEXT, userWord)
//                    .appendQueryParameter(LANGUAGE, languageDirection)
//                    .build();
//
//            //check connectivity
//            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//            if (networkInfo != null && networkInfo.isConnected()){
//                result = GET(uriBuilder.toString());
//                Log.d("test", "Here3");
//
//                translationResult = getTranslationFromJson(result);
//                textView = (TextView) findViewById(R.id.testview);
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("test", "Here4");
//
//                        String compareTxt = "[\"" + userWord + "\"]";
//                        if(translationResult.equals(compareTxt)) {
//                            textView.setText("Unable to translate word. Please try another word.");
//                            Log.v("Translation", "Not Found");
//                        }
//                        else {
//                            Log.v("Translation", translationResult);
//                            Log.v("Translation", "Found");
//                        }
//                    }
//                });
//            }
//            else{
//                Log.v("NETWORK", "No network connection");
//            }
//
//            return null;
//        }
//
//    }
//    // Take the raw JSON data to get the data we need?
//    private String getTranslationFromJson(String jsonStr) {
//        String resultStr;
//
//        try{
//            // JSON objects that need to be extracted
//            final String TEXT = "text";
//
//            JSONObject textJSON = new JSONObject(jsonStr);
//            JSONArray resultArray = textJSON.getJSONArray(TEXT);
//            resultStr = resultArray.toString();
//
//        }catch(JSONException e){
//            return null;
//        }
//        return resultStr;
//    }
//
//    private String GET(String url) {
//
//        InputStream is;
//        String result = "";
//        URL request = null;
//
//        try{
//            request = new URL(url);
//        }catch(MalformedURLException e){
//            e.printStackTrace();
//        }
//
//        HttpURLConnection conn = null;
//        try{
//            conn = (HttpURLConnection) request.openConnection();
//            conn.connect();
//
//            is = conn.getInputStream();
//            if(is != null){
//                result = convertInputStreamToString(is);
//            }else{
//                result = "Did not work!";
//            }
//
//        }catch(IOException e){
//            e.printStackTrace();
//
//        }finally{
//            conn.disconnect();
//        }
//        return result;
//    }
//
//    private String convertInputStreamToString(InputStream is) throws IOException{
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
//        String line;
//        String result = "";
//        while((line = bufferedReader.readLine()) != null){
//            result += line;
//        }
//        is.close();
//        return result;
//    }

}
