package group22.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CreateMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout for activity
        setContentView(R.layout.card_creator);

        // this can be added wherever the api is needed.
        SharedPreferences langPref = getSharedPreferences("setLanguage", MODE_PRIVATE);
        String languageSet = langPref.getString("language", "");
        Log.v("language chosen", languageSet);
    }

    public void setType(View view) {
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();

        if (button_clicked.equals("Picture Card")) {

            Intent intent = new Intent(this, CreatePicActivity.class);
            startActivity(intent);
        } else if (button_clicked.equals("Word Card")) {

            Intent intent = new Intent(this, CreateWordActivity.class);
            startActivity(intent);
        }

    }



    /*
    private void onCaptureImageResult(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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

        ivImage.setImageBitmap(imageBitmap);
    }

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
}

