package com.test;

import com.fw.internal.sys.io.Io;
import com.fw.internal.sys.operator.OperatorManager;
import com.fw.main.*;

import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.AttributedCharacterIterator;

public class Test extends Base {

    // 1. 텍스트를 저장할 변수들
    private final StringBuilder textBuffer = new StringBuilder(); // 완성된 글자들
    private String composingText = ""; // 현재 조합 중인 글자

    static {
        Core.setConfig(new Config(new
                Config.Builder("fwTest").
                setWindowWidth(640).
                setWindowHeight(340)
        ));
    }

    // ★ [핵심] super()가 호출되기 "직전"과 "직후"에 IME 설정을 확실하게 박아넣기 위한 초기화 블록
    {
        // 부모 생성자가 실행되기 전에 미리 IME 허용 상태로 세팅 시도
        this.enableInputMethods(true);
    }

    public Test() {
        super(new Builder().setIntegerKey(1).setStringKey("1"));

        // 프레임이 다 뜬 후에도 확실하게 다시 한번 IME 활성화 및 포커스 선점
        this.enableInputMethods(true);
        this.requestFocusInWindow();

        // IME(한글 조합) 리스너 등록
        this.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                AttributedCharacterIterator text = event.getText();
                if (text != null) {
                    int committedCharacterCount = event.getCommittedCharacterCount();
                    char c = text.first();

                    // 완성된 글자 처리
                    StringBuilder committed = new StringBuilder();
                    for (int i = 0; i < committedCharacterCount; i++) {
                        committed.append(c);
                        c = text.next();
                    }
                    textBuffer.append(committed.toString());

                    // 조합 중인 글자 처리
                    StringBuilder composing = new StringBuilder();
                    while (c != AttributedCharacterIterator.DONE) {
                        composing.append(c);
                        c = text.next();
                    }
                    composingText = composing.toString();
                } else {
                    composingText = "";
                }
                event.consume();
                repaint(); // 즉시 화면 갱신 유도
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {
            }
        });

        // 지우기(Backspace) 처리를 위한 키 리스너 등록
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (composingText.length() > 0) {
                        composingText = ""; // 조합 중인 글자 날리기
                    } else if (textBuffer.length() > 0) {
                        textBuffer.deleteCharAt(textBuffer.length() - 1); // 완성된 글자 지우기
                    }
                    repaint();
                }
            }
        });
    }

    // 화면에 보여줄 최종 문자열 결합 메서드
    private String getInputText() {
        return textBuffer.toString() + composingText;
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

    // ★ OS에게 IME 입력을 처리하겠다고 증명하는 필수 오버라이드
    @Override
    public java.awt.im.InputMethodRequests getInputMethodRequests() {
        return new java.awt.im.InputMethodRequests() {
            @Override
            public java.awt.font.TextHitInfo getLocationOffset(int x, int y) { return null; }

            @Override
            public java.awt.Rectangle getTextLocation(java.awt.font.TextHitInfo offset) {
                // 한글 조합창(밑줄 생기는 미완성 글자 박스)이 뜰 창 기준 절대 좌표
                return new java.awt.Rectangle(50, 130, 0, 0);
            }

            @Override
            public java.text.AttributedCharacterIterator getSelectedText(java.text.AttributedCharacterIterator.Attribute[] attributes) { return null; }

            @Override
            public java.text.AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, java.text.AttributedCharacterIterator.Attribute[] attributes) { return null; }

            // 지난번에 누락되었던 필수 구현 값들 채워넣음
            @Override public int getCommittedTextLength() { return 0; }
            @Override public int getInsertPositionOffset() { return 0; }
            @Override public java.text.AttributedCharacterIterator cancelLatestCommittedText(java.text.AttributedCharacterIterator.Attribute[] attributes) { return null; }
        };
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        g.drawString("한글 입력 테스트 :", 50, 80);

        // 실시간 타이핑 중인 텍스트 렌더링
        g.setColor(Color.CYAN);
        g.drawString(getInputText() + "_", 50, 130);
    }

    public static void main(String[] args) {
        new Test();
    }
}