package group22.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;


public class QuickdrawResults extends AppCompatActivity {
    String languageSet = "";
    TextView scoreText, totalScoreText, percentageScoreText;
    Integer score = 0;
    Integer totalScore = 0;
    private ShareActionProvider mShareActionProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        languageSet = langPref.getString("language", "");
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_quickdraw_results);
//        ActionBar actionBar = getActionBar();
//        actionBar.show();
        scoreText = (TextView) findViewById(R.id.correctScore);
        totalScoreText = (TextView) findViewById(R.id.totalScore);
        percentageScoreText = (TextView) findViewById(R.id.percentageScore);

        if(getIntent().hasExtra("score")){
            score = getIntent().getExtras().getInt("score");
            totalScore = getIntent().getExtras().getInt("totalScore");
            if(totalScore == 0) {
                scoreText.setText("0");
                totalScoreText.setText("0");
                percentageScoreText.setText("0%");
            }else{
                Float percentageScore = ((float) score/ (float) totalScore) * 100;
                scoreText.setText(score.toString());
                totalScoreText.setText(totalScore.toString());

                String roundedScore = String.format("%.2f", percentageScore);
                percentageScoreText.setText(roundedScore + "%");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        //Create a share action provider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
         String language = "";
        //set the ShareIntent
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            if (languageSet == "en-de"){
                language = "German";
            }else if (languageSet == "en-es"){
                language = "Spanish";
            }
            String message = "I got " + score + " out of " + totalScore + " on Lingodecks " + language + " Quickdraw mode!";
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.setType("text/plain");
            mShareActionProvider.setShareIntent(shareIntent);
        }

        return true;

    }

}
