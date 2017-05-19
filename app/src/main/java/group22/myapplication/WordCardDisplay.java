package group22.myapplication;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WordCardDisplay extends Activity{

    public SQLiteDatabase myDB;
    private TextView textView9, tvCardTaken;
    private EditText EditTextView;
    private Button EditBtn, DeleteBtn, SubmitBtn, btnPictureCard;
    ImageView ivImage;
    LingodecksDBHelper DBHelper;
    public static SharedPreferences sp;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY_IMG = 1;

    String translatedWord = "";
    String languageSet = "";
    public static final String LOG_TAG = "WordCardDisplay";
    Context toast_context = this;
    CharSequence deleted_text = "Card was successfully deleted";
    CharSequence edit_text = "Card was successfully edited";
    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        languageSet = langPref.getString("language", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card_display);

        DBHelper = new LingodecksDBHelper(this,LingodecksDBHelper.DB_NAME,null,LingodecksDBHelper.DB_VERSION);
        myDB = DBHelper.getWritableDatabase();

        Intent intent = getIntent();

        String message = intent.getStringExtra("Card");
        String[] details = message.split(" - ");

        final String CardID = details[0];
        final String Translation = details[1];
        final String English = details[2];
        //final String Image = details[3];

        textView9 = (TextView)findViewById(R.id.textView9);
        textView9.setText(Translation);
        tvCardTaken = (TextView)findViewById(R.id.cardTaken);
        tvCardTaken.setText(English);

        //delete card
        DeleteBtn = (Button) findViewById(R.id.deletecard_button);
        final Toast deleteToast = Toast.makeText(toast_context, deleted_text, duration);
        final String DeleteQuery;
        if(languageSet == "en-de") {
            DeleteQuery = "DELETE FROM " + Contract.Lingodecks_Tables.TABLE_GERMAN + " WHERE "
                    + Contract.Lingodecks_Tables._ID + " = " + CardID;
            Log.v("GermanQuery", "Here");

        }
        else if(languageSet == "en-es"){
            DeleteQuery = "DELETE FROM " + Contract.Lingodecks_Tables.TABLE_SPANISH + " WHERE "
                    + Contract.Lingodecks_Tables._ID + " = " + CardID;
            Log.v("SpanishQuery", "Here");
        }
        Log.i(LOG_TAG, DeleteQuery);
        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDB.execSQL(DeleteQuery);
                deleteToast.show();
                Intent intent = new Intent(WordCardDisplay.this, CardList.class);
                startActivity(intent);
            }
        });

        EditBtn = (Button)findViewById(R.id.editcard_btn) ;
        EditTextView = (EditText)findViewById(R.id.EditWordtv);
        SubmitBtn = (Button)findViewById(R.id.returnBtn);
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView9.setVisibility(View.GONE);
                EditTextView.setVisibility(View.VISIBLE);
                EditTextView.setText(Translation);
                SubmitBtn.setText("Submit");

            }
        });

        SubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Getting text value of button pressed
                SubmitBtn = (Button)findViewById(R.id.returnBtn);
                String button_clicked = SubmitBtn.getText().toString();

                if (button_clicked.equals("Submit")) {
                    String user_input = EditTextView.getText().toString();

                    if(TextUtils.isEmpty(user_input)) {
                        //default value
                        textView9.setText("Please enter a word");
                        Log.v("hi", user_input);
                    } else {
                        //Translates the word the user entered
                        translateParams params = new translateParams(user_input, languageSet);
                        FetchTranslation myTask = new FetchTranslation();
                        myTask.execute(params);
                    }

                } else if (button_clicked.equals("Return to index")) {

                    Intent intent = new Intent(WordCardDisplay.this, CardList.class);
                    startActivity(intent);
                }
            }
        });

        //dialog for choosing image
        btnPictureCard = (Button) findViewById(R.id.editpic_btn);
        ivImage = (ImageView) findViewById(R.id.imageView);
        btnPictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating an AlertDialog with 3 items from an array
                final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(WordCardDisplay.this);

                //Setting a title for the Dialog box
                builder.setTitle("Add Photo!");

                //Choosing the function depending on which button the user clicked
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            dispatchTakePictureIntent();

                        } else if (items[item].equals("Choose from Gallery")) {
                            galleryIntent();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });

                builder.show();
            }
        });
    }

    //gallery photo
    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMG);
    }

    //photo from camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //shows img in imageview
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivImage.setImageBitmap(photo);
        }
        //gallery
        else if (requestCode == REQUEST_GALLERY_IMG && resultCode == Activity.RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static class translateParams {
        String userWord;
        String languageSet;

        translateParams(String userWord, String languageSet) {
            this.userWord = userWord;
            this.languageSet = languageSet;
            Log.v("Params", userWord);
            Log.v("Params", languageSet);
        }
    }

    class FetchTranslation extends AsyncTask<translateParams, String, String> {
        String apiKey = "trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873";
        String result = "";

        @Override
        protected String doInBackground(translateParams... params) {
            final String translationResult, jsonResult;
            final String userWord = params[0].userWord;
            String languageDirection = params[0].languageSet;

            // Example API call
            // https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873&text=cat&lang=en-it

            //creating URI
            final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
            final String API_KEY = "key";
            final String LANGUAGE = "lang";
            final String TEXT = "text";

            Uri uriBuilder = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, apiKey)
                    .appendQueryParameter(TEXT, userWord)
                    .appendQueryParameter(LANGUAGE, languageDirection)
                    .build();

            //check connectivity
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){
                result = GET(uriBuilder.toString());

                jsonResult = getTranslationFromJson(result);
                //removes json format
                translationResult = jsonResult.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String compareTxt = "[\"" + userWord + "\"]";
                        if(translationResult.equals(compareTxt)) {
                            textView9.setText("Unable to translate word. Please try another word.");
                            Log.v("Translation", "Not Found");
                        }
                        else {
                            //to get translated word from asynctask to insertdb
                            SharedPreferences.Editor editor = getSharedPreferences("TRANSLATION", MODE_PRIVATE).edit();
                            editor.putString("translated_word", translationResult);
                            editor.commit();
                            Log.v("AsyncTranslate", translationResult);
                            SharedPreferences langPref = getSharedPreferences("TRANSLATION", MODE_PRIVATE);
                            String res = langPref.getString("translated_word", "");
                            Log.v("TEST", res);
                        }
                    }
                });
            }
            else{
                Log.v("NETWORK", "No network connection");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            updateDB();
            Log.v("inserted", "yes");
        }

    }
    // Take the raw JSON data to get the data we need
    private String getTranslationFromJson(String jsonStr) {
        String resultStr;

        try{
            // JSON objects that need to be extracted
            final String TEXT = "text";

            JSONObject textJSON = new JSONObject(jsonStr);
            JSONArray resultArray = textJSON.getJSONArray(TEXT);
            resultStr = resultArray.toString();

        }catch(JSONException e){
            return null;
        }
        return resultStr;
    }

    private String GET(String url) {

        InputStream is;
        String result = "";
        URL request = null;

        try{
            request = new URL(url);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection) request.openConnection();
            conn.connect();

            is = conn.getInputStream();
            if(is != null){
                result = convertInputStreamToString(is);
            }else{
                result = "Did not work!";
            }

        }catch(IOException e){
            e.printStackTrace();

        }finally{
            conn.disconnect();
        }
        return result;
    }

    private String convertInputStreamToString(InputStream is) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }
        is.close();
        return result;
    }

    private void updateDB() {
        ContentValues values;
        String user_input = EditTextView.getText().toString();

        //gets translated word from asynctask
        sp = getSharedPreferences("TRANSLATION", MODE_PRIVATE);
        translatedWord = sp.getString("translated_word", translatedWord);

        if (languageSet == "en-de") {
            Cursor c = getContentResolver().query(Contract.BASE_CONTENT_URI1, null, Contract.Lingodecks_Tables.COLUMN_GER_ENG + " = " + DatabaseUtils.sqlEscapeString(user_input), null, null);
            if (c.getCount() == 0) {
                Intent intent = getIntent();

                String message = intent.getStringExtra("Card");
                String[] details = message.split(" - ");
                final String CardID = details[0];
                long ID = Long.parseLong(CardID);

                Contract.Lingodecks_Tables ContentURI = new Contract.Lingodecks_Tables();
                Uri CONTENT_URI1 = ContentURI.CONTENT_URI1;
                final Uri uri = ContentUris.withAppendedId(CONTENT_URI1, ID);

                values = new ContentValues();
                values.put(Contract.Lingodecks_Tables.COLUMN_GER_ENG, user_input);
                values.put(Contract.Lingodecks_Tables.COLUMN_GER, translatedWord);

                //Content resolver to be passed to LDContentProvider
                getContentResolver().update(uri, values, CardID, null);
                Log.v("Exists", "No");
            } else {
                tvCardTaken.setText("Choose different word! Already Taken");
                Log.v("Exists", "Yes");
            }
        }
        else if (languageSet == "en-es") {
            Cursor c = getContentResolver().query(Contract.BASE_CONTENT_URI2, null, Contract.Lingodecks_Tables.COLUMN_ESP_ENG + " = " + DatabaseUtils.sqlEscapeString(user_input), null, null);
            if (c.getCount() == 0) {
                //on the receiving side
                //get the intent that started this activity
                Intent intent = getIntent();

                String message = intent.getStringExtra("Card");
                String[] details = message.split(" - ");

                final String CardID = details[0];
                long ID = Long.parseLong(CardID);

                Contract.Lingodecks_Tables ContentURI = new Contract.Lingodecks_Tables();
                Uri CONTENT_URI2 = ContentURI.CONTENT_URI2;
                final Uri uri = ContentUris.withAppendedId(CONTENT_URI2, ID);

                //Defining content values to update database
                values = new ContentValues();
                values.put(Contract.Lingodecks_Tables.COLUMN_ESP_ENG, user_input);
                values.put(Contract.Lingodecks_Tables.COLUMN_ESP, translatedWord);

                getContentResolver().update(uri, values, CardID, null);
                Log.v("Exists", "No");

                final Toast editToast = Toast.makeText(toast_context, edit_text, duration);
                editToast.show();
                intent = new Intent(this, CardList.class);
                startActivity(intent);
            } else {
                tvCardTaken.setText("Choose different word! Already Taken");
                Log.v("Exists", "Yes");
            }
        }
    }

}
