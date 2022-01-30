package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.media.player.PlayerUtils;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class StartedState implements IMediaPlayerState {
    private Song currentSong;

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger, IMusicPlayerListener playerListener) throws RemoteException {
        switch (event) {
            case START:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.stop();
                    stateChanger.change(event, bundle, MediaPlayerStates.STOPPED, playerListener);
                } else {
                    mediaPlayer.start();
                    playerListener.onStarted(PlayerUtils.createSongBundle(currentSong));
                }
                break;
            case PAUSE:
                mediaPlayer.pause();
                stateChanger.change(MediaPlayerEvents.NONE, bundle, MediaPlayerStates.PAUSED, playerListener);
                break;
            case NONE:
                mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                    try {
                        stateChanger.change(MediaPlayerEvents.NEXT, new Bundle(), MediaPlayerStates.IDLE, playerListener);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
                song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    currentSong = song;
                }
                playerListener.onStarted(PlayerUtils.createSongBundle(currentSong));
                break;
            case NEXT:
            case PREVIOUS:
                song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.stop();
                    stateChanger.change(event, bundle, MediaPlayerStates.STOPPED, playerListener);
                }
                break;
            default:
                throw new IllegalStateException(String.format("STATE :%s, EVENT :%s",
                        this.getClass().getName(), event.name()));
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }
}
