package com.lucien3344.imageselector.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lucien3344.imageselector.MyAppliction;


/***
 *图片加载 工具类
 */
public class ImageLoader {

    /***
     *  加载图片
     * @param path  图片路径
     * @param imageView  显示的图片view
     */
    public static void load(String path, ImageView imageView) {
        Glide.with(MyAppliction.getInstance().getApplicationContext())
                .load(path)
                .apply(new RequestOptions().centerCrop())
                .into(imageView);
    }

    /**
     * 加载图片
     *
     * @param path             图片路径
     * @param imageView        显示的图片view
     * @param placeholderResId 预加载显示的图片resid
     */
    public static void load(String path, ImageView imageView, int placeholderResId) {
        Glide.with(MyAppliction.getInstance().getApplicationContext())
                .load(path)
                .apply(new RequestOptions().centerCrop().placeholder(placeholderResId))
                .into(imageView);
    }

    /**
     * 加载图片
     *
     * @param path             图片路径
     * @param imageView        显示的图片view
     * @param placeholderResId 预加载显示的图片resid
     * @param errorResId       加载失败显示的图片resid
     */
    public static void load(String path, ImageView imageView, int placeholderResId, int errorResId) {
        Glide.with(MyAppliction.getInstance().getApplicationContext())
                .load(path)
                .apply(new RequestOptions().centerCrop().placeholder(placeholderResId).error(errorResId))
                .into(imageView);
    }


}
