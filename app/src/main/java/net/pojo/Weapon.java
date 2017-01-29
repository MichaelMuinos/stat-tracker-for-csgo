package net.pojo;

public class Weapon {

    private String weaponName;
    private String weaponDrawable;
    private float weaponKills;
    private float weaponHits;
    private float weaponShots;

    public Weapon(String weaponName, String weaponDrawable, float weaponKills, float weaponHits, float weaponShots) {
        this.weaponName = weaponName;
        this.weaponDrawable = weaponDrawable;
        this.weaponKills = weaponKills;
        this.weaponHits = weaponHits;
        this.weaponShots = weaponShots;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public String getWeaponDrawable() {
        return weaponDrawable;
    }

    public float getWeaponKills() {
        return weaponKills;
    }

    public float getWeaponHits() {
        return weaponHits;
    }

    public float getWeaponShots() {
        return weaponShots;
    }

}
