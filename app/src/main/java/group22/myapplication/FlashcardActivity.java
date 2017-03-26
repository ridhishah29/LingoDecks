package group22.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FlashcardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard_menu);
    }

    public void setLanguage(View view){
        String button_clicked;
        String cardValue="";
        String cardType="";
        String translationInput="";
        int numOfCards=0;
        int counter=0;
        int score=0;
        button_clicked = ((Button) view).getText().toString();

        if(button_clicked.equals("10 Cards")) {
            numOfCards = 10;
        }
        else if(button_clicked.equals("20 Cards")){
            numOfCards=20;
        }
        else if(button_clicked.equals("40 Cards")){
            numOfCards=40;
        }
        else if(button_clicked.equals("100 Cards")){
            numOfCards=100;
        }
        else{
            //error message
        }

        while (counter<numOfCards){
            if(counter<0){/*error message*/}
            else {
                cardType = "Word";  //dummy variable
                //randomly determine card type

                if (cardType.equals("Picture")) {
                    setContentView(R.layout.flashcard_picture);
                    cardValue = /*get image name*/"";
                } else if (cardType.equals("Word")) {
                    setContentView(R.layout.flashcard_word);
                    cardValue = //((TextView) view).getText().toString();
                                "test";  //dummy variable
                } else {
                    //error message
                }

                if (button_clicked.equals("Submit")) {

                    translationInput = ((EditText) view).getText().toString();

                    if (translationInput.equals(cardValue)) {
                        score++;
                    }
                }
                counter++;
            }
        }
        if(counter>=numOfCards){
        //End Screen/Menu
        }
    }
}
