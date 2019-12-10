package com.lucien3344.imageselector.objet;

import java.io.Serializable;
import java.util.List;

/**
 * Folder对象
 *
 * @author lsh_2012@qq.com
 * @date 19/4/6.
 */
public class Folder implements Serializable {

    public String name;
    public String path;
    public Image cover;
    public List<Image> images;

    public Folder() {

    }

    @Override
    public boolean equals(Object o) {
        try {
            Folder other = (Folder) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}