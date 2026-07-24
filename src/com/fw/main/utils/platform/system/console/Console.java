package com.fw.main.utils.platform.system.console;

import com.fw.main.Base;
import com.fw.main.Core;
import com.fw.main.utils.input.korean.KoreanObject;
import com.fw.main.utils.input.korean.KoreanObjectEventListener;
import com.fw.main.utils.platform.system.console.autoComplete.AutoCompleteManager;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Console {
    public enum LogType {
        ROOT("root: "),
        CONSOLE("[Console] "),
        SYSTEM("[System] "),
        ERROR("[Console] Error: ");

        private final String prefix;
        LogType(String prefix) { this.prefix = prefix; }
        public String get() { return prefix; }
    }

    private static final Font FONT_LOG = new Font("Consolas", Font.PLAIN, 16);
    private static final Font FONT_LOG_INDEX = new Font("Consolas", Font.PLAIN, 12);
    private static final Font FONT_PROMPT = new Font("Consolas", Font.BOLD, 18);
    private static final Font FONT_SUGGEST_TOP = new Font("Consolas", Font.BOLD, 14);
    private static final Font FONT_SUGGEST_SUB = new Font("Consolas", Font.PLAIN, 14);
    private static final Font FONT_ALL_CANDIDATES = new Font("Consolas", Font.PLAIN, 13);

    private static final Color COLOR_BG = new Color(10, 10, 10, 240);
    private static final Color COLOR_BORDER = new Color(240, 240, 240);
    private static final Color COLOR_LOG_ERROR = new Color(180, 180, 180);
    private static final Color COLOR_LOG_ROOT = Color.WHITE;
    private static final Color COLOR_LOG_SYS = Color.GREEN;
    private static final Color COLOR_LOG_DEFAULT = Color.LIGHT_GRAY;
    private static final Color COLOR_GRAY_TEXT = Color.GRAY;

    private static final Color COLOR_PANEL_BG = new Color(20, 20, 20, 220);
    private static final Color COLOR_PANEL_BORDER = new Color(80, 80, 80);
    private static final Color COLOR_CANDIDATES_TEXT = new Color(130, 130, 130);

    private static final BasicStroke STROKE_BORDER = new BasicStroke(3f);

    private boolean isOpen = false;

    private final List<String> logs = new ArrayList<>();
    private int scrollOffset = 0;
    private int maxLines = 10;
    private AutoCompleteManager autoCompleteManager = new AutoCompleteManager();

    private final KoreanObject text;

    Base base;

    public Console(Base comp) {
        this.text = new KoreanObject();
        text.registerKoreanObjectEventListener(new KoreanObjectEventListener() {
            @Override
            public void enter() {
                enterAtConsole();
            }
            @Override
            public void tab() {
                handleTabCompletion();
            }
        });
        this.base = comp;
        this.targetCanvas = comp;

        comp.setFocusable(true);
        initBinding();
    }

    public boolean isOpen() { return isOpen; }
    public void toggle() { isOpen = !isOpen; text.setFocused(!text.isFocused());  }

    public void setMaxLines(int maxLines) {
        this.maxLines = Math.max(1, maxLines);
    }

    public void scrollUp() {
        if (logs.size() > maxLines && scrollOffset < logs.size() - maxLines) {
            scrollOffset++;
        }
    }

    public void scrollDown() {
        if (scrollOffset > 0) {
            scrollOffset--;
        }
    }

    public void CMD(List<String> cmd) {
        if (base.getConsoleCMD()!=null) {
            base.getConsoleCMD().CMD(cmd);
        }
    }

    public AutoCompleteManager getAuto() { return autoCompleteManager; }

    public void enterAtConsole() {
        String input = text.getInputText().trim();
        if (input.isEmpty()) {text.clear();return;}
        if (hasConsecutiveSpaces(input)) {
            addLog(LogType.ERROR, "multiple consecutive spaces detected.");
            return;
        }

        List<String> args = parseBuffer(input, false);
        if (args.isEmpty()) {return;}

        if (args.get(0) != null && args.get(0).equals("sys")) {
            internalCMD(args);
            return;
        }

        logs.add(String.format("[%tT] %s%s", System.currentTimeMillis(), LogType.ROOT.get(), input));
        scrollOffset = 0;
        CMD(args);
        text.clear();
    }

    public boolean hasConsecutiveSpaces(String sb) {
        if (sb == null) {
            return false;
        }
        return sb.contains("  ");
    }

    public List<String> parseBuffer(String sb, boolean preserveTrailingEmpty) {
        if (sb == null) {
            return Collections.emptyList();
        }

        String input = sb;
        if (input.trim().isEmpty()) {
            return Collections.emptyList();
        }

        int limit = preserveTrailingEmpty ? -1 : 0;
        String[] parsedArray = input.split(" ", limit);

        return Arrays.asList(parsedArray);
    }

    public void addLog(LogType type, String message) {
        logs.add(String.format("[%tT] %s%s", System.currentTimeMillis(), type.get() + message));
        scrollOffset = 0;
    }

    public void render(Graphics g) {
        if (!isOpen || g == null) return;
        Graphics2D g2 = (Graphics2D) g;

        // 1. 콘솔 배경 상자 렌더링
        g2.setColor(COLOR_BG);
        g2.fillRect(0, 0, 1920, 340);

        // 2. 하단 경계선 렌더링
        g2.setColor(COLOR_BORDER);
        g2.setStroke(STROKE_BORDER);
        g2.drawLine(0, 340, 1920, 340);

        // 3. 로그 메시지 렌더링
        int lineHeight = 25;
        int startY = 40;
        int totalLogs = logs.size();
        int endIndex = totalLogs - scrollOffset;
        int startIndex = Math.max(0, endIndex - maxLines);
        int lineCount = 0;

        for (int i = startIndex; i < endIndex; i++) {
            String line = logs.get(i);

            g2.setFont(FONT_LOG);
            if (line.contains("Error")) g2.setColor(COLOR_LOG_ERROR);
            else if (line.contains("root:")) g2.setColor(COLOR_LOG_ROOT);
            else if (line.contains("[System]")) g2.setColor(COLOR_LOG_SYS);
            else g2.setColor(COLOR_LOG_DEFAULT);

            g2.drawString(line, 30, startY + (lineCount * lineHeight));

            int currentY = startY + (lineCount * lineHeight);

            // 로그 번호 표시
            g2.setFont(FONT_LOG_INDEX);
            g2.setColor(COLOR_GRAY_TEXT);
            g2.drawString(String.format("#%d", i + 1), 1750, currentY);

            lineCount++;
        }

        // 전체 라인 수 표시
        g2.setFont(FONT_LOG_INDEX);
        g2.setColor(COLOR_GRAY_TEXT);
        g2.drawString(String.format("Lines: %d/%d", endIndex, totalLogs), 1820, 30);

        // 4. 입력 프롬프트 & 커서 렌더링
        int promptX = 30;
        int promptY = 310;

        String promptPrefix = "root@" + Core.get().getProjectName().toLowerCase() + ":~$ ";
        String currentInput = text.getInputText();
        String fullPrompt = promptPrefix + currentInput;

        g2.setFont(FONT_PROMPT);
        g2.setColor(COLOR_BORDER);
        g2.drawString(fullPrompt, promptX, promptY);

        FontMetrics fmPrompt = g2.getFontMetrics(FONT_PROMPT);
        int cursorX = promptX + fmPrompt.stringWidth(fullPrompt);
        int cursorY = promptY;

        String cursor = (System.currentTimeMillis() % 1000 > 300) ? "_" : "";
        g2.drawString(cursor, cursorX, cursorY);

        // 5. 추천어 데이터 조회
        List<String> allCandidates = getAllCandidates();      // [왼쪽 아래용]
        List<String> suggestions = getCurrentSuggestions();  // [오른쪽 아래용]

        int boxPadding = 8;
        int itemHeight = 20;

        // [A] 커서 왼쪽 아래: 이 위치에 들어올 수 있는 '모든 후보 목록' (항상 나열)
        if (!allCandidates.isEmpty()) {
            int maxWordWidth = 0;
            for (String cand : allCandidates) {
                maxWordWidth = Math.max(maxWordWidth, fmPrompt.stringWidth(cand));
            }
            int panelWidth = Math.max(maxWordWidth + (boxPadding * 2), 80);
            int panelHeight = (allCandidates.size() * itemHeight) + (boxPadding * 2);

            // 📍 커서 X좌표보다 '왼쪽'으로 배치
            int leftPanelX = cursorX - panelWidth - 10;
            int leftPanelY = cursorY + 15;

            // 반투명 배경 & 테두리
            g2.setColor(COLOR_PANEL_BG);
            g2.fillRect(leftPanelX, leftPanelY, panelWidth, panelHeight);
            g2.setColor(COLOR_PANEL_BORDER);
            g2.drawRect(leftPanelX, leftPanelY, panelWidth, panelHeight);

            // 후보 단어 나열 (회색/PLAIN)
            g2.setFont(FONT_SUGGEST_SUB);
            g2.setColor(COLOR_CANDIDATES_TEXT);
            for (int idx = 0; idx < allCandidates.size(); idx++) {
                String candidateWord = allCandidates.get(idx);
                int textY = leftPanelY + boxPadding + (idx + 1) * itemHeight - 4;
                g2.drawString(candidateWord, leftPanelX + boxPadding, textY);
            }
        }

        // [B] 커서 바로 오른쪽: 타이핑 시 활성화된 자동완성 목록 패널
        if (!suggestions.isEmpty()) {
            int maxWordWidth = 0;
            for (String sug : suggestions) {
                maxWordWidth = Math.max(maxWordWidth, fmPrompt.stringWidth(sug));
            }
            int panelWidth = maxWordWidth + (boxPadding * 2);
            int panelHeight = (suggestions.size() * itemHeight) + (boxPadding * 2);

            // 📍 커서 X좌표보다 '오른쪽'으로 배치
            int rightPanelX = cursorX + 10;
            int rightPanelY = cursorY + 15;

            // 반투명 배경 & 테두리
            g2.setColor(COLOR_PANEL_BG);
            g2.fillRect(rightPanelX, rightPanelY, panelWidth, panelHeight);
            g2.setColor(COLOR_PANEL_BORDER);
            g2.drawRect(rightPanelX, rightPanelY, panelWidth, panelHeight);

            // 활성화 단어 나열 (1순위 BOLD 흰색, 2순위~ 회색)
            for (int idx = 0; idx < suggestions.size(); idx++) {
                String suggestionWord = suggestions.get(idx);
                int textY = rightPanelY + boxPadding + (idx + 1) * itemHeight - 4;

                if (idx == 0) {
                    g2.setFont(FONT_SUGGEST_TOP);
                    g2.setColor(COLOR_LOG_ROOT);
                } else {
                    g2.setFont(FONT_SUGGEST_SUB);
                    g2.setColor(COLOR_GRAY_TEXT);
                }
                g2.drawString(suggestionWord, rightPanelX + boxPadding, textY);
            }
        }
    }

    private static final int CONSOLE_KEY_CODE = KeyEvent.VK_BACK_QUOTE;
    private final Canvas targetCanvas;

    private void initBinding() {
        targetCanvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == CONSOLE_KEY_CODE) {
                    toggle();
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == CONSOLE_KEY_CODE) {
                    e.consume();
                }
            }
        });
    }

    public void internalCMD(List<String> args) {
        if (!args.get(0).equals("sys")) {return;}
    }

    public void handleTabCompletion() {
        String inputText = text.getInputText();
        List<String> currentTokens = parseBuffer(inputText, true);

        if (currentTokens.isEmpty()) return;

        String currentToken = currentTokens.get(currentTokens.size() - 1);

        // 글자가 없는 공백 토큰이면 TAB을 눌러도 자동완성 안 함
        if (currentToken.isEmpty()) return;

        List<String> candidates = autoCompleteManager.getCandidates(currentTokens, currentToken);

        if (!candidates.isEmpty()) {
            String topCandidate = candidates.get(0);

            currentTokens.set(currentTokens.size() - 1, topCandidate);
            String completedInput = String.join(" ", currentTokens) + " ";

            text.setInputText(completedInput);
        }
    }

    // [오른쪽 아래용] 타이핑 중 활성화된 추천어 목록
    public List<String> getCurrentSuggestions() {
        String inputText = text.getInputText();
        List<String> currentTokens = parseBuffer(inputText, true);
        if (currentTokens.isEmpty()) {
            return Collections.emptyList();
        }
        String currentToken = currentTokens.get(currentTokens.size() - 1);
        return autoCompleteManager.getCandidates(currentTokens, currentToken);
    }

    // [왼쪽 아래용] 조건만 만족하는 전체 후보 목록
    public List<String> getAllCandidates() {
        String inputText = text.getInputText();
        List<String> currentTokens = parseBuffer(inputText, true);
        if (currentTokens.isEmpty()) {
            return Collections.emptyList();
        }
        return autoCompleteManager.getAllCandidates(currentTokens);
    }
}