package com.fw.internal.sys.io;

import com.fw.internal.api.io.IoObject;
import com.fw.internal.utils.InternalUtils;

import java.util.ArrayList;

public final class Load {
    private ArrayList<IoObject> ioObjects = new ArrayList<>();
    private boolean loadStart = false;
    private boolean loadEnd = false;
    private int progress = 0;
    private int maxProgress = 0;

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
        ioObjects.add(ioObject);
    }

    public float getProgress() {
        return (float) progress / maxProgress * 100;
    }

    public void load() {
        loadStart = true;
        maxProgress = ioObjects.size();
        System.out.println(InternalUtils.Time.getTimeFormate() + " / start load");
        for (IoObject l : ioObjects) {
            l.internalLoad();
            progress++;

        }
        loadEnd = true;
        System.out.println(InternalUtils.Time.getTimeFormate() + " / end load");
    }
}
