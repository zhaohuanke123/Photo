package com.vands.photo.mainhome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vands.photo.DragSelectRecyclerView;
import com.vands.photo.R;
import com.vands.photo.model.ActivityUtil;
import com.vands.photo.model.ImageListModel;
import com.vands.photo.model.ImageModel;
import com.vands.photo.model.SelectionModel;
import com.vands.photo.show.ShowInTurnActivity;

import java.io.IOException;
import java.util.ArrayList;

public class GridImageFragment extends Fragment implements GridAdapter.Listener, LoaderManager.LoaderCallbacks<ArrayList<ImageModel>> {
    private static GridImageFragment instance;
    private String currentDir;
    private RecyclerView recyclerView;
    private DragSelectRecyclerView dragSelectRecyclerView;
    private GridAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivity mainActivity;
    private TextView showInfo_tv;

    public GridImageFragment() {
        ActivityUtil.activities.put(this.getClass().getSimpleName(), this);
        mainActivity = (MainActivity) ActivityUtil.activities.get(MainActivity.class.getSimpleName());
        instance = this;
    }

    public GridImageFragment(ArrayList<ImageModel> imageList) {

    }

    public static GridImageFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_image, container, false);


        if (dragSelectRecyclerView == null){
            dragSelectRecyclerView = view.findViewById(R.id.drag_select_recycler_view);
            dragSelectRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 3)); // 设置布局管理器
            adapter = new GridAdapter(this);
            dragSelectRecyclerView.setAdapter(adapter); // 设置适配器
        }

        return view;
    }


    public void setSelectAll() {
        adapter.setSelectAll();
    }

    public void setSelectNone() {
        adapter.setSelectNone();
    }

    public void setSelectReverse() {
        adapter.setSelectReverse();
    }

    public void setSelectDelete() {
        adapter.setSelectDelete();
    }

    // 刷新当前页面,使用当前目录
    public void refresh() {
        refresh(currentDir);
    }

    // 刷新当前页面,使用指定目录
    public void refresh(String currentDir) {
        if (currentDir == null) {
            Toast.makeText(mainActivity, "所选目录为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.currentDir == null)
            this.currentDir = currentDir;

        ArrayList<ImageModel> imageList = null;

        try {
            imageList = ImageListModel.initImgList(currentDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.setImageFiles(imageList);
    }

    // 刷新当前页面,使用指定图片文件列表
    public void refresh(ArrayList<ImageModel> imageList) {
        adapter.setImageFiles(imageList);
    }

    public ArrayList<ImageModel> getImageList() {
        return adapter.getImageFiles();
    }

    public void setImageList(ArrayList<ImageModel> imageList) {
        String TAG = "setImageList";
        Log.d(TAG, "setImageList: " + adapter);
        Log.d(TAG, "setImageList: " + dragSelectRecyclerView);
        if (adapter == null) {
            adapter = new GridAdapter(this);
        }
        adapter.setImageFiles(imageList);
    }

    public String getCurrentDir() {
        return currentDir;
    }

    // 设置当前目录
    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    public void sortImage(String mode) {
        if (currentDir == null) {
            Toast.makeText(mainActivity, "当前目录为空", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<ImageModel> imageList = ImageListModel.refreshList(currentDir, mode);

        adapter.setImageFiles(imageList);
    }

    public GridAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onClick(int index) {
        if (SelectionModel.hasSelected()) {
            adapter.toggleSelected(index);
        } else {
            Intent intent = new Intent(mainActivity, ShowInTurnActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("imageFiles", adapter.getImageFiles());
            intent.putExtras(bundle);
            intent.putExtra("position", index);
            intent.putExtra("isShow", 0);

            mainActivity.showOnTurnlauncher.launch(intent);
        }
    }

    @Override
    public void onLongClick(int index) {
        dragSelectRecyclerView.setDragSelectActive(true, index);
    }

    @Override
    public void onSelectionChanged(int count) {

    }

    @NonNull
    @Override
    public Loader<ArrayList<ImageModel>> onCreateLoader(int id, @Nullable Bundle args) {
        assert args != null;
        String TAG = "onCreateLoader";
        Log.d(TAG, "onCreateLoader: " + args.getString("path"));
        String path = args.getString("path");
        return new ImageLoadingTask(mainActivity, path);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<ImageModel>> loader, ArrayList<ImageModel> data) {
        setImageList(data);
        mainActivity.setCurrentPage(1);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<ImageModel>> loader) {
        // 重置
    }

    // 使用Loader 在后台加载图片
    private static class ImageLoadingTask extends AsyncTaskLoader<ArrayList<ImageModel>> {

        private String path; // 图片目录路径

        public ImageLoadingTask(@NonNull Context context, String path) {
            super(context);
            String TAG = "ImageLoadingTask";
            Log.d(TAG, "ImageLoadingTask: ");
            this.path = path;
        }

        @Nullable
        @Override
        public ArrayList<ImageModel> loadInBackground() {
            ArrayList<ImageModel> imageFiles;
            try {
                imageFiles = ImageListModel.initImgList(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String TAG = "loadInBackground";
            Log.d(TAG, "loadInBackground: " + imageFiles.size());
            System.out.println(imageFiles.size());
            return imageFiles;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        protected boolean onCancelLoad() {
            return super.onCancelLoad();
        }
    }
}