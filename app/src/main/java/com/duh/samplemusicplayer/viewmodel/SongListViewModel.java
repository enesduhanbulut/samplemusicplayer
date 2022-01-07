package com.duh.samplemusicplayer.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.reactivex.rxjava3.annotations.NonNull;

public class SongListViewModel extends ViewModel {

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        public Factory() {
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SongListViewModel();
        }
    }
}
