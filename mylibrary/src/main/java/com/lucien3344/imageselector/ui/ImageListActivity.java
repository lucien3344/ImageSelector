package com.lucien3344.imageselector.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.common.Constant;
import com.lucien3344.imageselector.common.onclicklistener.OnFolderChangeListener;
import com.lucien3344.imageselector.common.onclicklistener.OnItemClickListener;
import com.lucien3344.imageselector.config.ImageConfig;
import com.lucien3344.imageselector.objet.Folder;
import com.lucien3344.imageselector.objet.Image;
import com.lucien3344.imageselector.ui.adapter.CustomViewPager;
import com.lucien3344.imageselector.ui.adapter.FolderListAdapter;
import com.lucien3344.imageselector.ui.adapter.ImageListAdapter;
import com.lucien3344.imageselector.ui.adapter.PreviewAdapter;
import com.lucien3344.imageselector.utils.ActivityUtil;
import com.lucien3344.imageselector.utils.DebugUtil;
import com.lucien3344.imageselector.utils.DisplayUtil;
import com.lucien3344.imageselector.utils.FileUtils;
import com.lucien3344.imageselector.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片选择
 *
 * @author lsh_2012@qq.com
 * on 2019/10/23.
 */
public class ImageListActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public static final String INTENT_RESULT = "result";
    private static final int IMAGE_CROP_CODE = 1;
    private static final int STORAGE_REQUEST_CODE = 1;
    private static final int LOADER_ALL = 10;
    private static final int LOADER_CATEGORY = 11;
    private static final int REQUEST_CAMERA = 12;
    private static final int CAMERA_REQUEST_CODE = 3;
    private ImageConfig imageConfig;
    private String cropImagePath;
    private RelativeLayout titleBarLayout;
    private ImageView img_back;
    private TextView tv_Title;
    private ImageView title_img;
    private Button btn_Confirm;
    private RecyclerView recyclerView;
    private CustomViewPager viewPager;

    private ArrayList<String> result = new ArrayList<>();
    private List<Folder> folderList = new ArrayList<>();
    private List<Image> imageList = new ArrayList<>();
    private ListPopupWindow folderPopupWindow;
    private ImageListAdapter imageListAdapter;
    private FolderListAdapter folderListAdapter;
    private PreviewAdapter previewAdapter;
    private boolean hasFolderGened = false;

    private File tempFile;
    private float degreeType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        StatusBarUtil.compat(this, Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        imageConfig = (ImageConfig) getIntent().getSerializableExtra("config");
        initView();
        // Android 6.0 checkSelfPermission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        } else {
            getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
        }
    }

    private void initView() {
        titleBarLayout = findViewById(R.id.titleBar);
        tv_Title = findViewById(R.id.tvTitle);
        title_img = findViewById(R.id.title_img);
        btn_Confirm = findViewById(R.id.btnConfirm);
        img_back = findViewById(R.id.title_back);
        recyclerView = findViewById(R.id.recyclerview);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(this);

        if (imageConfig != null) {
            if (imageConfig.multiSelect) {
                if (!imageConfig.rememberSelected) {
                    Constant.imageList.clear();
                }
                btn_Confirm.setText(String.format(getString(R.string.confirm_format), "确定", Constant.imageList.size(), imageConfig.maxNum));
            } else {
                Constant.imageList.clear();
                btn_Confirm.setVisibility(View.GONE);
            }
        }
        tv_Title.setText("所有图片");
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 3));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            int spacing = DisplayUtil.dip2px(recyclerView.getContext(), 6);
            int halfSpacing = spacing >> 1;

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = halfSpacing;
                outRect.right = halfSpacing;
                outRect.top = halfSpacing;
                outRect.bottom = halfSpacing;
            }
        });
        if (imageConfig.needCamera)
            imageList.add(new Image());

        imageListAdapter = new ImageListAdapter(this, imageList, imageConfig);
        imageListAdapter.setShowCamera(imageConfig.needCamera);
        imageListAdapter.setMutiSelect(imageConfig.multiSelect);
        recyclerView.setAdapter(imageListAdapter);
        imageListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public int onCheckedClick(int position, Image image) {
                return checkedImage(position, image);
            }

            @Override
            public void onImageClick(int position, Image image) {
                if (imageConfig.needCamera && position == 0) {
                    showCameraAction();
                } else {
                    if (imageConfig.multiSelect) {
                        viewPager.setAdapter((previewAdapter = new PreviewAdapter(ImageListActivity.this, imageList, imageConfig)));
                        previewAdapter.setListener(new OnItemClickListener() {
                            @Override
                            public int onCheckedClick(int position, Image image) {
                                return checkedImage(position, image);
                            }

                            @Override
                            public void onImageClick(int position, Image image) {
                                hidePreview();
                            }
                        });
                        if (imageConfig.needCamera) {
                            onPreviewChanged(position, imageList.size() - 1, true);
                        } else {
                            onPreviewChanged(position + 1, imageList.size(), true);
                        }
                        viewPager.setCurrentItem(imageConfig.needCamera ? position - 1 : position);
                        viewPager.setVisibility(View.VISIBLE);
                    } else {
                        onSingleImageSelected(image.path);
                    }
                }
            }
        });
        folderListAdapter = new FolderListAdapter(this, folderList);
    }

    /***
     * 返回 点击事件
     * @param view
     */
    public void OnBackClick(View view) {
        if (!hidePreview()) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    /***
     * 标题 点击事件
     * @param v
     */
    public void onAlbumSelectedClick(View v) {
        final int size = DisplayUtil.getScreenWidth(this);
        if (folderPopupWindow == null) {
            createPopupFolderList(size, size);
        }
        showTitleImgAnimation();
        if (folderPopupWindow.isShowing()) {
            folderPopupWindow.dismiss();
        } else {
            folderPopupWindow.show();
            if (folderPopupWindow.getListView() != null) {
                folderPopupWindow.getListView().setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.bottomBar_color)));
            }
            int index = folderListAdapter.getSelectIndex();
            index = index == 0 ? index : index - 1;
            folderPopupWindow.getListView().setSelection(index);

            folderPopupWindow.getListView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        folderPopupWindow.getListView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        folderPopupWindow.getListView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    int h = folderPopupWindow.getListView().getMeasuredHeight();
                    if (h > size) {
                        folderPopupWindow.setHeight(size);
                        folderPopupWindow.show();
                    }
                }
            });
            setBackgroundAlpha(0.6f);
        }
    }

    /***
     * 确定 点击事件
     * @param v
     */
    public void OnConfirmClick(View v) {
        if (Constant.imageList != null && !Constant.imageList.isEmpty()) {
            exit();
        } else {
            Toast.makeText(this, getString(R.string.minnum), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 点击头部分类时，箭头旋转180
     */
    public void showTitleImgAnimation() {
        title_img.animate().rotation(degreeType += 180).setDuration(300);
    }


    public void onSingleImageSelected(String path) {
        if (imageConfig.needCrop) {
            crop(path);
        } else {
            Constant.imageList.add(path);
            exit();
        }
    }


    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            if (imageConfig.needCrop) {
                crop(imageFile.getAbsolutePath());
            } else {
                Constant.imageList.add(imageFile.getAbsolutePath());
                imageConfig.multiSelect = false; // 多选点击拍照，强制更改为单选
                exit();
            }
        }
    }

    public void onPreviewChanged(int select, int sum, boolean visible) {
        if (visible) {
            tv_Title.setText(select + "/" + sum);
            title_img.setVisibility(View.GONE);
        } else {
            tv_Title.setText("所有图片");
            title_img.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMAGE_CROP_CODE:
                if (resultCode == RESULT_OK) {
                    Constant.imageList.add(cropImagePath);
                    imageConfig.multiSelect = false; // 多选点击拍照，强制更改为单选
                    exit();
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (tempFile != null) {
                        onCameraShot(tempFile);
                    }
                } else {
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /****
     *  退出
     */
    public void exit() {
        Intent intent = new Intent();
        result.clear();
        result.addAll(Constant.imageList);
        intent.putStringArrayListExtra(INTENT_RESULT, result);
        setResult(RESULT_OK, intent);
        if (!imageConfig.multiSelect) {
            Constant.imageList.clear();
        }
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
                } else {
                    Toast.makeText(this, "没有读写权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case CAMERA_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCameraAction();
                } else {
                    Toast.makeText(this, "获取拍照权限失败，无法启动相机！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (imageConfig.needCamera) {
            onPreviewChanged(position + 1, imageList.size() - 1, true);
        } else {
            onPreviewChanged(position + 1, imageList.size(), true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("config", imageConfig);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageConfig = (ImageConfig) savedInstanceState.getSerializable("config");
    }

    /****
     *   关闭 图片放大滑动选择
     * @return
     */
    public boolean hidePreview() {
        if (viewPager.getVisibility() == View.VISIBLE) {
            viewPager.setVisibility(View.GONE);
            onPreviewChanged(0, 0, false);
            imageListAdapter.notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    /***
     *  检测已选图片是否到达最大值
     * @param position
     * @param image
     * @return
     */
    private int checkedImage(int position, Image image) {
        if (image != null) {
            if (Constant.imageList.contains(image.path)) {
                Constant.imageList.remove(image.path);
                btn_Confirm.setText(String.format(getString(R.string.confirm_format), "确定", Constant.imageList.size(), imageConfig.maxNum));
            } else {
                if (imageConfig.maxNum <= Constant.imageList.size()) {
                    Toast.makeText(this, String.format(getString(R.string.maxnum), imageConfig.maxNum), Toast.LENGTH_SHORT).show();
                    return 0;
                }
                Constant.imageList.add(image.path);
                btn_Confirm.setText(String.format(getString(R.string.confirm_format), "确定", Constant.imageList.size(), imageConfig.maxNum));
            }
            return 1;
        }
        return 0;
    }

    /***
     * 启动裁剪功能
     * @param imagePath
     */
    private void crop(String imagePath) {
        File file = new File(FileUtils.createRootPath(this) + "/" + System.currentTimeMillis() + ".jpg");
        cropImagePath = file.getAbsolutePath();
        ActivityUtil.StartCrop(this, FileUtils.getImageContentUri(this, new File(imagePath)), imagePath, file, imageConfig, IMAGE_CROP_CODE);
    }

    /***
     *  启动照相机
     */
    private void showCameraAction() {
        if (imageConfig.maxNum <= Constant.imageList.size()) {
            Toast.makeText(this, String.format(getString(R.string.maxnum), imageConfig.maxNum), Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            tempFile = new File(FileUtils.createRootPath(this) + "/" + System.currentTimeMillis() + ".jpg");
            DebugUtil.e(tempFile.getAbsolutePath());
            FileUtils.createFile(tempFile);
            Uri uri = FileProvider.getUriForFile(this, FileUtils.getApplicationId(this) + ".image_provider", tempFile);
            List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); //Uri.fromFile(tempFile)
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "无法启动相机！", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     *  创建 图片文件夹 选择PopupWindow
     * @param width
     * @param height
     */
    private void createPopupFolderList(int width, int height) {
        folderPopupWindow = new ListPopupWindow(this);
        folderPopupWindow.setAnimationStyle(R.style.PopupAnimTop);
        folderPopupWindow.setBackgroundDrawable(null);
        folderPopupWindow.setAdapter(folderListAdapter);
        folderPopupWindow.setContentWidth(width);
        folderPopupWindow.setWidth(width);
        folderPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        folderPopupWindow.setAnchorView(titleBarLayout);
        folderPopupWindow.setModal(true);
        folderListAdapter.setOnFloderChangeListener(new OnFolderChangeListener() {
            @Override
            public void onChange(int position, Folder folder) {
                folderPopupWindow.dismiss();
                if (position == 0) {
                    getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                    tv_Title.setText("所有图片");
                } else {
                    imageList.clear();
                    if (imageConfig.needCamera)
                        imageList.add(new Image());
                    imageList.addAll(folder.images);
                    imageListAdapter.notifyDataSetChanged();
                    tv_Title.setText(folder.name);
                }
            }
        });
        folderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                showTitleImgAnimation();
                setBackgroundAlpha(1.0f);
            }
        });
    }


    /****
     *  加载本地图片 返回监听
     */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                return new CursorLoader(ImageListActivity.this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
            } else if (id == LOADER_CATEGORY) {
                return new CursorLoader(ImageListActivity.this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " not like '%.gif%'", null, MediaStore.Images.Media.DATE_ADDED + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    List<Image> tempImageList = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        Image image = new Image(path, name);
                        tempImageList.add(image);
                        if (!hasFolderGened) {
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            if (folderFile == null || !imageFile.exists() || imageFile.length() < 10) {
                                continue;
                            }

                            Folder parent = null;
                            for (Folder folder : folderList) {
                                if (TextUtils.equals(folder.path, folderFile.getAbsolutePath())) {
                                    parent = folder;
                                }
                            }
                            if (parent != null) {
                                parent.images.add(image);
                            } else {
                                parent = new Folder();
                                parent.name = folderFile.getName();
                                parent.path = folderFile.getAbsolutePath();
                                parent.cover = image;

                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);

                                parent.images = imageList;
                                folderList.add(parent);
                            }
                        }
                    } while (data.moveToNext());

                    imageList.clear();
                    if (imageConfig.needCamera)
                        imageList.add(new Image());
                    imageList.addAll(tempImageList);

                    imageListAdapter.notifyDataSetChanged();
                    folderListAdapter.notifyDataSetChanged();

                    hasFolderGened = true;
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        if (!hidePreview()) {
            Constant.imageList.clear();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
