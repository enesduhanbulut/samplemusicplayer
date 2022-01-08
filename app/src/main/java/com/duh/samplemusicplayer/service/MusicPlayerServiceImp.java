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
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MusicPlayerServiceImp extends Service implements IMusicPlayerService {
    private AudioProvider audioProvider;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ArrayList<Song> songList = new ArrayList<>();
    private final IMusicPlayerServiceAIDL.Stub binder = new IMusicPlayerServiceAIDL.Stub() {
        @Override
        public void startMusic(IMusicPlayerListener playerListener) {
        }

        @Override
        public void pauseMusic(IMusicPlayerListener playerListener) {

        }

        @Override
        public void stopMusic(IMusicPlayerListener playerListener) {

        }

        @Override
        public void next(IMusicPlayerListener playerListener) {

        }

        @Override
        public void previous(IMusicPlayerListener playerListener) {

        }

        @Override
        public void getSongList(IMusicPlayerListener playerListener) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.SONG_LIST, songList);
            playerListener.onSuccess(bundle);
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

