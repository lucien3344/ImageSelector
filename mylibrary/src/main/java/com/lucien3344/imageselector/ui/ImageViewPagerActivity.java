package com.lucien3344.imageselector.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.objet.Image;
import com.lucien3344.imageselector.ui.adapter.CustomViewPager;
import com.lucien3344.imageselector.ui.adapter.ImageViewPageAdapter;
import com.lucien3344.imageselector.utils.StatusBarUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片放大
 *
 * @author lsh_2012@qq.com
 * on 2019/10/23.
 */
public class ImageViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private CustomViewPager viewPager;
    private TextView tv_Title;
    private TextView tv_Delete;
    private ImageViewPageAdapter imageViewPageAdapter;
    private List<Image> imageList = new ArrayList<>();
    private int position = 0;
    private boolean isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewpager);
        StatusBarUtil.compat(this, Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        imageList = (List<Image>) getIntent().getSerializableExtra("imagelists");
        position = getIntent().getIntExtra("position", 0);
        isDelete = getIntent().getBooleanExtra("isDelete", false);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        tv_Title = findViewById(R.id.tvTitle);
        tv_Delete = findViewById(R.id.tv_Delete);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter((imageViewPageAdapter = new ImageViewPageAdapter(ImageViewPagerActivity.this, imageList)));
        tv_Title.setText(position + 1 + "/" + imageList.size());
        viewPager.setCurrentItem(position);
        if (isDelete) {
            tv_Delete.setVisibility(View.VISIBLE);
        } else {
            tv_Delete.setVisibility(View.INVISIBLE);
        }
    }

    /***
     * 返回 点击事件
     * @param view
     */
    public void OnBackClick(View view) {
        if (isDelete) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("result", (Serializable) imageList);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        }
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /***
     * 删除 点击事件
     * @param view
     */
    public void onDelete(View view) {
        if (isDelete) {
            int lastImage = position;
            imageList.remove(lastImage);
            imageViewPageAdapter.notifyDataSetChanged();
            if (imageList.size() > 0) {
                tv_Title.setText(((lastImage + 1) > imageList.size() ? imageList.size() : (lastImage + 1)) + "/" + imageList.size());
                imageViewPageAdapter.notifyDataSetChanged();
            } else {
                OnBackClick(null);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        tv_Title.setText(position + 1 + "/" + imageList.size());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("imagelists", (Serializable) imageList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageList = (List<Image>) savedInstanceState.getSerializable("imagelists");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
