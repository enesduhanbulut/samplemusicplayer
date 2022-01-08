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

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SongListViewModel extends ViewModel {
    private final IMusicPlayerServiceAIDL serviceAIDL;
    private final PublishSubject<Song> songPublisher = PublishSubject.create();

    public SongListViewModel(IMusicPlayerServiceAIDL serviceAIDL) {
        this.serviceAIDL = serviceAIDL;
    }

    public Bitmap getAlbumCover(String path) {
        try (MediaMetadataRetriever mmr = new MediaMetadataRetriever()) {
            mmr.setDataSource(path);
            return BitmapFactory.decodeByteArray(mmr.getEmbeddedPicture(), 0, mmr.getEmbeddedPicture().length);
        }
    }

    public Observable<Song> getSongObservable() {
        return songPublisher;
    }

    public void getSongList() throws RemoteException {
        serviceAIDL.getSongList(new IMusicPlayerListener() {
            @Override
            public void onSuccess(Bundle bundle) throws RemoteException {
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
            return (T) new SongListViewModel(serviceAIDL);
        }
    }
}
