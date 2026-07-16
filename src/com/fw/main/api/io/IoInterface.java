package com.fw.main.api.io;

import java.util.Properties;

public interface IoInterface {
    void save(Properties p);
    void load(Properties p);
    void initLoad(Properties p);
}
