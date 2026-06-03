package com.fw.internal.utils;

import com.fw.main.fw;

import java.io.File;

public class InternalUtils {
    public static String getProjectFolder() {
        return System.getProperty("user.home") + File.separator + "." + fw.projectName;
    }
    public static void launch() {

    }
}
