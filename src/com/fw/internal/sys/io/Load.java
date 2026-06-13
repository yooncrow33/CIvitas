package com.fw.internal.sys.io;

import com.fw.main.api.io.IoObject;
import com.fw.internal.utils.InternalUtils;

import java.util.ArrayList;

public final class Load {
    private boolean loadStart = false;
    private boolean loadEnd = false;
    private int progress = 0;
    private int maxProgress = 0;
    final Io io;
    public Load(Io io) {this.io = io;}

    public boolean isLoadEnd() {
        return loadEnd;
    }

    public boolean isLoadStart() {
        return loadStart;
    }

    public void addLoadObject(IoObject ioObject) {
        if (loadEnd) {
            System.err.println(InternalUtils.Time.getTimeFormate() + " / add loadObject to loadObject Array in after load!");
            return;
        }
        io.ioObjects.add(ioObject);
    }

    public float getProgress() {
        return (float) progress / maxProgress * 100;
    }

    public void load() {
        loadStart = true;
        maxProgress = io.ioObjects.size();
        System.out.println(InternalUtils.Time.getTimeFormate() + " / start load");
        for (IoObject l : io.ioObjects) {
            l.internalLoad();
            progress++;

        }
        loadEnd = true;
        System.out.println(InternalUtils.Time.getTimeFormate() + " / end load");
    }
}
