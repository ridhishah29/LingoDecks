package group22.myapplication;

import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class Quickdraw extends Activity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private LingodecksDBHelper DBHelper;
    private SQLiteDatabase db;
    private Button button;
    TextView textView, answer_1, answer_2, answer_3, answer_4, question_word;
    CountDownTimer countdowntimer;
    SimpleCursorAdapter adapter;
    Map<String, Integer> answerList = new LinkedHashMap<String, Integer>();
    Map<String, Integer> wordList = new LinkedHashMap<String, Integer>();
    String languageSet = "";
    Boolean clickedActivity = false;

    //created array for linkedHashMap to access indexing - for randomisation.
    ArrayList<String> answerListArray = new ArrayList<String>();
    ArrayList<String> wordListArray = new ArrayList<String>();

    public Integer score = 0;
    public Integer totalScore = 0;

    private SharedPreferences myPref;

    private static final int GERMAN_LOADER = 1;
    private static final int SPANISH_LOADER = 2;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        languageSet = langPref.getString("language", "");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.quickdraw);
        textView = (TextView) findViewById((R.id.timer));
        answer_1 = (TextView) findViewById(R.id.answer_1);
        answer_2 = (TextView) findViewById(R.id.answer_2);
        answer_3 = (TextView) findViewById(R.id.answer_3);
        answer_4 = (TextView) findViewById(R.id.answer_4);
        question_word = (TextView) findViewById(R.id.question_word);


        //grab score from previous question activity - only grabs if the quickdraw activity is not the first activity
        if(getIntent().hasExtra("score")){
            score = getIntent().getExtras().getInt("score");
            totalScore = getIntent().getExtras().getInt("totalScore");
        }
        else{
            score = 0;
            totalScore = 0;
        }
