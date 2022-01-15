package com.duh.samplemusicplayer.media.player.state;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

import java.io.File;
import java.io.IOException;

public class IdleState implements IMediaPlayerState {
    private final Context context;
    private final MediaPlayerManager.IMediaPlayerListener initListener;

    public IdleState(Context context, MediaPlayerManager.IMediaPlayerListener initListener) {
        this.context = context;
        this.initListener = initListener;
    }

    @Override
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger) {
        switch (event) {
            case START_OR_PAUSE:
                Song song = bundle.getParcelable(Constants.SONG);
                if (song != null) {
                    Uri uri = Uri.fromFile(new File(song.getPath()));
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(context, uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build());
                    initListener.onChanged(mediaPlayer);
                    stateChanger.change(event, bundle, MediaPlayerStates.INITIALIZED);

                } else {
                    throw new IllegalStateException("Can not pause on this state");
                }
                break;
            default:
                throw new IllegalStateException("Can not pause on this state");
        }
    }
}
