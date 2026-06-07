package com.fw.internal.graphics.object;

import java.awt.*;

public abstract class GraphicsComponent {
    public final int width = 1920;
    public final int height = 1080;
    public abstract void render(Graphics g);
}
