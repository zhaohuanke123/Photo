package com.vands.photo.model;

import java.util.Comparator;

// 用于排序, 按照文件大小排序
public class SortBySize implements Comparator<ImageModel> {
    @Override
    public int compare(ImageModel o1, ImageModel o2) {
        return Long.compare(o1.getFileLength(), o2.getFileLength());
    }
}
