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

    private final StringBuilder textBuffer = new StringBuilder(); // 완성된 글자들
    private String composingText = ""; // 현재 조합 중인 글자

    static {
        Core.setConfig(new Config(new
                Config.Builder("fwTest").
                setWindowWidth(640).
                setWindowHeight(340)
        ));
    }

    {
        this.enableInputMethods(true);
    }

    public Test() {
        super(new Builder().setIntegerKey(1).setStringKey("1"));

        new TestBinding(this);

        this.enableInputMethods(true);
        this.requestFocusInWindow();

        // 1. IME(한글 조합) 리스너
        this.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                AttributedCharacterIterator text = event.getText();
                if (text != null) {
                    int committedCharacterCount = event.getCommittedCharacterCount();
                    char c = text.first();

                    // 완성된 한글 처리
                    StringBuilder committed = new StringBuilder();
                    for (int i = 0; i < committedCharacterCount; i++) {
                        committed.append(c);
                        c = text.next();
                    }
                    textBuffer.append(committed.toString());

                    // 조합 중인 한글 처리
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
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {}
        });

        // 2. 키보드 리스너 (특수문자, 백스페이스, 엔터 처리)
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 백스페이스 처리
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (composingText.length() > 0) {
                        composingText = "";
                    } else if (textBuffer.length() > 0) {
                        textBuffer.deleteCharAt(textBuffer.length() - 1);
                    }
                    repaint();
                }

                // ★ 엔터키 처리
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // 한글 조합 중이었다면 해당 글자를 완성 버퍼로 강제 이동
                    if (composingText.length() > 0) {
                        textBuffer.append(composingText);
                        composingText = "";
                    }

                    // 엔터 이벤트 발생 시 실행할 로직 작성
                    onEnterPressed(textBuffer.toString());

                    repaint();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // 제어 문자(백스페이스, 엔터, 이스케이프 등)는 제외하고
                // 키보드로 입력되는 특수문자, 영어, 숫자 등을 버퍼에 직접 추가
                if (c != KeyEvent.CHAR_UNDEFINED && c >= 32 && c != 127) {
                    // 한글 조합 중이 아닐 때만 키 입력을 직접 받음 (한글은 InputMethodListener가 처리함)
                    if (composingText.length() == 0) {
                        textBuffer.append(c);
                        repaint();
                    }
                }
            }
        });
    }

    // ★ 엔터키를 쳤을 때 동작할 커스텀 메서드
    private void onEnterPressed(String fullText) {
        System.out.println("전송된 텍스트: " + fullText);

        // 예시: 엔터 치면 입력창 비우기 (원하지 않으면 주석 처리하셈)
        textBuffer.setLength(0);
    }

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
    public void update(double dt) {}

    @Override
    public java.awt.im.InputMethodRequests getInputMethodRequests() {
        return new java.awt.im.InputMethodRequests() {
            @Override public java.awt.font.TextHitInfo getLocationOffset(int x, int y) { return null; }
            @Override public java.awt.Rectangle getTextLocation(java.awt.font.TextHitInfo offset) {
                return new java.awt.Rectangle(50, 130, 0, 0);
            }
            @Override public java.text.AttributedCharacterIterator getSelectedText(java.text.AttributedCharacterIterator.Attribute[] attributes) { return null; }
            @Override public java.text.AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, java.text.AttributedCharacterIterator.Attribute[] attributes) { return null; }
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
        g.setFont(new Font("", Font.BOLD, 18));
        g.drawString("Korean Test :", 50, 80);

        // 실시간 타이핑 중인 텍스트 렌더링
        g.setColor(Color.CYAN);
        g.drawString(getInputText() + "_", 50, 130);
    }

    public static void main(String[] args) {
        new Test();
    }
}