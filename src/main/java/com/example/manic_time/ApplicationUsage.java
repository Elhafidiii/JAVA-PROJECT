package com.example.manic_time;

import javafx.scene.image.Image;

public class ApplicationUsage {

    private final String application;
    private final String time;
    private final Image icon;

    public ApplicationUsage(String application, String time, Image icon) {
        this.application = application;
        this.time = time;
        this.icon = icon;
    }

    public String getApplication() {
        return application;
    }

    public String getTime() {
        return time;
    }

    public Image getIcon() {
        return icon;
    }
}
