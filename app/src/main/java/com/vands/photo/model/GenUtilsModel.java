package com.vands.photo.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.vands.photo.mainhome.MainActivity;
import com.vands.photo.R;
import com.vands.recyclerlisttreeview.ListTree;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Describe: 通用工具类
 * 用于获取文件的格式化大小,格式化时间等
 */
public class GenUtilsModel {
    private static final double KB = 1024.0;
    private static final double MB = 1024.0 * 1024.0;
    private static final double GB = 1024.0 * 1024.0 * 1024.0;

    private static MainActivity mainActivity;

    public static void init() {
        mainActivity = (MainActivity) ActivityUtil.activities.get(MainActivity.class.getSimpleName());
    }


    // 获取文件的大小,并转换为标准单位
    @SuppressLint("DefaultLocale")
    public static String getFormatSize(long fileLength) {
        String Standardsize;
        if (fileLength < KB) {
            Standardsize = String.format("%d Byte", fileLength);
        } else if (fileLength < MB) {
            Standardsize = String.format("%.0f KB", fileLength / KB);
        } else if (fileLength < GB) {
            Standardsize = String.format("%.2f MB", fileLength / MB);
        } else {
            Standardsize = String.format("%.2f GB", fileLength / GB);
        }
        return Standardsize;
    }

    // 获取文件的修改时间，并转换为标准时间格式
    public static String getFormatTime(long time) {
        Date data = new Date(time);
        // 默认的时间格式是yyyy-MM-dd'T'HH:mm:ss.SSSZ，这里需要自定义时间格式
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(data);
    }

//    static byte[] getByteByFile(File file) {
//        try (FileInputStream fis = new FileInputStream(file);
//             ByteArrayOutputStream bos = new ByteArrayOutputStream(1024)) {
//            byte[] bytes = new byte[1024];
//            int i;
//            while ((i = fis.read(bytes)) != -1) {
//                bos.write(bytes, 0, i);
//            }
//            return bos.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    static boolean getFileByByte(byte[] bytes, File file) {
//        try (FileOutputStream fos = new FileOutputStream(file);
//             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
//            bos.write(bytes);
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public static void initDirTree(File root, ListTree.TreeNode node, ListTree listTree) {
        File[] files = root.listFiles();
        if (files == null) return;
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.startsWith(".") || fileName.equals("Android") || fileName.startsWith("com"))
                continue;
            if (file.isDirectory()) {
                ListTree.TreeNode addNode = listTree.addNode(node, file, R.layout.dir_tree_item);
                initDirTree(file, addNode, listTree);
            }
        }
    }

    // 获取用于设置壁纸的图片
    public static Bitmap getBitmapFromUri(ImageModel imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getFile().getAbsolutePath(), options);
        // 获取屏幕分辨率
        DisplayMetrics dm = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        File image = imageFile.getFile();
        // 获取图片文件的宽高
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        // 计算缩放比例
        int scaleX = imageWidth / screenWidth;
        int scaleY = imageHeight / screenHeight;
        int scale = 1;
        if (scaleX > scaleY && scaleY >= 1) {
            scale = scaleX;
        }
        if (scaleY > scaleX && scaleX >= 1) {
            scale = scaleY;
        }
        // 设置缩放比例
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(image.getAbsolutePath(), options);
    }

    // 计算图片的缩放值
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高比率最小的一个作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return inSampleSize;
    }
}
