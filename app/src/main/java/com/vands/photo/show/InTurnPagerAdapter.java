package com.vands.photo.show;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.vands.photo.R;
import com.vands.photo.model.ImageModel;

import java.util.ArrayList;

// viewpager2 的 适配器
public class InTurnPagerAdapter extends RecyclerView.Adapter<InTurnPagerAdapter.CardViewHolder> {
    private ArrayList<ImageModel> imageViews;
    private ArrayList<String> imageTitles;
    private Context context;

    public InTurnPagerAdapter(Context context, ArrayList<ImageModel> imageViews, ArrayList<String> imageTitles) {
        this.context = context;
        this.imageViews = imageViews;
        this.imageTitles = imageTitles;
    }


    @NonNull
    @Override
    public InTurnPagerAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.show_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InTurnPagerAdapter.CardViewHolder holder, int position) {
        // 使用Glide加载图片
        Glide.with(context).load(imageViews.get(position).file).into(holder.gestureImageView);
        holder.gestureImageView.getController().getSettings()
                .setMaxZoom(2f)
                .setDoubleTapZoom(-1f) // Falls back to max zoom level
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(true)
                .setRestrictRotation(true)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(2f)
                .setFillViewport(false);
//                .setFitMethod(Settings.Fit.INSIDE)
//                .setGravity(Gravity.CENTER);
        // holder.photoView.getController().enableScrollInViewPager(viewPager);
    }

    @Override
    public int getItemCount() {
        return imageViews.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
//        public PhotoView photoView;
        private GestureImageView gestureImageView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            gestureImageView = itemView.findViewById(R.id.image);
        }
    }
}