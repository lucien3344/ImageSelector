package com.lucien3344.imageselector.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.config.CameraConfig;
import com.lucien3344.imageselector.config.ImageConfig;
import com.lucien3344.imageselector.objet.Image;
import com.lucien3344.imageselector.ui.CameraActivity;
import com.lucien3344.imageselector.ui.ImageListActivity;
import com.lucien3344.imageselector.ui.ImageViewPagerActivity;

import java.io.File;
import java.io.Serializable;
import java.util.List;


/**
 * Activity 跳转util
 *
 * @author lsh_2012@qq.com
 * @date 19/4/6..
 */

public class ActivityUtil {

    /***
     *  打开照相机
     * @param content
     * @param config  打开照相机的配置参数
     * @param   RequestCode 返回值
     */
    public static void startCameraActivity(Object content, CameraConfig config, int RequestCode) {
        Intent intent = null;
        if (content instanceof Activity) {
            intent = new Intent((Activity) content, CameraActivity.class);
            intent.putExtra("config", config);
            ((Activity) content).startActivityForResult(intent, RequestCode);
            ((Activity) content).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (content instanceof Fragment) {
            intent = new Intent(((Fragment) content).getActivity(), CameraActivity.class);
            intent.putExtra("config", config);
            ((Fragment) content).startActivityForResult(intent, RequestCode);
            ((Fragment) content).getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (content instanceof android.app.Fragment) {
            intent = new Intent(((android.app.Fragment) content).getActivity(), CameraActivity.class);
            intent.putExtra("config", config);
            ((android.app.Fragment) content).startActivityForResult(intent, RequestCode);
        }
    }

    /***
     * 打开图片选择器
     * @param content
     * @param config   图片选择器的配置参数
     * @param RequestCode  返回值
     */
    public static void startImageListActivity(Object content, ImageConfig config, int RequestCode) {
        Intent intent = null;
        if (content instanceof Activity) {
            intent = new Intent((Activity) content, ImageListActivity.class);
            intent.putExtra("config", config);
            ((Activity) content).startActivityForResult(intent, RequestCode);
        } else if (content instanceof Fragment) {
            intent = new Intent(((Fragment) content).getActivity(), ImageListActivity.class);
            intent.putExtra("config", config);
            ((Fragment) content).startActivityForResult(intent, RequestCode);
        } else if (content instanceof android.app.Fragment) {
            intent = new Intent(((android.app.Fragment) content).getActivity(), ImageListActivity.class);
            intent.putExtra("config", config);
            ((android.app.Fragment) content).startActivityForResult(intent, RequestCode);
        }
        startIn(content);
    }

    /***
     *  查看大图
     * @param content
     * @param position 当前图片数组下标
     * @param images  图片对象数组
     * @param isDelete  是否带删除按钮
     * @param RequestCode 返回值
     */
    public static void showBigImageActivity(Object content, int position, List<Image> images, boolean isDelete, int RequestCode) {
        Intent intent = null;
        if (content instanceof Activity) {
            intent = new Intent((Activity) content, ImageViewPagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("imagelists", (Serializable) images);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            intent.putExtra("isDelete", isDelete);
            ((Activity) content).startActivityForResult(intent, RequestCode);
        } else if (content instanceof Fragment) {
            intent = new Intent(((Fragment) content).getActivity(), ImageViewPagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("imagelists", (Serializable) images);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            intent.putExtra("isDelete", isDelete);
            ((Fragment) content).startActivityForResult(intent, RequestCode);
        } else if (content instanceof android.app.Fragment) {
            intent = new Intent(((android.app.Fragment) content).getActivity(), ImageViewPagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("imagelists", (Serializable) images);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            intent.putExtra("isDelete", isDelete);
            ((android.app.Fragment) content).startActivityForResult(intent, RequestCode);
        }
        startIn(content);
    }

    /****
     *  启动图片 裁剪
     * @param content
     * @param imagePath
     * @param file
     * @param imageConfig
     * @param RequestCode
     */
    public static void StartCrop(Object content, Uri uri, String imagePath, File file, ImageConfig imageConfig, int RequestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", imageConfig.aspectX);
        intent.putExtra("aspectY", imageConfig.aspectY);
        intent.putExtra("outputX", imageConfig.outputX);
        intent.putExtra("outputY", imageConfig.outputY);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        if (content instanceof Activity) {
            ((Activity) content).startActivityForResult(intent, RequestCode);
        } else if (content instanceof Fragment) {
            ((Fragment) content).startActivityForResult(intent, RequestCode);
        } else if (content instanceof android.app.Fragment) {
            ((android.app.Fragment) content).startActivityForResult(intent, RequestCode);
        }
    }

    /***
     *  activity 启动动画
     * @param content
     */
    public static void startIn(Object content) {
        if (content instanceof Activity) {
            ((Activity) content).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (content instanceof Fragment) {
            ((Fragment) content).getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (content instanceof android.app.Fragment) {
            ((android.app.Fragment) content).getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }


    /***
     *  activity 退出动画
     * @param content
     */
    public static void startOut(Object content) {
        if (content instanceof Activity) {
            ((Activity) content).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (content instanceof Fragment) {
            ((Fragment) content).getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (content instanceof android.app.Fragment) {
            ((android.app.Fragment) content).getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
