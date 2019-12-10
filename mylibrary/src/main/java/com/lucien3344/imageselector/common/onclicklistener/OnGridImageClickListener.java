package com.lucien3344.imageselector.common.onclicklistener;


import com.lucien3344.imageselector.objet.Image;

import java.util.List;

/**
 * @author lsh_2012@qq.com
 * @date 19/10/27
 */
public interface OnGridImageClickListener {

    void onDeleteClick(int position, Image image);

    void onAddClick(int position, List<Image> images);

    void onImagesClick(int position, List<Image> images);
}
