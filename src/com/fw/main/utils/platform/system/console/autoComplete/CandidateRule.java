package com.fw.main.utils.platform.system.console.autoComplete;

import java.util.ArrayList;
import java.util.List;

public class CandidateRule {
    private final String candidateValue;
    private final List<Condition> conditions = new ArrayList<>();

    public List<Condition> getConditions() {
        return conditions;
    }

    public CandidateRule(String candidateValue) {
        this.candidateValue = candidateValue;
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    public String getCandidateValue() {
        return candidateValue;
    }

    public boolean isSatisfied(List<String> currentTokens) {
        for (Condition condition : conditions) {
            if (!condition.matches(currentTokens)) {
                return false;
            }
        }
        return true;
    }
}