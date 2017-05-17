package group22.myapplication;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class QuickdrawResults extends Activity {
    TextView scoreText, totalScoreText, percentageScoreText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quickdraw_results);
        scoreText = (TextView) findViewById(R.id.correctScore);
        totalScoreText = (TextView) findViewById(R.id.totalScore);
        percentageScoreText = (TextView) findViewById(R.id.percentageScore);


        if(getIntent().hasExtra("score")){
            Integer score = getIntent().getExtras().getInt("score");
            Integer totalScore = getIntent().getExtras().getInt("totalScore");
            if(totalScore == 0) {
                scoreText.setText("0");
                totalScoreText.setText("0%");
                percentageScoreText.setText("0 %");
            }else{
                Float percentageScore = ((float) score/ (float) totalScore) * 100;
                scoreText.setText(score.toString());
                totalScoreText.setText(totalScore.toString());

                String roundedScore = String.format("%.2f", percentageScore);
                percentageScoreText.setText(roundedScore + "%");
            }


        }



    }
}
