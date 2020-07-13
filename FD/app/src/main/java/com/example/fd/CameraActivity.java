package com.example.fd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CameraActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private final static int REQUEST_CODE_1 = 1;

    private ArFragment fragment;
    private boolean isTracking;
    private boolean isHitting;
    private PointerDrawable pointer = new PointerDrawable();
    private ModelRenderable andyRenderable;
    private HitResult myhit;
    private boolean measure_height = false;
    private Anchor anchor1 = null;
    private float fl_measurement = 0.0f;
    private List<AnchorNode> anchorNodes = new ArrayList<>();
    private AnchorNode myanchornode;
    private ImageView image;
    private DrawerLayout drawer;
    private Uri gmailPhoto;
    private String gmailUsername;
    private String gmailEmail;

    private ViewRenderable imageRenderable;
    private ImageView testImage;
    private SeekBar sk_height_control;
    private int catalogImgNum = 0;
    private File folder;
    private PicturesFolder picFolder;

    // TODO change this domain later once you've finished your backend and bought an actual server
    private String url ="http://192.168.1.9:5000"; // MY DOMAIN WHICH IS CURRENTLY LOCALHOST
    private RetrofitClient retrofitClient;
    private String pictureString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

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

        if(savedInstanceState == null){
            navigationView.setCheckedItem(R.id.nav_ar_camera);
        }

        Context context = getBaseContext();
        folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Furniture/"); //This is just to cast to a File type since you pass it as a String
        picFolder = new PicturesFolder(folder);


        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        Session session = null;
        try {
            session = new Session(CameraActivity.this);
        } catch (UnavailableArcoreNotInstalledException
                | UnavailableApkTooOldException
                | UnavailableSdkTooOldException
                | UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        }

        Config ar_session_config = session.getConfig();
        ar_session_config.setFocusMode(Config.FocusMode.AUTO);
        session.configure(ar_session_config);

        FloatingActionButton fab = findViewById(R.id.fab);
        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();
        });


        // Change this to be a single black point
        ModelRenderable.builder()
                .setSource(this, R.raw.cubito2)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });


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
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        retrofitClient = retrofit.create(RetrofitClient.class);

        PictureParams pictureParams = new PictureParams(pictureString, "1" ,"1");
//      Get one image and make a view model
        if(!picFolder.getAllFiles().isEmpty()){
            setRenderabletoImage();
        }

        retrofitClient.getParameters(pictureString).enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.v("that worked!", "That worked2 " + response.body());
                if(response.body() != "None") {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(response.body());
                        JSONObject pictureJson = new JSONObject(json.getString("Picture"));
                        String savedHeight = pictureJson.getString("height");
                        String savedWidth = pictureJson.getString("width");
                        if(savedHeight != ""){
                            pictureParams.setHeight(savedHeight);
                        }
                        if(savedWidth != ""){
                            pictureParams.setWidth(savedWidth);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v("Something went wrong", "something wong " + t.getMessage());
            }
        });

        sk_height_control = (SeekBar) findViewById(R.id.sk_height_control);
        sk_height_control.setProgress(5);
        sk_height_control.setVisibility(View.GONE);

        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }
                    myhit = hitResult;

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();

                    AnchorNode anchorNode = new AnchorNode(anchor);


                    anchorNode.setParent(fragment.getArSceneView().getScene());

                    emptyAnchors();
                    anchor1 = anchor;

                    myanchornode = anchorNode;
                    anchorNodes.add(anchorNode);

                    // image testing
                    TransformableNode img = new TransformableNode(fragment.getTransformationSystem());
                    img.setParent(anchorNode);
                    img.setRenderable(imageRenderable);
                    imageRenderable.setShadowCaster(false); // Remove shadowcasting by the imageRenderable
                    imageRenderable.setShadowReceiver(false); // Remove shadowcasting by the imageRenderable
                    img.setLocalPosition(new Vector3(0.0f, -0.4f, 0.0f));

