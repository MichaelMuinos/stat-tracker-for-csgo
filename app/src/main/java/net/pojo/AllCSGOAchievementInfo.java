package net.pojo;

import java.io.Serializable;

public class AllCSGOAchievementInfo implements Serializable {

    Achievement achievement;
    GlobalAchievement globalAchievement;
    AvailableAchievement availableAchievement;

    public AllCSGOAchievementInfo(AvailableAchievement availableAchievement, Achievement achievement,
                                  GlobalAchievement globalAchievement) {
        this.availableAchievement = availableAchievement;
        this.achievement = achievement;
        this.globalAchievement = globalAchievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setGlobalAchievement(GlobalAchievement globalAchievement) {
        this.globalAchievement = globalAchievement;
    }

    public GlobalAchievement getGlobalAchievement() {
        return globalAchievement;
    }

    public AvailableAchievement getAvailableAchievement() {
        return availableAchievement;
    }

}
