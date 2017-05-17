package group22.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import group22.myapplication.R;

public class CreateMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout for activity
        setContentView(R.layout.card_creator);
    }

    public void setType(View view) {
        String button_clicked;
        button_clicked = ((Button) view).getText().toString();

        if (button_clicked.equals("Picture Card")) {

            Intent intent = new Intent(this, CreatePicActivity.class);
            startActivity(intent);
        } else if (button_clicked.equals("Word Card")) {

            Intent intent = new Intent(this, CreateWordCard.class);
            startActivity(intent);
        }
    }
}


