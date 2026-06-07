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
    private final ViewMetrics viewMetrics;
    protected final Io io = new Io();
    private OperatorManager operatorManager = new OperatorManager();

    public GraphicsComponent loadingComponent = null;

    public Base() {
        if (!Core.isIsSetConfig()) {
            System.err.println("config is null!");
            System.exit(0);
        }
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(true);

        frame.setPreferredSize((new Dimension(1280,720)));
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

        // ★ 사용자가 창 창닫기(X) 버튼을 눌렀을 때도 exit() 로직이 수행되도록 리스너 추가
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exit();
            }
        });

        init(io, operatorManager);

        startGameLoop();

        new Thread(() -> {
            io.load.load();
        }).start();
    }

    private void startGameLoop() {
        System.out.println(InternalUtils.getTimeFormate() + " / thread start");
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
        System.out.println("Base 인스턴스 종료 시작...");

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
            System.out.println("스레드가 성공적으로 종료되었습니다.");
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
    }
}