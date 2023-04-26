package com.vands.photo.model;

import java.util.Comparator;

// 用于排序, 按照文件名排序
public class SortByName implements Comparator<ImageModel> {
    @Override
    public int compare(ImageModel o1, ImageModel o2) {
        // 按照文件名排序
        return o1.getImageName().compareTo(o2.getImageName());
    }
}
