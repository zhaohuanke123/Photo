package com.vands.photo.mainhome;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.vands.photo.R;
import com.vands.photo.model.ActivityUtil;
import com.vands.photo.model.ImageListModel;
import com.vands.photo.model.ImageModel;
import com.vands.recyclerlisttreeview.ListTree;
import com.vands.recyclerlisttreeview.ListTreeAdapter;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class DirTreeAdapter extends ListTreeAdapter<DirTreeAdapter.BaseViewHolder> {
    private int clickNum = 0;
    private final Handler handler = new Handler();
    //记录弹出菜单是在哪个行上出现的
    private ListTree.TreeNode currentNode;
    private GridImageFragment gridImageFragment;
    private MainActivity mainActivity;

    public DirTreeAdapter(ListTree listTree) {
        super(listTree);
        // gridImageFragment = (GridImageFragment) ActivityUtil.activities.get(GridImageFragment.class.getSimpleName());
        gridImageFragment = GridImageFragment.getInstance();
        ActivityUtil.activities.put(this.getClass().getSimpleName(), this);
        mainActivity = (MainActivity) ActivityUtil.activities.get(MainActivity.class.getSimpleName());
    }

    public ListTree.TreeNode getCurrentNode() {
        return currentNode;
    }

    @Override
    protected BaseViewHolder onCreateNodeView(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //创建不同的行View
        if (viewType == R.layout.dir_tree_item) {
            //注意！此处有一个不同！最后一个参数必须传true！
            View view = inflater.inflate(viewType, parent, true);
            //用不同的ViewHolder包装
            return new GroupViewHolder(view);
        } else {
            return null;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindNodeViewHolder(BaseViewHolder holder, int position) {
        View view = holder.itemView;
        //get node at the position
        ListTree.TreeNode node = tree.getNodeByPlaneIndex(position);

        if (node.getLayoutResId() == R.layout.dir_tree_item) {
            //group node
            File file = (File) node.getData();

            GroupViewHolder gvh = (GroupViewHolder) holder;
            gvh.textViewFileName.setText(file.getName());
            Instant instant = Instant.ofEpochMilli(file.lastModified());
            LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    //文件行的Holder基类
    class BaseViewHolder extends ListTreeAdapter.ListTreeViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    class GroupViewHolder extends BaseViewHolder {
        MaterialButton textViewFileName;

        public GroupViewHolder(View itemView) {
            super(itemView);

            textViewFileName = itemView.findViewById(R.id.file_name);

            textViewFileName.setAllCaps(false);
            // 设置点击事件
            textViewFileName.setOnClickListener(v -> {
                // System.out.println("点击");
                // 更新点击的目录数据
                clickNum++;
                handler.postDelayed(() -> {
                    if (clickNum == 1) {
                        int planePos = getAdapterPosition();
                        if (planePos < 0) {
                            return;
                        }
                        String TAG = "GroupViewHolder";
                        Log.d(TAG, "onClick: " + planePos);
                        ListTree.TreeNode node = tree.getNodeByPlaneIndex(planePos);
                        currentNode = node;

                        if (node.isShowExpandIcon()) {
                            int nodePlaneIndex = tree.getNodePlaneIndex(node);
                            if (node.isExpand()) {
                                // 收起
                                int count = tree.collapseNode(nodePlaneIndex);
                                notifyItemChanged(nodePlaneIndex);
                                // 通知view将相关的行删掉
                                notifyItemRangeRemoved(nodePlaneIndex + 1, count);
                            } else {
                                // 展开
                                int count = tree.expandNode(nodePlaneIndex);
                                notifyItemChanged(nodePlaneIndex);
                                // 通知view插入相关的行
                                notifyItemRangeInserted(nodePlaneIndex + 1, count);
                            }
                        }
                    } else if (clickNum == 2) {
                        // 更新点击的目录数据
                        // 获取当前点击的行
                        int planePos = getAdapterPosition();
                        if (planePos < 0) {
                            String Tag = "GroupViewHolder";
                            Log.d(Tag, "onClick: " + currentNode.getData().toString());
                            return;
                        }
                        ListTree.TreeNode node = tree.getNodeByPlaneIndex(planePos);
                        currentNode = node;

                        // 弹出对话框，提醒用户是否打开该目录里的图片
                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                        builder.setTitle("打开目录");
                        builder.setMessage("是否打开该目录下的图片？");
                        builder.setPositiveButton("确定", (dialog, which) -> {
                            // 使用Loader 在后台加载图片
                            Bundle newArgs = new Bundle();
                            newArgs.putString("path", node.getData().toString());
                            String TAG = "GroupViewHolder";
                            Log.d(TAG, "GroupViewHolder: "+node.getData().toString());
                            Log.d(TAG, "onClick: " + gridImageFragment.getAdapter());
                            Log.d(TAG, "onClick: " + gridImageFragment);
                            LoaderManager.getInstance(mainActivity).restartLoader(0, newArgs, gridImageFragment);
                            gridImageFragment.setCurrentDir(node.getData().toString());

                            Toast.makeText(mainActivity, "打开目录成功", Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton("取消", (dialog, which) -> {
                            // 什么都不做
                        });
                        builder.show();
                    }
                    //防止handler引起的内存泄漏
                    handler.removeCallbacksAndMessages(null);
                    clickNum = 0;
                }, 180);
            });

        }
    }
}