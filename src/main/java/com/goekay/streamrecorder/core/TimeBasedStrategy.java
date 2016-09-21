package com.goekay.streamrecorder.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
public class TimeBasedStrategy implements StopRecordingStrategy, Runnable {

    private boolean shouldContinue = true;

    public TimeBasedStrategy(long recordDurationInSeconds) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        executor.schedule(this, recordDurationInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean shouldContinue(int bytesRead) {
        return shouldContinue;
    }

    @Override
    public void run() {
        shouldContinue = false;
    }
}
