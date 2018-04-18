package com.ideog.android.gallery;

import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GalleryActivity extends AppCompatActivity {
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
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                findImages(search_edit.getText().toString());
            }
        });
    }

    private void findImages(String text) {
        if (text.equals(""))
            return;

        String url = Uri.parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("method", "flickr.photos.search")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("text", text)
                .appendQueryParameter("sort", "interestingness-asc")
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_m")
                .appendQueryParameter("per_page", "50")
                .build().toString();
        Log.i(TAG, "findImages: url request = " + url);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override  public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                try {
                    JSONObject raw = new JSONObject(resp);
                    JSONObject photos = raw.getJSONObject("photos");
                    JSONArray photo = photos.getJSONArray("photo");
                    int len = photo.length();
                    imageUrls.clear();
                    for (int i=0; i < len; i++) {
                        String url = photo.getJSONObject(i).getString("url_m");
                        imageUrls.add(url);
                    }

                    Log.i(TAG, "onResponse: " + photo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
