package com.example.manic_time;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ApplicationTime {

    private final StringProperty appName;
    private final StringProperty timeLimit;
    private final StringProperty remainingTime;

    // Constructor
    public ApplicationTime(String appName, String timeLimit, String remainingTime) {
        this.appName = new SimpleStringProperty(appName);
        this.timeLimit = new SimpleStringProperty(timeLimit);
        this.remainingTime = new SimpleStringProperty(remainingTime);
    }

    // Getters for properties
    public StringProperty appNameProperty() {
        return appName;
    }

    public StringProperty timeLimitProperty() {
        return timeLimit;
    }

    public StringProperty remainingTimeProperty() {
        return remainingTime;
    }

    // Getters for values
    public String getAppName() {
        return appName.get();
    }

    public String getTimeLimit() {
        return timeLimit.get();
    }

    public String getRemainingTime() {
        return remainingTime.get();
    }

    // Setters for values
    public void setAppName(String appName) {
        this.appName.set(appName);
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit.set(timeLimit);
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime.set(remainingTime);
    }
}
