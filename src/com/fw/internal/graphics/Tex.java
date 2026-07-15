package com.fw.internal.graphics;

import com.fw.main.utils.graphics.RU;

import java.awt.*;

public class Tex {
    final String text;
    final Font fontObject;
    public Color color;
    public int x;
    public int y;
    final boolean centering;

    public Tex(boolean centering,String text, Font font, int x, int y, Color color) {
        this.text = text;
        this.centering = centering;
        this.x = x;
        this.y = y;
        fontObject = font;
        this.color = color;
    }

    public void render(Graphics g) {
        g.setColor(color);
        g.setFont(fontObject);
        if (centering) {
            RU.drawStringCenter(g,text,x,y);
        } else {
            g.drawString(text, x, y);
        }
    }
}
