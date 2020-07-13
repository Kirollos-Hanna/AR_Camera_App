package com.example.fd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoActivity extends AppCompatActivity {

    private Button cancelBtn;
    private Button saveBtn;
    private Button analyzeBtn;
    private ProgressBar analyzingPB;
    private ImageView imageView;
    private TextView notificationTextView;

    private RetrofitClient retrofitClient;
    // TODO change this domain later once you've finished your backend and bought an actual server
    private String url ="http://192.168.1.9:5000"; // MY DOMAIN WHICH IS CURRENTLY LOCALHOST
    private Bitmap bitmap;

    ArrayList<String> furnituretoSave = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        retrofitClient = retrofit.create(RetrofitClient.class);

        notificationTextView = (TextView)findViewById(R.id.notification_text);
        analyzingPB = (ProgressBar)findViewById(R.id.analyzing_progress_bar);
        notificationTextView.setVisibility(View.INVISIBLE);
        analyzingPB.setVisibility(View.INVISIBLE);


        cancelBtn = (Button)findViewById(R.id.cancel_btn);
        saveBtn = (Button)findViewById(R.id.save_btn);
        analyzeBtn = (Button)findViewById(R.id.analyze_btn);
        imageView = (ImageView)findViewById(R.id.image_taken);

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("IMAGENAME");
        Log.v("fileName", fileName);

        Glide.with(PhotoActivity.this)
                .load(fileName)
                .into(imageView);

        cancelBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                File file = new File(fileName);
                file.delete();
                finish();
            }
        });

        saveBtn.setOnClickListener(view -> finish());

        analyzeBtn.setOnClickListener(view -> {
            try {
                analyzeFurniture(fileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void analyzeFurniture(String fileName) throws JSONException {
        SetInitialAnalyzationView();
        SendImagetoBackend(fileName);
    }

    private void SetInitialAnalyzationView(){
        LinearLayout buttonLayout = (LinearLayout)findViewById(R.id.buttons_layout);
        ConstraintLayout photoLayout = (ConstraintLayout)findViewById(R.id.photo_view);
        notificationTextView.setVisibility(View.VISIBLE);
        analyzingPB.setVisibility(View.VISIBLE);
        photoLayout.removeView(imageView);
        buttonLayout.removeView(analyzeBtn);
        saveBtn.setOnClickListener(view -> {
            for (int i = 0; i < furnituretoSave.size(); i++) {
                saveBtn.setEnabled(false);
                String filename = generateFilenameForFurniture(String.valueOf(i));
                Glide.with(PhotoActivity.this)
                        .asBitmap()
                        .load(furnituretoSave.get(i))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Log.v("Resource is available" , "here");
                                // Save bitmap to furniture pictures folder
                                try {
                                    saveBitmapToDisk(resource, filename);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // Display toast to show it has successfully saved the image
                                Toast toast = Toast.makeText(PhotoActivity.this, "Image has been saved successfully!",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                Log.v("what is this?", "asdf");
                            }
                        });
            }
//            startActivity(new Intent(PhotoActivity.this, CameraActivity.class));
        });

    }

    private void SendImagetoBackend(String fileName) throws JSONException {
        final String imageString = convertImagetoBase64(fileName);

        PojoImage paramObject = new PojoImage(imageString);
        retrofitClient.uploadPicture(paramObject).enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.v("that worked!", response.body());
                try {
                    notificationTextView.setText(R.string.analyzing_image);
                    returnRecognizedFurniture(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v("Something went wrong", t.getMessage());
            }
        });
    }

    private void returnRecognizedFurniture(String PictureName) throws JSONException {
        LinearLayout layoutInScrollView1 = findViewById(R.id.scroller_view1);
        LinearLayout layoutInScrollView2 = findViewById(R.id.scroller_view2);

        retrofitClient.getImages(PictureName).enqueue(new Callback<ArrayList>(){
            @Override
            public void onResponse(Call<ArrayList> call, retrofit2.Response<ArrayList> response) {
                Log.v("that actually worked!", response.body().toString());
                analyzingPB.setVisibility(View.INVISIBLE);
                if(response.body().toString() == "[]"){
                    notificationTextView.setText(R.string.no_images);
                } else {
                    notificationTextView.setVisibility(View.INVISIBLE);
                }

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response.body().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] strArr = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        strArr[i] = jsonArray.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for(int i = 0; i < strArr.length; i++) {
                    // Display the first 500 characters of the response string.
                    String imageUrl = url + "/" + strArr[i];
                    Log.v("Array of string array", imageUrl);

                    RelativeLayout relativeLayout = new RelativeLayout(PhotoActivity.this);

                    ImageView tickMark = createTickMark();
                    relativeLayout.addView(tickMark);

                    tickMark.setVisibility(View.INVISIBLE);

                    ImageView imgV = createImageView(imageUrl);
                    imgV.setOnClickListener(view -> {
                        if(tickMark.getVisibility() == View.INVISIBLE){
                            tickMark.setVisibility(View.VISIBLE);
                            furnituretoSave.add(imageUrl);
                            Log.v("added", furnituretoSave.toString());
                        } else {
                            tickMark.setVisibility(View.INVISIBLE);
                            furnituretoSave.remove(imageUrl);
                            Log.v("removed", furnituretoSave.toString());
                        }
                    });
                    relativeLayout.addView(imgV);


                    int width = getResources().getDisplayMetrics().widthPixels/2;
                    int height = getResources().getDisplayMetrics().heightPixels/2;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                    params.setMargins(5, 5, 5, 5);
                    relativeLayout.setLayoutParams(params);

                    if(i % 2 == 0){
                        layoutInScrollView1.addView(relativeLayout);
                    } else{
                        layoutInScrollView2.addView(relativeLayout);
                    }
                }

            }

            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {
                Log.v("Something went wrong again2", t.getMessage());
            }
        });
    }

    private String convertImagetoBase64(String fileName){
        //getting image from gallery
        Uri uri = Uri.fromFile(new File(fileName));
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //converting image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void loadImageIntoImageView(String imageUrl, ImageView imageView){
        Glide.with(PhotoActivity.this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into(imageView);
    }

    private ImageView createImageView(String imageUrl){
        ImageView imgV = new ImageView(PhotoActivity.this);
        loadImageIntoImageView(imageUrl, imgV);
        RelativeLayout.LayoutParams imageParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        imgV.setLayoutParams(imageParams);
        return imgV;
    }

    private ImageView createTickMark(){
        ImageView tickMark = new ImageView(PhotoActivity.this);
        tickMark.setImageDrawable(ContextCompat.getDrawable(PhotoActivity.this, R.drawable.checkmark));
        RelativeLayout.LayoutParams tickMarkParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
        tickMarkParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tickMarkParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        tickMark.setLayoutParams(tickMarkParams);
        return tickMark;
    }

    private String generateFilenameForFurniture(String number) {
        Context context = getBaseContext();
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Furniture/" + date + "_screenshot" + number + ".jpg";
//        Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES) + File.separator + "FurniturePictures/" + date + "_screenshot.jpg";
    }

    private String generateFilename() {
        Context context = getBaseContext();
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Furniture/" + date + "_screenshot.jpg";
//        Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES) + File.separator + "FurniturePictures/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }
}



//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);