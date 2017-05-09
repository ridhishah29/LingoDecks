package group22.myapplication;

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
import java.util.Collections;
import java.util.Random;

public class Quickdraw extends Activity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private LingodecksDBHelper DBHelper;
    private SQLiteDatabase db;
    private Button button;
    TextView textView, answer_1, answer_2, answer_3, answer_4, question_word;
    CountDownTimer countdowntimer;
    SimpleCursorAdapter adapter;
    ArrayList<String> answerList = new ArrayList<>();
    ArrayList<String> wordList = new ArrayList<>();

    private static final int GERMAN_LOADER = 1;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quickdraw);

        textView = (TextView) findViewById((R.id.timer));

        getLoaderManager().initLoader(GERMAN_LOADER, null, this);

        answer_1 = (TextView) findViewById(R.id.answer_1);
        answer_2 = (TextView) findViewById(R.id.answer_2);
        answer_3 = (TextView) findViewById(R.id.answer_3);
        answer_4 = (TextView) findViewById(R.id.answer_4);
        question_word = (TextView) findViewById(R.id.question_word);

        if (savedInstanceState == null) {
            countdowntimer = new CountDownTimerClass(61000, 1000);
            countdowntimer.start();
            Log.d("InstanceState", "NULL");
        }
        else if (savedInstanceState != null && savedInstanceState.getBoolean("TIMER_RUNNING") == true) {
            int getTimerState = Integer.parseInt(savedInstanceState.getString("TIMER_STATE"));
            countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
            countdowntimer.start();
            Log.d("RunningTrue", "Not null and true");
        }
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
                countdowntimer = new CountDownTimerClass(120 * 1000, 1000);
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences myPref = getSharedPreferences("WordData", MODE_PRIVATE);
        int length = myPref.getInt("data_length", 0);

        answerList.clear();

        for(int i = 0; i < length; i++){
            answerList.add(myPref.getString("answer_" + i, "no data"));

            Log.v("Pref", myPref.getString("answer_" + i, "no data"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences myPref = getSharedPreferences("WordData", MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPref.edit();

        myEditor.clear();

        myEditor.putString("answer_1", answer_1.getText().toString());
        myEditor.putString("answer_2", answer_2.getText().toString());
        myEditor.putString("answer_3", answer_3.getText().toString());
        myEditor.putString("answer_4", answer_4.getText().toString());

        myEditor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == 1) {
            String[] columns = {
                    Contract.Lingodecks_Tables._ID,
                    Contract.Lingodecks_Tables.COLUMN_GER,
                    Contract.Lingodecks_Tables.COLUMN_GER_ENG
            };

            return new CursorLoader(this, Contract.Lingodecks_Tables.CONTENT_URI1, columns, null, null, "RANDOM()");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor != null && cursor.getCount() > 0) {
            wordList.clear();
            answerList.clear();

            int count = 0;
            while (cursor.moveToNext() && count < 4) {
                answerList.add(cursor.getString(1));
                wordList.add(cursor.getString(2));
                count += 1;
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


            //assign positions for answers and choose a question from list
            question_word.setText(wordList.get(numList.get(randInt)));
            answer_1.setText(answerList.get(numList.get(0)));
            answer_2.setText(answerList.get(numList.get(1)));
            answer_3.setText(answerList.get(numList.get(2)));
            answer_4.setText(answerList.get(numList.get(3)));
        }else{
            answer_1.setText("No answer here");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        adapter.swapCursor(null);
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
        String setTimerState = textView.getText().toString();
        outState.putString("TIMER_STATE", setTimerState);
        outState.putBoolean("TIMER_RUNNING", isRunning);
        super.onSaveInstanceState(outState);

//        String answer_one = answer_1.getText().toString();
//        String answer_two = answer_2.getText().toString();
//        String answer_three = answer_3.getText().toString();
//        String answer_four = answer_4.getText().toString();

        outState.putString("key", setTimerState);
//        outState.putString("answer_1", answer_one);
//        outState.putString("answer_2", answer_two);
//        outState.putString("answer_3", answer_three);
//        outState.putString("answer_4", answer_four);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        answer_1.setText(savedInstanceState.getString("answer_1"));
//        answer_2.setText(savedInstanceState.getString("answer_2"));
//        answer_3.setText(savedInstanceState.getString("answer_3"));
//        answer_4.setText(savedInstanceState.getString("answer_4"));
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

        }
    }
}
