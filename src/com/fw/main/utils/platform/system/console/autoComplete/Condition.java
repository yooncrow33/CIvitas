package com.fw.main.utils.platform.system.console.autoComplete;

import java.util.List;

public class Condition {
    private final int targetIndex;
    public int getTargetIndex() {
        return targetIndex;
    }
    private final String expectedValue;

    public Condition(int targetIndex, String expectedValue) {
        this.targetIndex = targetIndex;
        this.expectedValue = expectedValue;
    }

    public boolean matches(List<String> currentTokens) {
        if (targetIndex < 0 || targetIndex >= currentTokens.size()) {
            return false;
        }
        return currentTokens.get(targetIndex).equalsIgnoreCase(expectedValue);
    }
    
}