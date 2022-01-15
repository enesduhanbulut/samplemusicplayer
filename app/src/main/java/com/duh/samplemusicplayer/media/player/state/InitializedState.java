package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class InitializedState implements IMediaPlayerState {
    private final MediaPlayerManager.IMediaPlayerListener mediaPlayerListener;

    public InitializedState(MediaPlayerManager.IMediaPlayerListener mediaPlayerListener) {
        this.mediaPlayerListener = mediaPlayerListener;
    }

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger) {
        switch (event) {
            case START_OR_PAUSE:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.setOnPreparedListener(preparedMediaPlayer -> {
                        mediaPlayerListener.onChanged(preparedMediaPlayer);
                        stateChanger.change(event, bundle, MediaPlayerStates.PREPARED);
                    });
                    mediaPlayer.prepareAsync();
                } else {
                    throw new IllegalStateException("Can not pause on this state");
                }
                break;
            default:
                throw new IllegalStateException("Can not pause on this state");
        }
    }
}
