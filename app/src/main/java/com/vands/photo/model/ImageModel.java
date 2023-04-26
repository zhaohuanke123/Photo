package com.vands.photo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * @Describe: 图片的实体类
 * 实现Parcelable接口，使得ImageModel对象可以被序列化
 */
public class ImageModel implements Parcelable {
    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };
    public File file;

    public ImageModel(File imageFile) {
        this.file = imageFile;
    }

    protected ImageModel(Parcel in) {
        file = (File) in.readValue(File.class.getClassLoader());
    }

    public String getFormatSize() {
        long totalSize = file.length();
        return GenUtilsModel.getFormatSize(totalSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(file);
    }

    public String getImageName() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }

    public long getFileLength() {
        return file.length();
    }

    public long getImageLastModified() {
        return file.lastModified();
    }

    public String getName() {
        return file.getName();
    }

    public String getPath() {
        return file.getPath();
    }

    public String getFormatModifyTime() {
        // 获取文件的修改时间
        // 将时间转换为字符串
        return GenUtilsModel.getFormatTime(file.lastModified());
    }

    public String getFileName() {
        return file.getName();
    }
}