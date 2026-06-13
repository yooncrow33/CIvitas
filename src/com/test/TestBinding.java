package com.test;

import com.fw.main.utils.input.KeyBindingBase;

import javax.swing.*;

public class TestBinding extends KeyBindingBase {
    public TestBinding(JComponent comp) {
        super(comp);
    }

    @Override
    protected void onKeyAPress() {
        System.out.println("Press A!");
    }
}
