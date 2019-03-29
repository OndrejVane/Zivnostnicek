package com.example.ondrejvane.zivnostnicek.activities.bill;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.ApplicationClass;
import com.ortiz.touchview.TouchImageView;


/**
 * Aktivita, která zobrazuje obrázek na celou plochu obrazovky
 */
public class ShowPictureActivity extends AppCompatActivity {


    private TouchImageView touchImageView;

    /**
     * Metoda, která je volána při spuštění aktivity a nastaví
     * všechny potřebné prvky aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //skryje horní panel
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_show_picture);

        touchImageView = findViewById(R.id.touchImageView);

        if (getIntent().hasExtra("BITMAP_PATH")) {
            setBitmap(getIntent().getStringExtra("BITMAP_PATH"));
        } else {
            //pouze pro účely unit testů
            setBitmap("content://media/external/images/media/5694");
        }

    }

    /**
     * Metoda, která nastaví obrázek do image view
     *
     * @param pickedImage cesta vybraného obrázku
     */
    private void setBitmap(String pickedImage) {
        double percentageSize = 0.9;
        int width = (int) (ApplicationClass.screenWidth * percentageSize);
        int height = (int) (ApplicationClass.screenHeight * percentageSize);
        Glide.with(this).load(pickedImage).apply(new RequestOptions().override(width, height)).into(touchImageView);
    }

    /**
     * Metoda, která se zavole před ukončením aktivity.
     * Vynulování image view kvuli apměti a spuštění GC.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        touchImageView.setImageBitmap(null);
        System.gc();
    }
}
