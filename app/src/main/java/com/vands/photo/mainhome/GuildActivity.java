package com.vands.photo.mainhome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.alexvasilkov.gestures.commons.CropAreaView;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.vands.photo.R;

public class GuildActivity extends AppCompatActivity {

    private final int pageCount = 3;
    private ViewPager2 pager;
    private ViewGroup viewGroup;
    private MaterialButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guild);

        find_view();
        // 添加布局管理器,设置间隔

        for (int i = 0; i < pageCount; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.bga_banner_point_disabled);
            imageView.setSelected(i == 0);
            viewGroup.addView(imageView);
        }

        RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.guild_item, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 3;
            }
        };

        pager.setAdapter(adapter);
        ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == pageCount - 1) {
                    button.setVisibility(MaterialButton.VISIBLE);
                } else {
                    button.setVisibility(MaterialButton.GONE);
                }
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    ImageView childAt = (ImageView) viewGroup.getChildAt(i);
                    if (i == position) {
                        // 设置成别的图片
                        childAt.setImageResource(R.drawable.bga_banner_point_enabled);
                    } else {
                        childAt.setImageResource(R.drawable.bga_banner_point_disabled);
                    }
                }
            }
        };
        pager.registerOnPageChangeCallback(callback);

        button.setOnClickListener(v -> {
            finish();
        });
    }

    private void find_view() {
        pager = findViewById(R.id.GuildPager);
        viewGroup = findViewById(R.id.icon_group);
        button = findViewById(R.id.enter_app);
        CropAreaView cropAreaView = findViewById(R.id.crop_area);
        GestureImageView imageView = new GestureImageView(this);
        Glide.with(this).load(R.drawable.ic_launcher_background).into(imageView);
        cropAreaView.setImageView(imageView);
    }

}