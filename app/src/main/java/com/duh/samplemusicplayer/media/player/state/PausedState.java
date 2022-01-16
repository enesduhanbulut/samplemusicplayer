package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class PausedState implements IMediaPlayerState {

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger) {
        switch (event) {
            case START:
            case NEXT:
            case PREVIOUS:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.stop();
                    stateChanger.change(MediaPlayerEvents.START, bundle, MediaPlayerStates.STOPPED);
                } else {
                    mediaPlayer.start();
                    stateChanger.change(MediaPlayerEvents.NONE, bundle, MediaPlayerStates.STARTED);
                }
                break;
            case STOP:
                mediaPlayer.stop();
                stateChanger.change(event, bundle, MediaPlayerStates.STOPPED);
                break;
            case NONE:
                break;
            default:
                throw new IllegalStateException(String.format("STATE :%s, EVENT :%s",
                            this.getClass().getName(), event.name()));
        }
    }
}
