package com.vands.photo.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;


/**
 * @Describe: 图片列表工具类
 */
public class ImageListModel {
    // 判断文件是否为图片 支持jpg/jpeg/png/gif/bmp
    public static boolean isSupportedImg(String fileName) {
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                fileName.endsWith(".bmp");
    }

    // 初始化图片列表
    public static ArrayList<ImageModel> initImgList(String path) throws IOException {
        ArrayList<ImageModel> imgList = new ArrayList<>(); // 默认根据name进行排序
        if (path == null || path.equals(""))
            return null;
        Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String fileName = file.getFileName().toString().toLowerCase();
                if (isSupportedImg(fileName)) {
                    imgList.add(new ImageModel(new File(path+ "/"+fileName))); // 获取绝对路径
                }
                return FileVisitResult.CONTINUE;
            }

            // 只访问当前文件夹 不进行递归访问
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (dir.toString().equals(path)) {
                    return FileVisitResult.CONTINUE;
                } else
                    return FileVisitResult.SKIP_SUBTREE;
            }

            // 处理访问系统文件的异常
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.SKIP_SUBTREE;
            }
        });
        imgList.sort(new SortByName());

        return imgList;
    }

    // 刷新文件夹 返回新列表
    public static ArrayList<ImageModel> refreshList(String path) {
        ArrayList<ImageModel> list;
        try {
            list = initImgList(path);
        } catch (IOException e) {
            return null;
        }
        return list;
    }

    // 带排序的刷新 对文件夹排序 mode->排序模式
    public static ArrayList<ImageModel> refreshList(String path, String mode) {
        ArrayList<ImageModel> list = refreshList(path);
        switch (mode) {
            case SortParam.SBND:
                assert list != null;
                Collections.reverse(list);
                return list;
            case SortParam.SBSR:
                assert list != null;
                list.sort(new SortBySize());
                return list;
            case SortParam.SBSD:
                assert list != null;
                list.sort(new SortBySize());
                Collections.reverse(list);
                return list;
            case SortParam.SBDR:
                assert list != null;
                list.sort(new SortByDate());
                return list;
            case SortParam.SBDD:
                assert list != null;
                list.sort(new SortByDate());
                Collections.reverse(list);
                return list;
        }
        return list;
    }
}