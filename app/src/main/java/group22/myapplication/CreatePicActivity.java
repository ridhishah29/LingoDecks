package group22.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class CreatePicActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout for activity
        setContentView(R.layout.picture_card);

        // this can be added wherever the api is needed.
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        String languageSet = langPref.getString("language", "");
        Log.v("language chosen", languageSet);
    }

}
