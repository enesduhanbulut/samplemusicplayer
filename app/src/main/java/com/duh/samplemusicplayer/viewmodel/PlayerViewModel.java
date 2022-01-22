package com.duh.samplemusicplayer.viewmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;
import com.duh.samplemusicplayer.ServiceException;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class PlayerViewModel extends ViewModel {
    private final IMusicPlayerServiceAIDL serviceAIDL;
    private final PublishSubject<Song> songPublisher = PublishSubject.create();
    private final PublishSubject<Song> currentSongObservable = PublishSubject.create();
    private final PublishSubject<Long> currentDurationObservable = PublishSubject.create();

    public PlayerViewModel(IMusicPlayerServiceAIDL serviceAIDL) {
        this.serviceAIDL = serviceAIDL;
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

    public void startMusic(Song song) {
        try {
            Bundle data = new Bundle();
            data.putParcelable(Constants.SONG, song);
            serviceAIDL.startMusic(data);
            currentSongObservable.onNext(song);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startMusic() {
        try {
            serviceAIDL.startMusic(new Bundle());
            currentSongObservable.onNext(getCurrentSong());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startNextMusic() {
        try {
            serviceAIDL.next();
            currentSongObservable.onNext(getCurrentSong());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startPreviousMusic() {
        try {
            serviceAIDL.previous();
            currentSongObservable.onNext(getCurrentSong());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void pauseMusic() {
        try {
            serviceAIDL.pauseMusic();
            currentSongObservable.onNext(getCurrentSong());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        try {
            return serviceAIDL.isPlaying();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Song getCurrentSong() {
        try {
            return serviceAIDL.getCurrentSong().getParcelable(Constants.SONG);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Observable<Song> getSongListObservable() {
        return songPublisher;
    }

    public Observable<Song> getCurrentSongObservable() {
        return currentSongObservable
                .delay(100, TimeUnit.MILLISECONDS);
    }

    public Observable<Long> getCurrentDurationObservable() {
        return Observable.interval(1, TimeUnit.SECONDS)
                .flatMap(aLong -> Observable.just(serviceAIDL.getCurrentDuration()));
    }

    public void getSongList() throws RemoteException {
        serviceAIDL.getSongList(new IMusicPlayerListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                ArrayList<Song> songs = bundle.getParcelableArrayList(Constants.SONG_LIST);
                for (Song song : songs) {
                    songPublisher.onNext(song);
                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                songPublisher.onError(new ServiceException(errorCode, errorMessage));
            }

            @Override
            public IBinder asBinder() {
                // TODO: 8.01.2022 whats this
                return null;
            }
        });
    }

    @Override
    protected void onCleared() {
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
