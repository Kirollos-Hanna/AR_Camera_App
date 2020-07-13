package com.example.fd;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class RoomsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // TODO write the logic to retrieve and send measurement info to and from the server using the action bar
    // TODO make your action bar appear only when you long press/hold on a picture
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            case R.id.nav_user:
////                startActivity(new Intent(RoomsActivity.this, UserActivity.class));
//                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


// Useless code, commented just in case

//    private static final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/";

//    private RetrofitClient retrofitClient;
//    String url ="http://192.168.1.6:5000"; // MY DOMAIN WHICH IS CURRENTLY LOCALHOST
//    Bitmap bitmap;

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(1, TimeUnit.MINUTES)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();
//
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(url)
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        retrofitClient = retrofit.create(RetrofitClient.class);

//
//    private void SendImageToBackend() throws JSONException {
//        String[] strArr = {"0image.png", "1image.png", "2image.png", "3image.png"};
//        LinearLayout imageLayout = findViewById(R.id.image_layout);
////        Glide.get(RoomsActivity.this).clearMemory();
//        for(int i = 0; i < strArr.length; i++) {
//            Log.v("Array of string array", url + "/" + strArr[i]);
//            ImageView imgV = new ImageView(RoomsActivity.this);
//            Glide.with(RoomsActivity.this)
//                    .load(url + "/" + strArr[i])
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .apply(new RequestOptions().override(600, 200))
//                    .centerCrop()
//                    .into(imgV);
//            int width = getResources().getDisplayMetrics().widthPixels / 4;
//            int height = getResources().getDisplayMetrics().heightPixels / 4;
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
//            params.setMargins(5, 5, 5, 5);
//            imgV.setLayoutParams(params);
//            imageLayout.addView(imgV);
//        }
//    }
//
//    private void returnRecognizedFurniture(String PictureName) throws JSONException {
//        LinearLayout imageLayout = findViewById(R.id.image_layout);
//
//        retrofitClient.getImages(PictureName).enqueue(new Callback<ArrayList>(){
//            @Override
//            public void onResponse(Call<ArrayList> call, retrofit2.Response<ArrayList> response) {
//                Log.v("that actually worked!", response.body().toString());
//                if(response.body().toString() == "[]"){
//                    Log.v("Couldn't detect any furniture", "Show this in a textview");
//                }
//                JSONArray jsonArray = null;
//                        try {
//                            jsonArray = new JSONArray(response.body().toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        String[] strArr = new String[jsonArray.length()];
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            try {
//                                strArr[i] = jsonArray.getString(i);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        for(int i = 0; i < strArr.length; i++) {
//                            //                          Display the first 500 characters of the response string.
//                            Log.v("Array of string array", url + "/" + strArr[i]);
//                            ImageView imgV = new ImageView(RoomsActivity.this);
//                            Glide.with(RoomsActivity.this)
//                                    .load(url + "/" + strArr[i])
//                                    .apply(new RequestOptions().override(600, 200))
//                                    .centerCrop()
//                                    .into(imgV);
//                            int width = getResources().getDisplayMetrics().widthPixels / 3;
//                            int height = getResources().getDisplayMetrics().heightPixels / 3;
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
//                            params.setMargins(5, 5, 5, 5);
//                            imgV.setLayoutParams(params);
//                            imageLayout.addView(imgV);
//                        }
//
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList> call, Throwable t) {
//                Log.v("Something went wrong again2", t.getMessage());
//            }
//        });
//    }
//
//    private ArrayList<String> getAllFiles(){
//        ArrayList<String> result = new ArrayList<String>(); //ArrayList cause you don't know how many files there is
//        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/"); //This is just to cast to a File type since you pass it as a String
//        File[] filesInFolder = folder.listFiles(); // This returns all the folders and files in your path
//        for (File file : filesInFolder) { //For each of the entries do:
//            if (!file.isDirectory()) { //check that it's not a dir
//                result.add(new String(file.getName())); //push the filename as a string
//            }
//        }
//
//        return result;
//    }
//
//    private String generateFilename() {
//        String date =
//                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
//        return Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES) + File.separator + "FurniturePictures/" + date + "_screenshot.jpg";
//    }
//
//    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {
//
//        File out = new File(filename);
//        if (!out.getParentFile().exists()) {
//            out.getParentFile().mkdirs();
//        }
//        try (FileOutputStream outputStream = new FileOutputStream(filename);
//             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
//            outputData.writeTo(outputStream);
//            outputStream.flush();
//            outputStream.close();
//        } catch (IOException ex) {
//            throw new IOException("Failed to save bitmap to disk", ex);
//        }
//    }



//        progressDialog.setMessage("Uploading, please wait...");
//        progressDialog.show();
//                progressDialog.dismiss();

//        progressDialog = new ProgressDialog(RoomsActivity.this);
//        progressDialog.setMessage("Uploading, please wait...");
//        progressDialog.show();
//                progressDialog.dismiss();
//
//        //getting image from gallery
//        Uri uri = Uri.fromFile(new File(path + fileNames.get(0)));
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //converting image to base64 string
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        Log.v("image bytes", String.valueOf(imageBytes.length));
//        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//        //sending image to server
//        StringRequest request = new StringRequest(Request.Method.POST, postUrl+"image", new Response.Listener<String>(){
//            @Override
//            public void onResponse(String s) {
//                progressDialog.dismiss();
//                if(s.equals("true")){
//                    Toast.makeText(RoomsActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
//                }
//                else{
//                    Toast.makeText(RoomsActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
//                }
//            }
//        },new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Log.v("String Length", String.valueOf(imageString.length()));
//                Log.v("Error11" , volleyError.toString());
//                Toast.makeText(RoomsActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
//            }
//        }) {
//            //adding parameters to send
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> parameters = new HashMap<String, String>();
//                parameters.put("image", imageString.substring(0,imageString.length()/4));
//                return parameters;
//            }
//        };
//
//        queue.add(request);


//    private void uploadBitmap(final Bitmap bitmap) {
//
//        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, postUrl+"image.png",
//                new Response.Listener<NetworkResponse>() {
//                    @Override
//                    public void onResponse(NetworkResponse response) {
//                        try {
//                            JSONObject obj = new JSONObject(new String(response.data));
//                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.e("GotError",""+error.getMessage());
//                    }
//                }) {
//
//            //            @Override
////            protected Map<String, String> getParams() throws AuthFailureError {
////                Map<String, String> params = new HashMap<>();
////                params.put("tags", tags);
////                return params;
////            }
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                long imagename = System.currentTimeMillis();
//                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
//                return params;
//            }
//        };
//
//        //adding the request to volley
//        Volley.newRequestQueue(this).add(volleyMultipartRequest);
//    }

//        File file = new File(path + fileNames.get(0));
//        RequestBody requestFile =
//                RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        // MultipartBody.Part is used to send also the actual file name
//        MultipartBody.Part body =
//                        MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//
//        // add another part within the multipart request
//        RequestBody fullName =
//                        RequestBody.create(MediaType.parse("multipart/form-data"), "Your Name");

//        Log.v("Holy shit!", "nothing");
//        retrofitClient.updateProfile(fullName, body).enqueue(new Callback<ResponseBody>(){
//            @Override
//            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                Log.v("Holy shit that actually worked!", response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.v("Something went wrong again", t.getMessage());
////                textViewResult.setText(t.getMessage());
//            }
//        });
//        Post post = new Post(23, "New Title", "New Text");

//        Map<String, String> fields = new HashMap<>();
//        fields.put("userId", "25");
//        fields.put("title", "New Title");

//        Call<String> call = retrofitClient.createImageString(fields);

//        call.enqueue(new Callback<String>() {
////            @Override
////            public void onResponse(Call<String> call, Response<String> response) {
//
////                if (!response.isSuccessful()) {
//////                    textViewResult.setText("Code: " + response.code());
////                    return;
////                }
//
////                Bitmap postResponse = response.body();
////
////                String content = "";
////                content += "Code: " + response.code() + "\n";
////                content += "ID: " + postResponse.getId() + "\n";
////                content += "User ID: " + postResponse.getUserId() + "\n";
////                content += "Title: " + postResponse.getTitle() + "\n";
////                content += "Text: " + postResponse.getText() + "\n\n";
//
////                textViewResult.setText(content);
////            }
//
//            @Override
//            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
////                textViewResult.setText(t.getMessage());
//            }
//        });


// Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//
////         Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        JSONArray jsonArray = null;
//                        try {
//                            jsonArray = new JSONArray(response);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        String[] strArr = new String[jsonArray.length()];
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            try {
//                                strArr[i] = jsonArray.getString(i);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        for(int i = 0; i < strArr.length; i++) {
//                            //                          Display the first 500 characters of the response string.
//                            Log.v("Array of string array", url + strArr[i]);
//                            ImageView imgV = new ImageView(RoomsActivity.this);
//                            Glide.with(RoomsActivity.this)
//                                    .load(url + strArr[i])
//                                    .apply(new RequestOptions().override(600, 200))
//                                    .centerCrop()
//                                    .into(imgV);
//                            int width = getResources().getDisplayMetrics().widthPixels / 3;
//                            int height = getResources().getDisplayMetrics().heightPixels / 3;
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
//                            params.setMargins(5, 5, 5, 5);
//                            imgV.setLayoutParams(params);
//                            imageLayout.addView(imgV);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.v("error ", error.toString());
//            }
//        });
//
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                40000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
////         Add the request to the RequestQueue.
//        queue.add(stringRequest);



//    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
//        return byteArrayOutputStream.toByteArray();
//    }


//    // Method to show Progress bar
//    private void showProgressDialogWithTitle(String title,String substring) {
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        //Without this user can hide loader by tapping outside screen
//        progressDialog.setCancelable(false);
//        //Setting Title
//        progressDialog.setTitle(title);
//        progressDialog.setMessage(substring);
//        progressDialog.show();
//
//    }
//
//    // Method to hide/ dismiss Progress bar
//    private void hideProgressDialogWithTitle() {
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.dismiss();
//    }


//        retrofitClient.getSingleImage("0image.png").enqueue(new Callback<Response>() {
//            @Override
//            public void onResponse(Call<Response> call, Response<Response> response) {
//                Log.v("ressss", response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<Response> call, Throwable t) {
//                Log.v("Something is wrong", t.getMessage());
//            }
//        });
//        ArrayList<String> fileNames = getAllFiles();
//        //getting image from gallery
//        Uri uri = Uri.fromFile(new File(path + fileNames.get(7)));
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //converting image to base64 string
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//        PojoImage paramObject = new PojoImage(imageString);
//        retrofitClient.uploadPicture(paramObject).enqueue(new Callback<String>(){
//            @Override
//            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                Log.v("that actually worked!", response.body());
//                try {
//                    returnRecognizedFurniture(response.body());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.v("Something went wrong again", t.getMessage());
////               textViewResult.setText(t.getMessage());
//                }
//            });


//        CoordinatorLayout layout = findViewById(R.id.analyze_layout);
//        ProgressBar progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
//        progressBar.setIndeterminate(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            progressBar.setMinWidth(layout.getWidth());
//        }
//        progressBar.setVisibility(View.VISIBLE);
//        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(100,100);
//        layout.addView(progressBar,params);
//        layout.addView(progressBar);

// Use the AlertDialog.Builder to configure the AlertDialog.
//        AlertDialog.Builder alertDialogBuilder =
//                new AlertDialog.Builder(this)
//                        .setTitle("Analyzing Image")
//                        .setMessage("Uploading, please wait...")
////                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
////                            public void onClick(DialogInterface dialog, int which) {
//////                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//////                                mContext.startActivities(new Intent[]{intent});
////                            }
////                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });

//        alertDialog = new AlertDialog(RoomsActivity.this);
//        alertDialog.setMessage("Uploading, please wait...");

//        try {
//            // Show the AlertDialog.
////            AlertDialog alertDialog = alertDialogBuilder.show();
////            SendImageToBackend();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        Button reqBtn = (Button) findViewById(R.id.request_btn);
//        reqBtn.setOnClickListener(view -> Log.v("test", "Send request using http"));


//        Glide.with(RoomsActivity.this)
//                .asBitmap()
//                .load("http://192.168.1.6:5000/0image.png")
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        Log.v("Resource is available" , "here");
//                        String filename = generateFilename();
//                        try {
//                            saveBitmapToDisk(resource, filename);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
//        LinearLayout linearLayout = findViewById(R.id.scroller_view1);
//        RelativeLayout relativeLayout = new RelativeLayout(RoomsActivity.this);
//
//        // Add imageview to relativelayout
//        ImageView image = new ImageView(RoomsActivity.this);
////        image.setDrawingCacheEnabled(true);
//        image.setImageDrawable(ContextCompat.getDrawable(RoomsActivity.this, R.drawable.checkmark));
//        RelativeLayout.LayoutParams imageParams =
//                new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.MATCH_PARENT
//                );
//        imageParams.addRule(RelativeLayout.ALIGN_PARENT_END);
//        imageParams.addRule(RelativeLayout.ALIGN_PARENT_START);
//        image.setLayoutParams(imageParams);
//        relativeLayout.addView(image);
////        Log.v("imageview for image", image.getDrawingCache().toString());
//
//        // Add checkmark to relativelayout
//        ImageView tickMark = new ImageView(RoomsActivity.this);
//        tickMark.setImageDrawable(ContextCompat.getDrawable(RoomsActivity.this, R.drawable.checkmark));
//        RelativeLayout.LayoutParams tickMarkParams =
//                new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT
//                );
//        tickMarkParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        tickMarkParams.addRule(RelativeLayout.ALIGN_PARENT_START);
//        tickMark.setLayoutParams(tickMarkParams);
//        relativeLayout.addView(tickMark);
//
//
//        int width = getResources().getDisplayMetrics().widthPixels/2;
//        int height = getResources().getDisplayMetrics().heightPixels/2;
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
//        params.setMargins(5, 5, 5, 5);
//        relativeLayout.setLayoutParams(params);
//
//        linearLayout.addView(relativeLayout);
//
//
//        ArrayList<String> fileNames = getAllFiles();
//        LinearLayout imageLayout = findViewById(R.id.image_layout);