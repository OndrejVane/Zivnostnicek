package com.example.ondrejvane.zivnostnicek.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class DbBitmapUtility {

    /**
     * Převede obrázek ve formě bitmapy do pole
     * bytu pro uložení do databáze.
     * @param bitmap
     * @return
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] output = stream.toByteArray();
        return output;
    }

    /**
     * Převede obrázek z pole bytů do bitmapy pro získání
     * obrázku z databáze.
     * @param image
     * @return
     */
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


}
