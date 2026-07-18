package com.fw.internal.sys.io;

import com.fw.internal.utils.InternalUtils;
import com.fw.main.api.io.Io;

public final class Save {
    private int progress = 0;
    private int maxProgress = 0;
    final Io io;
    public Save(Io io) {this.io = io;}

    public float getProgress() {
        return (float) progress / maxProgress * 100;
    }

    public void save() {
        maxProgress = io.ioObjects.size();
        System.out.println(InternalUtils.Time.getTimeFormate() + " / start save");
        for (IoObject l : io.ioObjects) {
            l.internalSave();
            progress++;

        }
        System.out.println(InternalUtils.Time.getTimeFormate() + " / end save");
    }
}
