package com.ideog.android.gallery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{
    private static String TAG = "ImageAdapter";
    private static List<String> imageUrls;
    private Context context;

    public ImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(context, v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = imageUrls.get(position);
        holder.position = position;
        holder.itemCount = getItemCount();

        Log.i(TAG, "onBindViewHolder: url = " + url);
        Glide.with(context)
                .load(url)
                .into(holder.itemImageView);
    }

    @Override public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.gallery_item_image) ImageView itemImageView;
        private int position;
        private int itemCount;
        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.gallery_item_image) public void onPick(View v) {
            if (v instanceof ImageView) {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra("image", imageUrls.get(position));
                intent.putExtra("position", position);
                intent.putExtra("itemCount", itemCount);

                context.startActivity(intent);
            }
        }
    }
}
