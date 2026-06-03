package com.fw.main;

import com.fw.internal.load.Load;
import com.fw.internal.sys.view.IFrameSize;
import com.fw.internal.sys.view.ViewMetrics;
import com.fw.main.graphics.Rect;
import com.fw.main.graphics.object.FlashScreen;

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
    public JFrame frame = new JFrame("powered by Forward Swing");
    private long lastTime;
    private final ViewMetrics viewMetrics;
    public static int state = 0;

    private final FlashScreen flashScreen;

    private int tick = 0;

    // 기존 데이터 체계 호환 유지를 위한 수치용 Rect
    private Rect left = new Rect(0, 0, 20, 1080, Color.white);
    private Rect right = new Rect(1900, 0, 20, 1080, Color.white);

    /*
        public static void follow(int targetX, int targetY) {
        x += (int) ((targetX - 40 - x) * lerp);
        y += (int) ((targetY - 40 - y) * lerp);
    }
     */
    public Base() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // 플래시 스크린 엔진 생성
        this.flashScreen = new FlashScreen();

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

        startGameLoop();
    }

    private void startGameLoop() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        lastTime = System.nanoTime();

        executor.scheduleAtFixedRate(() -> {
            try {
                long now = System.nanoTime();
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                // 분리된 클래스의 내부 상수 속도 축 계산 추출
                double scaledDelta = deltaTime * FlashScreen.SPEED_MULTIPLIER;

                // 모듈 내부 수치 애니메이션 업데이트 수행
                flashScreen.update(state, scaledDelta);

                if (state == 0) {
                    // 글자 고정 대기 틱 보정 연산
                    if (tick >= (75 / FlashScreen.SPEED_MULTIPLIER)) {
                        state = 1;
                    } else {
                        tick++;
                    }
                } else if (state == 1) {
                    // 고정 상태 머신 추적 로직 유지
                    left.x += (int) ((1900 - left.x) * 0.05 * FlashScreen.SPEED_MULTIPLIER);
                    right.x += (int) ((0 - right.x) * 0.05 * FlashScreen.SPEED_MULTIPLIER);

                    System.out.println(left.x);
                    System.out.println(right.x);

                    // 셔터 닫힌 뒤 다음 로딩으로 텀 없이 바로 진입하도록 동기화 컷오프
                    if (left.x >= 1830) {
                        state = 2;
                    }
                } else if (state == 2) {
                    Load.load();
                } else if (state == 3) {
                    update(deltaTime);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            SwingUtilities.invokeLater(this::repaint);

        }, 0, 16, TimeUnit.MILLISECONDS);
    }

    private void renderFlashScreenGraphics(Graphics g) {
        // paintComponent에서 변환된 Graphics2D 인스턴스를 모듈에 그대로 바인딩 위임
        flashScreen.render((Graphics2D) g, state);
    }

    public abstract void update(double dt);
    public abstract void render(Graphics g);

    @Override public int getComponentWidth() { return this.getWidth(); }
    @Override public int getComponentHeight() { return this.getHeight(); }

    public int getMouseX() {return viewMetrics.getVirtualMouseX();}
    public int getMouseY() {return viewMetrics.getVirtualMouseY();}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D d2 = (Graphics2D) g;

        d2.translate(viewMetrics.getCurrentXOffset(), viewMetrics.getCurrentYOffset());
        d2.scale(viewMetrics.getCurrentScale(), viewMetrics.getCurrentScale());

        if (state == 1 || state == 0) {
            renderFlashScreenGraphics(d2);
        } else if (state == 2) {
            renderFlashScreenGraphics(d2);
        } else if (state == 3) {
            render(g);
        }

    }
}