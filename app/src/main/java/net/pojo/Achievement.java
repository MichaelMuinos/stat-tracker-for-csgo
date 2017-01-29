package net.pojo;

import java.io.Serializable;

public class Achievement implements Serializable {

    String apiname;
    int achieved;

    public String getApiname() {
        return apiname;
    }

    public int getAchieved() {
        return achieved;
    }

}
