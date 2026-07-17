package com.fw.main;

import com.fw.internal.graphics.object.GraphicsComponent;
import com.fw.internal.sys.io.Io;
import com.fw.internal.sys.operator.OperatorManager;
import com.fw.internal.sys.view.IFrameSize;
import com.fw.internal.sys.view.ViewMetrics;
import com.fw.internal.utils.InternalUtils;
import com.fw.main.utils.input.korean.KoreanModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

public abstract class Base extends Canvas implements IFrameSize {
    public JFrame frame = new JFrame("Civitas Engine");

    private Thread logicThread;
    private Thread renderThread;
    private volatile boolean running = false;

    private final Mouse mouse = new Mouse(this);
    public Mouse getMouse() { return mouse; }
    private final ViewMetrics viewMetrics;
    protected final Io io = new Io();
    private final OperatorManager operatorManager = new OperatorManager();

    private BufferStrategy bufferStrategy;
    private VolatileImage vramBuffer;

    public GraphicsComponent loadingComponent = null;
    private KoreanModule koreanModule;

    public Base(Builder builder) {
        if (!Core.isIsSetConfig()) {
            System.err.println("config is null! you should init config to Core.java in the static block!");
            System.exit(0);
        }

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(true);

        this.setPreferredSize(new Dimension(Core.get().initWindowWidth, Core.get().getInitWindowHeight()));
        setFocusable(true);

        viewMetrics = new ViewMetrics(this);

        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        this.requestFocus();

        setBackground(Color.BLACK);
        viewMetrics.calculateViewMetrics();

        this.createBufferStrategy(2);
        this.bufferStrategy = this.getBufferStrategy();

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                viewMetrics.updateVirtualMouse(e.getX(), e.getY());
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

        if (Core.get().isUseKoreanModule()) {
            koreanModule = new KoreanModule(this);
        }

        Fw.add(builder.integerKey, this);
        Fw.add(builder.stringKey, this);

        init(io, operatorManager);

        launch();

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

        public int x() { return base.getMouseX(); }
        public int y() { return base.getMouseY(); }
    }

    private void launch() {
        System.out.println(InternalUtils.Time.getTimeFormate() + " / logic thread start");

        running = true;
        logicThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            final double targetFps = 60.0;
            final long nsPerTick = (long) (1000000000.0 / targetFps);

            while (running) {
                long now = System.nanoTime();
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                try {
                    if (io.load.isLoadEnd()) {
                        update(deltaTime);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }

                long timeTaken = System.nanoTime() - now;
                long timeLeftNs = nsPerTick - timeTaken;

                if (timeLeftNs > 2_000_000) {
                    try {
                        Thread.sleep((timeLeftNs - 2_000_000) / 1_000_000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        running = false;
                    }
                }

                while (System.nanoTime() - now < nsPerTick) {
                    Thread.yield();
                }
            }
        });

        logicThread.setName("logicLoop");
        logicThread.start();

        System.out.println(InternalUtils.Time.getTimeFormate() + " / render thread start");

        running = true;
        renderThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            final double targetFps = 60.0;
            final long nsPerTick = (long) (1000000000.0 / targetFps);

            while (running) {
                long now = System.nanoTime();
                lastTime = now;

                try {
                    renderLoop();
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                long timeTaken = System.nanoTime() - now;
                long timeLeftNs = nsPerTick - timeTaken;

                if (timeLeftNs > 2_000_000) {
                    try {
                        Thread.sleep((timeLeftNs - 2_000_000) / 1_000_000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        running = false;
                    }
                }

                while (System.nanoTime() - now < nsPerTick) {
                    Thread.yield();
                }
            }
        });

        renderThread.setName("renderLoop");
        renderThread.start();
    }

    private void renderLoop() {
        //RenderThread안에서는 Atomic변수들만 참조하도록 권장.
        if (bufferStrategy == null) return;

        int currentWidth = getWidth();
        int currentHeight = getHeight();

        if (currentWidth <= 0 || currentHeight <= 0) return;

        if (vramBuffer == null ||
                vramBuffer.getWidth() != currentWidth ||
                vramBuffer.getHeight() != currentHeight ||
                vramBuffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {

            vramBuffer = getGraphicsConfiguration().createCompatibleVolatileImage(currentWidth, currentHeight);
        }

        do {
            if (vramBuffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_RESTORED) {
                // VRAM 복구 이벤트 발생 시 필요한 처리
            }

            Graphics2D d2 = vramBuffer.createGraphics();
            try {
                d2.setColor(Color.BLACK);
                d2.fillRect(0, 0, currentWidth, currentHeight);

                d2.translate(viewMetrics.getCurrentXOffset(), viewMetrics.getCurrentYOffset());
                d2.scale(viewMetrics.getCurrentScale(), viewMetrics.getCurrentScale());

                if (!io.load.isLoadEnd()) {
                    renderLoadingScreen(d2);
                } else {
                    render(d2);
                }

                if (Fw.Debugger.showHitbox) {
                    Fw.Debugger.Internal.renderHitbox(d2);
                }

            } finally {
                d2.dispose();
            }

            Graphics hwGraphics = bufferStrategy.getDrawGraphics();
            try {
                hwGraphics.drawImage(vramBuffer, 0, 0, null);
            } finally {
                hwGraphics.dispose();
            }

            bufferStrategy.show();

        } while (vramBuffer.contentsLost());
    }

    public abstract void init(Io io, OperatorManager operators);
    public abstract void update(double dt);
    public abstract void render(Graphics g);

    @Override public int getComponentWidth() { return this.getWidth(); }
    @Override public int getComponentHeight() { return this.getHeight(); }

    public int getMouseX() { return viewMetrics.getVirtualMouseX(); }
    public int getMouseY() { return viewMetrics.getVirtualMouseY(); }

    public void save() {
        io.save.save();
    }

    public void exit() {
        save();
        operatorManager.exitOperatorPack.launch();

        running = false;
        if (logicThread != null) {
            try {
                logicThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (renderThread != null) {
                try {
                    (renderThread).join(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
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
    public java.awt.im.InputMethodRequests getInputMethodRequests() {
        return new java.awt.im.InputMethodRequests() {
            @Override public java.awt.font.TextHitInfo getLocationOffset(int x, int y) { return null; }
            @Override public java.awt.Rectangle getTextLocation(java.awt.font.TextHitInfo offset) {
                return new java.awt.Rectangle(50, 130, 0, 0);
            }
            @Override public java.text.AttributedCharacterIterator getSelectedText(
                    java.text.AttributedCharacterIterator.Attribute[] attributes) { return null; }
            @Override public java.text.AttributedCharacterIterator
            getCommittedText(int beginIndex, int endIndex, java.text.AttributedCharacterIterator.Attribute[] attributes)
            { return null; }
            @Override public int getCommittedTextLength() { return 0; }
            @Override public int getInsertPositionOffset() { return 0; }
            @Override public java.text.AttributedCharacterIterator
            cancelLatestCommittedText(java.text.AttributedCharacterIterator.Attribute[] attributes) { return null; }
        };
    }
}
//-Dsun.java2d.opengl=true