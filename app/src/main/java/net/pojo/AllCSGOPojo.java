package net.pojo;

public class AllCSGOPojo {

    CSGOStats csgoStats;
    CSGOEconSchema csgoEconSchema;
    CSGOAchievements csgoAchievements;
    CSGOStatSchema csgoStatSchema;
    CSGOGlobalAchievements csgoGlobalAchievements;

    public AllCSGOPojo(CSGOStats csgoStats,
                       CSGOEconSchema csgoEconSchema, CSGOAchievements csgoAchievements,
                       CSGOStatSchema csgoStatSchema, CSGOGlobalAchievements csgoGlobalAchievements) {
        this.csgoStats = csgoStats;
        this.csgoEconSchema = csgoEconSchema;
        this.csgoAchievements = csgoAchievements;
        this.csgoStatSchema = csgoStatSchema;
        this.csgoGlobalAchievements = csgoGlobalAchievements;
    }

    public CSGOStats getCsgoStats() {
        return csgoStats;
    }

    public CSGOEconSchema getCsgoEconSchema() {
        return csgoEconSchema;
    }

    public CSGOAchievements getCsgoAchievements() {
        return csgoAchievements;
    }

    public CSGOStatSchema getCsgoStatSchema() {
        return csgoStatSchema;
    }

    public CSGOGlobalAchievements getCsgoGlobalAchievements() {
        return csgoGlobalAchievements;
    }

}
