package com.duh.samplemusicplayer.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.model.Song;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;

public class SongRecyclerViewAdapter extends AbstractRecyclerViewAdapter<Song, SongRecyclerViewAdapter.ViewHolder> {
    private final OnItemClickListener onClickListener;

    public SongRecyclerViewAdapter(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    boolean areItemsSame(Song object, Song object2) {
        return object.equals(object2);
    }

    @Override
    boolean areContentsTheSame(Song object, Song object2) {
        return object.getSongId() == object2.getSongId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), onClickListener);
    }

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView songTitle;
        private final MaterialTextView songArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.textViewSongTitle);
            songArtist = itemView.findViewById(R.id.textViewSongArtist);
        }

        public void bind(Song song, OnItemClickListener listener) {
            songArtist.setText(song.getSongArtist());
            songTitle.setText(new File(song.getPath()).getName());
            itemView.setOnClickListener(view -> listener.onItemClick(song));
        }
    }
}
