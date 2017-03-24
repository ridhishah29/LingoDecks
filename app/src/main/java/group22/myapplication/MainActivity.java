package group22.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setLanguage(View view){
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();

        if(button_clicked.equals("German")){
            //set language to be german
            Intent intent = new Intent(this, GameModeActivity.class);
            startActivity(intent);
        }
        else if(button_clicked.equals("Spanish")){
            //set language to be spanish
            Intent intent = new Intent(this, GameModeActivity.class);
            startActivity(intent);
        }
    }
}
