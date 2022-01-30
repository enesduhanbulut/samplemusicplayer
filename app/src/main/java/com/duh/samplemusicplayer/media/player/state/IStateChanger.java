package com.duh.samplemusicplayer.media.player.state;

import android.os.Bundle;
import android.os.RemoteException;

import com.duh.samplemusicplayer.IMusicPlayerListener;

public interface IStateChanger {
    void change(MediaPlayerEvents event, Bundle bundle, MediaPlayerStates mediaPlayerState, IMusicPlayerListener mediaPlayerListener) throws RemoteException;
}
