package com.test;

import com.fw.main.api.io.Io;
import com.fw.internal.sys.operator.OperatorManager;
import com.fw.main.*;
import com.fw.main.api.io.IoInterface;
import com.fw.main.utils.input.korean.KoreanObject;
import com.fw.main.utils.input.korean.KoreanObjectEventListener;
import com.fw.main.utils.input.mouse.FwMouseAPI;
import com.fw.main.utils.input.mouse.MouseInterface;

import java.awt.*;
import java.util.Properties;

public class Test extends Base {
    KoreanObject ko = new KoreanObject();
    float aFloat;

    static {
        Core.setConfig(new
                Config.Builder("projectName"). // = folder name.
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

        io.addIoObject("default", new IoInterface() {
            @Override
            public void save(Properties p) {
                p.setProperty("float", Float.toString(aFloat));
            }

            @Override
            public void load(Properties p) {
                aFloat = Float.parseFloat((String) p.get("float"));
            }

            @Override
            public void initLoad(Properties p) {
                aFloat = (float) Math.random();
            }
        });

        registerMouseInterface(new MouseInterface() {
            @Override
            public void mouseClicked(FwMouseAPI e) {
                System.out.println("Click!");
                System.out.println("from :" + e.getButton());
            }

            @Override
            public void mousePressed(FwMouseAPI e) {
                if(e.isDoubleClick()) {
                    System.out.println("Double CLick!");
                }
            }

            @Override
            public void mouseReleased(FwMouseAPI e) {
                //other implements.....
            }

            @Override
            public void mouseEntered(FwMouseAPI e) {

            }

            @Override
            public void mouseExited(FwMouseAPI e) {

            }

            @Override
            public void mouseWheelMoved(FwMouseAPI e) {

            }
        });
    }

    @Override
    public void update(double dt) {
        aFloat = (float) Math.random();
    }

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