package com.example.pollin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv= findViewById(R.id.webview);
        wv.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                //hide loading image
                findViewById(R.id.pollin_logo).setVisibility(View.GONE);
                findViewById(R.id.motto_text).setVisibility(View.GONE);
                //show webview
                findViewById(R.id.webview).setVisibility(View.VISIBLE);
            }


        });
        wv.loadUrl("https://pollinn.herokuapp.com/home/recent");

        WebSettings web_settings= wv.getSettings();
        web_settings.setJavaScriptEnabled(true);
        web_settings.setSupportZoom(false);
        web_settings.setSavePassword(true);

        wv.setDownloadListener(new DownloadListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
            //base64 download


                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Pollin-Result");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading Result", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public void onBackPressed() {
        if(wv.canGoBack()){
            wv.goBack();
        }
        else {
            super.onBackPressed();
        }
        }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);


        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Pollin-Result-"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}



