package com.fw.main.api.sys.graphics;

import com.fw.main.Base;

import java.awt.*;
import java.awt.image.VolatileImage;
import java.util.LinkedHashMap;
import java.util.Map;

public class LazyCall extends Call {
    private VolatileImage sharedBuffer;
    protected int w = 10;
    protected int h = 10;
    final Base base;

    public LazyCall(Base base, Dirty dirty) {
        super(dirty);
        this.base = base;
    }

    protected Map<Integer, DrawCall> drawCallMap = new LinkedHashMap<>();

    public void addState(int state, CallBuilder builder) {
        drawCallMap.put(state, builder.getCall());
        this.w = builder.width;
        this.h = builder.height;
    }

    @Override
    public void updateCache() {
        if (sharedBuffer == null || isDirty() || sharedBuffer.contentsLost()) {
            this.state = getCurrentState();
            reBake(state);
        }
    }

    @Override
    public VolatileImage getBuffer() {return  sharedBuffer;}

    private void reBake(int state) {
        // 1. 버퍼가 없거나 소실되었으면 새로 생성
        if (sharedBuffer == null || sharedBuffer.contentsLost()) {
            sharedBuffer = base.graphicsConfiguration().createCompatibleVolatileImage(w, h, Transparency.TRANSLUCENT);
        }

        Graphics2D g = sharedBuffer.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.SrcOver);

        DrawCall logic = drawCallMap.get(state);
        if (logic != null) {
            logic.draw(g);
        } else {
            System.err.println("DrawCall interface is null!");
        }

        g.dispose();
        this.state = state; // 상태 업데이트
    }

    public class CallBuilder {
        private int width = 10;
        private int height = 10;
        private DrawCall call;

        public CallBuilder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public CallBuilder drawCall(DrawCall call) {
            this.call = call;
            return this;
        }

        // getter 메서드들
        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public DrawCall getCall() {
            return call;
        }
    }
}
