package group22.myapplication;

import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
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

        if(savedInstanceState == null) {
            countdowntimer = new CountDownTimerClass(60000, 1000);
            countdowntimer.start();
        } else {
            String what = savedInstanceState.getString("key");
            int whatty = Integer.parseInt(what);
            countdowntimer = new CountDownTimerClass(whatty*1000,1000);
            countdowntimer.start();
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        String rf = textView.getText().toString();
        outState.putString("key", rf);
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
