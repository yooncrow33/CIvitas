package com.fw.main.api.io;

import com.fw.internal.sys.io.IoObject;
import com.fw.internal.sys.io.Load;
import com.fw.internal.sys.io.Save;
import com.fw.internal.utils.Internal;

import java.util.ArrayList;

public final class Io {
    public Load load = new Load(this);
    public Save save = new Save(this);
    @Internal
    public ArrayList<IoObject> ioObjects = new ArrayList<>();
    public void addIoObject(String fileName, IoInterface ioInterface) {ioObjects.add(new IoObject(fileName,ioInterface));}
}
