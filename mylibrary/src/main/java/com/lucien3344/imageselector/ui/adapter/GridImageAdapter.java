package com.lucien3344.imageselector.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lucien3344.imageselector.GlideApp;
import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.common.onclicklistener.OnGridImageClickListener;
import com.lucien3344.imageselector.objet.Image;
import com.lucien3344.imageselector.view.DragGridView;

import java.util.List;

/**
 * 9宫格图片添加 适配器
 *
 * @author lsh_2012@qq.com
 * on 2019/10/25.
 */
public class GridImageAdapter extends BaseAdapter implements DragGridView.DragGridBaseAdapter {

    private Context context;
    private List<Image> imageList;
    private boolean isAddImage = true;
    private OnGridImageClickListener onGridImageClick;

    public GridImageAdapter(Context context, List<Image> imageList, boolean isAddImage) {
        this.context = context;
        if (isAddImage) {
            imageList.add(new Image("*", "*"));
        }
        this.imageList = imageList;
        this.isAddImage = isAddImage;
    }

    @Override
    public int getCount() {
        return imageList == null ? 0 : imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList == null ? null : imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Image image = imageList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_image, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.InitData(image, position);
        return convertView;
    }

    public void setData(List<Image> images) {
        imageList.clear();
        if (images != null && images.size() > 0) {
            imageList.addAll(images);
        }
        if (isAddImage) {
            imageList.add(new Image("*", "*"));
        }
        notifyDataSetChanged();
    }

    public void setOnGridImageClickListener(OnGridImageClickListener onGridImageClick) {
        this.onGridImageClick = onGridImageClick;
    }

    @Override
    public boolean reOrderItems(int oldPosition, int newPosition) {
        return false;
    }

    @Override
    public void setHideItem(int hidePosition) {

    }


    public class ViewHolder {
        private View itemview;
        private ImageView image_view;
        private ImageButton delete_btn;

        public ViewHolder(View itemView) {
            itemview = itemView.findViewById(R.id.itemview);
            image_view = (ImageView) itemView.findViewById(R.id.image_view);
            delete_btn = (ImageButton) itemView.findViewById(R.id.delete_btn);
        }

        public void InitData(final Image image, final int position) {
            if (image.name.equals("*") && image.path.equals("*")) {
                delete_btn.setVisibility(View.GONE);
                /*****加载图片****/
                GlideApp.with(context)
                        .load(R.drawable.ic_add_image)
                        .centerCrop()
                        .into(image_view);
            } else {
                delete_btn.setVisibility(View.VISIBLE);
                /*****加载图片****/
                GlideApp.with(context)
                        .load(image.path)
                        .centerCrop()
                        .into(image_view);
            }
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onGridImageClick == null) {
                        return;
                    }
                    if (image.name.equals("*") && image.path.equals("*")) {
                        onGridImageClick.onAddClick(position, imageList);
                    } else {
                        onGridImageClick.onImagesClick(position, imageList);
                    }
                }
            });
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onGridImageClick != null) {
                        onGridImageClick.onDeleteClick(position, image);
                    }
                }
            });
        }
    }

}
