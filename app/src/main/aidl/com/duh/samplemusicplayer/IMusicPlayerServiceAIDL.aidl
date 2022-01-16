package com.duh.samplemusicplayer;
interface IMusicPlayerServiceAIDL {
    void startMusic(in Bundle bundle);
    void pauseMusic();
    void stopMusic();
    void next();
    void previous();
    void getSongList(com.duh.samplemusicplayer.IMusicPlayerListener playerListener);
    boolean isPlaying();
}