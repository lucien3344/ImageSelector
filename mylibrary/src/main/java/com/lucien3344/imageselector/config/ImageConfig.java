package com.lucien3344.imageselector.config;

import android.os.Environment;

import com.lucien3344.imageselector.utils.FileUtils;

import java.io.Serializable;

public class ImageConfig implements Serializable {

    /**
     * 是否需要裁剪
     */
    public boolean needCrop;

    /**
     * 是否多选
     */
    public boolean multiSelect = false;

    /**
     * 是否记住上次的选中记录(只对多选有效)
     */
    public boolean rememberSelected = true;

    /**
     * 最多选择图片数
     */
    public int maxNum = 9;

    /**
     * 第一个item是否显示相机
     */
    public boolean needCamera;

    /**
     * 拍照存储路径
     */
    public String filePath;

    /**
     * 裁剪输出大小
     */
    public int aspectX = 1;
    public int aspectY = 1;
    public int outputX = 500;
    public int outputY = 500;
    public int checkedResId = -1;
    public int checkResId = -1;

    public ImageConfig(Builder builder) {
        this.needCrop = builder.needCrop;
        this.multiSelect = builder.multiSelect;
        this.rememberSelected = builder.rememberSelected;
        this.maxNum = builder.maxNum;
        this.needCamera = builder.needCamera;
        this.filePath = builder.filePath;
        this.aspectX = builder.aspectX;
        this.aspectY = builder.aspectY;
        this.outputX = builder.outputX;
        this.outputY = builder.outputY;
        this.checkResId = builder.checkResId;
        this.checkedResId = builder.checkedResId;
    }

    public static class Builder implements Serializable {

        private boolean needCrop = false;
        private boolean multiSelect = true;
        private boolean rememberSelected = true;
        private int maxNum = 9;
        private boolean needCamera = true;
        private String filePath;
        private int aspectX = 1;
        private int aspectY = 1;
        private int outputX = 400;
        private int outputY = 400;
        private int checkResId = -1;
        private int checkedResId = -1;

        public Builder() {
            if (FileUtils.isSdCardAvailable())
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera";
            else
                filePath = Environment.getRootDirectory().getAbsolutePath() + "/Camera";
            FileUtils.createDir(filePath);
        }

        public Builder needCrop(boolean needCrop) {
            this.needCrop = needCrop;
            return this;
        }

        public Builder multiSelect(boolean multiSelect) {
            this.multiSelect = multiSelect;
            return this;
        }

        public Builder rememberSelected(boolean rememberSelected) {
            this.rememberSelected = rememberSelected;
            return this;
        }

        public Builder maxNum(int maxNum) {
            this.maxNum = maxNum;
            return this;
        }

        public Builder needCamera(boolean needCamera) {
            this.needCamera = needCamera;
            return this;
        }

        public Builder checkResId(int checkedResId, int checkResId) {
            this.checkedResId = checkedResId;
            this.checkResId = checkResId;
            return this;
        }


        private Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder cropSize(int aspectX, int aspectY, int outputX, int outputY) {
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            this.outputX = outputX;
            this.outputY = outputY;
            return this;
        }

        public ImageConfig build() {
            return new ImageConfig(this);
        }
    }
}
