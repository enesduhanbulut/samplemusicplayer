package com.duh.samplemusicplayer.model;

import android.os.Bundle;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerEvents;

public class MediaPlayerQueueModel {
    private MediaPlayerEvents event;
    private Bundle bundle;
    private IMusicPlayerListener playerListener;

    public MediaPlayerQueueModel(MediaPlayerEvents event, Bundle bundle, IMusicPlayerListener playerListener) {
        this.event = event;
        this.bundle = bundle;
        this.playerListener = playerListener;
    }

    public MediaPlayerEvents getEvent() {
        return event;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public IMusicPlayerListener getPlayerListener() {
        return playerListener;
    }

    public void setEvent(MediaPlayerEvents event) {
        this.event = event;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void setPlayerListener(IMusicPlayerListener playerListener) {
        this.playerListener = playerListener;
    }
}
