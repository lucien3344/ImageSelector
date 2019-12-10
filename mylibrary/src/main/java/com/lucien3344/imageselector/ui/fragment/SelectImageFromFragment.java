package com.lucien3344.imageselector.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.common.onclicklistener.OnSelectItemClickListener;


/**
 * 点击添加图片出现的选择界面，选择拍照或者选择图片
 *
 * @author lsh_2012@qq.com
 * on 2019/10/22.
 */
public class SelectImageFromFragment extends DialogFragment {
    private View mParent;
    private View mCamera;
    private View mGallery;
    private View mCancel;
    private OnSelectItemClickListener onSelectItemClickListener;

    public static SelectImageFromFragment createInstance(OnSelectItemClickListener onSelectItemClickListener) {
        SelectImageFromFragment selectImageFromFragment = new SelectImageFromFragment();
        Bundle bundle = new Bundle();
        selectImageFromFragment.setArguments(bundle);
        selectImageFromFragment.onSelectItemClickListener = onSelectItemClickListener;
        return selectImageFromFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = R.style.DownShowDialog;
        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 设置宽度占满屏幕
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mParent = inflater.inflate(R.layout.fragment_image_from, container, false);
        mCamera = mParent.findViewById(R.id.selector_camera);
        mGallery = mParent.findViewById(R.id.selector_gallery);
        mCancel = mParent.findViewById(R.id.tv_cancel);
        Log.e("", "onCreateView ");
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectItemClickListener != null) {
                    onSelectItemClickListener.OnItemClick(v, 0);
                }
                dismiss();
            }
        });
        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectItemClickListener != null) {
                    onSelectItemClickListener.OnItemClick(v, 1);
                }
                dismiss();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return mParent;
    }


}
