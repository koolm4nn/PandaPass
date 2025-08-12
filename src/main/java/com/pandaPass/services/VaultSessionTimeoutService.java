package com.pandaPass.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for enforcing a session timeout.
 * If no user interaction occurs within a defined time interval,
 * this service will trigger a user-defined timeout action (e.g. locking the vault).
 */
public class VaultSessionTimeoutService {

    // Single thread as only one background thread is necessary to monitor session timeout
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Handle to currently scheduled timer task. When user interacts its interrupted and a fresh one is started
    private ScheduledFuture<?> timeoutTask;

    // Action to perform when the timeout happens
    private final Runnable onTimeoutAction;

    // Seconds before timeout is triggered
    private final long timeoutDuration;

    private final LocalDateTime startTime;
    private LocalDateTime lastResetTime;
    private LocalDateTime stopTime;

    /**
     * Constructs a timeout instance
     * @param timeoutDuration Time duration in seconds before the session is considered idle
     * @param onTimeoutAction Action to execute when the timeout elapses
     */
    public VaultSessionTimeoutService(long timeoutDuration, Runnable onTimeoutAction){
        this.timeoutDuration = timeoutDuration;
        this.onTimeoutAction = onTimeoutAction;
        startTime = LocalDateTime.now();
    }

    /**
     * Resets the session timeout timer.
     * Cancels any current timeout task and starts a fresh one.
     */
    public void resetTimer(){
        if(timeoutTask != null && !timeoutTask.isDone()){
            timeoutTask.cancel(false);
        }
        timeoutTask = scheduler.schedule(() -> {
          try{
              onTimeoutAction.run();
          } catch (Exception e){
              System.err.println("Error in timeout-thread: " + e.getMessage());
          }
        }, timeoutDuration, TimeUnit.SECONDS);
        lastResetTime = LocalDateTime.now();
    }

    /**
     * Stops the timeout service by cancelling any running timeout task and shuts down the scheduler.
     * Should be called when the session ends permanently.
     */
    public void stopTimer(){
        stopTime = LocalDateTime.now();
        System.out.print(LocalDateTime.now().toLocalTime() + ": Timer stopped.");
        System.out.print(". Duration since Start: " + (secondsBetweenDates(startTime, stopTime)));
        System.out.println(". Duration since lats refresh: " + (secondsBetweenDates(lastResetTime, stopTime)));

        if(timeoutTask != null){
            timeoutTask.cancel(false);
            scheduler.shutdown();
        }
        scheduler.shutdown();
    }

    private long secondsBetweenDates(LocalDateTime firstDate, LocalDateTime secondDate){
        return Duration.between(firstDate, secondDate).getSeconds();
    }
}
