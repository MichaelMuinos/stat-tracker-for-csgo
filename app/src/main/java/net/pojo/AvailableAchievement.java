package net.pojo;

import java.io.Serializable;

public class AvailableAchievement implements Serializable {

    String name;
    String displayName;
    String description;
    String icon;

    public AvailableAchievement(String name, String displayName, String description, String icon) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

}
