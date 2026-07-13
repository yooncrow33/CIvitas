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

    // 외부 클래스로 분리한 Ko 객체 생성
    private final Ko koComponent = new Ko();

    static {
        Core.setConfig(new Config(new
                Config.Builder("fwTest").
                setWindowWidth(1280).
                setWindowHeight(3720)
        ));
    }

    public Test() {
        super(new Builder().setIntegerKey(1).setStringKey("1"));

        //new TestBinding(this);

        this.enableInputMethods(true);
        this.requestFocusInWindow();

        // 테스트를 위해 초기 포커스를 true로 설정 (실제 구현시 필요에 따라 조절)
        koComponent.setFocused(true);

        // 1. IME(한글/영문 입력) 리스너
        this.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                // 포커스가 가있을 때만 입력을 받음
                if (!koComponent.isFocused()) {
                    return;
                }

                AttributedCharacterIterator text = event.getText();
                if (text != null) {
                    int committedCharacterCount = event.getCommittedCharacterCount();
                    char c = text.first();

                    // 완성된 글자 (한글 완성 및 한글 모드에서의 영어/숫자/특수문자 처리)
                    StringBuilder committed = new StringBuilder();
                    for (int i = 0; i < committedCharacterCount; i++) {
                        committed.append(c);
                        c = text.next();
                    }
                    koComponent.getTextBuffer().append(committed.toString());

                    // 조합 중인 한글 처리
                    StringBuilder composing = new StringBuilder();
                    for(composing = new StringBuilder(); c != '\uffff'; c = text.next()) {
                        composing.append(c);
                    }
                    koComponent.setComposingText(composing.toString());
                } else {
                    koComponent.setComposingText("");
                }
                event.consume();

                System.out.println("Committed 카운트: " + event.getCommittedCharacterCount());
                System.out.println("텍스트 전체: " + event.getText());
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {}
        });

        // 2. 키보드 리스너 (제어 문자 및 영문 모드 직접 입력 처리)
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!koComponent.isFocused()) {
                    return;
                }

                // 백스페이스 처리
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (koComponent.getComposingText().length() > 0) {
                        koComponent.setComposingText("");
                    } else if (koComponent.getTextBuffer().length() > 0) {
                        koComponent.getTextBuffer().deleteCharAt(koComponent.getTextBuffer().length() - 1);
                    }
                    repaint();
                }

                // 엔터키 처리
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (koComponent.getComposingText().length() > 0) {
                        koComponent.getTextBuffer().append(koComponent.getComposingText());
                        koComponent.setComposingText("");
                    }

                    onEnterPressed(koComponent.getInputText());
                    repaint();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!koComponent.isFocused()) {
                    return;
                }

                char c = e.getKeyChar();

                // 일반 영어 모드이거나 OS 키 바인딩 간섭을 피하기 위한 직접 입력 처리
                if (c != KeyEvent.CHAR_UNDEFINED && c >= 32 && c != 127) {
                    // 한글 조합 중이 아닐 때만 KeyTyped의 입력을 누적
                    if (koComponent.getComposingText().length() == 0) {
                        // OS 및 IME 상태에 따라 영문이 중복 입력되는 것을 방지하기 위해
                        // 현재 입력된 문자가 직전 InputMethodListener에 의해 이미 들어가지 않았는지 검증할 수 있습니다.
                        koComponent.getTextBuffer().append(c);
                    }
                }
            }
        });

        // 생성자 맨 아래에 추가
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                Test.this.setFocusable(true);
                Test.this.requestFocusInWindow();
                System.out.println("현재 컴포넌트가 포커스를 가졌나요?: " + Test.this.isFocusOwner());
            }
        });
    }

    private void onEnterPressed(String fullText) {
        System.out.println("전송된 텍스트: " + fullText);
        koComponent.clear(); // 입력창 비우기
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
        g.drawString("Korean/English Test (Focus: " + koComponent.isFocused() + ") :", 50, 80);

        // Ko 객체로부터 텍스트를 받아와서 렌더링
        g.setColor(koComponent.isFocused() ? Color.CYAN : Color.GRAY);
        g.drawString(koComponent.getInputText() + "_", 50, 130);
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

    public static void main(String[] args) {
        new Test();
    }
}