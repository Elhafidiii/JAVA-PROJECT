package com.example.manic_time;



public class ApplicationUsage {

    private final String application;
    private final String time;

    public ApplicationUsage(String application, String time) {
        this.application = application;
        this.time = time;
    }

    public String getApplication() {
        return application;
    }

    public String getTime() {
        return time;
    }

}
