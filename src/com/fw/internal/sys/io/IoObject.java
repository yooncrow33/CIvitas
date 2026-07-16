package com.fw.internal.sys.io;

import com.fw.internal.utils.InternalUtils;
import com.fw.main.api.io.IoInterface;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class IoObject {
    public final String fileName;
    public final String fullPath;
    private final IoInterface ioInterface;

    public IoObject(String fileName, IoInterface ioInterface) {
        this.fileName = fileName;
        this.ioInterface = ioInterface;
        fullPath = InternalUtils.getProjectFolder() + File.separator + fileName + ".fw";
    }

    public void internalLoad() {
        Properties p = new Properties();
        File file = new File(fullPath);
        if (!file.exists()) {
            ioInterface.initLoad(p);
        }
        try (FileInputStream in = new FileInputStream(fullPath)) {
            p.load(in);
            ioInterface.load(p);
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "load fail: " + e.getMessage());
        }
    }

    public void internalSave() {
        Properties props = new Properties();

        ioInterface.save(props);

        File file = new File(fullPath);
        file.getParentFile().mkdirs();

        try (FileOutputStream out = new FileOutputStream(fullPath)) {
            props.store(out, "Data");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "save fail: " + e.getMessage());
        }
    }
}
