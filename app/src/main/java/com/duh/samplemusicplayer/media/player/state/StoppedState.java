package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class StoppedState implements IMediaPlayerState {

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger, IMusicPlayerListener playerListener) throws RemoteException {
        switch (event) {
            case START:
            case NEXT:
            case PREVIOUS:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.release();
                    stateChanger.change(MediaPlayerEvents.START, bundle, MediaPlayerStates.IDLE, playerListener);
                } else {
                    throw new IllegalStateException(String.format("STATE :%s, EVENT :%s",
                            this.getClass().getName(), event.name()));
                }
                break;
            case NONE:
                playerListener.onStopped();
                break;
            default:
                throw new IllegalStateException(String.format("STATE :%s, EVENT :%s",
                        this.getClass().getName(), event.name()));
        }
    }
}
