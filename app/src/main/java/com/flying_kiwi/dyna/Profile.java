package com.flying_kiwi.dyna;

import java.io.Serializable;

public class Profile implements Serializable {
    //TODO: Eventually have a website to share data with friends
    private String name;
    private String displayName;
    // Other settings like dark/light mode etc
    public Profile(String name){
        this.name = name;
        this.displayName = name;
    }
    public void changeName(String newName){
        this.displayName = newName;
        //Have calling class save to file
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }
}
