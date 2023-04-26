package com.vands.photo.model;

import com.vands.photo.mainhome.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 存放已选图片的工具类。
 * 包含一个列表{@link ArrayList}，和一些对已选图片的显示状态的更改
 */
public class SelectionModel {
    private static ArrayList<Boolean> selection = new ArrayList<>();

    // 待复制的图片列表
    private static ArrayList<ImageModel> toCopyList = new ArrayList<>();
    private static ArrayList<ImageModel> imageModelList = new ArrayList<>(); //存放选中的图片本身，作为后续复制粘贴等批量操作的源

    private static MainActivity mainActivity;

    public static void initSelection() {
        mainActivity = (MainActivity) ActivityUtil.activities.get(MainActivity.class.getSimpleName());
    }

    // 初始化选中状态
    public static void initSelection(int num) {
        selection.clear();
        imageModelList.clear();
        for (int i = 0; i < num; i++) {
            selection.add(false);
        }
        mainActivity.setTitle("| 已选中 " + imageModelList.size() + " 张");
    }

    public static ArrayList<ImageModel> getImageModelList() {
        return imageModelList;
    }

    // 改变选中状态
    public static void change(int positon, ImageModel im) {
        if (contains(positon)) {
            selection.set(positon, false);
            imageModelList.remove(im);
        } else {
            selection.set(positon, true);
            imageModelList.add(im);
        }
        try {
            mainActivity.setTitle("| 已选中 " + imageModelList.size() + " 张");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void add(int positon, ImageModel im) {
        selection.set(positon, true);
        imageModelList.add(im);
        try {
            mainActivity.setTitle("| 已选中 " + imageModelList.size() + " 张");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void remove(int positon, ImageModel im) {
//        selection.remove(positon);
        selection.set(positon, false);
        imageModelList.remove(im);
        try {
            mainActivity.setTitle("| 已选中 " + imageModelList.size() + " 张");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        selection.replaceAll(ignored -> false);
        imageModelList.clear();
        try {
            mainActivity.setTitle("| 已选中 " + imageModelList.size() + " 张");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Describe: 移除所有选中状态
     */
    public static void removeAll(int listSize) {
        if (listSize != -1) {
            selection.clear();
            for (int i = 0; i < listSize; i++) {
                selection.add(false);
            }
        } else {
            Collections.fill(selection, false);
        }
        imageModelList.clear();
        try {
            mainActivity.setTitle("| 已选中 " + imageModelList.size() + " 张");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Describe: 选中所有图片
     * @param imageList 图片列表
     */
    public static void selectAll(ArrayList<ImageModel> imageList) {
        imageModelList.clear();
        for (int i = 0; i < imageList.size(); i++) {
            selection.set(i, true);
            imageModelList.add(imageList.get(i));
        }
        try {
            mainActivity.setTitle("| 已选中 " + imageModelList.size() + " 张");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Describe: 反选所有图片
     * @param imageList 图片列表
     */
    public static void selectReverse(ArrayList<ImageModel> imageList) {
        imageModelList.clear();

        assert selection.size() == imageList.size();

        for (int i = 0; i < selection.size(); i++) {
            selection.set(i, !selection.get(i));
            if (selection.get(i)) {
                imageModelList.add(imageList.get(i));
            }
        }
        try {
            mainActivity.setTitle("| 已选中 " + imageModelList.size() + " 张");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Describe: 判断是否有图片被选中
     */
    public static boolean hasSelected() {
        return !imageModelList.isEmpty();
    }

    /**
     * @Describe: 判断是否有图片被选中
     */
    public static boolean contains(int position) {
        if (selection == null)
            return false;
        if (position < 0 || position >= selection.size()) {
            return false;
        }

        return selection.get(position);
    }
}

