package com.fw.main.graphics.object;

import com.fw.main.graphics.Tex;
import com.fw.main.graphics.Poly;
import java.awt.*;

public class FlashScreen {
    // 속도 제어 핵심 상수
    public static final float SPEED_MULTIPLIER = 1.0f;

    private Tex main = new Tex(true, "Forward Swing", new Font("Impact", Font.BOLD, 130), 1920 / 2, 500, Color.white);
    private Tex un = new Tex(true, "Powered By FW", new Font("Impact", Font.PLAIN, 45), 1920 / 2, 600, Color.white);
    private Tex l = new Tex(true, "Load..", new Font("Impact", Font.PLAIN, 28), 1920 / 2, 900, Color.white);

    private Poly[] gridPolys = new Poly[4];
    private Poly shutterLeft;
    private Poly shutterRight;

    private float globalAlpha = 0.0f;
    private double animTime = 0;
    private double shutterOffset = 0;
    private int shakeOffset = 0;

    public FlashScreen() {
        // 완벽한 대칭 육각형 좌표계 설정
        int[] hexX = {0, 130, 130, 0, -130, -130};
        int[] hexY = {-150, -75, 75, 150, 75, -75};

        for (int i = 0; i < gridPolys.length; i++) {
            int[] scaledX = new int[hexX.length];
            int[] scaledY = new int[hexY.length];
            double scale = 1.0 + (i * 1.3);
            for (int j = 0; j < hexX.length; j++) {
                scaledX[j] = (int) (hexX[j] * scale);
                scaledY[j] = (int) (hexY[j] * scale);
            }
            gridPolys[i] = new Poly(1920 / 2, 540, scaledX, scaledY, new Color(255, 255, 255, 15));
        }

        int[] sLeftX = {-1200, 0, -300, -1200};
        int[] sLeftY = {0, 0, 1080, 1080};
        shutterLeft = new Poly(0, 0, sLeftX, sLeftY, new Color(20, 20, 22));

        int[] sRightX = {1200, 300, 1200, 1200};
        int[] sRightY = {0, 0, 1080, 1080};
        shutterRight = new Poly(1920, 0, sRightX, sRightY, new Color(35, 35, 38));
    }

    /**
     * Base 클래스의 루프 안에서 비주얼 관련 수치 변동을 처리하는 메서드
     */
    public void update(int state, double scaledDelta) {
        animTime += scaledDelta;

        if (state == 0) {
            if (globalAlpha < 1.0f) {
                globalAlpha += 0.05f * SPEED_MULTIPLIER;
                if (globalAlpha > 1.0f) globalAlpha = 1.0f;
            }

            for (int i = 0; i < gridPolys.length; i++) {
                int dynamicY = 540 + (int) (Math.sin(animTime * 2.5 + i) * 10);
                gridPolys[i].setY(dynamicY);
            }
        } else if (state == 1) {
            // 화면 진동 연출용 셰이크 값 계산 (Base 내부의 left.x 보간 추적 대신 시간축 활용 계산)
            shakeOffset = (int) (Math.sin(animTime * 50) * 8);

            // 셔터의 이동 가속도 반영
            shutterOffset += (75 * 60 * scaledDelta);
            shutterLeft.setX((int) shutterOffset);
            shutterRight.setX(1920 - (int) shutterOffset);

            // 글씨 페이드아웃 속도 제어
            globalAlpha += (0.0f - globalAlpha) * 0.35 * SPEED_MULTIPLIER;
        } else if (state == 2) {
            shakeOffset = 0; // 로딩 진입 시 진동 초기화
        }
    }

    public void render(Graphics2D g2d, int state) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 무채색 코어 컬러 배경 클리어
        g2d.setColor(new Color(10, 10, 11));
        g2d.fillRect(0, 0, 1920, 1080);

        // 화면 흔들림 트랜스레이트 적용
        if (state == 1) {
            g2d.translate(0, shakeOffset);
        }

        int alphaVal = Math.max(0, Math.min(255, (int) (globalAlpha * 255)));

        if (state == 0 || state == 1) {
            // 무채색 헥사곤 그리드 렌더링
            for (int i = 0; i < gridPolys.length; i++) {
                gridPolys[i].color = new Color(255, 255, 255, (int) (alphaVal * 0.08f));
                gridPolys[i].render(g2d);
            }

            // 가로 세로 가이드라인
            g2d.setColor(new Color(255, 255, 255, (int) (alphaVal * 0.12f)));
            g2d.drawLine(0, 540, 1920, 540);
            g2d.drawLine(1920 / 2, 0, 1920 / 2, 1080);

            // 고해상도 그림자 입체 표현 및 텍스트 출력
            main.color = new Color(80, 80, 80, (int) (alphaVal * 0.3f));
            main.render(g2d);

            main.color = new Color(255, 255, 255, alphaVal);
            main.render(g2d);

            un.color = new Color(180, 180, 180, alphaVal);
            un.render(g2d);
        }

        if (state == 1 || state == 2) {
            shutterLeft.render(g2d);
            shutterRight.render(g2d);

            // 메탈릭 셔터 경계선 슬릿
            g2d.setColor(new Color(220, 220, 225, 200));
            g2d.drawLine((int) shutterOffset, 0, (int) shutterOffset - 300, 1080);
            g2d.drawLine(1920 - (int) shutterOffset, 0, 2220 - (int) shutterOffset, 1080);
        }

        // 트랜스레이트 복원
        if (state == 1) {
            g2d.translate(0, -shakeOffset);
        }

        if (state == 2) {
            int blink = (int) (130 + 125 * Math.sin(animTime * 8));
            blink = Math.max(0, Math.min(255, blink));

            // 무채색 인디케이터 바
            g2d.setColor(new Color(255, 255, 255, blink / 6));
            g2d.fillRect(1920 / 2 - 200, 840, 400, 3);
            g2d.setColor(new Color(255, 255, 255, blink));
            g2d.fillRect(1920 / 2 - 80, 840, 160, 3);

            l.color = new Color(240, 240, 240, blink);
            l.render(g2d);
        }
    }
}