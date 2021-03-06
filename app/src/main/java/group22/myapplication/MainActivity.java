package group22.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.Calendar;

public class MainActivity extends Activity{
    private LingodecksDBHelper DBHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up daily notification to notify the user at 6PM everyday.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        // if the current time is past the set time, then a day is added and the notification will not appear until the next day at 6PM.
        if (Calendar.getInstance().after(calendar)) {
            Log.v("Here", "Day added");
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        DBHelper = new LingodecksDBHelper(getApplicationContext(), LingodecksDBHelper.DB_NAME,null,LingodecksDBHelper.DB_VERSION);
        db = DBHelper.getWritableDatabase();
    }

    public void setLanguage(View view){
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();

        if(button_clicked.equals("German")){
            //set language to be german
            Intent intent = new Intent(this, GameModeActivity.class);
            SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
            SharedPreferences.Editor edit = langPref.edit();
            edit.clear();
            edit.putString("language", "en-de");
            edit.commit();

            startActivity(intent);
        }
        else if(button_clicked.equals("Spanish")){
            //set language to be spanish
            Intent intent = new Intent(this, GameModeActivity.class);
            SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
            SharedPreferences.Editor edit = langPref.edit();
            edit.clear();
            edit.putString("language", "en-es");
            edit.commit();

            startActivity(intent);

        }
    }
    public void openGuide(View view) {
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();
        if (button_clicked.equals("User Guide")) {
            Intent intent = new Intent(this, Readme.class);
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
        //Quit app
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

