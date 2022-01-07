package com.duh.samplemusicplayer.app;

import android.app.Application;

import com.duh.samplemusicplayer.service.ServiceManager;

public class MusicApp extends Application {
    public ServiceManager serviceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceManager = new ServiceManager();
    }
}
