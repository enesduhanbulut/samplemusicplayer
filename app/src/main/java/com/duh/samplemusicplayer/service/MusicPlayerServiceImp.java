package com.duh.samplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.media.AudioProvider;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerEvents;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerManager;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MusicPlayerServiceImp extends Service implements IMusicPlayerService {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final ArrayList<Song> songList = new ArrayList<>();
    private AudioProvider audioProvider;
    private MediaPlayerManager mediaPlayerManager;
    private final IMusicPlayerServiceAIDL.Stub binder = new IMusicPlayerServiceAIDL.Stub() {
        @Override
        public void startMusic(Bundle bundle, IMusicPlayerListener playerListener) throws RemoteException {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.START_OR_PAUSE, bundle);
            playerListener.onSuccess(new Bundle());
        }

        @Override
        public void pauseMusic(IMusicPlayerListener playerListener) throws RemoteException {
           /* if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
            playerListener.onSuccess(null);*/
        }

        @Override
        public void stopMusic(IMusicPlayerListener playerListener) throws RemoteException {
            /*if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = null;
            playerListener.onSuccess(null);*/
        }

        @Override
        public void next(IMusicPlayerListener playerListener) throws RemoteException {
            /*int nextIndex = songList.indexOf(currentSong) + 1;
            if (nextIndex < songList.size()) {
                Bundle nextMusic = new Bundle();
                nextMusic.putParcelable(Constants.SONG, songList.get(nextIndex));
                startMusic(nextMusic, playerListener);
            } else {
                playerListener.onError(-1, "Can not play");
            }*/
        }

        @Override
        public void previous(IMusicPlayerListener playerListener) throws RemoteException {
            /*int nextIndex = songList.indexOf(currentSong) - 1;
            if (nextIndex < 0) {
                Bundle nextMusic = new Bundle();
                nextMusic.putParcelable(Constants.SONG, songList.get(nextIndex));
                startMusic(nextMusic, playerListener);
            } else {
                playerListener.onError(-1, "Can not play");
            }*/
        }

        @Override
        public void getSongList(IMusicPlayerListener playerListener) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.SONG_LIST, songList);
            playerListener.onSuccess(bundle);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        audioProvider = ((MusicApp) getApplication()).audioProvider;
        compositeDisposable.add(audioProvider.getSongObservable()
                .subscribeWith(new DisposableObserver<Song>() {
                    @Override
                    public void onNext(@NonNull Song song) {
                        songList.add(song);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

        audioProvider.getSongs();
        mediaPlayerManager = new MediaPlayerManager(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}

