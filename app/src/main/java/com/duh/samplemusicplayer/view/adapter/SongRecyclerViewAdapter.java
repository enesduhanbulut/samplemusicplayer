package com.duh.samplemusicplayer.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.model.Song;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;

public class SongRecyclerViewAdapter extends AbstractRecyclerViewAdapter<Song, SongRecyclerViewAdapter.ViewHolder> {

    @Override
    boolean areItemsSame(Song object, Song object2) {
        return object.equals(object2);
    }

    @Override
    boolean areContentsTheSame(Song object, Song object2) {
        return (object.getSongTitle() != null && object.getSongTitle().equals(object2.getSongTitle())) &&
                (object.getSongArtist() != null && object.getSongArtist().equals(object2.getSongArtist())) &&
                (object.getGenre() != null && object.getGenre().equals(object2.getGenre()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.songArtist.setText(getItem(position).getSongArtist());
        holder.songTitle.setText(new File(getItem(position).getPath()).getName());
        // TODO: 8.01.2022 get album cover set it to imageview
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView albumCover;
        private final MaterialTextView songTitle;
        private final MaterialTextView songArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumCover = itemView.findViewById(R.id.imageViewAlbumCover);
            songTitle = itemView.findViewById(R.id.textViewSongTitle);
            songArtist = itemView.findViewById(R.id.textViewSongArtist);
        }
    }
}
