package com.riftlabs.communicationlib.data;

import android.graphics.Color;

import java.io.Serializable;

public class KickFilter implements Serializable {
    private int category;
    private String name;
    private int index;
    private int r;
    private int g;
    private int b;
    private boolean initialized;

    public KickFilter() {
    }

    public KickFilter(int category, int index, String name, int r, int g, int b) {
        this.setCategory(category);
        this.setIndex(index);
        this.setName(name);
        this.setR(r & 0xFF);
        this.setG(g & 0xFF);
        this.setB(b & 0xFF);
        this.setInitialized(true);
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r & 0xFF;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g & 0xFF;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b & 0xFF;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public int getColor() {
        return Color.argb(0xFF, getR(),  getG(), getB());
    }

    public static KickFilter[] GetDefaultFilters() {
        return new KickFilter[]{
            new KickFilter(0, 0, "Chromakey Blue", 47, 0, -1),
            new KickFilter(0, 1, "Chromakey Green", 0, -5, 0),
            new KickFilter(0, 2, "Plus Green", -79, -5, 98),
            new KickFilter(0, 3, "1/2 Plus Green", -32, -5, -118),
            new KickFilter(0, 4, "1/4 Plus Green", -5, -7, -90),
            new KickFilter(0, 5, "1/8  Plus Green", -5, -24, -85),
            new KickFilter(0, 6, "1/8 Minus Green", -5, -58, -83),
            new KickFilter(0, 7, "1/4 Minus Green", -5, -67, -83),
            new KickFilter(0, 8, "1/2 Minus Green", -5, -101, -95),
            new KickFilter(0, 9, "Minus Green", -5, 109, -109),
            new KickFilter(0, 10, "Double CTB", 5, 67, -5),
            new KickFilter(0, 11, "Full CTB", 72, -105, -5),
            new KickFilter(0, 12, "3/4 CTB", 87, -65, -5),
            new KickFilter(0, 13, "1/2 CTB", -104, -45, -5),
            new KickFilter(0, 14, "1/4 CTB", -19, -6, -5),
            new KickFilter(0, 15, "1/8 CTB", -5, -20, -44),
            new KickFilter(0, 16, "1/8 CTO", -5, -63, -119),
            new KickFilter(0, 17, "1/4 CTO", -5, -94, 106),
            new KickFilter(0, 18, "1/2 CTO", -5, -113, 70),
            new KickFilter(0, 19, "3/4 CTO", -5, -125, 45),
            new KickFilter(0, 20, "Full CTO", -5, 93, 27),
            new KickFilter(0, 21, "Double CTO", -5, 61, 0),
            new KickFilter(0, 22, "Apricot", -5, 77, 26),
            new KickFilter(0, 23, "Gold Tint", -5, 109, 92),
            new KickFilter(0, 24, "M.B Amber", -5, 113, 80),
            new KickFilter(0, 25, "Bastard Amber", -5, -117, 106),
            new KickFilter(0, 26, "Pale Gold", -5, 109, 81),
            new KickFilter(0, 27, "Pale Salmon", -5, 87, 94),
            new KickFilter(0, 28, "Pale Rose", -5, 122, 105),
            new KickFilter(0, 29, "Light Pink", -5, 107, -127),
            new KickFilter(0, 30, "Medium Pink", -5, 53, 102),
            new KickFilter(0, 31, "Light Rose", -5, 33, 64),
            new KickFilter(0, 32, "English Rose", -5, 45, 5),
            new KickFilter(0, 33, "Light Salmon", -5, 52, 72),
            new KickFilter(0, 34, "Middle Rose", -5, 43, 114),
            new KickFilter(0, 35, "Dark Pink", -5, 0, 86),
            new KickFilter(0, 36, "Pink", -5, 4, 44),
            new KickFilter(0, 37, "Loving Amber", -5, 61, 57),
            new KickFilter(0, 38, "Flesh Pink", -5, 1, 70),
            new KickFilter(0, 39, "Paler Lavender", -19, -31, -5),
            new KickFilter(0, 40, "Surprise Pink", -118, 65, -5),
            new KickFilter(0, 41, "Lavender Tint", -5, -53, -59),
            new KickFilter(0, 42, "Light Lavender", -79, 106, -5),
            new KickFilter(0, 43, "Fuchsia Pink", -72, 22, -5),
            new KickFilter(0, 44, "S. Lavender", 118, 61, -5),
            new KickFilter(0, 45, "Pale Lavender", -5, -111, -19),
            new KickFilter(0, 46, "Deep Lavender", -13, 81, -5),
            new KickFilter(0, 47, "Lilac Tint", -5, -68, -33),
            new KickFilter(0, 48, "Rose Purple", -5, 8, -37),
            new KickFilter(0, 49, "Medium Purple", -5, 0, -36),
            new KickFilter(0, 50, "Mauve", -5, 0, -46),
            new KickFilter(0, 51, "Bright Pink", -5, 0, 87),
            new KickFilter(0, 52, "Follies Pink", -5, 0, -117),
            new KickFilter(0, 53, "Rose Pink", -5, 16, -110),
            new KickFilter(0, 54, "Smokey Pink", -5, 19, 75),
            new KickFilter(0, 55, "Scarlet", -5, 0, 15),
            new KickFilter(0, 56, "S. Rose Pink", -5, 0, 53),
            new KickFilter(0, 57, "Bright Rose", -5, 0, 41),
            new KickFilter(0, 58, "Dark Magenta", -5, 0, 23),
            new KickFilter(0, 59, "Magenta", -5, 0, 25),
            new KickFilter(0, 60, "Bright Red", -5, 0, 0),
            new KickFilter(0, 61, "Primary Red", -5, 0, 0),
            new KickFilter(0, 62, "Plasa Red", -5, 0, 0),
            new KickFilter(0, 63, "Medium Red", -5, 0, 1),
            new KickFilter(0, 64, "Light Red", -5, 0, 0),
            new KickFilter(0, 65, "Flame", -5, 0, 0),
            new KickFilter(0, 66, "Fire", -5, 0, 0),
            new KickFilter(0, 67, "Sunset Red", -5, 10, 4),
            new KickFilter(0, 68, "Deep Golden Amber", -5, 0, 0),
            new KickFilter(0, 69, "Gold Amber", -5, 19, 0),
            new KickFilter(0, 70, "Deep Orange", -5, 21, 0),
            new KickFilter(0, 71, "Golden Amber", -5, 38, 13),
            new KickFilter(0, 72, "Surprise Peach", -5, 76, 31),
            new KickFilter(0, 73, "1/4 Mustard Yellow", -5, -123, 0),
            new KickFilter(0, 74, "1/2 Mustard Yellow", -5, 118, 0),
            new KickFilter(0, 75, "Mustard Yellow", -5, 109, 0),
            new KickFilter(0, 76, "Urban Sodium", -5, 38, 1),
            new KickFilter(0, 77, "LO Sodium", -5, 52, 0),
            new KickFilter(0, 78, "Amber Delight", -5, 14, 0),
            new KickFilter(0, 79, "Industry Sodium", -5, -74, 36),
            new KickFilter(0, 80, "Chocolate", -5, -124, 65),
            new KickFilter(0, 81, "Dark Salmon", -5, 27, 19),
            new KickFilter(0, 82, "Orange", -5, 40, 0),
            new KickFilter(0, 83, "Dark Amber", -5, 0, 0),
            new KickFilter(0, 84, "Chrome Orange", -5, 78, 0),
            new KickFilter(0, 85, "Medium Amber", -5, 53, 0),
            new KickFilter(0, 86, "Deep Straw", -5, 80, 0),
            new KickFilter(0, 87, "Light Amber", -5, -115, 14),
            new KickFilter(0, 88, "Pale Yellow", -5, -34, 112),
            new KickFilter(0, 89, "Medium Yellow", -5, -14, 0),
            new KickFilter(0, 90, "Spring Yellow", -26, -5, 0),
            new KickFilter(0, 91, "Yellow", -5, -67, 0),
            new KickFilter(0, 92, "Deepl Amber", -5, 97, 0),
            new KickFilter(0, 93, "Straw Tint", -5, -107, 49),
            new KickFilter(0, 94, "Straw", -5, -32, 118),
            new KickFilter(0, 95, "Pale Amber Gold", -5, -107, 67),
            new KickFilter(0, 96, "No Colour Straw", -5, -44, -114),
            new KickFilter(0, 97, "Slate Blue", 0, 94, -5),
            new KickFilter(0, 98, "Pale Navy Blue", 0, -119, -5),
            new KickFilter(0, 99, "Mist Blue", -84, -23, -5),
            new KickFilter(0, 100, "Pale Blue", 122, -47, -5),
            new KickFilter(0, 101, "Alice Blue", 0, 34, -5),
            new KickFilter(0, 102, "True Blue", 0, 123, -5),
            new KickFilter(0, 103, "Evening Blue", 0, 36, -5),
            new KickFilter(0, 104, "Just Blue", 0, 1, -5),
            new KickFilter(0, 105, "Dark Blue", 0, 0, -4),
            new KickFilter(0, 106, "Deeper Blue", 0, 0, -5),
            new KickFilter(0, 107, "Deep Blue", 12, 0, -5),
            new KickFilter(0, 108, "Regal Blue", 23, 0, -5),
            new KickFilter(0, 109, "Zenith Blue", 2, 0, -5),
            new KickFilter(0, 110, "Palace Blue", 21, 0, -5),
            new KickFilter(0, 111, "Tokyo Blue", 27, 0, -4),
            new KickFilter(0, 112, "Congo Blue", 36, 0, -5),
            new KickFilter(0, 113, "S.M. Lavender", 57, 0, -5),
            new KickFilter(0, 114, "Dark Lavender", 80, 0, -5),
            new KickFilter(0, 115, "Lavender", 111, 0, -5),
            new KickFilter(0, 116, "Pale Violet", 78, 41, -5),
            new KickFilter(0, 117, "Daylight Blue", 0, 81, -5),
            new KickFilter(0, 118, "Sky Blue", 0, 51, -5),
            new KickFilter(0, 119, "Medium Blue", 0, 40, -5),
            new KickFilter(0, 120, "Moonlight Blue", 0, 109, -5),
            new KickFilter(0, 121, "Bright Blue", 0, 97, -5),
            new KickFilter(0, 122, "No Colour Blue", 0, -90, -5),
            new KickFilter(0, 123, "Summer Blue", 0, -70, -5),
            new KickFilter(0, 124, "Lagoon Blue", 0, -83, -5),
            new KickFilter(0, 125, "M. Blue-Green", 0, -5, -35),
            new KickFilter(0, 126, "Peacock Blue", 0, -5, -53),
            new KickFilter(0, 127, "Marine Blue", 0, -5, -54),
            new KickFilter(0, 128, "Special Steel Blue", 0, -21, -5),
            new KickFilter(0, 129, "Steel Blue", 18, -15, -5),
            new KickFilter(0, 130, "Dark Steel Blue", 27, 119, -5),
            new KickFilter(0, 131, "Cornflower", 11, 104, -5),
            new KickFilter(0, 132, "Light Blue", 0, 37, -5),
            new KickFilter(0, 133, "Glacier Blue", 0, 116, -5),
            new KickFilter(0, 134, "Lighter Blue", 0, -65, -5),
            new KickFilter(0, 135, "S.M. Blue", 0, 1, -4),
            new KickFilter(0, 136, "Soft Green", 0, -5, -126),
            new KickFilter(0, 137, "Jade", 0, -5, -128),
            new KickFilter(0, 138, "Green", 1, -5, 15),
            new KickFilter(0, 139, "Fern Green", 0, -5, 25),
            new KickFilter(0, 140, "Dark Green", 0, -5, 28),
            new KickFilter(0, 141, "Primary Green", 0, -5, 0),
            new KickFilter(0, 142, "Forest Green", 0, -5, 53),
            new KickFilter(0, 143, "Pale Green", -125, -5, 59),
            new KickFilter(0, 144, "Flourescent Green", 0, -5, -93),
            new KickFilter(0, 145, "Dark Yellow Green", 0, -5, 7),
            new KickFilter(0, 146, "Moss Green", 0, -5, 19),
            new KickFilter(0, 147, "Lime Green", 104, -5, 11),
            new KickFilter(0, 148, "White Flame Green", -48, -5, -123),
            new KickFilter(0, 149, "Clear", -5, -41, -80),
        };
    }
}
