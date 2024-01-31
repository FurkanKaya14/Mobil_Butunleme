package com.example.mobil5.model;

import java.util.ArrayList;
import java.util.List;

public class Photo {
    private String id;
    private List<String> labelIds = new ArrayList<>();
    private List<String> labelNames;
    private String imageUrl;
    private List<Label> labels;

    public Photo() {
        // Boş constructor
    }

    public Photo(String id, List<String> labelIds, List<String> labelNames, String imageUrl) {
        this.id = id;
        this.labelIds = labelIds;
        this.labelNames = labelNames;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public List<String> getLabelIds() {
        return labelIds;
    }
    public List<String> getLabelNames() { return labelNames; }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Label> getLabels() {
        return labels;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}
