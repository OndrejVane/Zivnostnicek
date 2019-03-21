package com.example.ondrejvane.zivnostnicek.utilities;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PictureUtility {

    private static final String IMAGE_DIR = "images";
    private static final String IMAGE_FORMAT = ".bmp";
    private static final String TAG = "PictureUtility";

    private PictureUtility() {
    }


    public static String saveToInternalStorage(Bitmap bitmapImage, Context context) {


        //vygenerováno názvu pro pořízenou fotku
        String pictureName = createPictureName();

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, pictureName + IMAGE_FORMAT);
        String imagePath = directory.getAbsolutePath()+"/"+pictureName+IMAGE_FORMAT;

        FileOutputStream fos = null;
        try {
            //fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            //bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            //new AndroidBmpUtil().save(bmImage, file);

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

    public static Bitmap getBitmap(String path) {

        InputStream in;
        try {
            final int IMAGE_MAX_SIZE = 30000; // 1.2MP
            File file = new File(path);
            in = new FileInputStream(file);

            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();


            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + options.outWidth + ",orig-height: " + options.outHeight);

            Bitmap resultBitmap = null;
            in = new FileInputStream(file);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG, "bitmap size - width: " + resultBitmap.getWidth() + ", height: " +
                    resultBitmap.getHeight());
            return resultBitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static String getAbsolutePathFromUri(Uri imageUri, ContentResolver contentResolver){
        Cursor cursor = contentResolver.query(imageUri, new String[]{
                        MediaStore.Images.ImageColumns.DATA},
                null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String imageFilePath = cursor.getString(0);
        cursor.close();
        return imageFilePath;
    }


    private static String createPictureName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

}