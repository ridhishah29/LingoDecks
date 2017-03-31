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


        button = (Button) findViewById(R.id.start_timer);
        textView = (TextView) findViewById((R.id.timer));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdowntimer = new CountDownTimerClass(60000,1000);
                countdowntimer.start();
            }

        });
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
