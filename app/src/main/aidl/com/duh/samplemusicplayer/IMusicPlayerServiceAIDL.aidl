package com.duh.samplemusicplayer;

interface IMusicPlayerServiceAIDL {
    void startMusic(in Bundle bundle, com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    void pauseMusic(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    void stopMusic(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
}