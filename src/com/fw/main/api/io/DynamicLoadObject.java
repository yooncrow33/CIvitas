package com.fw.main.api.io;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DynamicLoadObject {
    private static final ExecutorService loadExecutor = Executors.newSingleThreadExecutor();

    private final AtomicBoolean loadStart = new AtomicBoolean(false);
    private final AtomicBoolean loadEnd = new AtomicBoolean(false);
    private final String fullPath;

    public DynamicLoadObject(String fullPath) {
        this.fullPath = fullPath;
    }

    public void internalLoad() {
        Properties p = new Properties();
        File file = new File(fullPath);
        if (!file.exists()) {
            System.err.println("Not found Path! at: " + fullPath);
            return;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            p.load(in);
            load(p);
        } catch (IOException | NumberFormatException e) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "Load fail: " + e.getMessage())
            );
        }
    }

    public void launch() {
        if (!loadStart.compareAndSet(false, true)) {
            return;
        }

        loadExecutor.submit(() -> {
            try {
                internalLoad();
            } finally {
                loadEnd.set(true);
            }
        });
    }

    public abstract void load(Properties p);

    public boolean isLoaded() {
        return loadEnd.get();
    }
}