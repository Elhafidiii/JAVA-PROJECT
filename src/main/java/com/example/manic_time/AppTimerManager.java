package com.example.manic_time;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AppTimerManager {

    private static final HashMap<String, Timer> appTimers = new HashMap<>();
    private static final HashMap<String, Integer> appTimeLimits = new HashMap<>();
    private static final HashMap<String, Integer> appRemainingTime = new HashMap<>();

    // Start a timer for an app
    public static void startAppTimer(String appName, int timeLimitInMinutes) {
        if (appTimers.containsKey(appName)) {
            cancelAppTimer(appName);  // Cancel any existing timer
        }

        Timer timer = new Timer();
        appTimers.put(appName, timer);
        appTimeLimits.put(appName, timeLimitInMinutes);
        appRemainingTime.put(appName, timeLimitInMinutes);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int remainingTime = appRemainingTime.get(appName);
                if (remainingTime > 0) {
                    appRemainingTime.put(appName, remainingTime - 1);
                } else {
                    cancelAppTimer(appName);  // Timer finished, cancel it
                    // Show alert to the user
                    System.out.println("Time is up for " + appName);
                }
            }
        }, 0, 60 * 1000);  // 1 minute interval
    }

    // Cancel a specific app's timer
    public static void cancelAppTimer(String appName) {
        if (appTimers.containsKey(appName)) {
            appTimers.get(appName).cancel();
            appTimers.remove(appName);
            appRemainingTime.put(appName, appTimeLimits.get(appName));  // Reset the remaining time
        }
    }

    // Get remaining time for a specific app
    public static int getRemainingTime(String appName) {
        return appRemainingTime.getOrDefault(appName, 0);
    }
}
