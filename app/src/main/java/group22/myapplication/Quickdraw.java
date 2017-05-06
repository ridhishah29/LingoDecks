package group22.myapplication;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Quickdraw extends Activity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private LingodecksDBHelper DBHelper;
    private SQLiteDatabase db;
    private Button button;
    TextView textView, answer_1, answer_2, answer_3, answer_4;
    CountDownTimer countdowntimer;
    SimpleCursorAdapter adapter;

    private static final int GERMAN_LOADER = 1;
    boolean isRunning = false;
    public AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quickdraw);

        textView = (TextView) findViewById((R.id.timer));
//
//        getLoaderManager().initLoader(GERMAN_LOADER, null, this);

        //adapters are for listviews
//        adapter = new SimpleCursorAdapter(
//                this,
//                R.layout.quickdraw,
//                null,
//                new String[] {Contract.Lingodecks_Tables.COLUMN_GER},
//                new int[]{ R.id.answer_1, R.id.answer_2, R.id.answer_3, R.id.answer_4 },
//                0
//        );

        answer_1 = (TextView) findViewById(R.id.answer_1);
        answer_2 = (TextView) findViewById(R.id.answer_2);
        answer_3 = (TextView) findViewById(R.id.answer_3);
        answer_4 = (TextView) findViewById(R.id.answer_4);

        //opens custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pause_dialog,null);
        builder.setView(dialogView);
        dialog = builder.create();
//
//        if (savedInstanceState == null) {
//            Log.v("nope", "nope");
//            countdowntimer = new CountDownTimerClass(60000, 1000);
//            countdowntimer.start();
//
//        } else {
//            Log.v("yes", "yes");
//            int getTimerState = Integer.parseInt(savedInstanceState.getString("key"));
//            countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
//            countdowntimer.start();
//
//        }

        Button pause = (Button) findViewById(R.id.pause_btn);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                countdowntimer.cancel();
                dialog.show();
                Button btnresume = (Button) dialog.findViewById(R.id.btn_resumeGame);
                btnresume.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        int pausedcount = Integer.parseInt(setTimerState.toString());
                        countdowntimer = new CountDownTimerClass(pausedcount*1000, 1000);
                        countdowntimer.start();
                        dialog.dismiss();
                    }
                });

                keepDialogOpen(dialog);

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
        Log.v("opensesame1", Boolean.toString(dialog.isShowing()));
        if (!setTimerState.toString().equals("") && diagIsOpen.equals(("false"))) {
            int pausedcount = Integer.parseInt(setTimerState.toString());
            countdowntimer = new CountDownTimerClass(pausedcount*1000, 1000);
            countdowntimer.start();
            Log.v("timer started", "Nope");
        }
        else if (!setTimerState.toString().equals("") && diagIsOpen.equals(("true"))) {
            int pausedcount = Integer.parseInt(setTimerState.toString());
            countdowntimer = new CountDownTimerClass(pausedcount*1000, 1000);
            countdowntimer.start();
            countdowntimer.cancel();
            Log.v("timer started", "maybe");
        }
        else{
            countdowntimer = new CountDownTimerClass(60000, 1000);
            countdowntimer.start();
            Log.v("timer started", "yues");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        countdowntimer.cancel();
        Log.v("opensesame2", Boolean.toString(dialog.isShowing()));
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
//        if(id == 1) {
//            String[] columns = {
//                    Contract.Lingodecks_Tables._ID,
//                    Contract.Lingodecks_Tables.COLUMN_GER,
//                    Contract.Lingodecks_Tables.COLUMN_GER_ENG
//            };
//            return new CursorLoader(this, Contract.Lingodecks_Tables.CONTENT_URI1, columns, null, null, null);
//        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        if(cursor != null && cursor.getCount() > 0){
//            StringBuilder stringBuilderQueryResult = new StringBuilder("");
//            while(cursor.moveToNext()){
//                stringBuilderQueryResult.append(cursor.getString(1));
//            }
//            answer_1.setText(stringBuilderQueryResult.toString());
//
//        }
//        else{
//            answer_1.setText("No answer here");
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        Log.v("reset", "reset");
    }
    // prevent pause screen open when orientation changes
    private void keepDialogOpen(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    String setTimerState = new String();
    String diagIsOpen = new String();
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        setTimerState = textView.getText().toString();
        diagIsOpen = Boolean.toString(dialog.isShowing());
        outState.putString("key", setTimerState);
        outState.putString("isOpen", diagIsOpen);
        Log.v("testest", diagIsOpen);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTimerState = savedInstanceState.getString("key");
        diagIsOpen = savedInstanceState.getString("isOpen");
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
            isRunning = false;
        }
    }


}
