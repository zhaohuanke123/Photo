package com.vands.photo.show;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vands.photo.R;
import com.vands.photo.model.ImageModel;

// recyclerView 的适配器
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    private static final int TYPE_DETAIL = 4;
    private ImageModel imageFile;

    public DetailAdapter(ImageModel imageFile) {
        this.imageFile = imageFile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_image_mes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 0，2，4，6位置显示key，1，3，5，7位置显示value
        if (position % 2 == 0) {
            switch (position) {
                case 0:
                    holder.tv_key.setText("文件名:");
                    holder.tv_val.setText("");
                    break;
                case 2:
                    holder.tv_key.setText("文件路径:");
                    holder.tv_val.setText("");
                    break;
                case 4:
                    holder.tv_key.setText("文件大小:");
                    holder.tv_val.setText("");
                    break;
                case 6:
                    holder.tv_key.setText("文件修改时间:");
                    holder.tv_val.setText("");
                    break;
            }
        } else  {
            switch (position) {
                case 1:
                    holder.tv_key.setText("");
                    holder.tv_val.setText(imageFile.getName());
                    break;
                case 3:
                    holder.tv_key.setText("");
                    holder.tv_val.setText(imageFile.getPath());
                    break;
                case 5:
                    holder.tv_key.setText("");
                    holder.tv_val.setText(imageFile.getFormatSize());
                    break;
                case 7:
                    holder.tv_key.setText("");
                    holder.tv_val.setText(imageFile.getFormatModifyTime());
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return TYPE_DETAIL * 2;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_key;
        TextView tv_val;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_key = itemView.findViewById(R.id.tv_key);
            tv_val = itemView.findViewById(R.id.tv_val);
        }
    }
}