//                    sk_height_control.setVisibility(View.VISIBLE);
                    int picHeight = 1, picWidth = 1;
                    try {
                        if(pictureString != ""){
//                            Log.v("asdf", "asdf " + pictureParams.height() + " " + pictureParams.width());
                            picHeight = Integer.parseInt(pictureParams.height());
                            picWidth = Integer.parseInt(pictureParams.width());
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        picHeight = 1;
                        picWidth = 1;
                    }
                    img.setLocalScale(new Vector3(picWidth*0.01f, picHeight*0.01f, 0.1f));
                    img.select();
                    img.getScaleController().setEnabled(false);
                });

        fab.setOnClickListener(view -> takePhoto());
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(CameraActivity.this);
        if (acct != null) {
            gmailUsername = acct.getDisplayName();
            gmailEmail = acct.getEmail();
            gmailPhoto = acct.getPhotoUrl();
        }

        NavigationView navView = findViewById(R.id.nav_view);
        LinearLayout navLayout = (LinearLayout) navView.getHeaderView(0);

        ImageView gmailPic = navLayout.findViewById(R.id.gmail_picture);
        TextView gmailName = navLayout.findViewById(R.id.gmail_name);
        TextView gmailMail = navLayout.findViewById(R.id.gmail_email);

        gmailMail.setText(gmailEmail);
        gmailName.setText(gmailUsername);
        Glide.with(CameraActivity.this)
                .load(gmailPhoto)
                .centerCrop()
                .into(gmailPic);
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
//        return folder + "/Rooms/" + date + "_screenshot.jpg";
        String imageFileName = date + "_screenshot";
        File storageDir = new File(folder + "/Rooms/");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir     /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.v("Error", ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.fd.ar.codelab.name.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            List<String> imgs = Stream.of(new File(folder + "/Rooms/").listFiles())
                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .collect(Collectors.toList());
            Collections.sort(imgs);

            Intent fileIntent = new Intent(CameraActivity.this, PhotoActivity.class);
            fileIntent.putExtra("IMAGENAME", folder + "/Rooms/" + imgs.get(imgs.size() - 1));
            startActivityForResult(fileIntent, REQUEST_CODE_1);
        }
    }

    private void setRenderabletoImage() {
        testImage = new ImageView(CameraActivity.this);
        Intent imgIntent = getIntent();
        catalogImgNum = imgIntent.getIntExtra("imageNum", 0);
        pictureString = picFolder.getAllFiles().get(catalogImgNum);

        Glide.with(CameraActivity.this)
                .load(folder + "/" + pictureString)
                .centerCrop()
                .into(testImage);

        ViewRenderable.builder()
                .setView(CameraActivity.this, testImage)
                .build()
                .thenAccept(renderable -> imageRenderable = renderable);
    }


    private void onUpdate() {
//        boolean trackingChanged = updateTracking();
//        View contentView = findViewById(android.R.id.content);
//        if (trackingChanged) {
////            if (isTracking) {
////                contentView.getOverlay().add(pointer);
////            } else {
////                contentView.getOverlay().remove(pointer);
////            }
//            contentView.invalidate();
//        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
//                contentView.invalidate();
            }
        }
    }

    /**
     * Check whether the device supports the tools required to use the measurement tools
     * @param activity
     * @return boolean determining whether the device is supported or not
     */
    private boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    private void emptyAnchors(){
        anchor1 = null;
        for (AnchorNode n : anchorNodes) {
            fragment.getArSceneView().getScene().removeChild(n);
            n.getAnchor().detach();
            n.setParent(null);
            n = null;
        }
    }

    private boolean updateTracking() {
        Frame frame = fragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth()/2, vw.getHeight()/2);
    }

    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return folder + "/Rooms/" + date + "_screenshot.jpg";
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

    // TODO copy this code to the NormalCameraActivity once you're done connecting the camera intent
    private void takePhoto() {
        final String filename = generateFilename();
        ArSceneView view = fragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                    Intent fileIntent = new Intent(CameraActivity.this, PhotoActivity.class);
                    fileIntent.putExtra("IMAGENAME", filename);
                    startActivityForResult(fileIntent, REQUEST_CODE_1);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(CameraActivity.this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

            } else {
                Toast toast = Toast.makeText(CameraActivity.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

    public void addNodeToScene(Anchor anchor, ModelRenderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    public void onException(Throwable throwable){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(throwable.getMessage())
                .setTitle("Codelab error!");
        AlertDialog dialog = builder.create();
        dialog.show();
        return;
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_ar_camera:

                break;
            case R.id.nav_normal_camera:
                dispatchTakePictureIntent();
                break;
            case R.id.nav_furniture_catalog:
                startActivity(new Intent(CameraActivity.this, FurnitureDisplayActivity.class));
                break;
            case R.id.nav_room_catalog:
                startActivity(new Intent(CameraActivity.this, RoomsActivity.class));
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

//        ViewRenderable.builder()
//                .setView(this, R.layout.activity_camera)
//                .build()
//                .thenAccept(renderable -> {
//                    testImage = (ImageView)renderable.getView();
//                });

//            case R.id.nav_user:
//                startActivity(new Intent(CameraActivity.this, UserActivity.class));
//                break;

//        sk_height_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////                upDistance = progress;
//                int height = progress - 50;
//                Log.v("height", "high " + String.valueOf(height));
//                int picHeight, picWidth;
//                try {
//                    picHeight = Integer.parseInt(pictureParams.height());
//                    picWidth = Integer.parseInt(pictureParams.width());
//                }
//                catch (NumberFormatException e)
//                {
//                    picHeight = 1;
//                    picWidth = 1;
//                }
//                fl_measurement = progress/100f;
//                myanchornode.setLocalPosition(new Vector3(picWidth*0.1f, height/10f, 0.1f));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });



//                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
//                        "Photo saved", Snackbar.LENGTH_LONG);
//                snackbar.setAction("Open in Photos", v -> {
//                    File photoFile = new File(filename);
//
//                    Uri photoURI = FileProvider.getUriForFile(CameraActivity.this,
//                            CameraActivity.this.getPackageName() + ".ar.codelab.name.provider",
//                            photoFile);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
//                    intent.setDataAndType(photoURI, "image/*");
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(intent);
//
//                });
//                snackbar.show();


//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // ARCore requires camera permission to operate.
////        if (!CameraPermissionHelper.hasCameraPermission(this)) {
////            CameraPermissionHelper.requestCameraPermission(this);
////            return;
////        }
//    }


//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            ContentResolver resolver = context.getContentResolver();
//            ContentValues contentValues = new ContentValues();
//////            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
//////            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
//            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Sceneform/");
////
//            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//
//            folder = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES.toString() + "/Rooms/");
//        }


//    @Override
//    public void onActivityResult(int requestCode,
//                                 int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            Log.v("Whateves2", data.toString());
//            if (resultCode == RESULT_OK) {
//                Log.v("Whateves", data.toString());
//                catalogImgNum = data.getIntExtra("imageNum",1);
////                Bundle extras = data.getExtras();
////                byte[] byteArray = extras.getByteArray("picture");
////                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
////                image = new ImageView(this);
////                image.setImageBitmap(bmp);
//
//            }
//        }
//    }


//        btn_height = (Button) findViewById(R.id.btn_height);
//        btn_save = (Button) findViewById(R.id.btn_save);
//        btn_width = (Button) findViewById(R.id.btn_width);
//        text = (TextView) findViewById(R.id.text);
//        sk_height_control = (SeekBar) findViewById(R.id.sk_height_control);