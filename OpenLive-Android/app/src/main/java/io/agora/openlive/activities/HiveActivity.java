package io.agora.openlive.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import github.nisrulz.screenshott.ScreenShott;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HiveActivity extends BaseActivity {

    ProgressDialog progressBar;

    private void sendServer(final File file) {
        Thread thread = new Thread() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar = new ProgressDialog(HiveActivity.this);
                        progressBar.setTitle("Sending image...");
                        progressBar.show();
                    }
                });

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("image", "", RequestBody.create(MEDIA_TYPE_PNG, file))
                        .build();
                Request request = new Request.Builder()
                        .url("https://api.thehive.ai/api/v2/task/sync")
                        .method("POST", body)
                        .addHeader("accept", "application/json")
                        .addHeader("authorization", "token <YOUR_API_TOKEN>")
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.dismiss();
                        Toast.makeText(HiveActivity.this,"Success",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
        thread.start();
    }

    private void takeScreenshot() {
        try {
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            //Use this code for FragmentView
            //View v1 = getActivity().getWindow().getDecorView().getRootView();

            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
// OR this
// RootView
// Bitmap bitmap = ScreenShott.getInstance().takeScreenShotOfRootView(v1);


            File imageFile = ScreenShott.getInstance().saveScreenshotToPicturesFolder(this, bitmap, "my_screenshot_filename");

            sendServer(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
}





