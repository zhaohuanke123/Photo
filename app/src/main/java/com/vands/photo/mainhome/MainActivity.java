package com.vands.photo.mainhome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vands.photo.R;
import com.vands.photo.model.ActivityUtil;
import com.vands.photo.model.GenUtilsModel;
import com.vands.photo.model.ImageModel;
import com.vands.photo.model.SearchImageModel;
import com.vands.photo.model.SelectedModel;
import com.vands.photo.model.SelectionModel;
import com.vands.photo.model.SortParam;
import com.vands.photo.show.ShowInTurnActivity;
import com.vands.photo.show.TipsActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final String[] tabs = new String[]{"文件目录", "图片"}; // tab的标题
    public ActivityResultLauncher<Intent> showOnTurnlauncher; // 用于显示单张图片activity的回调
    private ViewPager2 pager2; // 用于切换页面的控件
    private TabLayout tabLayout; // 用于显示tab的控件
    private TabLayoutMediator mediator; // 用于绑定tab和viewpager2
    private final int activeColor = Color.parseColor("#018786"); // 选中时tab的颜色
    private final int normalColor = Color.parseColor("#666666"); // 未选中时tab的颜色
    private final int normalSize = 21; // 未选中时tab的大小
    private TextView title; // 用于显示标题的控件
    private Menu menu; // 用于显示菜单的控件
    private HeadPageAdapter headPageAdapter; // 用于绑定viewpager2和fragment
    private DirTreeFragment dirTreeFragment; // 用于显示文件目录的fragment
    private GridImageFragment gridImageFragment; // 用于显示图片的fragment
    private Toolbar toolbar; // 用于显示标题栏的控件
    private MaterialButton sliderButton; // 用于显示侧边栏的控件
    // MdButton
    private MaterialButton searchButton; // 用于显示搜索的控件
    private MaterialButton sortButton; // 用于显示排序的控件
    // 用于监听viewpager2的滑动事件
    private final ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int position) {
            //可以来设置选中时tab的大小
            int tabCount = tabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                assert tab != null;
                TextView tabView = (TextView) tab.getCustomView();
                if (tab.getPosition() == position) {
                    assert tabView != null;
                    tabView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    assert tabView != null;
                    tabView.setTypeface(Typeface.DEFAULT);
                }
                tabView.setTextSize(normalSize);
            }
            // 切换页面时，让菜单按钮不可见
            if (position == 0) {
                searchButton.setVisibility(SearchView.GONE);
                sliderButton.setVisibility(SearchView.GONE);
                sortButton.setVisibility(SearchView.GONE);
                title.setText("图库");
                set_head_menu_visible(false);
            } else {
                searchButton.setVisibility(SearchView.VISIBLE);
                sliderButton.setVisibility(SearchView.VISIBLE);
                sortButton.setVisibility(SearchView.VISIBLE);
                title.setText(String.format(getResources().getString(R.string.image_count), SelectionModel.getImageModelList().size()));
                set_head_menu_visible(true);
                menu.findItem(R.id.action_paste).setVisible(SelectedModel.getSourcePath() != null && SelectedModel.getCopyOrMove() != -1);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    private void set_head_menu_visible(boolean isVis) {
        menu.findItem(R.id.action_cut).setVisible(isVis);
        menu.findItem(R.id.action_rename).setVisible(isVis);
        menu.findItem(R.id.action_copy).setVisible(isVis);
        menu.findItem(R.id.action_select_all).setVisible(isVis);
        menu.findItem(R.id.action_cnacel_select).setVisible(isVis);
        menu.findItem(R.id.action_select_reverse).setVisible(isVis);
        menu.findItem(R.id.action_delete).setVisible(isVis);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 将当前activity加入到ActivityUtil中
        ActivityUtil.activities.put(this.getClass().getSimpleName(), this);

        // 申请存储权限
        apply_for_storage_permission();
        // 初始化控件
        init_view();

        SelectionModel.initSelection();
        GenUtilsModel.init();

        // 初始化回调
        showOnTurnlauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        Bundle bundle1 = result.getData().getExtras();
                        ArrayList<ImageModel> tmpFiles = bundle1.getParcelableArrayList("imageFiles");
                        if (tmpFiles != null) {
                            int size = gridImageFragment.getImageList().size();
                            gridImageFragment.setImageList(tmpFiles);
                            gridImageFragment.getAdapter().notifyItemRangeChanged(0, size);
                        }
                    }
                });
    }

    private void init_view() {
        find_view();
        init_toolbar();
        init_pager2();
        init_sliderButton();
        init_searchButton();
        init_sortButton();
    }

    private void init_sortButton() {
        sortButton.setOnClickListener(v -> {
            // 排序,弹出排序菜单
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());
            // 分别设置菜单项的点击事件，有按照名称排序，按照时间排序，按照大小排序，三个都是升序和降序， 命名按照sort_by_xx_asc
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.sort_by_name_asc) {
                    gridImageFragment.sortImage(SortParam.SBNR);
                } else if (item.getItemId() == R.id.sort_by_name_desc) {
                    gridImageFragment.sortImage(SortParam.SBND);
                } else if (item.getItemId() == R.id.sort_by_time_asc) {
                    gridImageFragment.sortImage(SortParam.SBDR);
                } else if (item.getItemId() == R.id.sort_by_time_desc) {
                    gridImageFragment.sortImage(SortParam.SBDD);
                } else if (item.getItemId() == R.id.sort_by_size_asc) {
                    gridImageFragment.sortImage(SortParam.SBSR);
                } else if (item.getItemId() == R.id.sort_by_size_desc) {
                    gridImageFragment.sortImage(SortParam.SBSD);
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void init_searchButton() {
        searchButton.setOnClickListener(v -> {
            // 搜索，弹出搜索框
            // 一个输入框
            EditText searchText = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("搜索");
            builder.setView(searchText);

            builder.setPositiveButton("确定", (dialog, which) -> {
                // 搜索
                gridImageFragment.refresh();
                String query = searchText.getText().toString();
                if (query.equals("")) {
                    gridImageFragment.refresh();
                } else {
                    ArrayList<ImageModel> imageFiles = gridImageFragment.getImageList();
                    ArrayList<ImageModel> searchedFiles = SearchImageModel.fuzzySearch(query, imageFiles);
                    gridImageFragment.refresh(searchedFiles);
                    gridImageFragment.getAdapter().notifyItemRangeRemoved(0, imageFiles.size());
                    Toast.makeText(this, "搜索到：" + searchedFiles.size() + " 张图片", Toast.LENGTH_SHORT).show();
                }

            });
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.show();
        });
    }

    private void init_sliderButton() {
        sliderButton.setOnClickListener(v -> {
            // 启动显示图片的activity
            if (gridImageFragment.getImageList() == null || gridImageFragment.getImageList().size() == 0) {
                Toast.makeText(this, "当前没有图片", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, ShowInTurnActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("imageFiles", gridImageFragment.getImageList());
            intent.putExtras(bundle);
            intent.putExtra("position", 0);
            intent.putExtra("isShow", 1);
            showOnTurnlauncher.launch(intent);
        });
    }

    private void find_view() {
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        pager2 = findViewById(R.id.Pager);
        sliderButton = findViewById(R.id.sliderShow);
        searchButton = findViewById(R.id.search);
        sortButton = findViewById(R.id.sort);
        menu = toolbar.getMenu();

        ActivityUtil.activities.put(ViewPager2.class.getSimpleName(), pager2);
    }

    private void init_pager2() {
        gridImageFragment = new GridImageFragment();
        dirTreeFragment = new DirTreeFragment();

        headPageAdapter = new HeadPageAdapter(this, gridImageFragment, dirTreeFragment);
        pager2.setAdapter(headPageAdapter);

        // viewPager 页面切换监听
        pager2.registerOnPageChangeCallback(changeCallback);
        mediator = new TabLayoutMediator(tabLayout, pager2, (tab, position) -> {
            //这里可以自定义TabView
            TextView tabView = new TextView(MainActivity.this);

            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_selected};
            states[1] = new int[]{};

            int[] colors = new int[]{activeColor, normalColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            tabView.setText(tabs[position]);
            tabView.setTextSize(normalSize);
            tabView.setTextColor(colorStateList);

            tab.setCustomView(tabView);
        });
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
        pager2.setCurrentItem(1);
        pager2.setOffscreenPageLimit(2);
    }

    private void init_toolbar() {
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_tips) {
                // 启动提示activity
                Intent intent = new Intent(this, TipsActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.action_settings) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.action_copy) {
                copy();
                return true;
            } else if (item.getItemId() == R.id.action_cut) {
                cut();
                return true;
            } else if (item.getItemId() == R.id.action_paste) {
                paste();
                return true;
            } else if (item.getItemId() == R.id.action_cnacel_select) {
                cancel_select();
                return true;
            } else if (item.getItemId() == R.id.action_select_all) {
                // 全选
                select_all();
                return true;
            } else if (item.getItemId() == R.id.action_select_reverse) {
                // 反选
                select_reverse();
                return true;
            } else if (item.getItemId() == R.id.action_delete) {
                delete_select();
                return true;
            } else if (item.getItemId() == R.id.action_rename) {
                rename();
                return true;
            } else if (item.getItemId() == R.id.action_retrun) {
                finish();
                return true;
            }

            return false;
        });
    }

    private void cut() {
        // 如果没有选中文件，提示用户
        ArrayList<ImageModel> sourceList = SelectionModel.getImageModelList();
        if (sourceList.size() == 0) {
            Toast.makeText(this, "请选择要剪切的文件", Toast.LENGTH_SHORT).show();
            return;
        }

        SelectedModel.setSourcePath(sourceList);
        SelectedModel.setWaitingPasteNum(sourceList.size());
        SelectedModel.setCopyOrMove(1);
        Toast.makeText(this, "已剪切", Toast.LENGTH_SHORT).show();

        menu.findItem(R.id.action_paste).setVisible(true);
    }

    // 复制文件按钮的点击事件
    private void copy() {
        // 如果没有选中文件，提示用户
        ArrayList<ImageModel> sourceList = SelectionModel.getImageModelList();
        if (sourceList.size() == 0) {
            Toast.makeText(this, "请选择要复制的文件", Toast.LENGTH_SHORT).show();
            return;
        }

        SelectedModel.setSourcePath(sourceList);
        SelectedModel.setWaitingPasteNum(sourceList.size());
        SelectedModel.setCopyOrMove(0);
        Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show();

        menu.findItem(R.id.action_paste).setVisible(true);
    }

    // 粘贴文件按钮的点击事件
    private void paste() {
        // 如果没有复制或剪切，提示用户
        if (SelectedModel.getCopyOrMove() == -1) {
            Toast.makeText(this, "请先复制或剪切", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentPath = gridImageFragment.getCurrentDir();
        SelectedModel.pasteImage(currentPath);
        if (SelectedModel.getHavePastedNum() == SelectedModel.getWaitingPasteNum()) {
            Toast.makeText(this, "粘贴成功", Toast.LENGTH_SHORT).show();
        }

        // 隐藏粘贴按钮
        menu.findItem(R.id.action_paste).setVisible(false);
        // 刷新当前页面
        gridImageFragment.refresh(currentPath);
    }

    // 取消选择按钮的点击事件
    private void rename() {
        // 获取选中的文件
        ArrayList<ImageModel> sourceList = SelectionModel.getImageModelList();
        // 如果没有选中文件，提示用户
        if (sourceList.size() == 0) {
            Toast.makeText(this, "请选择要重命名的文件", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sourceList.size() > 1) {
            SelectedModel.setSourcePath(sourceList);
        } else {
            SelectedModel.setSourcePath(sourceList.get(0));
        }

        // 弹出一个对话框，输入新的文件名
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("重命名");
        builder.setMessage("请输入新的文件名");
        EditText editText = new EditText(this);
        // 去掉后缀
        editText.setText(sourceList.get(0).getName().substring(0, sourceList.get(0).getName().lastIndexOf(".")));
        builder.setView(editText);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String newName = editText.getText().toString();

            // 如果文件名为空，提示用户
            if (newName.equals("")) {
                Toast.makeText(this, "文件名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            // 添加后缀
            newName += sourceList.get(0).getName().substring(sourceList.get(0).getName().lastIndexOf("."));

            if (SelectedModel.renameImage(newName)) {
                Toast.makeText(this, "重命名成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "重命名失败", Toast.LENGTH_SHORT).show();
            }

            SelectedModel.setCopyOrMove(-1);
            menu.findItem(R.id.action_paste).setVisible(false);
            // 刷新当前页面
            gridImageFragment.refresh();
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    // 反选事件
    private void select_reverse() {
        gridImageFragment.setSelectReverse();
        if (SelectionModel.getImageModelList().size() == 0) {
            menu.findItem(R.id.action_cnacel_select).setVisible(false);
        } else {
            menu.findItem(R.id.action_cnacel_select).setVisible(true);
        }
    }

    // 全选事件
    private void select_all() {
        gridImageFragment.setSelectAll();
        menu.findItem(R.id.action_cnacel_select).setVisible(true);
    }

    // 删除选中的文件
    private void delete_select() {
        gridImageFragment.setSelectDelete();
    }

    // 取消选中
    private void cancel_select() {
        gridImageFragment.setSelectNone();
        menu.findItem(R.id.action_cnacel_select).setVisible(false);
    }

    // 设置标题
    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setCurrentPage(int page) {
        this.pager2.setCurrentItem(page, true);
    }

    @Override
    protected void onDestroy() {
        mediator.detach();
        pager2.unregisterOnPageChangeCallback(changeCallback);
        super.onDestroy();
    }

    /**
     * 申请存储权限
     */
    private void apply_for_storage_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // 弹出对话框, 请求用户授予权限
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("需要授予存储权限才能使用本应用");
                builder.setPositiveButton("确定", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                });
                builder.setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
                builder.show();
            }
        } else {
            // android 10 以下申请权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 弹出对话框, 请求用户授予权限
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("需要授予存储权限才能使用本应用");
                builder.setPositiveButton("确定", (dialog, which) -> {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                });
                builder.setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
                builder.show();
            }
        }
    }
}