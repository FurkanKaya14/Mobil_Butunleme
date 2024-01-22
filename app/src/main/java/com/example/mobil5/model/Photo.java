package com.example.mobil5.model;

public class Photo {
    private String id;
    private String labelId;
    private String labelName;
    private String imageUrl;

    public Photo() {
        // Boş yapıcı metot Firebase veri okuma işlemi için gereklidir.
    }

    public Photo(String id, String labelId, String labelName, String imageUrl) {
        this.id = id;
        this.labelId = labelId;
        this.labelName = labelName;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getLabelId() {
        return labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public int getImageUrl() {
        return imageUrl;
    }
}
