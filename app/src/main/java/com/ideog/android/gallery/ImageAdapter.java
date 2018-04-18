package com.ideog.android.gallery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{
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

        Picasso.get()
                .load(url)
                .into(holder.itemImageView);
    }

    @Override public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView itemImageView;
        private int position;
        private int itemCount;
        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            itemImageView = itemView.findViewById(R.id.gallery_item_image);
            itemImageView.setOnClickListener(this);
        }

        @Override public void onClick(View v) {
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
