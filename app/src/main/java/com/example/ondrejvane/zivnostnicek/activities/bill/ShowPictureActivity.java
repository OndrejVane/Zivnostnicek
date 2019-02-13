package com.example.ondrejvane.zivnostnicek.activities.bill;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.ApplicationClass;
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

        touchImageView = findViewById(R.id.touchImageView);

        setBitmap(Uri.parse(getIntent().getStringExtra("BITMAP_URI")));

    }

    private void setBitmap(Uri pickedImage) {
        double percentageSize = 0.6;
        int width = (int)(ApplicationClass.screenWidth * percentageSize);
        int height = (int)(ApplicationClass.screenHeight * percentageSize);
        Glide.with(this).load(pickedImage).apply(new RequestOptions().override(width, height)).into(touchImageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        touchImageView.setImageBitmap(null);
        System.gc();
    }
}
