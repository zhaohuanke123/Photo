package com.vands.photo.mainhome;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vands.photo.R;
import com.vands.photo.model.ActivityUtil;
import com.vands.photo.model.GenUtilsModel;
import com.vands.recyclerlisttreeview.ListTree;

import java.io.File;

public class DirTreeFragment extends Fragment {

    private RecyclerView treeView;
    private DirTreeAdapter dirTreeAdapter;

    private MainActivity mainActivity;

    public DirTreeFragment() {
        mainActivity = (MainActivity) ActivityUtil.activities.get(MainActivity.class.getSimpleName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dir_tree, container, false);

        treeView = view.findViewById(R.id.dir_tree);


        Loader<ListTree> loader = LoaderManager.getInstance(this).restartLoader(1,null, new LoaderManager.LoaderCallbacks<ListTree>() {
            @NonNull
            @Override
            public Loader<ListTree> onCreateLoader(int id, Bundle args) {
                return new DirTreeLoader(mainActivity);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<ListTree> loader, ListTree data) {
                treeView.setLayoutManager(new LinearLayoutManager(DirTreeFragment.this.getContext()));
                dirTreeAdapter = new DirTreeAdapter(data);
                treeView.setAdapter(dirTreeAdapter);
            }

            @Override
            public void onLoaderReset(@NonNull Loader<ListTree> loader) {

            }
        });

        return view;
    }

    private static class DirTreeLoader extends Loader<ListTree> {
        private ListTree listTree;
        private GridImageFragment gridImageFragment;

        public DirTreeLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (listTree != null) {
                deliverResult(listTree);
            } else {
                forceLoad();
            }
        }

        @Override
        protected void onForceLoad() {
            super.onForceLoad();
            String root = Environment.getExternalStorageDirectory().toString();
            listTree = new ListTree();
            GenUtilsModel.initDirTree(new File(root), null, listTree);
            deliverResult(listTree);
        }
    }
}