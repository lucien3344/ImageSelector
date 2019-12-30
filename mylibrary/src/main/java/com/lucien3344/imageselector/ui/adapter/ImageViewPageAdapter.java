package com.lucien3344.imageselector.ui.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.lucien3344.imageselector.GlideApp;
import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.objet.Image;
import com.lucien3344.imageselector.utils.DebugUtil;

import java.util.List;

/**
 * 图片方大查看/选择 适配器
 *
 * @author lsh_2012@qq.com
 * on 2019/10/26.
 */
public class ImageViewPageAdapter extends PagerAdapter {

    private Activity activity;
    private List<Image> images;

    public ImageViewPageAdapter(Activity activity, List<Image> images) {
        this.activity = activity;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }


    /***
     *  要删除item  必须要重写此方法  否则notifyDataSetChanged方法无效
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
//        return POSITION_UNCHANGED; //默认
        return POSITION_NONE;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View root = View.inflate(activity, R.layout.item_select_pager_image, null);
        final ImageView photoView = (ImageView) root.findViewById(R.id.ivImage);
        final ImageView ivChecked = (ImageView) root.findViewById(R.id.ivPhotoCheaked);
        ivChecked.setVisibility(View.GONE);
        container.addView(root);
        displayImage(photoView, images.get(position).path);
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
}
