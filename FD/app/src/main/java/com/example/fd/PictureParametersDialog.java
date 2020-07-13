package com.example.fd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PictureParametersDialog extends DialogFragment {
    public static final int MEASUREMENT_REQUEST = 1;
    private static final String TAG = "PictureParametersDialog";

    protected TextInputEditText width;
    protected TextInputEditText height;

    private Button heightMeasurementBtn;
    private Button widthMeasurementBtn;
    private Button cancelBtn;
    private Button okBtn;

    // TODO change this domain later once you've finished your backend and bought an actual server
    private String url ="http://192.168.1.9:5000"; // MY DOMAIN WHICH IS CURRENTLY LOCALHOST
    private RetrofitClient retrofitClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_picture_parameters, container, false);

        width = view.findViewById(R.id.width);
        height = view.findViewById(R.id.height);
        heightMeasurementBtn = view.findViewById(R.id.height_measurement_btn);
        widthMeasurementBtn = view.findViewById(R.id.width_measurement_btn);
        cancelBtn = view.findViewById(R.id.action_cancel);
        okBtn = view.findViewById(R.id.action_ok);

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


        Bundle args = getArguments();
        String picName = args.getString("PICTURE_NAME");

        retrofitClient.getParameters(picName).enqueue(new Callback<String>(){
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
                        width.setText(savedWidth);
                        height.setText(savedHeight);
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

        heightMeasurementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent heightIntent = new Intent(getActivity(), MeasurementActivity.class);
                heightIntent.putExtra("PARAMETER", "height");
                startActivityForResult(heightIntent,1);
            }
        });

        widthMeasurementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent widthIntent = new Intent(getActivity(), MeasurementActivity.class);
                widthIntent.putExtra("PARAMETER", "width");
                startActivityForResult(widthIntent, MEASUREMENT_REQUEST);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String heightInput = height.getText().toString();
                String widthInput = width.getText().toString();

                PictureParams params = new PictureParams(picName, heightInput, widthInput);
                retrofitClient.saveParameters(params).enqueue(new Callback<String>(){
                    @Override
                    public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        Log.v("that worked!", "That worked " + response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.v("Something went wrong", "something wong " + t.getMessage());
                    }
                });
                getDialog().dismiss();
            }
        });

        return view;
    }

    public PictureParametersDialog() {
        super();
    }
}
