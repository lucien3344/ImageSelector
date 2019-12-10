package com.lucien3344.imageselector.common.onclicklistener;


import com.lucien3344.imageselector.objet.Image;
/**
 * @author lsh_2012@qq.com
 * @date 19/3/6.
 */
public interface OnItemClickListener {

    int onCheckedClick(int position, Image image);

    void onImageClick(int position, Image image);
}
