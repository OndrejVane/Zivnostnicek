package com.example.ondrejvane.zivnostnicek.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.example.ondrejvane.zivnostnicek.R;
import com.ortiz.touchview.TouchImageView;

import java.io.IOException;

public class ShowPictureActivity extends AppCompatActivity {


    private TouchImageView touchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //skryje horní panel
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_show_picture);
        /*
        touchImageView = findViewById(R.id.touchImageView);
        Bitmap bitmap = getBitmapFromUri(Uri.parse(getIntent().getStringExtra("BITMAP_URI")));
        touchImageView.setImageBitmap(bitmap);
        */
    }

    private Bitmap getBitmapFromUri(Uri pickedImage) {
        //TODO zjistit proč naroste halda při načítání obrázku!!!!!!!
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedImage);
        } catch (IOException e) {
            bitmap = null;
        }
        return bitmap;
    }
}
