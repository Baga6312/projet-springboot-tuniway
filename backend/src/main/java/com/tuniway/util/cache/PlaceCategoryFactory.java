package com.tuniway.util.cache;

import com.tuniway.model.enums.PlaceCategory;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class PlaceCategoryFactory {
    private static final Map<PlaceCategory, CategoryData> categoryCache = new HashMap<>();

    // TODO tansech te5DM les icons


    static {
        categoryCache.put(PlaceCategory.HISTORICAL,
                new CategoryData("🏛️", "Historical sites and monuments", "#8B4513"));
        categoryCache.put(PlaceCategory.BEACH,
                new CategoryData("🏖️", "Beaches and coastal areas", "#1E90FF"));
        categoryCache.put(PlaceCategory.RESTAURANT,
                new CategoryData("🍽️", "Dining and restaurants", "#FF6347"));
        categoryCache.put(PlaceCategory.HOTEL,
                new CategoryData("🏨", "Hotels and accommodations", "#4169E1"));
        categoryCache.put(PlaceCategory.MUSEUM,
                new CategoryData("🏛️", "Museums and galleries", "#800080"));
        categoryCache.put(PlaceCategory.NATURE,
                new CategoryData("🌳", "Natural parks and reserves", "#228B22"));
        categoryCache.put(PlaceCategory.SHOPPING,
                new CategoryData("🛍️", "Shopping centers and markets", "#FFD700"));
        categoryCache.put(PlaceCategory.ENTERTAINMENT,
                new CategoryData("🎭", "Entertainment and activities", "#FF1493"));
    }

    public static CategoryData getCategoryData(PlaceCategory category) {
        CategoryData data = categoryCache.get(category);
        if (data == null) {
            throw new IllegalArgumentException("Unknown category: " + category);
        }
        System.out.println("Reusing cached CategoryData for: " + category);
        return data;
    }

    public static int getCacheSize() {
        return categoryCache.size();
    }
}