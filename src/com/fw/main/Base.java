package com.fw.main;

import com.fw.internal.graphics.object.GraphicsComponent;
import com.fw.internal.sys.io.Io;
import com.fw.internal.sys.operator.OperatorManager;
import com.fw.internal.sys.view.IFrameSize;
import com.fw.internal.sys.view.ViewMetrics;
import com.fw.internal.utils.InternalUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Base extends JPanel implements IFrameSize {
    public JFrame frame = new JFrame("Forward Swing Program.");
    private ScheduledExecutorService executor;
    private long lastTime;
    private final Mouse mouse = new Mouse(this);
    public Mouse getMouse() {return mouse;}
    private final ViewMetrics viewMetrics;
    protected final Io io = new Io();
    private final OperatorManager operatorManager = new OperatorManager();

    public GraphicsComponent loadingComponent = null;

    public Base(Builder builder) {
        if (!Core.isIsSetConfig()) {
            System.err.println("config is null! you should init config to Core.java in the static block!");
            System.exit(0);
        }
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(true);

        frame.setPreferredSize((new Dimension(Core.get().initWindowWidth,Core.get().getInitWindowHeight())));
        setFocusable(true);

        viewMetrics = new ViewMetrics(this);

        frame.add(this);
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.requestFocus();
        frame.pack();

        setBackground(Color.BLACK);

        viewMetrics.calculateViewMetrics();

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                viewMetrics.updateVirtualMouse(e.getX(),e.getY());
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                viewMetrics.calculateViewMetrics();
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exit();
            }
        });

        Fw.add(builder.integerKey,this);
        Fw.add(builder.stringKey, this);

        init(io, operatorManager);

        startGameLoop();

        new Thread(() -> {
            io.load.load();
        }).start();
    }

    public static class Builder {
        String stringKey;
        Integer integerKey;

        public Builder setStringKey(String stringKey) {
            this.stringKey = stringKey;
            return this;
        }

        public Builder setIntegerKey(Integer integerKey) {
            this.integerKey = integerKey;
            return this;
        }
    }

    public class Mouse {
        final Base base;

        public Mouse(Base base) {
            this.base = base;
        }

        public int x() {return base.getMouseX();}
        public int y() {return  base.getMouseY();}
    }

    private void startGameLoop() {
        System.out.println(InternalUtils.Time.getTimeFormate() + " / thread start");
        executor = Executors.newSingleThreadScheduledExecutor();
        lastTime = System.nanoTime();

        executor.scheduleAtFixedRate(() -> {
            try {
                long now = System.nanoTime();
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                if (io.load.isLoadEnd()) {
                    update(deltaTime);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            SwingUtilities.invokeLater(this::repaint);

        }, 0, 16, TimeUnit.MILLISECONDS);
    }

    public abstract void init(Io io, OperatorManager operators);
    public abstract void update(double dt);
    public abstract void render(Graphics g);

    @Override public int getComponentWidth() { return this.getWidth(); }
    @Override public int getComponentHeight() { return this.getHeight(); }

    public int getMouseX() {return viewMetrics.getVirtualMouseX();}
    public int getMouseY() {return viewMetrics.getVirtualMouseY();}

    public void exit() {
        //System.out.println("exit now..");

        io.save.save();
        operatorManager.exitOperatorPack.launch();

        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            //System.out.println("exit.");
        }

        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    private void renderLoadingScreen(Graphics g) {
        if (loadingComponent != null) {
            loadingComponent.render(g);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D d2 = (Graphics2D) g;

        d2.translate(viewMetrics.getCurrentXOffset(), viewMetrics.getCurrentYOffset());
        d2.scale(viewMetrics.getCurrentScale(), viewMetrics.getCurrentScale());

        if (!io.load.isLoadEnd()) {
            renderLoadingScreen(g);
        } else {
            render(g);
        }

        if (Fw.Debugger.showHitbox) {
            Fw.Debugger.Internal.renderHitbox(g);
        }
    }
}