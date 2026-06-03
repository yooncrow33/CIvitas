package com.fw.main.graphics;

import java.awt.*;

public class Rect {
    public Color color;
    public int x;
    public int y;
    final int width;
    final int height;

    public Rect(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect(x,y,width,height);
    }
}
