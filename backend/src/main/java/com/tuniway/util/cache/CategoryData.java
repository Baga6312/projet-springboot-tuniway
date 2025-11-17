package com.tuniway.util.cache;

public class CategoryData {
    private final String icon;
    private final String description;
    private final String colorCode;

    public CategoryData(String icon, String description, String colorCode) {
        this.icon = icon;
        this.description = description;
        this.colorCode = colorCode;
    }

    public String getIcon() { return icon; }
    public String getDescription() { return description; }
    public String getColorCode() { return colorCode; }

    // Display method using extrinsic state (place name)
    public void display(String placeName) {
        System.out.println("Place: " + placeName + " | Icon: " + icon +
                " | Description: " + description + " | Color: " + colorCode);
    }
}

