package com.fw.internal.api.io;

import com.fw.internal.utils.InternalUtils;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public abstract class IoObject {
    public final String fileName;
    public final String fullPath;

    public IoObject(String fileName) {
        this.fileName = fileName;
        fullPath = InternalUtils.getProjectFolder() + File.separator + fileName + ".fw";
    }

    public void internalLoad() {
        Properties p = new Properties();
        File file = new File(fullPath);
        if (!file.exists()) {
            initLoad(p);
        }
        load(p);
        try (FileInputStream in = new FileInputStream(fullPath)) {
            p.load(in);
            load(p);
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "load fail: " + e.getMessage());
        }
    }

    public void internalSave() {
        Properties props = new Properties();

        save(props);

        // 폴더 생성 확인
        File file = new File(fullPath);
        file.getParentFile().mkdirs();

        try (FileOutputStream out = new FileOutputStream(fullPath)) {
            props.store(out, "Data");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "save fail: " + e.getMessage());
        }
    }

    public abstract void save(Properties p);
    public abstract void load(Properties p);
    public abstract void initLoad(Properties p);
}
