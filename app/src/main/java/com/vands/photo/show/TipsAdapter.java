package com.vands.photo.show;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vands.photo.R;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.Holder> {

    String [] tips;
    public TipsAdapter(String[] tips) {
        this.tips = tips;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tip_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TipsAdapter.Holder holder, int position) {
        holder.title.setTextSize(30);
        holder.title.getPaint().setFakeBoldText(true);
        holder.title.setText("tip " + (position + 1) + ":");
        holder.textView.setTextSize(20);
        holder.textView.setText(tips[position]);
    }

    @Override
    public int getItemCount() {
        return tips.length;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView title;
        TextView textView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tip_title);
            textView = itemView.findViewById(R.id.tip_text);
        }
    }
}
