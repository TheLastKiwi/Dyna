package com.example.dyna;

import java.io.Serializable;

public class Profile implements Serializable {
    String name;
    String displayName;
    // Other settings like dark/light mode etc
    public Profile(String name){
        this.name = name;
    }
}
