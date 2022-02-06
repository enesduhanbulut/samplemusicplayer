package com.duh.samplemusicplayer;
interface IMusicPlayerServiceAIDL {
    void setListener(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    void startMusic(in Bundle bundle);
    void pauseMusic();
    void stopMusic();
    void next();
    void previous();
    void getSongList();
    boolean isPlaying();
    Bundle getCurrentSong();
    long getCurrentDuration();
}