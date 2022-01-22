package com.duh.samplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.media.AudioProvider;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerEvents;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerManager;
import com.duh.samplemusicplayer.utils.Constants;

import java.util.ArrayList;

public class MusicPlayerServiceImp extends Service implements IMusicPlayerService {
    private MediaPlayerManager mediaPlayerManager;
    private final IMusicPlayerServiceAIDL.Stub binder = new IMusicPlayerServiceAIDL.Stub() {
        @Override
        public void startMusic(Bundle bundle) {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.START, bundle);
        }

        @Override
        public void pauseMusic() {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.PAUSE, new Bundle());
        }

        @Override
        public void stopMusic() {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.STOP, new Bundle());
        }

        @Override
        public void next() {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.NEXT, new Bundle());
        }

        @Override
        public void previous() {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.PREVIOUS, new Bundle());
        }

        @Override
        public void getSongList(IMusicPlayerListener playerListener) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.SONG_LIST, (ArrayList<? extends Parcelable>) mediaPlayerManager.getSongList());
            playerListener.onSuccess(bundle);
        }

        @Override
        public boolean isPlaying() {
            return mediaPlayerManager.isPlaying();
        }

        @Override
        public Bundle getCurrentSong() {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.SONG, mediaPlayerManager.getCurrentSong());
            return bundle;
        }

        @Override
        public long getCurrentDuration() {
            return mediaPlayerManager.getCurrentDuration();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        AudioProvider audioProvider = ((MusicApp) getApplication()).audioProvider;
        mediaPlayerManager = new MediaPlayerManager(this, audioProvider);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayerManager.release();
    }
}

