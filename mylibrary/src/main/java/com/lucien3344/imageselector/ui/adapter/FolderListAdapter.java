package com.lucien3344.imageselector.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lucien3344.imageselector.GlideApp;
import com.lucien3344.imageselector.R;
import com.lucien3344.imageselector.common.onclicklistener.OnFolderChangeListener;
import com.lucien3344.imageselector.objet.Folder;

import java.util.List;

/**
 * 图片所在文件夹 适配器
 *
 * @author lsh_2012@qq.com
 * on 2019/10/23.
 */
public class FolderListAdapter extends BaseAdapter {

    private Context context;
    private List<Folder> folderList;

    private int selected = 0;
    private OnFolderChangeListener listener;

    public FolderListAdapter(Context context, List<Folder> folderList) {
        this.context = context;
        this.folderList = folderList;
    }

    @Override
    public int getCount() {
        return folderList == null ? 0 : folderList.size();
    }

    @Override
    public Object getItem(int position) {
        return folderList == null ? null : folderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FolderHolder folderHolder;
        Folder folder = folderList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_folder, null);
            folderHolder = new FolderHolder(convertView);
            convertView.setTag(folderHolder);
        } else {
            folderHolder = (FolderHolder) convertView.getTag();
        }
        folderHolder.InitData(folder, position);
        return convertView;
    }


    public void setData(List<Folder> folders) {
        folderList.clear();
        if (folders != null && folders.size() > 0) {
            folderList.addAll(folders);
        }
        notifyDataSetChanged();
    }

    private int getTotalImageSize() {
        int result = 0;
        if (folderList != null && folderList.size() > 0) {
            for (Folder folder : folderList) {
                result += folder.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int position) {
        if (selected == position)
            return;
        if (listener != null)
            listener.onChange(position, folderList.get(position));
        selected = position;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selected;
    }

    public void setOnFloderChangeListener(OnFolderChangeListener listener) {
        this.listener = listener;
    }

    public class FolderHolder {
        private View itemview;
        private TextView tvFolderName;
        private TextView tvImageNum;
        private ImageView ivFolder;
        private View viewLine;
        private ImageView indicator;

        public FolderHolder(View itemView) {
            itemview = itemView.findViewById(R.id.itemview);
            ivFolder = (ImageView) itemView.findViewById(R.id.ivFolder);
            tvFolderName = (TextView) itemView.findViewById(R.id.tvFolderName);
            tvImageNum = (TextView) itemView.findViewById(R.id.tvImageNum);
            indicator = (ImageView) itemView.findViewById(R.id.indicator);
            viewLine = itemView.findViewById(R.id.viewLine);
        }

        public void InitData(final Folder folder, final int position) {
            if (position == 0) {
                tvFolderName.setText("所有图片");
                tvImageNum.setText("共" + getTotalImageSize() + "张");
                if (folderList.size() > 0) {
                    GlideApp.with(context)
                            .load(folder.cover.path)
                            .centerCrop()
                            .into(ivFolder);
                }
            } else {
                tvFolderName.setText(folder.name);
                tvImageNum.setText("共" + folder.images.size() + "张");
                if (folderList.size() > 0) {
                    GlideApp.with(context)
                            .load(folder.cover.path)
                            .centerCrop()
                            .into(ivFolder);
                }
            }
            if (position != getCount() - 1) {
                viewLine.setVisibility(View.VISIBLE);
            } else {
                viewLine.setVisibility(View.GONE);
            }
            if (selected == position) {
                indicator.setVisibility(View.VISIBLE);
            } else {
                indicator.setVisibility(View.GONE);
            }
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectIndex(position);
                }
            });
        }
    }

}
