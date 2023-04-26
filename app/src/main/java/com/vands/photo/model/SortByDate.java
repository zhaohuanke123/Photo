package com.vands.photo.model;

import java.util.Comparator;

// 用于排序, 按照时间排序
public class SortByDate implements Comparator<ImageModel> {
    @Override
    public int compare(ImageModel o1, ImageModel o2) {
        return Long.compare(o1.getImageLastModified(), o2.getImageLastModified());
    }
}
