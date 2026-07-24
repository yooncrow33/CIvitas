package com.fw.main.utils.platform.system.console.autoComplete;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoCompleteManager {
    // Key: targetTokenIndex (추천이 나타날 토큰 인덱스)
    // Value: 해당 인덱스에 등록된 규칙(Rule) 리스트
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
                return parent; // 다시 CandidateBuilder로 돌아가서 추가 .whenToken() 체이닝 가능
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
        // 완전 접두사 일치 시 높은 가중치
        if (candidate.startsWith(input)) return 1000 + (100 - candidate.length());
        if (candidate.toLowerCase().startsWith(input.toLowerCase())) return 500;

        // 부분 포함 점수
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
                // 입력 중인 글자가 포함/시작되는 경우만 active로 간주
                if (rule.getCandidateValue().toLowerCase().contains(currentInputToken.toLowerCase())) {
                    activeCandidates.add(rule.getCandidateValue());
                }
            }
        }

        sortCandidates(currentInputToken, activeCandidates);
        return activeCandidates;
    }

    /**
     * [왼쪽 아래용] 입력 글자 필터링 없이, 해당 위치 조건만 만족하는 '모든 추천어' 반환
     */
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