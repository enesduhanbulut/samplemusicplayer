package com.duh.samplemusicplayer.media.player.state;

import static com.duh.samplemusicplayer.media.player.state.MediaPlayerEvents.START;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.duh.samplemusicplayer.IMusicPlayerListener;
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
    public void handle(MediaPlayerEvents event, Bundle bundle, MediaPlayer mediaPlayer, IStateChanger stateChanger, IMusicPlayerListener playerListener) throws RemoteException {
        switch (event) {
            case START:
            case PREVIOUS:
            case NEXT:
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
                    stateChanger.change(START, bundle, MediaPlayerStates.INITIALIZED, playerListener);

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
