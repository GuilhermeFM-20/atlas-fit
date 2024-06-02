package com.example.app_sd;

import android.app.Application;

public class MyApp extends Application {
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
