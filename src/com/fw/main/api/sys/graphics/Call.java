package com.fw.main.api.sys.graphics;

import java.awt.image.VolatileImage;

public abstract class Call {
    protected int state = -1;
    private final Dirty dirty;

    public Call(Dirty dirty) {
        this.dirty = dirty;
    }
    public boolean isDirty() {return state!=dirty.getState();}
    public int getCurrentState() {return dirty.getState();}

    public abstract void updateCache();
    public abstract VolatileImage getBuffer();
}