package com.example.mobil5.model;

public class Label {
    private String id;
    private String name;
    private boolean isSelected;

    public Label() {

    }

    public Label(String id, String name) {
        this.id = id;
        this.name = name;


    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}