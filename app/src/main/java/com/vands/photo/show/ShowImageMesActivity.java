package com.vands.photo.show;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.vands.photo.R;
import com.vands.photo.model.ImageModel;

/**
 * 显示图片的详细信息
 */
public class ShowImageMesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private MaterialButton ib_back;
    private ImageModel imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_mes);

        // 获取传递过来的数据
        imageFile = (ImageModel) getIntent().getParcelableExtra("imageFile");

        init_view();
    }

    private void init_view() {
        recyclerView = findViewById(R.id.rv_show_image_mes);
        toolbar = findViewById(R.id.toolbar_show_image_mes);
        ib_back = findViewById(R.id.ib_back);

        ib_back.setOnClickListener(v -> {
            finish();
        });

        init_recyclerView();
    }

    private void init_recyclerView() {
        DetailAdapter detailAdapter = new DetailAdapter(imageFile);
        // 设置布局管理器，自定义间距，单独设置每个item的大小
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 2 == 0) {
                    return 1;
                } else {
                    return 3;
                }
            }
        };
        gridLayoutManager.setSpanSizeLookup(spanSizeLookup);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(detailAdapter);
    }

}