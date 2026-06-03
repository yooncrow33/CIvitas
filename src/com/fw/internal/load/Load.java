package com.fw.internal.load;

import com.fw.internal.api.LoadObject;
import com.fw.main.fw;

import java.util.ArrayList;

public class Load {
    private static ArrayList<LoadObject> loadObjects = new ArrayList<>();
    public static void addLoadObject(LoadObject loadObject) {
        loadObjects.add(loadObject);
    }

    public static void load() {

        //end
        fw.internal.launch();
    }
}
