package com.duh.samplemusicplayer.app;

import android.app.Application;

import com.duh.samplemusicplayer.media.AudioProvider;
import com.duh.samplemusicplayer.service.ServiceManager;

public class MusicApp extends Application {
    public ServiceManager serviceManager;
    public AudioProvider audioProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceManager = new ServiceManager();
        audioProvider = new AudioProvider(getApplicationContext());
    }
}
