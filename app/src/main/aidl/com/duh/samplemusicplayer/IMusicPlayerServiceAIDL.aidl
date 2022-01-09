package com.duh.samplemusicplayer;
interface IMusicPlayerServiceAIDL {
    void startMusic(in Bundle bundle, com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    void pauseMusic(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    void stopMusic(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    void next(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    void previous(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    void getSongList(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    boolean isPlaying();
}