//
//        if(getIntent().hasExtra("clicked")){
//            if(getIntent().getExtras().getBoolean("clicked")== true){
//                Log.v("clickedVar1", "Clicked answer");
//                if (getIntent().hasExtra("time")){
//                    String data = getIntent().getExtras().getString("time");
//                    int getTimerState = Integer.parseInt(data);
//                    countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
//                    countdowntimer.start();
//                }
//            }else if(clickedActivity == false){
//                Log.v("clickedVar2", "rotated screen");
//                SharedPreferences timePref = getSharedPreferences("timer", MODE_PRIVATE);
//
//                SharedPreferences.Editor myEditor = timePref.edit();
//
//                myEditor.clear();
//
//                String timeData = getIntent().getExtras().getString("time");
//
//
//                myEditor.putString("timerCurrent", timeData);
//
//                myEditor.commit();
//
//                int getTimerState = Integer.parseInt(timePref.getString("timerCurrent", "0"));
//                countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
//                countdowntimer.start();
//            }
//        }
//        else{
//            Log.v("clickedVar3", "enter Screen");
//            countdowntimer = new CountDownTimerClass(61000, 1000);
//            countdowntimer.start();
//
//        }


        //if answer button is clicked, it starts a new activity and this saves the time
        if (getIntent().hasExtra("time")){
            String data = getIntent().getExtras().getString("time");
            int getTimerState = Integer.parseInt(data);
            countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
            countdowntimer.start();
        } //if screen has not rotated and therefore not saved a value
        else if (savedInstanceState == null) {
            countdowntimer = new CountDownTimerClass(61000, 1000);
            countdowntimer.start();
            Log.d("InstanceState", "NULL");
        } //if the screen has been rotated and the timer is still running i.e. not in pause screen
        else if (savedInstanceState != null && savedInstanceState.getBoolean("TIMER_RUNNING") == true) {
            // need to catch if int == "Time's up"
            int getTimerState = Integer.parseInt(savedInstanceState.getString("TIMER_STATE"));
            countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
            countdowntimer.start();
            Log.d("RunningTrue", "Not null and true");
        }//if pause screen is active
        else {
            int getTimerState = Integer.parseInt(savedInstanceState.getString("TIMER_STATE"));
            countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
            String re = String.valueOf(getTimerState);
            textView.setText(re);
            Log.d("RunningFalse", "Not null and false");
        }



        //opens full screen custom dialog
        final Dialog dialog = new Dialog(Quickdraw.this, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pause_dialog);

        Button pause = (Button) findViewById(R.id.pause_btn);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.show();
                countdowntimer.cancel();
                isRunning = false;
                keepDialogOpen(dialog);
            }
        });

        Button btnResume = (Button) dialog.findViewById(R.id.btn_resumeGame);
        btnResume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                int getTimerState = Integer.parseInt(textView.getText().toString());
                countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
                countdowntimer.start();
                isRunning = true;
                Log.d("ResumeClick", Boolean.toString(isRunning));
            }
        });

        Button btn = (Button) dialog.findViewById(R.id.btn_mainMenu);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Quickdraw.this, GameModeActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        //answer buttons create multiple activities - back button will go back each activity.
        Intent setIntent = new Intent(this, GameModeActivity.class);

        //reset score
        score = 0;
        totalScore = 0;
        countdowntimer.cancel();
        startActivity(setIntent);
    }

    public void answerBtn(View v){
        clickedActivity = true;

        Intent intent = new Intent(this, Quickdraw.class);
        intent.putExtra("time", textView.getText().toString());
        intent.putExtra("clicked", clickedActivity);

        TextView buttonClicked = (TextView) findViewById(v.getId());

        if(answerList.get(buttonClicked.getText()) == (wordList.get(question_word.getText()))) {
            Log.v("QuestionStatus", "Correct answer");
            score += 1;
            totalScore += 1;
        }
        else{
            Log.v("QuestionStatus", "Wrong answer");
            totalScore += 1;
        }
        clickedActivity = false;
        intent.putExtra("score", score);
        intent.putExtra("totalScore", totalScore);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (languageSet == "en-de") {
            getLoaderManager().initLoader(GERMAN_LOADER, null, this);
        }else if (languageSet == "en-es"){
            getLoaderManager().initLoader(SPANISH_LOADER, null, this);
        }

        myPref = getSharedPreferences("quickDraw", MODE_PRIVATE);

        //initiate answerList array which holds all answers
        wordListArray.clear();
        answerListArray.clear();

        //add answers to array saved in sharedpreferences
        for(int i = 1; i < 5; i++){
            answerListArray.add(myPref.getString("answer_" + i, "no data"));
            Log.v("savedanswer", (myPref.getString("answer_" + i, "no data")));
        }

        //reassign question to textview after resuming activity.
        question_word.setText(myPref.getString("question_word", "no data"));

        //reassign the answers to their textviews after resuming activity.
        answer_1.setText(answerListArray.get(0));
        answer_2.setText(answerListArray.get(1));
        answer_3.setText(answerListArray.get(2));
        answer_4.setText(answerListArray.get(3));
        score = myPref.getInt("score", 0);
        totalScore = myPref.getInt("totalScore", 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myPref = getSharedPreferences("quickDraw", MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPref.edit();

        myEditor.clear();

        myEditor.putString("question_word", question_word.getText().toString());
        myEditor.putString("answer_1", answer_1.getText().toString());
        myEditor.putString("answer_2", answer_2.getText().toString());
        myEditor.putString("answer_3", answer_3.getText().toString());
        myEditor.putString("answer_4", answer_4.getText().toString());
        myEditor.putInt("score", score);
        myEditor.putInt("totalScore", totalScore);

        myEditor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stops timer when outside of activity i.e. back button pressed
        countdowntimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v("cursorStatus", "Getting cursor loader");
        if(id == 1) {
            String[] columns = {
                    Contract.Lingodecks_Tables._ID,
                    Contract.Lingodecks_Tables.COLUMN_GER,
                    Contract.Lingodecks_Tables.COLUMN_GER_ENG
            };
            return new CursorLoader(this, Contract.Lingodecks_Tables.CONTENT_URI1, columns, null, null, "RANDOM()");
        }
        else if(id == 2){
            String[] columns = {
                    Contract.Lingodecks_Tables._ID,
                    Contract.Lingodecks_Tables.COLUMN_ESP,
                    Contract.Lingodecks_Tables.COLUMN_ESP_ENG
            };
            return new CursorLoader(this, Contract.Lingodecks_Tables.CONTENT_URI2, columns, null, null, "RANDOM()");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.v("cursorStatus", "Finished getting loader");

        if(cursor != null && cursor.getCount() > 0) {
            wordList.clear();
            answerList.clear();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                answerList.put(cursor.getString(1), cursor.getInt(0));
                wordList.put(cursor.getString(2), cursor.getInt(0));
            }

            //Create array of ints from 0 to 3 for randomisation
            ArrayList<Integer> numList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                numList.add(new Integer(i));
            }
            //randomise int list
            Collections.shuffle(numList);

            //Create a random number for the question
            Random rand = new Random();
            int randInt = rand.nextInt(4);

            answerListArray = new ArrayList<String>(answerList.keySet());
            wordListArray = new ArrayList<String>(wordList.keySet());

            //assign positions for answers and choose a question from list
            question_word.setText(wordListArray.get(numList.get(randInt)));
            answer_1.setText(answerListArray.get(numList.get(0)).toString());
            answer_2.setText(answerListArray.get(numList.get(1)).toString());
            answer_3.setText(answerListArray.get(numList.get(2)).toString());
            answer_4.setText(answerListArray.get(numList.get(3)).toString());
        }else{
            question_word.setText("No question here");
            answer_1.setText("No answer here");
            answer_2.setText("No answer here");
            answer_3.setText("No answer here");
            answer_4.setText("No answer here");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v("cursorStatus", "resetting loader");
        loader = null;
    }
    // prevent pause screen open when orientation changes
    private void keepDialogOpen(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        if (getIntent().hasExtra("clicked")) {
            if (getIntent().getExtras().getBoolean("clicked")== true){
                //rotated but with answer clicked
                Log.v("SAVED1", "ASPDOISD");
                String setTimerState = textView.getText().toString();
                Log.v("timerSet",setTimerState);
                outState.putString("TIMER_STATE", setTimerState);
                outState.putBoolean("TIMER_RUNNING", isRunning);
                super.onSaveInstanceState(outState);
                outState.putString("key", setTimerState);
            }
            else {
                Log.v("SAVED2", "ASPDOISD");
                String setTimerState = textView.getText().toString();
                Log.v("timerSet",setTimerState);
                outState.putString("TIMER_STATE", setTimerState);
                outState.putBoolean("TIMER_RUNNING", isRunning);
                super.onSaveInstanceState(outState);
                outState.putString("key", setTimerState);
            }

        }
        else{
            //rotated without clicking
            Log.v("SAVED3", "ASPDOISD");
            String setTimerState = textView.getText().toString();
            Log.v("timerSet",setTimerState);
            outState.putString("TIMER_STATE", setTimerState);
            outState.putBoolean("TIMER_RUNNING", isRunning);
            super.onSaveInstanceState(outState);
            outState.putString("key", setTimerState);
        }


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getString("TIMER_STATE");

    }

    public class CountDownTimerClass extends CountDownTimer {

        public CountDownTimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished/1000);
            textView.setText(Integer.toString(progress));
            isRunning = true;
        }

        @Override
        public void onFinish() {
            textView.setText("Time's up");
            //call results page with score
            Intent instantIntent = new Intent(getApplicationContext(), QuickdrawResults.class);
            instantIntent.putExtra("score", score);
            instantIntent.putExtra("totalScore", totalScore);

            //prevent multiple calls
            instantIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(instantIntent);
            //reset score
            score = 0;
            totalScore = 0;

        }
    }
}
