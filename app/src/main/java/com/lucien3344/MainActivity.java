package com.lucien3344;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lucien3344.imageselector.GlideApp;
import com.lucien3344.imageselector.common.onclicklistener.OnSelectItemClickListener;
import com.lucien3344.imageselector.config.CameraConfig;
import com.lucien3344.imageselector.config.ImageConfig;
import com.lucien3344.imageselector.objet.Image;
import com.lucien3344.imageselector.ui.fragment.GridImageFragment;
import com.lucien3344.imageselector.ui.fragment.SelectImageFromFragment;
import com.lucien3344.imageselector.utils.ActivityUtil;
import com.lucien3344.imageselector.utils.DebugUtil;
import com.lucien3344.imageselector.utils.ImageLoader;
import com.lucien3344.imageselector.utils.StatusBarUtil;
import com.lucien3344.imageselector.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_HEARD = 3224;
    private static final int REQUEST_TAKE_PHOTO = 3225;
    private static final int REQUEST_LIST_CODE = 3226;
    private ImageView bg_image;
    private CircleImageView header_image;
    private GridImageFragment gridImage_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.compat(this, Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bg_image = (ImageView) findViewById(R.id.bg_image);
        header_image = (CircleImageView) findViewById(R.id.header_image);
        gridImage_fragment = (GridImageFragment) getSupportFragmentManager().findFragmentById(R.id.gridImage_fragment);
        ImageConfig config = new ImageConfig.Builder()
                // 是否多选
                .multiSelect(true)
                .needCrop(false)
                .rememberSelected(false)
                .cropSize(1, 1, 200, 200)
                // 第一个是否显示相机
                .needCamera(false)
                .checkResId(R.mipmap.btn_img_selected, R.mipmap.btn_img_unselected)
                .build();
        gridImage_fragment.setNumColumns(4);
        gridImage_fragment.setImageConfig(config);
    }

    public void onHeaderClick(View v) {
        SelectImageFromFragment fragment = SelectImageFromFragment.createInstance(new OnSelectItemClickListener() {
            @Override
            public void OnItemClick(View view, Object o) {
                int type = (int) o;
                switch (type) {
                    case 0:
                        CameraConfig cameraConfig = new CameraConfig.Builder()
                                .needCrop(false)
                                .cropSize(1, 1, 200, 200)
                                .build();
                        ActivityUtil.startCameraActivity(MainActivity.this, cameraConfig, REQUEST_TAKE_PHOTO);
                        break;
                    case 1:
                        ImageConfig config = new ImageConfig.Builder()
                                // 是否多选
                                .multiSelect(false)
                                .needCrop(false)
                                .cropSize(1, 1, 200, 200)
                                // 第一个是否显示相机
                                .needCamera(true)
                                .build();
                        ActivityUtil.startImageListActivity(MainActivity.this, config, REQUEST_CODE_HEARD);
                        break;
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "");
    }


    public void onSelectClick(View v) {
//        ImageConfig config = new ImageConfig.Builder()
//                .multiSelect(true)
//                // 是否记住上次选中记录
//                .rememberSelected(false)
//                // 第一个是否显示相机
//                .needCamera(true)
//                // 最大选择图片数量
//                .maxNum(1)
//                .checkResId(R.mipmap.btn_img_selected, R.mipmap.btn_img_unselected)
//                .build();
//        ActivityUtil.startImageListActivity(this, config, REQUEST_LIST_CODE);


        ArrayList<Image> images = new ArrayList<>();
        images.add(new Image("https://pic.koudaihk.com/server/public/static/upload/xiaoMei/xiaoMei2019122710210713286.jpg", ""));
        images.add(new Image("https://pic.koudaihk.com/server/public/static/upload/xiaoMei/xiaoMei20191227102108643WK.jpg", ""));
        images.add(new Image("https://pic.koudaihk.com/server/public/static/upload/xiaoMei/xiaoMei2019122710210798443.jpg", ""));
        images.add(new Image("https://pic.koudaihk.com/server/public/static/upload/xiaoMei/xiaoMei201912271021088862T.jpg", ""));
        images.add(new Image("https://pic.koudaihk.com/server/public/static/upload/xiaoMei/xiaoMei20191227102109176W9.jpg", ""));
        images.add(new Image("https://pic.koudaihk.com/server/public/static/upload/xiaoMei/xiaoMei2019122710210798443.jpg", ""));
        ActivityUtil.showBigImageActivity(this, 1, images, false, 0);
    }


    /* 选择了图片之后回调的方法 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_HEARD: {
                List<String> pathList = data.getStringArrayListExtra("result");
                for (String path : pathList) {
                    DebugUtil.e(path);
                }
                ImageLoader.load(pathList.get(0), header_image);
                break;
            }
            case REQUEST_TAKE_PHOTO: {
                String path = data.getStringExtra("result");
                ImageLoader.load(path, header_image);
                break;
            }
            case REQUEST_LIST_CODE: {
                List<String> pathList = data.getStringArrayListExtra("result");
                for (String path : pathList) {
                    DebugUtil.e(path);
                }
                ImageLoader.load(pathList.get(0), bg_image);
                StatusBarUtil.compatTransStatusBar(this, Color.TRANSPARENT);
                break;
            }
        }
    }

}
