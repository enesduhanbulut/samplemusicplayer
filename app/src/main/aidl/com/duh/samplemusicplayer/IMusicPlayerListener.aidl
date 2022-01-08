package com.duh.samplemusicplayer;

interface IMusicPlayerListener {
    void onSuccess(in Bundle bundle);
    void onError(int errorCode, String errorMessage);
}