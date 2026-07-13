package com.fw.main.utils.input.korean;

import java.util.UUID;

public class KoreanObject {
    final UUID id = UUID.randomUUID();
    private String composingText = "";
    private boolean focused = false;
    private final StringBuilder textBuffer = new StringBuilder(); // 완성된 글자들
    KoreanObjectEventListener listener;

    public KoreanObject() {
        KoreanManager.koreanObjectPut(this);
    }

    public void registerKoreanObjectEventListener(KoreanObjectEventListener koreanObjectEventListener) {this.listener = koreanObjectEventListener;}

    public void free() {
        KoreanManager.koreanObjectRemove(this);
    }


    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            KoreanManager.activeObjectPut(this);
        } else {
            KoreanManager.activeObjectRemove(this);
        }
    }

    public boolean isFocused() {
        return this.focused;
    }

    public String getComposingText() {
        return composingText;
    }

    public StringBuilder getStringBuilder() {return textBuffer;}

    public void clear() {
        textBuffer.setLength(0);
        composingText = "";
    }
}
