package group22.myapplication;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static group22.myapplication.Contract.Lingodecks_Tables.COLUMN_GER_PIC;

public class CardDisplay extends Activity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChosenTask;
    ImageView imageView2, ivImage;
    SimpleCursorAdapter adapter;
    private static final int GERMAN_LOADER = 1;


    String translatedWord = "";
    String languageSet = "";
    public SQLiteDatabase myDB;
    private TextView textView7;
    private EditText EditTextView;
    private Button EditBtn, DeleteBtn, PictureBtn, SubmitBtn;
    LingodecksDBHelper DBHelper;

    public static final String LOG_TAG = "WordCardDisplay";
    Context toast_context = this;
    CharSequence deleted_text = "Card was successfully deleted";
    CharSequence edit_text = "Card was successfully edited";
    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_index);

        getLoaderManager().initLoader(GERMAN_LOADER, null, this);

        textView7 = (TextView) findViewById(R.id.textView7);
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        languageSet = langPref.getString("language", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card_display);

        DBHelper = new LingodecksDBHelper(this,LingodecksDBHelper.DB_NAME,null,LingodecksDBHelper.DB_VERSION);
        myDB = DBHelper.getWritableDatabase();

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
        textView7.setText(English);

        //delete the card
        DeleteBtn = (Button) findViewById(R.id.deletecard_button);
        final Toast deleteToast = Toast.makeText(toast_context, deleted_text, duration);
        final String DeleteQuery = "DELETE FROM " + Contract.Lingodecks_Tables.TABLE_GERMAN + " WHERE "
                + Contract.Lingodecks_Tables._ID + " = " + CardID;
        Log.i(LOG_TAG, DeleteQuery);
        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDB.execSQL(DeleteQuery);
                deleteToast.show();
                Intent intent = new Intent(CardDisplay.this, CardList.class);
                startActivity(intent);
            }
        });

        EditBtn = (Button)findViewById(R.id.editcard_btn) ;
        EditTextView = (EditText)findViewById(R.id.EditWordtv);
        SubmitBtn = (Button)findViewById(R.id.returnBtn);
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView7.setVisibility(View.GONE);
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
                        textView7.setText("Please enter a word");
                        Log.v("hi", user_input);
                    } else {
                        //Translates the word the user entered
                        translateParams params = new translateParams(user_input, languageSet);
                        FetchTranslation myTask = new FetchTranslation();
                        myTask.execute(params);

                        //Calls function to update the Database
                        updateDB();

                    }

                } else if (button_clicked.equals("Return to index")) {

                    Intent intent = new Intent(CardDisplay.this, CardList.class);
                    startActivity(intent);
                }
            }
        });

        ivImage = (ImageView) findViewById(R.id.imageView2);
        PictureBtn = (Button)findViewById(R.id.choosepic_btn);
        PictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void selectImage() {

        //Creating an AlertDialog with 3 items from an array
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CardDisplay.this);

        //Setting a title for the Dialog box
        builder.setTitle("Add Photo!");

        //Choosing the function depending on which button the user clicked
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChosenTask = "Take Photo";
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChosenTask = "Choose from Library";
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == 1) {
            String[] columns = {
                    Contract.Lingodecks_Tables._ID,
                    Contract.Lingodecks_Tables.COLUMN_GER,
                    Contract.Lingodecks_Tables.COLUMN_GER_ENG,
                    Contract.Lingodecks_Tables.COLUMN_GER_PIC
            };
            return new CursorLoader(this, Contract.Lingodecks_Tables.CONTENT_URI1, columns, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder stringBuilderQueryResult = new StringBuilder("");
            while (cursor.moveToNext()) {
                stringBuilderQueryResult.append(cursor.getString(1));
            }
            textView7.setText(stringBuilderQueryResult.toString());
        }
        imageView2.setImageURI(Contract.Lingodecks_Tables.CONTENT_URI1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        Log.v("reset", "reset");
    }

    private byte[] getBytes() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        return imageInByte;
    }

    private void updateDB() {
        ContentValues values;
        String user_input = EditTextView.getText().toString();

        if (languageSet == "en-de") {
            Cursor c = getContentResolver().query(Contract.BASE_CONTENT_URI1, null, Contract.Lingodecks_Tables.COLUMN_GER_ENG + " = " + DatabaseUtils.sqlEscapeString(user_input), null, null);
            if (c.getCount() == 0) {
            //on the receiving side
            //get the intent that started this activity
            Intent intent = getIntent();

            //grab the data from CardList
            String message = intent.getStringExtra("Card");

            //split data into arrays
            String[] details = message.split(" - ");

            //declare variables for array
            final String CardID = details[0];

            //Convert from string to long
            long ID = Long.parseLong(CardID);

            //accessing Content_Uri
            Contract.Lingodecks_Tables ContentURI = new Contract.Lingodecks_Tables();
            Uri CONTENT_URI1 = ContentURI.CONTENT_URI1;

            //declaring a uri
            final Uri uri = ContentUris.withAppendedId(CONTENT_URI1, ID);

            //Defining content values to update database
            values = new ContentValues();
            values.put(Contract.Lingodecks_Tables.COLUMN_GER_ENG, user_input);
            values.put(Contract.Lingodecks_Tables.COLUMN_GER, translatedWord);
            values.put(Contract.Lingodecks_Tables.COLUMN_GER_PIC, getBytes());

            //Content resolver to be passed to LDContentProvider
            getContentResolver().update(uri, values, CardID, null);
                Log.v("Exists", "No");
            } else {
                textView7.setText("Choose different word! Already Taken");
                Log.v("Exists", "Yes");
            }
        }else if (languageSet == "en-es") {
            Cursor c = getContentResolver().query(Contract.BASE_CONTENT_URI2, null, Contract.Lingodecks_Tables.COLUMN_ESP_ENG + " = " + DatabaseUtils.sqlEscapeString(user_input), null, null);
            if (c.getCount() == 0) {
            //on the receiving side
            //get the intent that started this activity
            Intent intent = getIntent();

            //grab the data from CardList
            String message = intent.getStringExtra("Card");

            //split data into arrays
            String[] details = message.split(" - ");

            //declare variables for array
            final String CardID = details[0];

            //Convert from string to long
            long ID = Long.parseLong(CardID);

            //accessing Content_Uri
            Contract.Lingodecks_Tables ContentURI = new Contract.Lingodecks_Tables();
            Uri CONTENT_URI2 = ContentURI.CONTENT_URI2;

            //declaring a uri
            final Uri uri = ContentUris.withAppendedId(CONTENT_URI2, ID);

            //Defining content values to update database
            values = new ContentValues();
            values.put(Contract.Lingodecks_Tables.COLUMN_ESP_ENG, user_input);
            values.put(Contract.Lingodecks_Tables.COLUMN_ESP, translatedWord);
            values.put(Contract.Lingodecks_Tables.COLUMN_ESP_PIC, getBytes());

            //Content resolver to be passed to LDContentProvider
            getContentResolver().update(uri, values, CardID, null);
                Log.v("Exists", "No");
            } else {
                Log.v("Exists", "Yes");
                textView7.setText("Choose different word! Already Taken");
            }
        }

        final Toast editToast = Toast.makeText(toast_context, edit_text, duration);
        editToast.show();
        Intent intent = new Intent(this, CardList.class);
        startActivity(intent);
    }

    private static class translateParams {
        String userWord;
        String languageSet;

        translateParams(String userWord, String languageSet) {
            this.userWord = userWord;
            this.languageSet = languageSet;
        }
    }

    class FetchTranslation extends AsyncTask<translateParams, String, String> {
        String apiKey = "trnsl.1.1.20170322T223343Z.49a364d7daed7f83.b32aca1f9e1461aa3089ebc0f88570e69f0c9873";
        String result = "";
        String translationResult;

        @Override
        protected String doInBackground(translateParams... params) {
//            SharedPreferences preferences = getSharedPreferences("TRANSLATION", MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.remove("word");
//            editor.commit();

            final String jsonResult;
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
            if (networkInfo != null && networkInfo.isConnected()) {
                result = GET(uriBuilder.toString());

                jsonResult = getTranslationFromJson(result);
                //removes json format
                translationResult = jsonResult.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String compareTxt = userWord;
                        if (translationResult.equals(compareTxt)) {
                            textView7.setText("Unable to translate word. Please try another word.");
                            Log.v("Translation", "Not Found");
                        } else {
                            //to get translated word from asynctask to insertdb
                            SharedPreferences.Editor editor = getSharedPreferences("TRANSLATION", MODE_PRIVATE).edit();
                            editor.remove("translated_word");
                            editor.apply();
                            editor.putString("translated_word", translationResult);
                            editor.apply();
                            Log.v("AsyncTranslate", translationResult);

                        }
                    }
                });
            } else {
                Log.v("NETWORK", "No network connection");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String translationResult) {
            super.onPostExecute(result);
            translatedWord = translationResult; //assign it to global variable
            //Log.e("resres", translatedWord);
        }
    }

    // Take the raw JSON data to get the data we need
    private String getTranslationFromJson(String jsonStr) {
        String resultStr;

        try {
            // JSON objects that need to be extracted
            final String TEXT = "text";

            JSONObject textJSON = new JSONObject(jsonStr);
            JSONArray resultArray = textJSON.getJSONArray(TEXT);
            resultStr = resultArray.toString();

        } catch (JSONException e) {
            return null;
        }
        return resultStr;
    }

    private String GET(String url) {

        InputStream is;
        String result = "";
        URL request = null;

        try {
            request = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) request.openConnection();
            conn.connect();

            is = conn.getInputStream();
            if (is != null) {
                result = convertInputStreamToString(is);
            } else {
                result = "Did not work!";
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            conn.disconnect();
        }
        return result;
    }

    private String convertInputStreamToString(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        is.close();
        return result;
    }
}
