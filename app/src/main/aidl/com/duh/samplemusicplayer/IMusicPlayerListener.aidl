package com.duh.samplemusicplayer;

interface IMusicPlayerListener {
    void onStarted(in Bundle songBundle);
    void onPaused();
    void onStopped();
    void onListChanged(in Bundle listBundle);
    void onError(int errorCode, String errorMessage);
}