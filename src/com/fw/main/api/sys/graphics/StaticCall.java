package com.fw.main.api.sys.graphics;

import com.fw.main.Base;

import java.awt.*;
import java.awt.image.VolatileImage;
import java.util.LinkedHashMap;
import java.util.Map;

public class StaticCall extends Call {
    private Map<Integer, VolatileImage> cache = new LinkedHashMap<>();
    protected Map<Integer, DrawCall> drawCallMap = new LinkedHashMap<>();
    protected int w = 10;
    protected int h = 10;
    final Base base;

    public StaticCall(Dirty dirty, Base base) {
        super(dirty);
        this.base = base;
    }

    @Override
    public void updateCache() {
        int currentState = getCurrentState();
        VolatileImage img = cache.get(currentState);

        if (isDirty() || img == null || img.contentsLost()) {
            this.state = currentState;
            if (!cache.containsKey(state)) {
                bake(state);
            }
        }


    }

    @Override
    public VolatileImage getBuffer() {return cache.get(state);}

    private void bake(int state) {
        VolatileImage img = cache.get(state);
        if (img == null || img.contentsLost()) {
            img = base.getGraphicsConfiguration().createCompatibleVolatileImage(w, h, Transparency.TRANSLUCENT);

            Graphics2D g = img.createGraphics();
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, w, h);
            g.setComposite(AlphaComposite.SrcOver);

            drawCallMap.get(state).draw(g);
            g.dispose();

            cache.put(state, img);
        }
    }
}
