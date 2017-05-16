package group22.myapplication;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WordCardDisplay extends Activity{

    private TextView textView9, textView10;
    private EditText EditTextView;
    private Button EditBtn;
    private static final int GERMAN_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card_display);

        //on the receiving side
        //get the intent that started this activity
        Intent intent = getIntent();

        //grab the data
        String message = intent.getStringExtra("Card");

        //split data into arrays
        String[] details = message.split(" - ");

        //declare variables for array
        final String CardID = details[0];
        final String Translation = details[1];
        final String English = details[2];

        //display the data
        textView9 = (TextView)findViewById(R.id.textView9);
        textView9.setText(Translation);
        textView10 = (TextView)findViewById(R.id.textView10);
        textView10.setText(English);

        //
        EditBtn = (Button)findViewById(R.id.editcard_btn) ;
        EditTextView = (EditText)findViewById(R.id.EditWordtv);
        final Button SubmitBtn = (Button)findViewById(R.id.returnBtn);
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView9.setVisibility(View.GONE);
                EditTextView.setVisibility(View.VISIBLE);
                EditTextView.setText(Translation);
                SubmitBtn.setText("Submit");
            }
        });
    }

    public void setType(View view) {
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();

        if (button_clicked.equals("Submit")) {
            //EditCard();

        } else if (button_clicked.equals("Return to Index")) {

            Intent intent = new Intent(this, CardList.class);
            startActivity(intent);
        }

    }

}
