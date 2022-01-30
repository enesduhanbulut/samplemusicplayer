package com.duh.samplemusicplayer.media.player.state;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;

import com.duh.samplemusicplayer.IMusicPlayerListener;

public interface IMediaPlayerState {
    void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger, IMusicPlayerListener playerListener) throws RemoteException;
}
