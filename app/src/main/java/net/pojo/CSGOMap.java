package net.pojo;

public class CSGOMap {

    private String mapName;
    private int mapDrawable;
    private float mapWins;
    private float mapLosses;

    public CSGOMap(String mapName, int mapDrawable, float mapWins, float mapLosses) {
        this.mapName = mapName;
        this.mapDrawable = mapDrawable;
        this.mapWins = mapWins;
        this.mapLosses = mapLosses;
    }

    public String getMapName() {
        return mapName;
    }

    public int getMapDrawable() {
        return mapDrawable;
    }

    public float getMapWins() {
        return mapWins;
    }

    public float getMapLosses() {
        return mapLosses;
    }

}
