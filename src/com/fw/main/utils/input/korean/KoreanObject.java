package com.fw.main.utils.input.korean;

import java.util.UUID;

public class KoreanObject {
    final UUID id = UUID.randomUUID();
    KoreanObjectEventListener listener;

    private final StringBuilder textBuffer = new StringBuilder();
    private String composingText = "";
    private boolean focused = false;
    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            KoreanManager.activeObjectPut(this);
        } else {
            KoreanManager.activeObjectRemove(this);
        }
    }
    public boolean isFocused() { return this.focused; }
    public StringBuilder getTextBuffer() { return textBuffer; }
    public String getComposingText() { return composingText; }
    public void setComposingText(String composingText) { this.composingText = composingText; }

    public void clear() {
        textBuffer.setLength(0);
        composingText = "";
    }

    public String getInputText() {
        return textBuffer.toString() + composingText;
    }

    public KoreanObject() {
        KoreanManager.koreanObjectPut(this);
    }

    public void registerKoreanObjectEventListener(KoreanObjectEventListener koreanObjectEventListener) {this.listener = koreanObjectEventListener;}

    public void free() {
        KoreanManager.koreanObjectRemove(this);
    }

}
