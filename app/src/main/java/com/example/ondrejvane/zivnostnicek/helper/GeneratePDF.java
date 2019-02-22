package com.example.ondrejvane.zivnostnicek.helper;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class GeneratePDF {

    private static final String TAG = "GeneratePDF";

    private Activity activity;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;

    public GeneratePDF(Activity activity){
        this.activity = activity;
    }

    public void generatePDF(){
        createPdfWrapper();
    }

    private void createPdfWrapper(){

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }

                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }else {
            createPdf();
        }
    }

    /*
    private void createPdfWrapper(){
        Log.d(TAG, "PDF WRAPPER");

        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {


            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CONTACTS)) {
                showMessageOKCancel("You need to allow access to Storage",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            }
                        });
                return;
            }

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }else {
            createPdf();
        }
    }
    */

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf(){

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            if(!docsFolder.mkdir()){
                Toast.makeText(activity,"Can not create a directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        try {
            pdfFile = new File(docsFolder.getAbsolutePath(),"HelloWorld.pdf");
            OutputStream output = new FileOutputStream(pdfFile);
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            String someText = "aasdsadasdsasadsadsdasda";
            document.add(new Paragraph(someText));
            PdfPTable table = new PdfPTable(8);
            for(int aw = 0; aw < 16; aw++){
                table.addCell("hi");
            }
            document.add(table);
            Image gray = Image.getInstance(1, 1, 1, 8, new byte[] { (byte)0x80 });
            gray.scaleAbsolute(30, 30);
            document.add(gray);
            document.close();
            previewPdf();
        }catch (FileNotFoundException e){

        }catch (DocumentException e){

        }
    }

    private void previewPdf() {

        PackageManager packageManager = activity.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            activity.startActivity(intent);
        }else{
            Toast.makeText(activity,"Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
