package com.fw.internal.utils;

import com.fw.main.Core;
import com.fw.main.fw;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InternalUtils {
    public static String getProjectFolder() {
        return System.getProperty("user.home") + File.separator + "." + Core.get().getProjectName();
    }
    public static LocalDateTime currentTime() {
        return LocalDateTime.now();
    }

    public static ZoneId getCurrentTimeZone() {
        return ZoneId.systemDefault();
    }

    public static String getTimeFormate() {
        LocalDateTime localTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localTime.format(formatter);
    }
}
