package com.example.fd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FurnitureDisplayActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = FurnitureDisplayActivity.class.getSimpleName();
    private static final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/";
    private byte[][] imageArr;
    private ImageView[] imageViews;
    private Intent intent;
    private byte[] byteArray;
    private DrawerLayout drawer;
    private PictureParametersDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_display);

        Intent measurementIntent = getIntent();

        if(measurementIntent != null){
            Log.v(TAG, "Value of intent " );

        }


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
            navigationView.setCheckedItem(R.id.nav_furniture_catalog);
        }

        Context context = getBaseContext();
        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Furniture/");
        PicturesFolder picFolder = new PicturesFolder(folder);
        ArrayList<String> fileNames = picFolder.getAllFiles();

        LinearLayout layoutInScrollView1 = findViewById(R.id.scroller_view1);
        LinearLayout layoutInScrollView2 = findViewById(R.id.scroller_view2);

        int width = getResources().getDisplayMetrics().widthPixels/2;
        int height = getResources().getDisplayMetrics().heightPixels/2;
        intent = new Intent(FurnitureDisplayActivity.this, CameraActivity.class);
        for(int i = 0; i < fileNames.size(); i++){
            ImageView imgV = new ImageView(this);
            final int num = i;
            Glide.with(FurnitureDisplayActivity.this)
                    .load(folder + "/" + fileNames.get(i))
                    .centerCrop()
                    .into(imgV);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.setMargins(5,5,5,5);
            imgV.setLayoutParams(params);
            imgV.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    intent.putExtra("imageNum", num);
                    startActivityForResult(intent, 1);
                }
            });
            int finalI = i;
            imgV.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v){
                    Bundle args = new Bundle();
                    args.putString("PICTURE_NAME", fileNames.get(finalI));
                    dialog = new PictureParametersDialog();
                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), "PictureParametersDialog");
                    return true;
                }
            });

            if(i % 2 == 0){
                layoutInScrollView1.addView(imgV);
            } else{
                layoutInScrollView2.addView(imgV);
            }
        }
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
//                startActivity(new Intent(FurnitureDisplayActivity.this, UserActivity.class));
//                break;
            case R.id.nav_ar_camera:
                startActivity(new Intent(FurnitureDisplayActivity.this, CameraActivity.class));
                break;
            case R.id.nav_normal_camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
//                startActivity(new Intent(FurnitureDisplayActivity.this, NormalCameraActivity.class));
                break;
            case R.id.nav_furniture_catalog:

                break;
            case R.id.nav_room_catalog:
                startActivity(new Intent(FurnitureDisplayActivity.this, RoomsActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int measurementVal = Math.round(data.getFloatExtra(MeasurementActivity.MEASUREMENT, 0f));
        String parameterMeasured = data.getStringExtra(MeasurementActivity.MEASUREMENT_PARAMETER);
        if(parameterMeasured.equals("width")){
            dialog.width.setText(String.valueOf(measurementVal));
        } else if(parameterMeasured.equals("height")){
            dialog.height.setText(String.valueOf(measurementVal));
        }
    }

    // For a simple image list:
//    @Override
//    public View getView(int position, View recycled, ViewGroup container) {
//        final ImageView myImageView;
//        if (recycled == null) {
//            myImageView = (ImageView) findViewById(R.id.scroller_view);
//        } else {
//            myImageView = (ImageView) recycled;
//        }
//
//        Glide.with(FurnitureDisplayActivity.this)
//                .load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/")
//                .centerCrop()
//                .into(myImageView);
//
//        return myImageView;
//    }
}
