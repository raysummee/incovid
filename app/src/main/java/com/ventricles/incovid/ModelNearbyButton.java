package com.ventricles.incovid;

public class ModelNearbyButton {
    private int background;
    private int draw_src;
    private String type;


    public ModelNearbyButton(int background, int draw_src, String type) {
        this.background = background;
        this.draw_src = draw_src;
        this.type = type;
    }

    public int getBackground() {
        return background;
    }

    public int getDraw_src() {
        return draw_src;
    }

    public String getType() {
        return type;
    }
}
