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

public class SongRecyclerViewAdapter extends AbstractRecyclerViewAdapter<Song, SongRecyclerViewAdapter.ViewHolder> {

    @Override
    boolean areItemsSame(Song object, Song object2) {
        return object.equals(object2);
    }

    @Override
    boolean areContentsTheSame(Song object, Song object2) {
        return object.getSingerName().equals(object2.getSingerName()) &&
                object.getSongName().equals(object2.getSongName()) &&
                object.getAlbumCoverPath().equals(object2.getAlbumCoverPath());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song, parent, false);
        return new ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.singerName.setText(getItem(position).getSingerName());
        holder.songName.setText(getItem(position).getSongName());
        // TODO: 8.01.2022 get album cover set it to imageview
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumCover;
        private MaterialTextView songName;
        private MaterialTextView singerName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumCover = itemView.findViewById(R.id.imageViewAlbumCover);
            songName = itemView.findViewById(R.id.textViewSongName);
            singerName = itemView.findViewById(R.id.textViewSingerName);
        }
    }
}
