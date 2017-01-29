package net.pojo;

import java.io.Serializable;

public class Player implements Serializable {

    String personaname;
    String avatarfull;
    int communityvisibilitystate;
    float lastlogoff;

    public Player(String personaname, String avatarfull, float lastlogoff) {
        this.personaname = personaname;
        this.avatarfull = avatarfull;
        this.lastlogoff = lastlogoff;
    }

    public String getPersonaname() {
        return personaname;
    }

    public String getAvatar() {
        return avatarfull;
    }

    public int getCommunityvisibilitystate() {
        return communityvisibilitystate;
    }

    public float getLastlogoff() {
        return lastlogoff;
    }

}
