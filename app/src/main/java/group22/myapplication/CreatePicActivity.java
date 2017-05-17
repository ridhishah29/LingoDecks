package group22.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CreatePicActivity extends Activity {

    private Button btnPictureCard;
    private ImageView ivImage;
    private Bitmap image;
    String languageSet = "";
    String translatedWord = "";
    TextView textView;
    EditText editText;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        languageSet = langPref.getString("language", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_card);

        //Setting the button for the Select Photo Dialog
        btnPictureCard = (Button) findViewById(R.id.choosepic_btn);
        ivImage = (ImageView) findViewById(R.id.imageView);
        btnPictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating an AlertDialog with 3 items from an array
                final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePicActivity.this);

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

        Button btnSubmit = (Button) findViewById(R.id.submitpic_btn);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editText = (EditText) findViewById(R.id.enterWord);
                String user_input = editText.getText().toString();

                if (TextUtils.isEmpty(user_input)) {
                    textView = (TextView) findViewById(R.id.errorMsg);
                    textView.setText("Please enter a word");
                } else {
                    translateParams params = new translateParams(user_input, languageSet);
                    FetchTranslation myTask = new FetchTranslation();
                    myTask.execute(params);
                    Log.v("Translated word", translatedWord);
                    insertDB();
                }
            }
        });
    }

    // convert from bitmap to byte array
    private byte[] getBytes() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        return imageInByte;
    }


    private void insertDB() {
        ContentValues values;
        editText = (EditText) findViewById(R.id.enterWord);
        String user_input = editText.getText().toString();

        if (languageSet == "en-de") {
            values = new ContentValues();
            values.put(Contract.Lingodecks_Tables.COLUMN_GER_ENG, user_input);
            values.put(Contract.Lingodecks_Tables.COLUMN_GER, translatedWord);
            values.put(Contract.Lingodecks_Tables.COLUMN_GER_PIC, getBytes());

            getContentResolver().insert(Contract.Lingodecks_Tables.CONTENT_URI1, values);

        } else if (languageSet == "en-es") {
            values = new ContentValues();
            values.put(Contract.Lingodecks_Tables.COLUMN_ESP_ENG, user_input);
            values.put(Contract.Lingodecks_Tables.COLUMN_ESP, translatedWord);
            values.put(Contract.Lingodecks_Tables.COLUMN_ESP_PIC, getBytes());

            getContentResolver().insert(Contract.Lingodecks_Tables.CONTENT_URI2, values);
        }
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivImage.setImageBitmap(photo);
        } else if (requestCode == REQUEST_GALLERY_IMG && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivImage.setImageBitmap(photo);
        }
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

        @Override
        protected String doInBackground(translateParams... params) {
            final String translationResult;
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

                translationResult = getTranslationFromJson(result);
                textView = (TextView) findViewById(R.id.errorMsg);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String compareTxt = "[\"" + userWord + "\"]";
                        if (translationResult.equals(compareTxt)) {
                            textView.setText("Unable to translate word. Please try another word.");
                        } else {
                            translatedWord = translationResult;
                        }
                    }
                });
            } else {
                Log.v("NETWORK", "No network connection");
            }

            return null;
        }

    }

    // Take the raw JSON data to get the data we need?
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

    /*
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
    }*/

