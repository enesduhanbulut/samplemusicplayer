package com.duh.samplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;
import com.duh.samplemusicplayer.ServiceException;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.media.AudioProvider;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MusicPlayerServiceImp extends Service implements IMusicPlayerService{
    private AudioProvider audioProvider;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final ArrayList<Song> songList = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Song currentSong;
    private final IMusicPlayerServiceAIDL.Stub binder = new IMusicPlayerServiceAIDL.Stub() {
        @Override
        public void startMusic(Bundle bundle, IMusicPlayerListener playerListener) throws RemoteException {
            Song song = bundle.getParcelable(Constants.SONG);
            currentSong = song;
            Uri uri = Uri.fromFile(new File(song.getPath()));
            if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            mediaPlayer = MediaPlayer.create(MusicPlayerServiceImp.this, uri);
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build());
                mediaPlayer.start();
                playerListener.onSuccess(new Bundle());
        }

        @Override
        public void pauseMusic(IMusicPlayerListener playerListener) throws RemoteException {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
            playerListener.onSuccess(null);
        }

        @Override
        public void stopMusic(IMusicPlayerListener playerListener) throws RemoteException {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = null;
            playerListener.onSuccess(null);
        }

        @Override
        public void next(IMusicPlayerListener playerListener) throws RemoteException {
            int nextIndex = songList.indexOf(currentSong) + 1;
            if (nextIndex < songList.size()) {
                Bundle nextMusic = new Bundle();
                nextMusic.putParcelable(Constants.SONG, songList.get(nextIndex));
                startMusic(nextMusic, playerListener);
            } else {
                playerListener.onError(-1, "Can not play");
            }
        }

        @Override
        public void previous(IMusicPlayerListener playerListener) throws RemoteException {
            int nextIndex = songList.indexOf(currentSong) - 1;
            if (nextIndex < 0) {
                Bundle nextMusic = new Bundle();
                nextMusic.putParcelable(Constants.SONG, songList.get(nextIndex));
                startMusic(nextMusic, playerListener);
            } else {
                playerListener.onError(-1, "Can not play");
            }
        }

        @Override
        public void getSongList(IMusicPlayerListener playerListener) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.SONG_LIST, songList);
            playerListener.onSuccess(bundle);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer != null && mediaPlayer.isPlaying();
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

