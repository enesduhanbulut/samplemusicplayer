package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class InitializedState implements IMediaPlayerState {
    private final MediaPlayerManager.IMediaPlayerListener mediaPlayerListener;

    public InitializedState(MediaPlayerManager.IMediaPlayerListener mediaPlayerListener) {
        this.mediaPlayerListener = mediaPlayerListener;
    }

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger, IMusicPlayerListener playerListener) {
        switch (event) {
            case START:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.setOnPreparedListener(preparedMediaPlayer -> {
                        mediaPlayerListener.onChanged(preparedMediaPlayer);
                        try {
                            stateChanger.change(event, bundle, MediaPlayerStates.PREPARED, playerListener);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                    mediaPlayer.prepareAsync();
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
