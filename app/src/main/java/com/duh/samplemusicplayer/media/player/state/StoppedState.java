package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class StoppedState implements IMediaPlayerState {

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger) {
        switch (event) {
            case START:
            case NEXT:
            case PREVIOUS:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.release();
                    stateChanger.change(MediaPlayerEvents.START, bundle, MediaPlayerStates.IDLE);
                } else {
                    throw new IllegalStateException(String.format("STATE :%s, EVENT :%s",
                            this.getClass().getName(), event.name()));
                }
                break;
            default:
                throw new IllegalStateException(String.format("STATE :%s, EVENT :%s",
                        this.getClass().getName(), event.name()));
        }
    }
}
