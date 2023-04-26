package com.vands.photo.show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vands.photo.R;

public class TipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        String[] tips = new String[]{
                "在目录树页面中双击某目录可以打开目录下图片"
                , "长按图片预览界面的图片可进入选择模式"
                , "在选择模式下，点击图片可选择或取消选择"
                , "点击右上角的按钮可进行各类图片操作"
        };

        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(new TipsAdapter(tips)) ;
    }
}