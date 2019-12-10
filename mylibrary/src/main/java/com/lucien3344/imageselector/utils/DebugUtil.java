package com.lucien3344.imageselector.utils;

import android.util.Log;

import com.lucien3344.imageselector.BuildConfig;

/**
 * @author lsh_2012@qq.com
 * on 2018/11/22.
 */
public class DebugUtil {
    private static final String TAG = "DebugUtil";

    public static void d(String msg) {
        if (BuildConfig.isDebug)
            Log.d(getTag(), getCurrentClassName() + getCurrentMethodName() + msg);
    }

    public static void i(String msg) {
        if (BuildConfig.isDebug)
            Log.i(getTag(), getCurrentClassName() + getCurrentMethodName() + msg);
    }

    public static void e(String msg) {
        if (BuildConfig.isDebug)
            Log.e(getTag(), getCurrentClassName() + getCurrentMethodName() + msg);
    }

    public static void w(String msg) {
        if (BuildConfig.isDebug)
            Log.w(getTag(), getCurrentClassName() + getCurrentMethodName() + msg);
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.isDebug)
            Log.e(tag, getCurrentClassName() + getCurrentMethodName() + msg);
    }

    //获取当前类名
    public static String getTag() {
        return TAG;
    }

    private static StackTraceElement getInvoker() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static String getCurrentMethodName() {
        int level = 2;//[1]是你当前方法执行堆栈,[2]就是上一级的方法堆栈 以此类推
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String methodName = stacks[level].getMethodName();
        return "[" + methodName + "方法()]： ";
    }

    public static String getCurrentClassName() {
        int level = 2;//[1]是你当前方法执行堆栈,[2]就是上一级的方法堆栈 以此类推
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String className = stacks[level].getClassName();
        return "->[" + className + "类]";
    }
}
