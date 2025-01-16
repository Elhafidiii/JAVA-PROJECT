package com.example.manic_time;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AppTimeLimit {
    private final StringProperty appName;
    private final StringProperty timeLimit;
    private final StringProperty remainingTime;

    public AppTimeLimit(String appName, String timeLimit, String remainingTime) {
        this.appName = new SimpleStringProperty(appName);
        this.timeLimit = new SimpleStringProperty(timeLimit);
        this.remainingTime = new SimpleStringProperty(remainingTime);
    }

    // Getters pour les propriétés
    public String getAppName() {
        return appName.get();
    }

    public StringProperty appNameProperty() {
        return appName;
    }

    public String getTimeLimit() {
        return timeLimit.get();
    }

    public StringProperty timeLimitProperty() {
        return timeLimit;
    }

    public String getRemainingTime() {
        return remainingTime.get();
    }

    public StringProperty remainingTimeProperty() {
        return remainingTime;
    }

    // Setters
    public void setAppName(String value) {
        appName.set(value);
    }

    public void setTimeLimit(String value) {
        timeLimit.set(value);
    }

    public void setRemainingTime(String value) {
        remainingTime.set(value);
    }
} 