package com.example.ondrejvane.zivnostnicek.utilities;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PictureUtility {

    private static final String IMAGE_DIR = "images";
    private static final String IMAGE_FORMAT = ".jpg";
    private static final String TAG = "PictureUtility";

    private PictureUtility() {
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


    public static String saveToInternalStorage(Bitmap bitmapImage, Context context) {

        //vygenerováno názvu pro pořízenou fotku
        String pictureName = createPictureName();

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, pictureName + IMAGE_FORMAT);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath()+"/"+pictureName+IMAGE_FORMAT;
    }

    public static Bitmap loadImageFromStorage(String path) {
        //parsování názvu souboru
        String pictureName = getPictureNameFromPath(path);

        //parsvoání cesty k souboru
        String picturePath = getPicturePathFromAbsolutPath(path);


        Log.d(TAG, "Name: " + pictureName);
        Log.d(TAG, "Path: " + picturePath);

        Bitmap b = null;
        try {
            File f = new File(picturePath, pictureName);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static boolean tryLoadImageFromStorage(String path){
        //parsování názvu souboru
        String pictureName = getPictureNameFromPath(path);

        //parsvoání cesty k souboru
        String picturePath = getPicturePathFromAbsolutPath(path);


        Log.d(TAG, "Name: " + pictureName);
        Log.d(TAG, "Path: " + picturePath);

        Bitmap b = null;
        try {
            File f = new File(picturePath, pictureName);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return b != null;
    }


    private static String createPictureName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    private static String getPictureNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1).trim();
    }

    private static String getPicturePathFromAbsolutPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }


}