package com.example.manic_time;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import java.util.Objects;
import javafx.application.Platform;
import java.util.concurrent.CompletableFuture;

public class ApplicationTime {

    private final StringProperty appName;
    private final StringProperty timeLimit;
    private final StringProperty remainingTime;
    private final ObjectProperty<ImageView> appIcon;

    // Constructor
    public ApplicationTime(String appName, String timeLimit, String remainingTime, ImageView appIcon) {
        this.appName = new SimpleStringProperty(appName);
        this.timeLimit = new SimpleStringProperty(timeLimit);
        this.remainingTime = new SimpleStringProperty(remainingTime);
        this.appIcon = new SimpleObjectProperty<>(appIcon);
    }

    // Nouveau constructeur sans l'icône
    public ApplicationTime(String appName, String timeLimit, String remainingTime) {
        this.appName = new SimpleStringProperty(appName);
        this.timeLimit = new SimpleStringProperty(timeLimit);
        this.remainingTime = new SimpleStringProperty(remainingTime);
        
        // Créer une ImageView avec l'icône appropriée
        ImageView icon = AppIconUtil.getApplicationIcon(appName);
        this.appIcon = new SimpleObjectProperty<>(icon);
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

    public ObjectProperty<ImageView> appIconProperty() {
        return appIcon;
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

    public ImageView getAppIcon() {
        return appIcon.get();
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

    public void setAppIcon(ImageView icon) {
        this.appIcon.set(icon);
    }
}
