package com.fw.internal.sys.io;

import com.fw.internal.api.io.IoObject;
import com.fw.internal.utils.InternalUtils;

import java.util.ArrayList;

public final class Save {
    private ArrayList<IoObject> ioObjects = new ArrayList<>();
    private boolean saveStart = false;
    private boolean saveEnd = false;
    private int progress = 0;
    private int maxProgress = 0;

    public boolean isSaveEnd() {
        return saveEnd;
    }

    public boolean isSaveStart() {
        return saveStart;
    }

    public void addSaveObject(IoObject ioObject) {
        if (saveEnd) {
            System.err.println(InternalUtils.Time.getTimeFormate() + " / add saveObject to saveObject Array in after save!");
            return;
        }
        ioObjects.add(ioObject);
    }

    public float getProgress() {
        return (float) progress / maxProgress * 100;
    }

    public void save() {
        saveStart = true;
        maxProgress = ioObjects.size();
        System.out.println(InternalUtils.Time.getTimeFormate() + " / start save");
        for (IoObject l : ioObjects) {
            l.internalSave();
            progress++;

        }
        saveEnd = true;
        System.out.println(InternalUtils.Time.getTimeFormate() + " / end save");
    }
}
