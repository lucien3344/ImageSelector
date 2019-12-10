package com.lucien3344.imageselector.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.common.onclicklistener.OnGridImageClickListener;
import com.lucien3344.imageselector.common.onclicklistener.OnSelectItemClickListener;
import com.lucien3344.imageselector.config.CameraConfig;
import com.lucien3344.imageselector.config.ImageConfig;
import com.lucien3344.imageselector.objet.Image;
import com.lucien3344.imageselector.ui.adapter.GridImageAdapter;
import com.lucien3344.imageselector.utils.ActivityUtil;
import com.lucien3344.imageselector.utils.DebugUtil;
import com.lucien3344.imageselector.view.DragGridView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 9宫格图片显示器/添加
 *
 * @author lsh_2012@qq.com
 * on 2019/10/27.
 */
public class GridImageFragment extends Fragment {


    private static final int REQUEST_LIST_CODE = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_BIG_IMAGE = 3333;
    private DragGridView drag_gridView;
    private GridImageAdapter gridImageAdapter;
    private boolean isAddImage = true;
    private boolean hisAddImage = true;
    private List<Image> imageList = new ArrayList<>();
    private ImageConfig imageConfig = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridimage, container, false);
        drag_gridView = (DragGridView) view.findViewById(R.id.drag_gridView);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageList.clear();
        gridImageAdapter = new GridImageAdapter(getContext(), imageList, isAddImage);
        drag_gridView.setAdapter(gridImageAdapter);
        hisAddImage = true;
        gridImageAdapter.setOnGridImageClickListener(new OnGridImageClickListener() {
            @Override
            public void onDeleteClick(int position, Image image) {
                imageList.remove(image);
                if (imageList.size() < imageConfig.maxNum && !hisAddImage) {
                    imageList.add(new Image("*", "*"));
                    hisAddImage = true;
                }
                gridImageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAddClick(int position, List<Image> images) {
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
                                ActivityUtil.startCameraActivity(GridImageFragment.this, cameraConfig, REQUEST_TAKE_PHOTO);
                                break;
                            case 1:
                                ImageConfig config = new ImageConfig.Builder()
                                        // 是否多选
                                        .multiSelect(imageConfig.multiSelect)
                                        .needCrop(imageConfig.needCrop)
                                        .rememberSelected(imageConfig.rememberSelected)
                                        .maxNum(getResidueImage())
                                        .cropSize(imageConfig.aspectX, imageConfig.aspectY, imageConfig.outputX, imageConfig.outputY)
                                        // 第一个是否显示相机
                                        .needCamera(imageConfig.needCamera)
                                        .checkResId(imageConfig.checkedResId, imageConfig.checkResId)
                                        .build();
                                ActivityUtil.startImageListActivity(GridImageFragment.this, config, REQUEST_LIST_CODE);
                                break;
                        }
                    }
                });
                fragment.show(getChildFragmentManager(), "");
            }

            @Override
            public void onImagesClick(int position, List<Image> images) {
                if (hisAddImage) {
                    images.remove(new Image("*", "*"));
                }
                ActivityUtil.showBigImageActivity(GridImageFragment.this, position, images, true,REQUEST_BIG_IMAGE);

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO: {
                String path = data.getStringExtra("result");
                String name = path.substring(path.lastIndexOf("/") + 1, path.length());
                imageList.add(0, new Image(path, name));
                if (imageList.size() > imageConfig.maxNum) {
                    imageList.remove(new Image("*", "*"));
                    hisAddImage = false;
                }
            }
            DebugUtil.e(" , " + REQUEST_TAKE_PHOTO);
            break;
            case REQUEST_LIST_CODE: {
                List<String> pathList = data.getStringArrayListExtra("result");
                for (String path : pathList) {
                    String name = path.substring(path.lastIndexOf("/") + 1, path.length());
                    imageList.add(0, new Image(path, name));
                }
                if (imageList.size() > imageConfig.maxNum) {
                    imageList.remove(new Image("*", "*"));
                    hisAddImage = false;
                }
            }
            DebugUtil.e(" , " + REQUEST_LIST_CODE);
            break;
            case REQUEST_BIG_IMAGE: {
                List<Image> images = (List<Image>) data.getSerializableExtra("result");
                imageList.clear();
                imageList.addAll(images);
            }
            if (imageList.size() < imageConfig.maxNum) {
                imageList.add(new Image("*", "*"));
                hisAddImage = true;
            }
            DebugUtil.e(" , " + REQUEST_BIG_IMAGE);
            break;
        }
        gridImageAdapter.notifyDataSetChanged();
    }

    /***
     *  剩余可添加图片数
     * @return
     */
    private int getResidueImage() {
        return imageConfig.maxNum - imageList.size() + 1;
    }


    /***
     * 设置DragGridView一行显示数量
     * @param numColumns
     */
    public void setNumColumns(int numColumns) {
        drag_gridView.setNumColumns(numColumns);
    }

    public void setImageConfig(ImageConfig config) {
        imageConfig = config;
    }
}
