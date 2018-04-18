package com.ideog.android.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
    private static String TAG = "ImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Bundle extras = getIntent().getExtras();
        String url = extras.getString("image");
        Log.i(TAG, "onCreate: " + url);

        ImageView imageView = findViewById(R.id.srcImage);
        Picasso.get()
                .load(url)
                .into(imageView);
    }
}
