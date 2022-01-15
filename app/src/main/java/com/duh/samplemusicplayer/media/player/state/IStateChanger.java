package com.duh.samplemusicplayer.media.player.state;

import android.os.Bundle;

public interface IStateChanger {
    void change(MediaPlayerEvents event, Bundle bundle, MediaPlayerStates mediaPlayerState);
}
