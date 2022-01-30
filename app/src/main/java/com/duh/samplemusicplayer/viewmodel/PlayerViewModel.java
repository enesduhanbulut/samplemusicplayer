package com.duh.samplemusicplayer.viewmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;
import com.duh.samplemusicplayer.media.player.PlayerModel;
import com.duh.samplemusicplayer.media.player.PlayerUtils;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerStates;
import com.duh.samplemusicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlayerViewModel extends ViewModel {
    private final IMusicPlayerServiceAIDL serviceAIDL;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PlayerModel playerModel = new PlayerModel();
    private final MutableLiveData<PlayerModel> playerLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Song>> songListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> durationLiveData = new MutableLiveData<>();
    private List<Song> songList = new ArrayList<>();
    private long currentCuration;

    private final IMusicPlayerListener playerListener = new IMusicPlayerListener() {

        @Override
        public void onStarted(Bundle songBundle) throws RemoteException {
            playerModel.setCurrentSong(PlayerUtils.getSongFromBundle(songBundle));
            playerModel.setCurrentState(MediaPlayerStates.STARTED);
            playerLiveData.postValue(playerModel);
        }

        @Override
        public void onPaused() throws RemoteException {
            playerModel.setCurrentState(MediaPlayerStates.PAUSED);
            playerLiveData.postValue(playerModel);
        }

        @Override
        public void onStopped() throws RemoteException {
            playerModel.setCurrentState(MediaPlayerStates.STOPPED);
            playerLiveData.postValue(playerModel);
        }

        @Override
        public void onListChanged(Bundle listBundle) throws RemoteException {
            songList = PlayerUtils.getSongListFromBundle(listBundle);
            songListLiveData.postValue(songList);
        }

        @Override
        public void onError(int errorCode, String errorMessage) throws RemoteException {
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };

    public PlayerViewModel(IMusicPlayerServiceAIDL serviceAIDL) {
        this.serviceAIDL = serviceAIDL;
        startDurationObservable();
    }

    private void startDurationObservable() {
        compositeDisposable.add(Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onNext(@NonNull Long aLong) {
                try {
                    currentCuration = serviceAIDL.getCurrentDuration();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                durationLiveData.postValue(currentCuration);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    public Bitmap getAlbumCover(String path) {
        try (MediaMetadataRetriever mmr = new MediaMetadataRetriever()) {
            mmr.setDataSource(path);
            if (mmr.getEmbeddedPicture() != null) {
                return BitmapFactory.decodeByteArray(mmr.getEmbeddedPicture(), 0, mmr.getEmbeddedPicture().length);
            } else {
                return null;
            }
        }
    }

    public LiveData<PlayerModel> getPlayerLiveData() {
        return playerLiveData;
    }

    public LiveData<List<Song>> getSongListLiveData() {
        return songListLiveData;
    }

    public LiveData<Long> getDurationLiveData() {
        return durationLiveData;
    }

    public void startMusic(Song song) {
        try {
            serviceAIDL.startMusic(PlayerUtils.createSongBundle(song), playerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startMusic() {
        try {
            serviceAIDL.startMusic(new Bundle(), playerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startNextMusic() {
        try {
            serviceAIDL.next(playerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startPreviousMusic() {
        try {
            serviceAIDL.previous(playerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void pauseMusic() {
        try {
            serviceAIDL.pauseMusic(playerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getSongList() throws RemoteException {
        serviceAIDL.getSongList(playerListener);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final IMusicPlayerServiceAIDL serviceAIDL;

        public Factory(IMusicPlayerServiceAIDL serviceAIDL) {
            this.serviceAIDL = serviceAIDL;
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PlayerViewModel(serviceAIDL);
        }
    }
}
