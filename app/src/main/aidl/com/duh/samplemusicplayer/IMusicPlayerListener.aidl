package com.duh.samplemusicplayer;

interface IMusicPlayerListener {
    void onSuccess();
    void onError(int errorCode, String errorMessage);
}