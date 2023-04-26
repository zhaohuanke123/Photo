package com.vands.photo.mainhome;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.vands.photo.IDragSelectAdapter;
import com.vands.photo.R;
import com.vands.photo.model.ActivityUtil;
import com.vands.photo.model.ImageModel;
import com.vands.photo.model.SelectionModel;
import com.vands.photo.show.ShowInTurnActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class GridAdapter extends RecyclerView.Adapter<GridAdapter.MainViewHolder>
        implements IDragSelectAdapter {

    private ArrayList<ImageModel> imageList = new ArrayList<>();
    private MainActivity mainActivity;

    private ViewPager2 viewPager2;
    private final Listener callback;

    GridAdapter(Listener callback) {
        super();
        mainActivity = (MainActivity) ActivityUtil.activities.get(MainActivity.class.getSimpleName());
        viewPager2 = (ViewPager2) ActivityUtil.activities.get(ShowInTurnActivity.class.getSimpleName());
        this.callback = callback;
        SelectionModel.initSelection(imageList.size());
    }

    public void setImageFiles(ArrayList<ImageModel> imageList) {
        if (imageList == null) {
            return;
        }
        this.imageList = imageList;
        SelectionModel.initSelection(imageList.size());
        notifyDataSetChanged();
    }
    public ArrayList<ImageModel> getImageFiles() {
        return imageList;
    }

    void toggleSelected(int index) {
        SelectionModel.change(index,imageList.get(index));
        notifyItemChanged(index);
        if (callback != null) {
            callback.onSelectionChanged(SelectionModel.getImageModelList().size());
        }
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new MainViewHolder(v, callback);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        // 绑定数据
        File file = imageList.get(position).getFile();
        holder.textView.setText(file.getName());
        // 设置图片显示的宽高
        holder.imageView.setLayoutParams(new ViewGroup.LayoutParams(mainActivity.getResources().getDisplayMetrics().widthPixels / 3, 360));
        // 设置纵横比缩放完全覆盖ImageView
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.imageView).load(file).into(holder.imageView);

        if (SelectionModel.contains(position)) {
            // 设置边框 R.drawable.border
            holder.imageView.setBackgroundResource(R.drawable.border);
            holder.imageView.setAlpha(0.5f);
        } else {
            holder.imageView.setBackgroundResource(0);
            holder.imageView.setAlpha(1f);
        }
    }

    public void setSelectAll() {
        SelectionModel.selectAll(imageList);
        for (int i = 0; i < imageList.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public void setSelectNone() {
        SelectionModel.removeAll(imageList.size());
        for (int i = 0; i < imageList.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public void setSelectReverse() {
        SelectionModel.selectReverse(imageList);
        for (int i = 0; i < imageList.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public void setSelectDelete() {
        // 弹出确认删除对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("删除图片");
        builder.setMessage("确认删除选中的图片吗？");
        builder.setPositiveButton("确认", (dialog, which) -> deleteSelected());
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.show();
    }

    private void deleteSelected() {
        ArrayList<ImageModel> tmpFiles = SelectionModel.getImageModelList();

        if (tmpFiles.size() == 0) {
            Toast.makeText(mainActivity, "请选择要删除的图片", Toast.LENGTH_SHORT).show();
            return;
        }

        for (ImageModel imageFile : tmpFiles) {
            imageList.remove(imageFile);
            imageFile.getFile().delete();
        }

        SelectionModel.removeAll(imageList.size());

        Toast.makeText(mainActivity, "删除成功", Toast.LENGTH_SHORT).show();

        notifyDataSetChanged();
    }

    @Override
    public void setSelected(int index, boolean selected) {
        Log.d("MainAdapter", "setSelected(" + index + ", " + selected + ")");
        if (!selected) {
            SelectionModel.remove(index, imageList.get(index));
        } else if (!SelectionModel.contains(index)) {
            SelectionModel.add(index, imageList.get(index));
        }
        notifyItemChanged(index);
        if (callback != null) {
            callback.onSelectionChanged(SelectionModel.getImageModelList().size());
        }
    }

    @Override
    public boolean contains(int index) {
        return SelectionModel.contains(index);
    }

    @Override
    public boolean isIndexSelectable(int index) {
        return true;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    interface Listener {
        void onClick(int index);

        void onLongClick(int index);

        void onSelectionChanged(int count);
    }

    static class MainViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final Listener callback;
        ImageView imageView;
        TextView textView;

        MainViewHolder(View itemView, Listener callback) {
            super(itemView);
            this.callback = callback;
            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }

        @Override
        public void onClick(View v) {
            if (callback != null) {
                callback.onClick(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (callback != null) {
                callback.onLongClick(getAdapterPosition());
            }
            return true;
        }
    }
}
