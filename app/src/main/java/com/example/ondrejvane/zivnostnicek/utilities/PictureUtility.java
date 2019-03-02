package com.example.ondrejvane.zivnostnicek.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class PictureUtility {

    private PictureUtility(){
    }

    public static boolean tryReadPicture(Uri pickedImage, Context context) {
        boolean returnValue;
        ContentResolver cr = context.getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cur = cr.query(pickedImage, projection, null, null, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                String filePath = cur.getString(0);

                if (new File(filePath).exists()) {
                    returnValue = true;
                } else {
                    returnValue = false;
                }
            } else {
                returnValue = false;
            }
        } else {
            returnValue = false;
        }
        cur.close();
        return returnValue;
    }

    public static void setBitmap(Uri pickedImage, Context context, ImageView imageView) {
        Glide.with(context)
                .load(pickedImage)
                .into(imageView);


    }

    /**
     * Metoda, která převede obrázek z Uri do
     * bitmapy
     *
     * @param pickedImage Uri vybraného obrázku
     * @return Bitmap bitmapa odpovídajícího Uri
     */
    public static Bitmap getBitmapFromUri(Uri pickedImage, Context context) {
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        cursor.close();
        return bitmap;
    }


}