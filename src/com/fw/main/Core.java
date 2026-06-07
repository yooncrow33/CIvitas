package com.fw.main;

public class Core {
    public static Config get() {return config;}
    private static Config config = null;
    private static boolean isSetConfig = false;
    public static void setConfig(Config config) {
        if (!isSetConfig) {
            isSetConfig = true;
            Core.config = config;
        }
    }

    public static boolean isIsSetConfig() {
        return isSetConfig;
    }
}
