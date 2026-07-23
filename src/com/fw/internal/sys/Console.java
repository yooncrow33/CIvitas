package com.fw.internal.sys;

import com.fw.main.Base;
import com.fw.main.Core;
import com.fw.main.utils.input.korean.KoreanObject;
import com.fw.main.utils.input.korean.KoreanObjectEventListener;

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

    private boolean isOpen = false;

    private final List<String> logs = new ArrayList<>();
    private int scrollOffset = 0;
    private int maxLines = 10;


    private final KoreanObject text;

    Base base;

    public Console(Base comp) {
        this.text = new KoreanObject();
        text.registerKoreanObjectEventListener(new KoreanObjectEventListener() {
            @Override
            public void enter() {
                enterAtConsole();
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

    public void enterAtConsole() {
        String input = text.getInputText().trim();
        if (input.isEmpty()) {text.clear();return;}
        if (hasConsecutiveSpaces(input)) {
            addLog(LogType.ERROR, "multiple consecutive spaces detected.");
            return;
        }

        List<String> args;
        args = parseBuffer(input, false);

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

        g2.setColor(new Color(10, 10, 10, 240));
        g2.fillRect(0, 0, 1920, 340);

        g2.setColor(new Color(240, 240, 240));
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, 340, 1920, 340);

        g2.setFont(new Font("Consolas", Font.PLAIN, 16));
        int lineHeight = 25;
        int startY = 40;

        int totalLogs = logs.size();
        int endIndex = totalLogs - scrollOffset;
        int startIndex = Math.max(0, endIndex - maxLines);
        int lineCount = 0;

        for (int i = startIndex; i < endIndex; i++) {
            String line = logs.get(i);

            g2.setFont(new Font("Consolas", Font.PLAIN, 16));
            if (line.contains("Error")) g2.setColor(new Color(180, 180, 180));
            else if (line.contains("root:")) g2.setColor(Color.WHITE);
            else if (line.contains("[System]")) g2.setColor(Color.GREEN);
            else g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(line, 30, startY + (lineCount * lineHeight));

            int currentY = startY + (lineCount * lineHeight);

            g2.setFont(new Font("Consolas", Font.PLAIN, 12));
            g2.setColor(Color.GRAY);
            g2.drawString(String.format("#%d", i + 1), 1750, currentY);

            lineCount++;
        }

        g2.setFont(new Font("Consolas", Font.PLAIN, 12));
        g2.setColor(Color.GRAY);
        g2.drawString(String.format("Lines: %d/%d", endIndex, totalLogs), 1820, 30);

        g2.setColor(new Color(240, 240, 240));
        g2.setFont(new Font("Consolas", Font.BOLD, 18));
        String cursor = (System.currentTimeMillis() % 1000 > 300) ? "_" : "";
        g2.drawString("root@"+ Core.get().getProjectName().toLowerCase() +":~$ " + text.getInputText() + cursor, 30, 320);
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
        if (!args.get(0).equals("sys")) {return;} //쓸대는 없지만 ㅎㅎ 그냥 불안해해서.

    }
}