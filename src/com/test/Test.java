package com.test;

import com.fw.internal.sys.io.Io;
import com.fw.internal.sys.operator.OperatorManager;
import com.fw.main.*;
import com.fw.main.utils.input.korean.KoreanObject;
import com.fw.main.utils.input.korean.KoreanObjectEventListener;

import java.awt.*;
public class Test extends Base {

    KoreanObject ko = new KoreanObject();

    static {
        Core.setConfig(new
                Config.Builder("fwTest").
                setWindowWidth(1280).
                setWindowHeight(720).
                setUseKoreanModule(true).build()
        );
    }

    public Test() {
        super(new Builder().setIntegerKey(1).setStringKey("1"));
        ko.setFocused(true);
        ko.registerKoreanObjectEventListener(new KoreanObjectEventListener() {
            @Override
            public void enter() {
                System.out.println(ko.getInputText());
                ko.clear();
            }
        });
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
    public void update(double dt) {}

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.setFont(new Font("", Font.BOLD, 18));
        g.drawString("Do Test", 50, 80);

        g.setColor(Color.CYAN);
        g.drawString(ko.getInputText() + "_", 50, 130);
    }

    public static void main(String[] args) {
        new Test();
    }
}