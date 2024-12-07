package com.example.manic_time;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class ApplicationUsage {
    private final StringProperty application;
    private final StringProperty time;
    private final ObjectProperty<Image> icon;

    public ApplicationUsage(String application, String time, Image icon) {
        this.application = new SimpleStringProperty(application);
        this.time = new SimpleStringProperty(time);
        this.icon = new SimpleObjectProperty<>(icon);
    }

    public String getApplication() {
        return application.get();
    }

    public StringProperty applicationProperty() {
        return application;
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public Image getIcon() {
        return icon.get();
    }

    public ObjectProperty<Image> iconProperty() {
        return icon;
    }
}
