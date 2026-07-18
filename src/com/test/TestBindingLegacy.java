package com.test;

import com.fw.main.utils.input.KeyBindingBase;
import com.fw.main.utils.input.LegacyKeyBindingBase;

import javax.swing.*;
import java.awt.*;

public class TestBindingLegacy extends KeyBindingBase {
    Test test;
    public TestBindingLegacy(Test test) {
        super(test);
        this.test = test;
    }
}
