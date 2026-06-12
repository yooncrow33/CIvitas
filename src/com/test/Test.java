package com.test;

import com.fw.internal.sys.io.Io;
import com.fw.internal.sys.io.Load;
import com.fw.internal.sys.operator.OperatorManager;
import com.fw.main.*;

import java.awt.*;

public class Test extends Base {

    static {
        Core.setConfig(new Config(new
                Config.Builder("fwTest").
                setWindowWidth(640).
                setWindowHeight(340)
        ));
    }

    public Test() {
        super(new Builder().setIntegerKey(1).setStringKey("1"));
    }

    @Override
    public void init(Io io, OperatorManager operatorManager) {
        operatorManager.exitOperatorPack.addOperator(new Operator() {
            @Override
            public void exe() {
                System.out.println("exit");
            }
        });
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render(Graphics g) {

    }

    public static void main(String[] args) {
        new Test();
    }
}
