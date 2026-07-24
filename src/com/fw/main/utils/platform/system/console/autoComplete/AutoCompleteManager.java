package com.fw.main.utils.platform.system.console.autoComplete;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoCompleteManager {
    private final Map<Integer, List<CandidateRule>> ruleMap = new ConcurrentHashMap<>();

    public CandidateBuilder suggestAt(int tokenIndex, String candidateValue) {
        CandidateRule rule = new CandidateRule(candidateValue);
        ruleMap.computeIfAbsent(tokenIndex, k -> new ArrayList<>()).add(rule);
        return new CandidateBuilder(tokenIndex, rule);
    }

    public static class CandidateBuilder {
        private final int tokenIndex;
        private final CandidateRule rule;

        public CandidateBuilder(int tokenIndex, CandidateRule rule) {
            this.tokenIndex = tokenIndex;
            this.rule = rule;
        }

        public WhenClause whenToken(int conditionIndex) {
            return new WhenClause(this, conditionIndex);
        }

        public class WhenClause {
            private final CandidateBuilder parent;
            private final int conditionIndex;

            public WhenClause(CandidateBuilder parent, int conditionIndex) {
                this.parent = parent;
                this.conditionIndex = conditionIndex;
            }

            public CandidateBuilder is(String expectedValue) {
                if (conditionIndex >= tokenIndex) {
                    System.err.println("Auto complete condition error: conditionIndex must be less than tokenIndex!");
                }
                for (Condition condition : rule.getConditions()) {
                    if (condition.getTargetIndex() == conditionIndex) {
                        System.err.println("overlapped condition!");
                    }
                }
                rule.addCondition(new Condition(conditionIndex, expectedValue));
                return parent;
            }
        }
    }

    private void sortCandidates(String input, List<String> candidates) {
        if (input == null || input.isEmpty()) return;

        candidates.sort((a, b) -> {
            int scoreA = calculateScore(input, a);
            int scoreB = calculateScore(input, b);
            if (scoreA != scoreB) return Integer.compare(scoreB, scoreA); // 높은 점수 우선
            return a.compareToIgnoreCase(b);
        });
    }

    private int calculateScore(String input, String candidate) {
        if (candidate.startsWith(input)) return 1000 + (100 - candidate.length());
        if (candidate.toLowerCase().startsWith(input.toLowerCase())) return 500;

        if (candidate.contains(input)) return 100;
        return 0;
    }

    public List<String> getCandidates(List<String> currentTokens, String currentInputToken) {
        int targetIndex = currentTokens.isEmpty() ? 0 : currentTokens.size() - 1;
        List<CandidateRule> rules = ruleMap.get(targetIndex);

        if (rules == null || rules.isEmpty()) {
            return Collections.emptyList();
        }

        if (currentInputToken == null || currentInputToken.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> activeCandidates = new ArrayList<>();

        for (CandidateRule rule : rules) {
            if (rule.isSatisfied(currentTokens)) {
                if (rule.getCandidateValue().toLowerCase().contains(currentInputToken.toLowerCase())) {
                    activeCandidates.add(rule.getCandidateValue());
                }
            }
        }

        sortCandidates(currentInputToken, activeCandidates);
        return activeCandidates;
    }

    public List<String> getAllCandidates(List<String> currentTokens) {
        int targetIndex = currentTokens.isEmpty() ? 0 : currentTokens.size() - 1;
        List<CandidateRule> rules = ruleMap.get(targetIndex);

        if (rules == null || rules.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> allCandidates = new ArrayList<>();
        for (CandidateRule rule : rules) {
            if (rule.isSatisfied(currentTokens)) {
                allCandidates.add(rule.getCandidateValue());
            }
        }
        return allCandidates;
    }
}