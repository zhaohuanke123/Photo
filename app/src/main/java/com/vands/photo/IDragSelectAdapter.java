package com.vands.photo;

public interface IDragSelectAdapter {
    void setSelected(int index, boolean selected);

    boolean isIndexSelectable(int index);

    int getItemCount();

    public boolean contains(int index);
}
