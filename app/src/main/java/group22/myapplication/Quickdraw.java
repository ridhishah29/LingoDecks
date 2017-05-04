package group22.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Quickdraw extends Activity {

    private Button button;
    TextView textView;
    CountDownTimer countdowntimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quickdraw);

        textView = (TextView) findViewById((R.id.timer));

        if (savedInstanceState == null) {
            countdowntimer = new CountDownTimerClass(60000, 1000);
            countdowntimer.start();

        } else {
            String getTimerState = savedInstanceState.getString("key");
            int intTimer = Integer.parseInt(getTimerState);
            countdowntimer = new CountDownTimerClass(intTimer * 1000, 1000);
            countdowntimer.start();
        }

        Button pause = (Button) findViewById(R.id.pause_btn);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Quickdraw.this);
                dialog.setContentView(R.layout.pause_dialog);
                dialog.setTitle("Paused");
                dialog.setCancelable(true);

                countdowntimer.cancel();
                dialog.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        String setTimerState = textView.getText().toString();
        outState.putString("key", setTimerState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public class CountDownTimerClass extends CountDownTimer {

        public CountDownTimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished/1000);
            textView.setText(Integer.toString(progress));
        }

        @Override
        public void onFinish() {
            textView.setText("Time's up");
        }
    }


}
