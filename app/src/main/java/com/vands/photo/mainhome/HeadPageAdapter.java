package com.vands.photo.mainhome;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vands.photo.mainhome.DirTreeFragment;
import com.vands.photo.mainhome.GridImageFragment;
import com.vands.photo.model.ImageModel;
import com.vands.recyclerlisttreeview.ListTree;

import java.util.ArrayList;

// fragment
public class HeadPageAdapter extends FragmentStateAdapter {

    private ArrayList<Boolean> mSelected;
    private ListTree listTree;
    private ArrayList<ImageModel> mImageList; // 复制的图片列表

    private GridImageFragment gridImageFragment;
    private DirTreeFragment dirTreeFragment;

    public HeadPageAdapter(@NonNull FragmentActivity fragmentActivity,GridImageFragment gridImageFragment,DirTreeFragment dirTreeFragment) {
        super(fragmentActivity);
        this.gridImageFragment = gridImageFragment;
        this.dirTreeFragment = dirTreeFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return dirTreeFragment;
            case 1:
                return gridImageFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
