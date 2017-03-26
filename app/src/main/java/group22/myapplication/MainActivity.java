package group22.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setLanguage(View view){
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();

        if(button_clicked.equals("German")){
            //set language to be german
            Intent intent = new Intent(this, GameModeActivity.class);
            startActivity(intent);
        }
        else if(button_clicked.equals("Spanish")){
            //set language to be spanish
            Intent intent = new Intent(this, GameModeActivity.class);
            startActivity(intent);
        }
    }

    class FetchTranslation extends AsyncTask<String, String, String> {
        String apiKey = "trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873";
        String languageDirection = "en-it";
        String result = "";
        String text = "";

        @Override
        protected String doInBackground(String... params) {
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

            //check connectivity m

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){
                result = GET(uriBuilder.toString());

                translationResult = getTranslationFromJson(result);
                if(translationResult != null){

                    Log.i("translation", translationResult);

                }
            }
            else{
                publishProgress("No network connection");
            }

            return null;
        }

    }
    // Take the raw JSON data to get the data we need?
    private String getTranslationFromJson(String jsonStr) {
//        String resultStr = new String();
//        try{
//            // JSON objects that need to be extracted
//            final String LANGUAGE = "lang";
//            final String TEXT = "text";
//
//            JSONObject textJSON = new JSONObject(jsonStr);
//
//        }catch(JSONException e){
//            return null;
//        }
//        return resultStr;
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
