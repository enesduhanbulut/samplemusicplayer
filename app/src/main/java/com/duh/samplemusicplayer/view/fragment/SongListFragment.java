package com.duh.samplemusicplayer.view.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;
import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.view.adapter.SongRecyclerViewAdapter;
import com.duh.samplemusicplayer.viewmodel.PlayerViewModel;

import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SongListFragment extends Fragment {
    private PlayerViewModel playerViewModel;
    private SongRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IMusicPlayerServiceAIDL serviceAIDL = ((MusicApp) getActivity().getApplication()).serviceManager.getMusicService();
        PlayerViewModel.Factory factory = new PlayerViewModel.Factory(serviceAIDL);
        playerViewModel = new ViewModelProvider(requireActivity(), factory).get(PlayerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songlist, container, false);
        RecyclerView songsRecyclerView = view.findViewById(R.id.recyclerViewSongs);
        adapter = new SongRecyclerViewAdapter(playerViewModel::getAlbumCover, this::onItemClicked);
        songsRecyclerView.setAdapter(adapter);

        playerViewModel.getSongListObservable()
                .observeOn(Schedulers.io())
                .subscribeWith(onSongsReceived());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            playerViewModel.getSongList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onItemClicked(Song song) {
        playerViewModel.startMusic(song);
    }

    private DisposableObserver<Song> onSongsReceived() {
        return new DisposableObserver<Song>() {

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
