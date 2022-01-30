package com.duh.samplemusicplayer.media.player;

import com.duh.samplemusicplayer.media.player.state.MediaPlayerStates;
import com.duh.samplemusicplayer.model.Song;

import java.util.Objects;

public class PlayerModel {
    private Song currentSong;
    private MediaPlayerStates currentState = MediaPlayerStates.IDLE;

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public MediaPlayerStates getCurrentState() {
        return currentState;
    }

    public void setCurrentState(MediaPlayerStates currentState) {
        this.currentState = currentState;
    }

}
