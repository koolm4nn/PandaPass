package com.pandaPass.models;

public class Category {
    private String title;
    private int id;

    public static final Category EMPTY_CATEGORY = new Category("", -1);

    public Category(String title, int id){
        this.title = title;
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public int getId(){
        return id;
    }

    @Override
    public String toString(){
        return title;
    }
}
