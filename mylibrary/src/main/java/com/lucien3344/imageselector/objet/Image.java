package com.lucien3344.imageselector.objet;

import java.io.Serializable;

/**
 * Image对象
 *
 * @author lsh_2012@qq.com
 * @date 19/4/6.
 */
public class Image implements Serializable {

    public String path;
    public String name;

    public Image(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public Image() {

    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}