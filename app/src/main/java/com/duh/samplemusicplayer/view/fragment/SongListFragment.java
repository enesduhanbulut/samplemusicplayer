package com.duh.samplemusicplayer.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.view.adapter.SongRecyclerViewAdapter;
import com.duh.samplemusicplayer.viewmodel.SongListViewModel;

import java.util.ArrayList;
import java.util.List;

public class SongListFragment extends Fragment {
    private SongListViewModel songListViewModel;
    private SongRecyclerViewAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SongListViewModel.Factory factory = new SongListViewModel.Factory();
        songListViewModel = factory.create(SongListViewModel.class);
        adapter = new SongRecyclerViewAdapter();
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
        songsRecyclerView.setAdapter(adapter);

        //Here is for test
        List<Song> list = new ArrayList<>();
        list.add(new Song("SongName", "SingerName", ""));
        list.add(new Song("SongName", "SingerName", ""));
        list.add(new Song("SongName", "SingerName", ""));
        adapter.updateList(list);


    }
}
