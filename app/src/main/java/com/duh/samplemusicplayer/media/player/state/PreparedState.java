package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class PreparedState implements IMediaPlayerState {

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger) {
        switch (event) {
            case START:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.start();
                    stateChanger.change(MediaPlayerEvents.NONE, bundle, MediaPlayerStates.STARTED);
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
