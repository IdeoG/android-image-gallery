package com.ideog.android.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

    private ArrayList<String> imageUrls = new ArrayList<>();

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
                try {
                    findImages(search_edit.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void findImages(String text) throws IOException {
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
                .appendQueryParameter("extras", "url_s")
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
                    int len = (photo.length() > 20) ? 20 : photo.length();
                    imageUrls.clear();
                    for (int i=0; i < len; i++) {
                        String url = photo.getJSONObject(i).getString("url_s");
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

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>  {
        private List<String> imageUrls;
        private Context context;

        public ImageAdapter(Context context, List<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.gallery_item, parent, false);
            return new ViewHolder(v);
        }

        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String url = imageUrls.get(position);
            holder.position = position;

            Picasso.get()
                    .load(url)
                    .into(holder.itemImageView);
        }

        @Override public int getItemCount() {
            return imageUrls.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView itemImageView;
            private int position;

            public ViewHolder(View itemView) {
                super(itemView);
                itemImageView = itemView.findViewById(R.id.gallery_item_image);
                itemImageView.setOnClickListener(this);
            }

            @Override public void onClick(View v) {
                if (v instanceof ImageView) {
                    Intent intent = new Intent(GalleryActivity.this, ImageActivity.class);
                    intent.putExtra("image", imageUrls.get(position));

                    startActivity(intent);
                }
            }
        }
    }
}
