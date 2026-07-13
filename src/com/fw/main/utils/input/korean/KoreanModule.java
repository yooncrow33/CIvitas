/*
package com.fw.main.utils.input.korean;

import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.AttributedCharacterIterator;

public class KoreanModule {
    private Component component;

    public KoreanModule(Component component) {
        this.component = component;
        component.enableInputMethods(true);
        component.requestFocusInWindow();



        // 1. IME(한글/영문 입력) 리스너
        component.addInputMethodListener(new InputMethodListener() {
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
                    while (c != AttributedCharacterIterator.DONE) {
                        composing.append(c);
                        c = text.next();
                    }
                    koComponent.setComposingText(composing.toString());
                } else {
                    koComponent.setComposingText("");
                }
                event.consume();
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {}
        });

        // 2. 키보드 리스너 (제어 문자 및 영문 모드 직접 입력 처리)
        component.addKeyListener(new KeyAdapter() {
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
                }

                // 엔터키 처리
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (koComponent.getComposingText().length() > 0) {
                        koComponent.getTextBuffer().append(koComponent.getComposingText());
                        koComponent.setComposingText("");
                    }

                    onEnterPressed(koComponent.getInputText());
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
    }

    private void onEnterPressed(String fullText) {
        System.out.println("전송된 텍스트: " + fullText);
        koComponent.clear(); // 입력창 비우기
    }
}
*/

