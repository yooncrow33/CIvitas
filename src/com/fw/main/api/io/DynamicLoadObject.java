package com.fw.main.api.io;

import com.fw.internal.utils.InternalUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

abstract public class DynamicLoadObject {
    boolean loadStart = false;
    boolean loadEnd = false;
    final String fullPath;
    public DynamicLoadObject(String fullPath) {
        this.fullPath = fullPath;
    }
    public void internalLoad() {
        Properties p = new Properties();
        File file = new File(fullPath);
        if (!file.exists()) {
            System.err.println("Not found Path! at: " + fullPath);
        }
        try (FileInputStream in = new FileInputStream(fullPath)) {
            p.load(in);
            load(p);
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "load fail: " + e.getMessage());
        }
    }
    public void launch() {
        new Thread(() -> {
            loadStart = true;
            //load();
            loadEnd = true;
        });
    }
    public abstract void load(Properties p);
}
