package com.test;

public class Ko {
    private final StringBuilder textBuffer = new StringBuilder(); // 완성된 글자들
    private String composingText = ""; // 현재 조합 중인 글자
    private boolean focused = false; // 포커스 여부

    public Ko() {}

    // 포커스 설정 및 해제
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return this.focused;
    }

    // 버퍼 제어 메서드들
    public StringBuilder getTextBuffer() {
        return textBuffer;
    }

    public String getComposingText() {
        return composingText;
    }

    public void setComposingText(String composingText) {
        this.composingText = composingText;
    }

    public void clear() {
        textBuffer.setLength(0);
        composingText = "";
    }

    // 화면 출력용 풀 텍스트 반환
    public String getInputText() {
        return textBuffer.toString() + composingText;
    }
}