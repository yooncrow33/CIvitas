package com.fw.main;

import com.fw.internal.utils.InternalUtils;

public class fw {
    private static boolean isSetProjectName = false;
    public static String projectName;
    public static void initProjectName(String name) {
        if (!isSetProjectName) {
            projectName = name;
            isSetProjectName = true;
        } else {
            System.err.println("Double Called 'initProjectName(String name)'");
            System.exit(0);
        }
    }
    public static InternalUtils internal;
}
