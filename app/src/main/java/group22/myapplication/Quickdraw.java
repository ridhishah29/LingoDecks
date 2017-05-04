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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Quickdraw extends Activity {

    private Button button;
    TextView textView;
    CountDownTimer countdowntimer;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quickdraw);

        textView = (TextView) findViewById((R.id.timer));

        if (savedInstanceState == null) {
            countdowntimer = new CountDownTimerClass(60000, 1000);
            countdowntimer.start();

        } else {
            int getTimerState = Integer.parseInt(savedInstanceState.getString("key"));
            countdowntimer = new CountDownTimerClass(getTimerState * 1000, 1000);
            countdowntimer.start();
        }


        //opens custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pause_dialog,null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        Button pause = (Button) findViewById(R.id.pause_btn);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                countdowntimer.cancel();
                dialog.show();
                Button btnresume = (Button) dialog.findViewById(R.id.btn_resumeGame);
                btnresume.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        int pausedcount = Integer.parseInt(textView.getText().toString());
                        countdowntimer = new CountDownTimerClass(pausedcount*1000, 1000);
                        countdowntimer.start();
                        dialog.dismiss();
                    }
                });

                keepDialogOpen(dialog);

            }
        });
    }

    // prevent pause screen open when orientation changes
    private void keepDialogOpen(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        String setTimerState = textView.getText().toString();
        outState.putString("key", setTimerState);
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
