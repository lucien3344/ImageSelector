package com.lucien3344.imageselector.ui.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lucien3344.imageselector.GlideApp;
import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.common.Constant;
import com.lucien3344.imageselector.common.onclicklistener.OnItemClickListener;
import com.lucien3344.imageselector.config.ImageConfig;
import com.lucien3344.imageselector.objet.Image;

import java.util.List;

/**
 * 图片方大查看/选择 适配器
 *
 * @author lsh_2012@qq.com
 * on 2019/10/26.
 */
public class PreviewAdapter extends PagerAdapter {

    private Activity activity;
    private List<Image> images;
    private ImageConfig config;
    private OnItemClickListener listener;

    public PreviewAdapter(Activity activity, List<Image> images, ImageConfig config) {
        this.activity = activity;
        this.images = images;
        this.config = config;
    }

    @Override
    public int getCount() {
        if (config.needCamera)
            return images.size() - 1;
        else
            return images.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View root = View.inflate(activity, R.layout.item_select_pager_image, null);
        final ImageView photoView = (ImageView) root.findViewById(R.id.ivImage);
        final ImageView ivChecked = (ImageView) root.findViewById(R.id.ivPhotoCheaked);
        if (config.multiSelect) {
            ivChecked.setVisibility(View.VISIBLE);
            final Image image = images.get(config.needCamera ? position + 1 : position);
            if (Constant.imageList.contains(image.path)) {
                if (config.checkedResId != -1) {
                    ivChecked.setImageResource(config.checkedResId);
                } else {
                    ivChecked.setImageResource(R.drawable.ic_check_24dp);
                }
            } else {
                if (config.checkResId != -1) {
                    ivChecked.setImageResource(config.checkResId);
                } else {
                    ivChecked.setImageResource(R.drawable.ic_uncheck_24dp);
                }
            }
            ivChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int ret = listener.onCheckedClick(position, image);
                        if (ret == 1) { // 局部刷新
                            if (Constant.imageList.contains(image.path)) {
                                if (config.checkedResId != -1) {
                                    ivChecked.setImageResource(config.checkedResId);
                                } else {
                                    ivChecked.setImageResource(R.drawable.ic_check_24dp);
                                }
                            } else {
                                if (config.checkResId != -1) {
                                    ivChecked.setImageResource(config.checkResId);
                                } else {
                                    ivChecked.setImageResource(R.drawable.ic_uncheck_24dp);
                                }
                            }
                        }
                    }
                }
            });
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onImageClick(position, images.get(position));
                    }
                }
            });
        } else {
            ivChecked.setVisibility(View.GONE);
        }
        container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        displayImage(photoView, images.get(config.needCamera ? position + 1 : position).path);
        return root;
    }

    private void displayImage(ImageView photoView, String path) {
        /*****加载图片****/
        GlideApp.with(activity)
                .load(path)
                .into(photoView);

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
