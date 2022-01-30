package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.duh.samplemusicplayer.IMusicPlayerListener;

public class ErrorState implements IMediaPlayerState {

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger, IMusicPlayerListener playerListener) {

    }
}
