package com.fw.main.utils;

import com.fw.internal.utils.InternalUtils;
import com.fw.main.Fw;

import java.awt.*;
import java.util.UUID;

public class Hitbox {
    private int x,y,width,height;
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public final UUID id = UUID.randomUUID();

    public Hitbox(Builder builder) {
        x = builder.x;
        y = builder.y;
        width = builder.width;
        height = builder.height;
        Fw.Debugger.Internal.addObject(this);
    }

    public void renderHitbox(Graphics g) {
        g.setColor(Color.green);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(InternalUtils.getBasicStroke());
        g2.drawRect(x,y,width,height);
    }

    public void Free() {
        Fw.Debugger.Internal.freeObject(this);
    }

    public static class Builder {
        private int x = 10;
        private int y = 10;
        private int width = 10;
        private int height = 10;

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }
    }
}
