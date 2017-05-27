package com.toby.mymaterialdemo.model;

/**
 * Created by Toby on 2016/11/2.
 */

public class WordsTabBaseEntity {

    private String type;
    private String name;

    public WordsTabBaseEntity(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
