package group22.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GameModeActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout for activity
        setContentView(R.layout.game_mode);


        // this can be added wherever the api is needed.
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        String languageSet = langPref.getString("language", "");
        Log.v("language chosen", languageSet);
    }

    public void setModes(View view){
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();

        if(button_clicked.equals("Quickdraw")){
            Intent intent = new Intent(this, Quickdraw.class);
            startActivity(intent);
        }
        else if(button_clicked.equals("Flashcards")){
            //set language to be spanish
            Intent intent = new Intent(this, FlashcardActivity.class);
            startActivity(intent);
        }
        else if(button_clicked.equals("Create Cards")){
            //set language to be spanish
<<<<<<< HEAD
            Intent intent = new Intent(this, CreateMenuActivity.class);
=======
            Intent intent = new Intent(this, CreateWordCard.class);
>>>>>>> origin/master
            startActivity(intent);
        }
        else if(button_clicked.equals("Custom Cards")){
            //set language to be spanish
            Intent intent = new Intent(this, GameModeActivity.class);
            startActivity(intent);
        }
        else if(button_clicked.equals("View Cards")){
            //set language to be spanish
            Intent intent = new Intent(this, CardList.class);
            startActivity(intent);
        }
    }
}
