package com.duh.samplemusicplayer;

interface IMusicPlayerService {
    void startMusic(Song song, IMusicPlayerListener playerListener);
    void pauseMusic(IMusicPlayerListener playerListener);
    void stopMusic(IMusicPlayerListener playerListener);
}