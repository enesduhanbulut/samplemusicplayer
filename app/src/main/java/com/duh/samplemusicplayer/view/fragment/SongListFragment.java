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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;
import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.view.adapter.SongRecyclerViewAdapter;
import com.duh.samplemusicplayer.viewmodel.PlayerViewModel;

import java.util.List;

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(songsRecyclerView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        songsRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(songsRecyclerView.getContext(), layoutManager.getOrientation());
        songsRecyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new SongRecyclerViewAdapter(this::onItemClicked);
        songsRecyclerView.setAdapter(adapter);

        playerViewModel.getSongListLiveData().observe(getViewLifecycleOwner(), this::onSongsReceived);
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

    private void onSongsReceived(List<Song> songList) {
        adapter.updateList(songList);
    }
}
