package com.vands.photo.show;


import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vands.photo.R;
import com.vands.photo.model.GenUtilsModel;
import com.vands.photo.model.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于轮播图片的Activity
 * 展示幻灯片效果
 */
public class ShowInTurnActivity extends AppCompatActivity {    // 用于存放数据
    private ArrayList<ImageModel> imageFiles;
    private ViewPager viewPager;
    // private InTurnPagerAdapter PaperAdapter;

    private ArrayList<String> mtitle = new ArrayList<>(); //存放标题的数组
    private TextView textView;
    private int position;

    private Toolbar toolbar;
    private Thread onShowThread;

    private ImageButton silderShowBtn;
    private MaterialButton ib_back;

    private FloatingActionButton fab;

    private List<GestureImageView> mViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_in_turn);

        // 接受传递过来的数据
        Bundle bundle = getIntent().getExtras();
        imageFiles = bundle.getParcelableArrayList("imageFiles");
        // 继续接受数据,获取前一个页面点击的图片的位置
        position = bundle.getInt("position");
        AtomicInteger isShow = new AtomicInteger(bundle.getInt("isShow"));

        for (ImageModel imageFile : imageFiles) {
            mtitle.add(imageFile.file.getName().split("\\.")[0]);
        }

        find_view();
        initViewPager();
        initToolbar();

        ib_back.setOnClickListener(v -> finish());
        fab.setOnClickListener(v -> {
            if (isShow.get() == 0) {
                isShow.set(1);
                fab.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_stop_black_24dp));
                // 提示用户开始播放
                Toast.makeText(this, "开始播放", Toast.LENGTH_SHORT).show();
                onShowThread = new Thread(() -> {
                    try {
                        while (true) {
                            Thread.sleep(1000);
                            runOnUiThread(() -> {
                                if (viewPager.getCurrentItem() == imageFiles.size() - 1) {
                                    viewPager.setCurrentItem(0);
                                } else {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                onShowThread.start();
            } else {
                isShow.set(0);
                fab.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_play_circle_filled_24));
                if (onShowThread != null) {
                    onShowThread.interrupt();
                }
                onShowThread = null;

                // 提示用户停止播放
                Toast.makeText(this, "停止播放", Toast.LENGTH_SHORT).show();
            }
        });

        // 定时轮播图片, 1秒钟切换一次
        onShowThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(1000);
                    runOnUiThread(() -> {
                        if (viewPager.getCurrentItem() == imageFiles.size() - 1) {
                            viewPager.setCurrentItem(0);
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        if (isShow.get() == 1) {
            fab.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_stop_black_24dp));
            onShowThread.start();
        }
    }

    private void find_view() {
        ib_back = findViewById(R.id.ib_back);
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        textView = findViewById(R.id.tv_title);
    }

    public void initViewPager() {
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
////            @Override
////            public void onPageSelected(int position) {
////                super.onPageSelected(position);
////                textView.setText(mtitle.get(position));
////            }
////        });
        // viewPager 设置页面切换监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                textView.setText(mtitle.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        for (int i = 0; i < imageFiles.size(); i++) {
            GestureImageView gestureImageView = new GestureImageView(this);
            gestureImageView.getController().enableScrollInViewPager(viewPager);
            Glide.with(this).load(imageFiles.get(i).file).into(gestureImageView);
            gestureImageView.getController().getSettings()
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
            mViews.add(gestureImageView);
        }

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imageFiles.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override  //第一：将当前视图添加到container中，第二：返回当前View
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(mViews.get(position));
                return mViews.get(position);
            }

            @Override  //从当前container中删除指定位置（position）的View;
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(mViews.get(position));
            }
        });

        // 设置起始页，取消动画
        // viewPager.setOffscreenPageLimit(1);
        if (position != 0)
            viewPager.setCurrentItem(position, false);
        else {
            textView.setText(mtitle.get(position));
            viewPager.setCurrentItem(position);
        }
    }

    public void initToolbar() {
        // 设置菜单点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                使用if else
                int id = item.getItemId();
                if (id == R.id.action_delete) {
                    deleteImage();
                } else if (id == R.id.action_share) {
                    shareImage();
                } else if (id == R.id.action_setWallpaper) {
                    setAsWallpaper();
                } else if (id == R.id.action_info) {
                    showImageInfo();
                }
                return true;
            }
        });
    }

    // 删掉图片按钮事件
    private void deleteImage() {
        // 弹出
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除图片");
        builder.setMessage("确定要删除这张图片吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 删除图片
                // 获取当前显示的图片
                ImageModel imageFile = imageFiles.get(viewPager.getCurrentItem());
                // 删除图片
                File file = imageFile.getFile();
                if (file.exists()) {
                    file.delete();
                    // 删除成功后, 弹出Toast
                    Toast.makeText(ShowInTurnActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    // 删除成功后, 从集合中移除该图片
                    imageFiles.remove(imageFile);
                    // 删除成功后, 从ViewPager中移除该图片
                    // PaperAdapter.notifyDataSetChanged();
                    Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();
                    // 退出Activity前, 将删除后的数据传递回前一个Activity
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("imageFiles", imageFiles);

                    setResult(RESULT_OK, intent);
                    // 退出Activity
                    finish();
                } else {
                    // 删除失败后, 弹出Toast
                    Toast.makeText(ShowInTurnActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 设置壁纸按钮事件
    private void setAsWallpaper() {
        // 显示一个Dialog, 确认是否设置为壁纸
        // 使用AlertDialog.Builder创建一个AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 设置AlertDialog的标题
        builder.setTitle("设置壁纸");
        // 设置AlertDialog的内容
        builder.setMessage("是否设置为壁纸?");
        // 设置AlertDialog的图标
        builder.setIcon(R.drawable.ic_launcher_foreground);
        // 设置AlertDialog的按钮
        builder.setPositiveButton("确定", (dialog, which) -> {
            // 弹出Toast
            Toast.makeText(this, "正在设置壁纸中", Toast.LENGTH_SHORT).show();
            // 使用多线程设置壁纸
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用WallpaperManager设置壁纸
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(ShowInTurnActivity.this);
                    // 获取当前显示的图片
                    ImageModel imageFile = imageFiles.get(viewPager.getCurrentItem());
                    Bitmap bitmap = GenUtilsModel.getBitmapFromUri(imageFile);
                    try {
                        wallpaperManager.suggestDesiredDimensions(bitmap.getWidth(), bitmap.getHeight());
                        wallpaperManager.setBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            // 设置完成后关闭Dialog
            dialog.dismiss();
            // 弹出Toast
            Toast.makeText(this, "设置完成", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            // 弹出Toast
            Toast.makeText(this, "取消设置壁纸", Toast.LENGTH_SHORT).show();
        });
        // 显示AlertDialog
        builder.show();


    }

    // 分享图片按钮事件
    private void shareImage() {
        // 分享图片, 使用FileProvider
        // 获取当前显示的图片
        ImageModel imageFile = imageFiles.get(viewPager.getCurrentItem());
        // 获取图片的路径
        String imagePath = imageFile.file.getPath();
        // 获取图片的Uri
        Uri imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(imagePath));
        // 创建分享图片的Intent
        Intent shareIntent = new Intent();
        // 设置分享图片的Action
        shareIntent.setAction(Intent.ACTION_SEND);
        // 设置分享图片的数据
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        // 设置分享图片的类型
        shareIntent.setType("image/*");
        // 创建分享图片的Dialog
        shareIntent = Intent.createChooser(shareIntent, "分享图片");
        // 显示分享图片的Dialog
        startActivity(shareIntent);
    }

    // 查看图片信息按钮事件
    private void showImageInfo() {
        // 传递当前查看的图片数据
        Bundle bundle = new Bundle();
        bundle.putParcelable("imageFile", imageFiles.get(viewPager.getCurrentItem()));
        // 跳转到图片信息的Activity
        Intent intent = new Intent(this, ShowImageMesActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showInTurn() {
    }
}

