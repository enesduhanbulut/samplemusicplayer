package com.duh.samplemusicplayer.view.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.view.adapter.SongRecyclerViewAdapter;
import com.duh.samplemusicplayer.viewmodel.SongListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SongListFragment extends Fragment {
    private SongListViewModel songListViewModel;
    private SongRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SongListViewModel.Factory factory = new SongListViewModel.Factory(((MusicApp)getActivity().getApplication()).serviceManager.getMusicService());
        songListViewModel = new ViewModelProvider(getActivity(), factory).get(SongListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView songsRecyclerView = view.findViewById(R.id.recyclerViewSongs);
        adapter = new SongRecyclerViewAdapter(songListViewModel::getAlbumCover, song -> Toast.makeText(view.getContext(), song.toString(), Toast.LENGTH_LONG).show());
        songsRecyclerView.setAdapter(adapter);

        songListViewModel.getSongObservable()
                .subscribeWith(onSongsReceived());
        try {
            songListViewModel.getSongList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private DisposableObserver<Song> onSongsReceived(){
        return new DisposableObserver<Song>(){

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Song song) {
                adapter.addItemWithUpdate(song);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        };
    }
}
