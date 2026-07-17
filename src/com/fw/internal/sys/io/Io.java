package com.fw.internal.sys.io;

import com.fw.main.api.io.IoInterface;

import java.util.ArrayList;

public class Io {
    public Load load = new Load(this);
    public Save save = new Save(this);
    ArrayList<IoObject> ioObjects = new ArrayList<>();
    public void addIoObject(String fileName, IoInterface ioInterface) {ioObjects.add(new IoObject(fileName,ioInterface));}
}
