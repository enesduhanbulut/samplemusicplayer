package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

public class StartedState implements IMediaPlayerState {
    private Song currentSong;

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger) {
        switch (event) {
            case START:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.stop();
                    stateChanger.change(event, bundle, MediaPlayerStates.STOPPED);
                } else {
                    mediaPlayer.start();
                }
                break;
            case PAUSE:
                mediaPlayer.pause();
                stateChanger.change(MediaPlayerEvents.NONE, bundle, MediaPlayerStates.PAUSED);
                break;
            case NONE:
                song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    currentSong = song;
                }
                break;
            case NEXT:
            case PREVIOUS:
                song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    mediaPlayer.stop();
                    stateChanger.change(event, bundle, MediaPlayerStates.STOPPED);
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
