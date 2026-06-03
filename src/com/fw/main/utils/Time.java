package com.fw.main.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Time {

    private long lastTime;

    private long lastTick;
    private long tick = 0;

    private void startGameLoop() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        lastTime = System.nanoTime();

        executor.scheduleAtFixedRate(() -> {
            try {
                long now = System.nanoTime();
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                tick++;

            } catch (Throwable t) {
                t.printStackTrace();
            }

        }, 0, 100, TimeUnit.MILLISECONDS);
    }
}
