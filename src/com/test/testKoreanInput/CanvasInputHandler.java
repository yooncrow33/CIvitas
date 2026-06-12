package com.test.testKoreanInput; // 패키지는 상황에 맞게 변경해줘

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;

public class CanvasInputHandler implements InputMethodListener {
    private final StringBuilder textBuffer = new StringBuilder(); // 완성된 텍스트
    private String composingText = ""; // 현재 조합 중인 글자 (예: '글'을 만들기 위해 'ㄱㅡ' 입력 중일 때)

    // 완성된 텍스트 + 조합 중인 텍스트 반환
    public String getText() {
        return textBuffer.toString() + composingText;
    }

    @Override
    public void inputMethodTextChanged(InputMethodEvent event) {
        AttributedCharacterIterator text = event.getText();

        if (text != null) {
            int committedCharacterCount = event.getCommittedCharacterCount();
            char c = text.first();

            // 1. 완성된 글자 처리 (엔터를 치거나 다음 글자로 넘어갈 때)
            StringBuilder committed = new StringBuilder();
            for (int i = 0; i < committedCharacterCount; i++) {
                committed.append(c);
                c = text.next();
            }
            textBuffer.append(committed.toString());

            // 2. 현재 조합 중인 글자 처리 (밑줄 그어지며 실시간으로 바뀌는 글자)
            StringBuilder composing = new StringBuilder();
            while (c != AttributedCharacterIterator.DONE) {
                composing.append(c);
                c = text.next();
            }
            composingText = composing.toString();
        } else {
            composingText = "";
        }

        // 이벤트 소비
        event.consume();
    }

    @Override
    public void caretPositionChanged(InputMethodEvent event) {
        // 커서 위치 변경 시 필요한 경우 구현 (여기선 생략 가능)
    }

    // 백스페이스(지우기) 처리를 위한 메서드
    public void backspace() {
        if (composingText.length() > 0) {
            // 조합 중인 글자가 있으면 조합 취소 (IME가 보통 처리하지만 안전장치)
            composingText = "";
        } else if (textBuffer.length() > 0) {
            // 완성된 글자 지우기
            textBuffer.deleteCharAt(textBuffer.length() - 1);
        }
    }
}