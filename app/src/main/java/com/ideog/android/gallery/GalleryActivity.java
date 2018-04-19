package com.ideog.android.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener{
    private static String TAG = "GalleryActivity";
    private final String API_KEY = "967e2082cbdb43b27b6c0df3325d1843";

    Button search_btn = null;
    EditText search_edit = null;
    RecyclerView recycler_view;
    RecyclerView.Adapter adapter;

    private static ArrayList<String> imageUrls = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initializeUI();
    }

    private void initializeUI() {
        search_btn = findViewById(R.id.search_btn);
        search_edit = findViewById(R.id.search_edit);
        recycler_view = findViewById(R.id.my_recycler_view);

        recycler_view.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ImageAdapter(
                GalleryActivity.this,
                imageUrls
        );
        recycler_view.setAdapter(adapter);
        search_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: Button click");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FlickrAPI flickrAPI = retrofit.create(FlickrAPI.class);

        flickrAPI.getPhotos(
                API_KEY,
                search_edit.getText().toString(),
                "interestingness-asc",
                "1",
                "json",
                "url_m"
        ).enqueue(new retrofit2.Callback<FlickrResultModel>() {
            @Override
            public void onResponse(retrofit2.Call<FlickrResultModel> call, retrofit2.Response<FlickrResultModel> response) {
                Log.i(TAG, "onResponse: code = " + response.code());
                Log.i(TAG, "onResponse: raw response: \n" + response.raw());

                if (!response.isSuccessful())
                    return;

                FlickrResultModel model = response.body();
                imageUrls.clear();
                for (Photo photo : model.getPhotos().getPhoto()) {
                    String url = photo.getUrlM();
                    String title = photo.getTitle();
                    imageUrls.add(0, url);
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(retrofit2.Call<FlickrResultModel> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
