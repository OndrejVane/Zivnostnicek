package com.example.ondrejvane.zivnostnicek.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.example.ondrejvane.zivnostnicek.R;

public class ShowPictureActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);


        //zákaz orientace na šířku
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);


        imageView = findViewById(R.id.showImageView);
        Bitmap bitmap = getIntent().getParcelableExtra("BILL");
        imageView.setImageBitmap(bitmap);
    }
}
