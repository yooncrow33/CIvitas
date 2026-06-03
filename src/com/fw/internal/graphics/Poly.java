package com.fw.internal.graphics;

import java.awt.*;

public class Poly {
    public Color color;
    private int xA[];
    private int yA[];
    private final int xP[];
    private final int yP[];
    private int x;
    private int y;

    public Poly(int x, int y, int xA[], int yA[], Color color) {
        this.xP = xA;
        this.yP = yA;
        this.x = x;
        this.y = y;
        this.color = color;
        this.xA = new int[xA.length];
        this.yA = new int[yA.length];
        setXA();
        setYA();
    }

    public void setX(int x) {
        this.x = x;
        setXA();
    }

    public void setY(int y) {
        this.y = y;
        setYA();
    }

    private void setXA() {
        for (int i = 0; xP.length > i; i++) {
            xA[i] = xP[i] + x;
        }
    }

    private void setYA() {
        for (int i = 0; yP.length > i; i++) {
            yA[i] = yP[i] + y;
        }
    }

    public void render(Graphics g) {
        if (xA.length != yA.length) {return;}
        g.setColor(color);
        g.drawPolygon(xA,yA,xA.length);
    }
}
