package com.kiocg;

import java.util.Arrays;

public class ChunkHot {
    // Total number of heat tracking intervals
    private static final int TIMES_LENGTH = 10;
    // Heat tracking intervals
    private final long[] times = new long[TIMES_LENGTH];
    // Current interval index
    private int index = -1;
    // Temporary value for the current interval, used for correcting the heat value of the ongoing interval
    private long temp;

    // Total heat value for all intervals
    private long total;

    // For calculations within each specific tracking process
    private long nanos;

    // Indicates whether the current tracking is ongoing
    private volatile boolean started = false;

    /**
     * Updates the interval index.
     */
    public void nextTick() {
        this.index = ++this.index % TIMES_LENGTH;
    }

    /**
     * Starts tracking a new interval.
     */
    public void start() {
        started = true;
        temp = times[this.index];
        times[this.index] = 0L;
    }

    /**
     * Checks whether tracking is ongoing.
     *
     * @return true if tracking is ongoing, false otherwise
     */
    public boolean isStarted() {
        return this.started;
    }

    /**
     * Ends tracking for the current interval and updates the total heat value.
     */
    public void stop() {
        started = false;
        total -= temp;
        total += times[this.index];
    }

    /**
     * Starts a specific heat tracking process.
     */
    public void startTicking() {
        if (!started) return;
        nanos = System.nanoTime();
    }

    /**
     * Ends a specific heat tracking process and adds its value to the current interval.
     * The maximum value for a single tracking process is capped at 1,000,000.
     */
    public void stopTickingAndCount() {
        if (!started) return;
        times[this.index] += Math.min(System.nanoTime() - nanos, 1_000_000L);
    }

    /**
     * Clears all tracking data (used when unloading a block).
     */
    public void clear() {
        started = false;
        Arrays.fill(times, 0L);
        temp = 0L;
        total = 0L;
        nanos = 0L;
    }

    /**
     * Calculates and retrieves the average heat value for the block.
     *
     * @return the average heat value
     */
    public long getAverage() {
        return total / ((long) TIMES_LENGTH * 20L);
    }
}