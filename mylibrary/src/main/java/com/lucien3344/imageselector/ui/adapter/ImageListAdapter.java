package com.lucien3344.imageselector.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
 * 图片适配器
 *
 * @author lsh_2012@qq.com
 * on 2019/10/22.
 */
public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean showCamera;
    private boolean mutiSelect;
    private List<Image> data;
    private ImageConfig config;
    private Context context;
    private OnItemClickListener listener;

    private static final int ITEM_TAKEPHOTO = 0;
    private static final int ITEM_IMAGE = 1;

    public ImageListAdapter(Context context, List<Image> list, ImageConfig config) {
        this.context = context;
        this.data = list;
        this.config = config;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TAKEPHOTO:
                View view = LayoutInflater.from(context).inflate(R.layout.item_select_camera, parent, false);
                return new TakePhotoHolder(view);
            case ITEM_IMAGE:
                View view1 = LayoutInflater.from(context).inflate(R.layout.item_select_image, parent, false);
                return new ImageViewHolder(view1);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Image item = data.get(position);
        if (holder instanceof TakePhotoHolder) {
            ((TakePhotoHolder) holder).InitData(item, position);
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).InitData(item, position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showCamera) {
            return ITEM_TAKEPHOTO;
        }
        return ITEM_IMAGE;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setMutiSelect(boolean mutiSelect) {
        this.mutiSelect = mutiSelect;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private ImageView ivPhotoCheaked;
        private View view_cover;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivPhotoCheaked = itemView.findViewById(R.id.ivPhotoCheaked);
            view_cover = itemView.findViewById(R.id.view_cover);
        }

        public void InitData(final Image item, final int position) {
            if (mutiSelect) {
                ivPhotoCheaked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int ret = listener.onCheckedClick(position, item);
                            if (ret == 1) { // 局部刷新
                                if (Constant.imageList.contains(item.path)) {
                                    if (config.checkedResId != -1) {
                                        ivPhotoCheaked.setImageResource(config.checkedResId);
                                    } else {
                                        ivPhotoCheaked.setImageResource(R.drawable.ic_check_24dp);
                                    }
                                    view_cover.setVisibility(View.VISIBLE);
                                } else {
                                    if (config.checkResId != -1) {
                                        ivPhotoCheaked.setImageResource(config.checkResId);
                                    } else {
                                        ivPhotoCheaked.setImageResource(R.drawable.ic_uncheck_24dp);
                                    }
                                    view_cover.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
            }
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageClick(position, item);
                }
            });
            GlideApp.with(context)
                    .load(item.path)
                  .centerCrop()
                    .into(ivImage);
            if (mutiSelect) {
                ivPhotoCheaked.setVisibility(View.VISIBLE);
                if (Constant.imageList.contains(item.path)) {
                    if (config.checkedResId != -1) {
                        ivPhotoCheaked.setImageResource(config.checkedResId);
                    } else {
                        ivPhotoCheaked.setImageResource(R.drawable.ic_check_24dp);
                    }
                    view_cover.setVisibility(View.VISIBLE);
                } else {
                    if (config.checkResId != -1) {
                        ivPhotoCheaked.setImageResource(config.checkResId);
                    } else {
                        ivPhotoCheaked.setImageResource(R.drawable.ic_uncheck_24dp);
                    }
                    view_cover.setVisibility(View.GONE);
                }
            } else {
                ivPhotoCheaked.setVisibility(View.GONE);
                view_cover.setVisibility(View.GONE);
            }
        }
    }

    public class TakePhotoHolder extends RecyclerView.ViewHolder {

        private ImageView ivTakePhoto;

        public TakePhotoHolder(View itemView) {
            super(itemView);
            ivTakePhoto = itemView.findViewById(R.id.ivTakePhoto);
        }

        public void InitData(final Image item, final int position) {
            ivTakePhoto.setImageResource(R.drawable.ic_take_photo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageClick(position, item);
                }
            });
        }
    }

}