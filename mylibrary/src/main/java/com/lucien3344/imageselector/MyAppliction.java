package com.lucien3344.imageselector;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.lucien3344.imageselector.utils.DebugUtil;

public class MyAppliction extends Application {


    /**
     * Vector兼容5.0以下系统
     */
    static {
        /*获取当前系统的android版本号*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 21)//适配android5.0以下
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static MyAppliction mInstance;

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initPhotoError();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        DebugUtil.e("MainApp_onTrimMemory (" + level + ")");
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        DebugUtil.e("MainApp_onLowMemory");
        Glide.get(this).clearMemory();
    }

    public static Application getInstance() {
        if (null == mInstance) {
            mInstance = new MyAppliction();
        }
        return mInstance;
    }

}
