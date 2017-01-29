package database.models;

import java.io.Serializable;

public class PastSearch implements Serializable {

    private String gamerTag;
    private String profileUrl;
    private String steamId;
    private float lastLogOn;

    public PastSearch(String steamId, String profileUrl, String gamerTag, float lastLogOn) {
        this.gamerTag = gamerTag;
        this.profileUrl = profileUrl;
        this.steamId = steamId;
        this.lastLogOn = lastLogOn;
    }

    public String getGamerTag() {
        return gamerTag;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getSteamId() {
        return steamId;
    }

    public float getLastLogOn() {
        return lastLogOn;
    }

}
