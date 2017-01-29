package net.pojo;

import java.io.Serializable;

public class WeaponInfo implements Serializable {

    String name;
    int defindex;
    String image_url;

    public String getName() {
        return name;
    }

    public int getDefindex() {
        return defindex;
    }

    public String getImage_url() {
        return image_url;
    }

}
