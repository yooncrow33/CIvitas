package com.fw.internal.sys.io;

import com.fw.main.api.io.IoObject;
import com.fw.internal.utils.InternalUtils;

import java.util.ArrayList;

public final class Save {
    private boolean saveStart = false;
    private boolean saveEnd = false;
    private int progress = 0;
    private int maxProgress = 0;
    final Io io;
    public Save(Io io) {this.io = io;}

    public boolean isSaveEnd() {
        return saveEnd;
    }

    public boolean isSaveStart() {
        return saveStart;
    }

    public void addSaveObject(IoObject ioObject) {
        io.ioObjects.add(ioObject);
    }

    public float getProgress() {
        return (float) progress / maxProgress * 100;
    }

    public void save() {
        saveStart = true;
        maxProgress = io.ioObjects.size();
        System.out.println(InternalUtils.Time.getTimeFormate() + " / start save");
        for (IoObject l : io.ioObjects) {
            l.internalSave();
            progress++;

        }
        saveEnd = true;
        System.out.println(InternalUtils.Time.getTimeFormate() + " / end save");
    }
}
