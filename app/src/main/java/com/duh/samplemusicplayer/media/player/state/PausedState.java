package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.media.player.PlayerUtils;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class PausedState implements IMediaPlayerState {

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger, IMusicPlayerListener playerListener) throws RemoteException {
        switch (event) {
            case START:
            case NEXT:
            case PREVIOUS:
                Song song = bundle.getParcelable(Constants.SONG);
                int seekTo = PlayerUtils.getSeekFromBundle(bundle);
                if (song != null) {
                    mediaPlayer.stop();
                    stateChanger.change(MediaPlayerEvents.START, bundle, MediaPlayerStates.STOPPED, playerListener);
                } else if (seekTo != -1) {
                    mediaPlayer.seekTo(seekTo * 1000);
                } else {
                    mediaPlayer.start();
                    stateChanger.change(MediaPlayerEvents.NONE, bundle, MediaPlayerStates.STARTED, playerListener);
                }
                break;
            case STOP:
                mediaPlayer.stop();
                stateChanger.change(event, bundle, MediaPlayerStates.STOPPED, playerListener);
                break;
            case NONE:
                playerListener.onPaused();
                break;
            default:
                throw new IllegalStateException(String.format("STATE :%s, EVENT :%s",
                        this.getClass().getName(), event.name()));
        }
    }
}
