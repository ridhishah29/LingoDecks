package group22.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Quickdraw extends Activity {

    TextView textView;
    CountDownTimer countdowntimer;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quickdraw);

        textView = (TextView) findViewById((R.id.timer));
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
